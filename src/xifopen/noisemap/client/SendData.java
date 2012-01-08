package xifopen.noisemap.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Sends nearest BSSID and noise level to a PHP page with the POST method
 */
public class SendData {
    public void test(){
        new SendData().send("http://craftsrv5.epfl.ch/projects/noisemap/help.php",
                "x=100&y=100&noise=80"); // To check the server side part (the output) use http://hurl.it
    }
    /**
     * @param targetURL
     * @param urlParameters like x=1&y=2
     */
    public void send(String targetURL, String urlParameters){
        int status = 0;
        URL url;
        HttpURLConnection conn = null;  
        try {
          System.setProperty("http.keepAlive", "false");
          url = new URL(targetURL);
          conn = (HttpURLConnection)url.openConnection();
          conn.setRequestMethod("POST");
          conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");		
          conn.setRequestProperty("Content-Length", "" + 
                   Integer.toString(urlParameters.getBytes().length));
          conn.setRequestProperty("Content-Language", "en-US");  
          conn.setUseCaches (false);
          conn.setDoInput(true);
          conn.setDoOutput(true);
          DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
          wr.writeBytes(urlParameters);    // sends request
          wr.flush();
          wr.close();
          status = conn.getResponseCode();
          if(status == HttpURLConnection.HTTP_OK)
              status = 0;
          else
              throw new Issue("Status: "+status+" URL: "+targetURL);
        }
        catch (IOException ex) {
          throw new Issue("Malformed URL: "+targetURL);
        }
        finally {
          if(conn != null) {
            conn.disconnect(); 
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
