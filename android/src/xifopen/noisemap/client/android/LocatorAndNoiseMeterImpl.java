package xifopen.noisemap.client.android;

import java.io.DataOutputStream;
import java.io.IOException;
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

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

public class LocatorAndNoiseMeterImpl implements LocatorAndNoiseMeter{
	private String bssid;
	public LocatorAndNoiseMeterImpl(WifiManager wifi){
		bssid = locator(wifi);         
    }
	public void send(){
		send(noiselevel()); 
	}
 	private String locator(WifiManager wifi){
		String strongestBSSID = "";
		//WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if(wifi==null)
        	throw new Issue("Please enable Wireless Connection");
        wifi.startScan();
        int maxdb = -200;
        for(ScanResult s : wifi.getScanResults())
        	if(maxdb<s.level && s.SSID.contains("epfl")){
        		maxdb = s.level;
        		strongestBSSID = s.BSSID;
        	}
        if(maxdb==-200)
        	throw new Issue("This application works only inside the Rolex Learning Center");
        return strongestBSSID;
	}
	private double noiselevel(){
        double db = -200;
        try{
	        int minBufferSize = -10;
	        int rate = -1;			 // device specific
	        int[] sampleRates = new int[] { 8000, 11025, 22050, 44100 };
	        for (int aRate : sampleRates) {
	        	try{
	        		minBufferSize = AudioRecord.getMinBufferSize(aRate,
	        				AudioFormat.CHANNEL_CONFIGURATION_MONO,
	        				AudioFormat.ENCODING_PCM_16BIT);
	        		rate = aRate;
	        		break;
	        	} catch(Exception e){} // continue
	        }
	        if(rate==-1)
	        	throw new Issue("Failed to get minimum buffer size of microphone");
	        if(minBufferSize>0){
		        AudioRecord audioInput = new AudioRecord(MediaRecorder.AudioSource.MIC,
		                rate,
		                AudioFormat.CHANNEL_CONFIGURATION_MONO,
		                AudioFormat.ENCODING_PCM_16BIT,
		                minBufferSize*2);
		        short[] buffer = new short[minBufferSize*2];
		        audioInput.startRecording();
		        int nread = audioInput.read(buffer, 0, buffer.length);
		        audioInput.stop();
		        if(nread>0){
		        	db = SignalPower.calculatePowerDb(buffer, 0, buffer.length);
		        	BigDecimal bd = new BigDecimal(db);
		            BigDecimal rounded = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
		            db = rounded.doubleValue();
		        }
		        else
		        	throw new Issue("Failed to read recorded data from microphone");
	        }
        }
        catch(Exception e){
        	throw new Issue("Unexpected error: "+e.getMessage());
        }
	    return db;
    }
    /**
     * @param targetURL
     * @param urlParameters like x=1&y=2
     * http://craftsrv5.epfl.ch/projects/noisemap/set.php?bssid=00:24:97:f2:9e:81&noise=-30&timestamp=1326216371395
     * "x=100&y=100&noise=80"); // To check the server side part (the output) use http://hurl.it
     */
	private void send(double db) {
		int status = 0;
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost("http://craftsrv5.epfl.ch/projects/noisemap/set.php");
	    try {
	    	System.setProperty("http.keepAlive", "false");
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("bssid", this.bssid));
	        nameValuePairs.add(new BasicNameValuePair("noise", ""+db));
	        nameValuePairs.add(new BasicNameValuePair("timestamp", ""+Calendar.getInstance().getTimeInMillis()));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        HttpResponse response = httpclient.execute(httppost);
	        status = response.getStatusLine().getStatusCode();
	        if(status == HttpStatus.SC_OK)
	        	status = 0;
	        else
	            throw new Issue("Status: "+status);
	    } catch (ClientProtocolException e) {
	    	throw new Issue("Malformed URL: "+e.getMessage());
	    } catch (IOException e) {
	    	throw new Issue(e.getMessage());
	    }
	}
    /**
     * Instead of polluting the java code with methods that throw exceptions,
     * a class can throw runtime exceptions as an inner class
     * Writing description of erroneous cases can describe indirectly the
     * functionality of the code in normal execution
     */
    public class Issue extends RuntimeException{
		private static final long serialVersionUID = 1L;
		private String mistake;
        public Issue(String say_what_happened){
            super(say_what_happened);
            String precondition = "Please check your internet connection.\n";
            mistake = precondition + say_what_happened;
        }
        public String get(){
            return mistake;
        }
    }
}
