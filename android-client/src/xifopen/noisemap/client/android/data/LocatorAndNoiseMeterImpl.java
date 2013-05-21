package xifopen.noisemap.client.android.data;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class LocatorAndNoiseMeterImpl implements LocatorAndNoiseMeter{
	private static final String TAG = "LocatorAndNoiseMeterImpl";
	private static final boolean wardriving = true;  // for the demo
	private WifiManager wifi;
	private int tried = 0;
	private String ip;

	public static String noisemap_server_url = "http://craftsrv5.epfl.ch/projects/noisemap";
	public static String payment_server_url = "http://128.178.254.243:8080/RestPaymentServer/webresources/paymentService";
	//"http://128.178.13.166:8080/RestPaymentServer/webresources/paymentService";
	public String bssid;
	private int rate = -1;		 	// device specific
	private int minBufferSize = -10;
	
	public class Locator extends BroadcastReceiver {
		 @Override 
	     public void onReceive(Context context, Intent i) {
			 NetworkInfo networkInfo = (NetworkInfo) i.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
			 if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI){
				 WifiManager myWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
				 bssid = myWifiManager.getConnectionInfo().getBSSID();
				 // if(maxdb==-200)
			     //	    throw new Issue("This application works only inside the Rolex Learning Center");
			 }
	     }
	}
	
	public LocatorAndNoiseMeterImpl(WifiManager wifi){
		this.wifi = wifi;
		if(!wardriving)
			this.bssid = locator(this.wifi); 
		
        int[] sampleRates = new int[] { 8000, 11025, 22050, 44100 };
        for (int aRate : sampleRates) {
        	try{
        		minBufferSize = AudioRecord.getMinBufferSize(aRate,
        				AudioFormat.CHANNEL_CONFIGURATION_MONO,
        				AudioFormat.ENCODING_PCM_16BIT);
        		rate = aRate;
        		break;				// if there is no exception then it uses this sample rate
        	} catch(Exception e){} 	// continue
        }
    }
	public void send(){
		double db = 0;
		if(wardriving)
			this.bssid = locator(this.wifi);
		for(int i=0; i<5; i++){
			try{					// if the user is speaking on the phone, then it tries after 5 minutes
				db += noiselevel();
				wait1sec();
			} catch(Exception e){
				wait5min();
			}
		}
		db = db/5;	// every 5 seconds it sends the avg of 5 measurements.
		// Each measurement is done 1 second after the previous one.
		Log.i(TAG, "Sendingnoise: "+db+"\n");
		send2noisemapserver(db); 
		//send2paymentserver(db);
		Log.v(TAG, "Sent successfully to server the following data: "+db+" db in the AOI near the router "+bssid+"\n");
	}
	private void wait1sec(){
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			throw new Issue("Interrupted while waiting for a sec");
		}
	}
	private void wait5min(){
		try {
			Thread.sleep(300000);
		} catch (InterruptedException e1) {
			throw new Issue("Interrupted while waiting for a sec");
		}
	}
 	private String locator(WifiManager wifi){		// method only for the demo
 		ip = wifi.getConnectionInfo().getMacAddress();	// probably not debuggable
 		if(ip.equals("38:E7:D8:1E:CA:1E"))
 			ip = "Auguste";
 		else if(ip.equals("54:04:A6:48:48:DA"))
 			ip = "Nikos";
        return bssid;
	}
	private double noiselevel(){
        double db = -200;
    	for(int i=0; i<5; i++){
    		try{
    			db = measure();
    		}
            catch(Exception e){
            	if(i==4)
            		throw new Issue("Failed repeatedly to read recorded data from microphone.\n"+e.getMessage());
            	else
            		wait1sec();
            }
    	}
	    return db;
    }
	private double measure() throws Issue{
		double db = -200;
		if(rate==-1 || minBufferSize<=0)
        	throw new Issue("Failed to get minimum buffer size of microphone");
		AudioRecord audioInput = new AudioRecord(MediaRecorder.AudioSource.MIC,
                rate,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                minBufferSize*2);
        short[] buffer = new short[minBufferSize*2];
        audioInput.startRecording();
        int nread = audioInput.read(buffer, 0, buffer.length);
        audioInput.stop();
        if(nread==AudioRecord.ERROR_BAD_VALUE || nread==AudioRecord.ERROR_INVALID_OPERATION)
        	throw new Issue("Check if the application Sound Recorder works.\n");
    	db = SignalPower.calculatePowerDb(buffer, 0, buffer.length);
    	if(db==Float.NEGATIVE_INFINITY)
    		throw new Issue("Check if sound is mute because input is empty.\n");
		BigDecimal bd = new BigDecimal(db);
		BigDecimal rounded = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
		db = rounded.doubleValue();
        return db;
	}
    /**
     * Sends HTTP POST with bssid, noise and timestamp in milliseconds
     */
	private void send2noisemapserver(double db){
		int status = 0;
	    HttpClient httpclient = new DefaultHttpClient();
	    String url = LocatorAndNoiseMeterImpl.noisemap_server_url+"/set.php";
	    HttpPost httppost = new HttpPost(url);
	    try {
	    	System.setProperty("http.keepAlive", "false");
	    	url += "?";
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
	        nameValuePairs.add(new BasicNameValuePair("bssid", this.bssid));
	        url += "bssid="+this.bssid+"&";
	        nameValuePairs.add(new BasicNameValuePair("noise", ""+db));
	        url += "noise="+db+"&";
	        String time = ""+Calendar.getInstance().getTimeInMillis();
	        nameValuePairs.add(new BasicNameValuePair("timestamp", time));
	        url += "timestamp="+time;
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        HttpResponse response = httpclient.execute(httppost);
	        status = response.getStatusLine().getStatusCode();
	        if(status == HttpStatus.SC_OK)
	        	status = 0;
	        else
	            throw new Issue("Status: "+status);
	        Log.i(TAG, url);		// in case of success, keep the url for debugging
	    } catch (ClientProtocolException e) {
	    	throw new Issue("Malformed URL: "+e.getMessage());
	    } catch (IOException e) {
	    	tried++;
	    	Log.w(TAG, "Failed to send data to noisemap, will try sending after 2 seconds for "+tried+" time\n");
	    	wait1sec();
	    	if(tried<30){
	    		send2noisemapserver(db);
	    		tried = 0;	// if no exception then it resets 'tried'
	    	}
	    	else{
	    		tried = 0;
	    		throw new Issue("Tried "+tried+" times to send data with no success\n"+e.getMessage());
	    	}
	    }
	}
	/**
     * Sends HTTP POST with bssid, noise and timestamp in milliseconds
     */
	private void send2paymentserver(double db){
		int status = 0;
	    HttpClient httpclient = new DefaultHttpClient();
	    String url = LocatorAndNoiseMeterImpl.payment_server_url;
	    HttpPost httppost = new HttpPost(url);
	    try {
	    	System.setProperty("http.keepAlive", "false");
	    	url += "?";
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
	        nameValuePairs.add(new BasicNameValuePair("userID", ip));
	        url += "userID="+ip+"&";
	        nameValuePairs.add(new BasicNameValuePair("routerName", ""+this.bssid));
	        url += "routerName="+this.bssid+"&";
	        nameValuePairs.add(new BasicNameValuePair("soundLevel", ""+db));
	        url += "soundLevel="+db;
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        HttpResponse response = httpclient.execute(httppost);
	        status = response.getStatusLine().getStatusCode();
	        if(status == HttpStatus.SC_OK)
	        	status = 0;
	        else
	            throw new Issue("Status: "+status);
	        Log.i(TAG, url);		// in case of success, keep the url for debugging
	    } catch (ClientProtocolException e) {
	    	throw new Issue("Malformed URL: "+e.getMessage());
	    } catch (IOException e) {
	    	tried++;
	    	Log.w(TAG, "Failed to send data to payment server, will try sending after 2 seconds for "+tried+" time\n");
	    	wait1sec();
	    	if(tried<30){
	    		send2paymentserver(db);
	    		tried = 0;	// if no exception then it resets 'tried'
	    	}
	    	else{
	    		tried = 0;
	    		throw new Issue("Tried "+tried+" times to send data with no success\n"+e.getMessage());
	    	}
	    }
	}
    /**
     * Instead of polluting the java code with methods that throw exceptions,
     * a class can throw runtime exceptions as an inner class
     * Writing description of erroneous cases can describe indirectly the
     * functionality of the code in normal execution
     */
    public class Issue extends RuntimeException{
    	private static final String TAG = "LocatorAndNoiseMeterImpl.TAG";
		private static final long serialVersionUID = 1L;
		private String mistake;
        public Issue(String say_what_happened){
            super(say_what_happened);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            printStackTrace(pw);
            String stacktrace = sw.toString();
            String precondition = "Please check your internet connection.\n";
            mistake = precondition + say_what_happened+"\n"+stacktrace;
            Log.e(TAG,mistake);
        }
        public String get(){
            return mistake;
        }
    }
}
