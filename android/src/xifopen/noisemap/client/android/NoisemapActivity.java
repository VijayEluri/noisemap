package xifopen.noisemap.client.android;

import java.math.BigDecimal;

import android.app.Activity;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class NoisemapActivity extends Activity {
	private static final String TAG = "NoisemapActivity";
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        tv.setText("18 ");
        //setContentView(R.layout.main);
        String strongestBSSID = "";
        double db = -200;
        
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifi.startScan();
        int maxdb = -200;
        for(ScanResult s : wifi.getScanResults())
        	if(maxdb<s.level){
        		maxdb = s.level;
        		strongestBSSID = s.BSSID;
        	}
        //AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
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
        Log.v(TAG, "rate=" + rate + " minBufferSize=" + minBufferSize);
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
        }
        new SendData().send("http://craftsrv5.epfl.ch/projects/noisemap/help.php","bssid="+strongestBSSID+"&noise="+db);
        
        tv.setText("BSSID with strongest signal: "+strongestBSSID+"\nNoise: "+db);
        setContentView(tv);
    }
    
}
