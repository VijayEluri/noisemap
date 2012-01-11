package xifopen.noisemap.client.android;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpEntity;
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
import org.apache.http.params.HttpParams;

import xifopen.noisemap.client.android.LocatorAndNoiseMeterImpl.Issue;

public class Plotter {
	// http://craftsrv5.epfl.ch/projects/noisemap/get.php?min=1326216371390&max=1326216371399
	// send("http://craftsrv5.epfl.ch/projects/noisemap/help.php","bssid="+bssid+"&noise="+noiselevel());
	private List<AreaOfInterest> get(long min, long max) {
		List<AreaOfInterest> list = new ArrayList<AreaOfInterest>();
	    HttpPost httppost = new HttpPost("http://craftsrv5.epfl.ch/projects/noisemap/get.php");
	    try {
	    	System.setProperty("http.keepAlive", "false");
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("min", ""+min));
	        nameValuePairs.add(new BasicNameValuePair("max", ""+max));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        HttpResponse response = new DefaultHttpClient().execute(httppost);
	        HttpEntity entity = response.getEntity();
	        if(entity!=null){
	        	BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
	            String line = null;
	            try {
	                while ((line = reader.readLine()) != null) {
	                	String[] fields = line.trim().split("\\s+");
	                	list.add(new AreaOfInterest(fields[0], fields[1], fields[2], fields[3], fields[4]));
	                }
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	        else
	        	throw new Issue("empty HttpEntity");
	    } catch (IOException e) {
	    	throw new Issue(e.getMessage());
	    }
	    return list;
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
