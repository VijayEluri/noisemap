package xifopen.noisemap.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * sends nearest BSSID and noise level to a PHP page with the POST method
 */
public class SendData {
    public void test(){
        System.out.println(new SendData().send("http://craftsrv5.epfl.ch/projects/noisemap/help.php",
                "x=100&y=100&noise=80")); // you can first test the server using http://hurl.it
    }
    /**
     * @param targetURL
     * @param urlParameters like x=1&y=2
     * @return 0 on success or -1 if the URL is malformed or the HTTP status code
     */
    public static int send(String targetURL, String urlParameters){
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
        }
        catch (IOException ex) {
          status = -1;
        }
        finally {
          if(conn != null) {
            conn.disconnect(); 
          }
        }
        return status;
    }
}
