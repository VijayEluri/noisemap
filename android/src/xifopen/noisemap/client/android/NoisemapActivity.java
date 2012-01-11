package xifopen.noisemap.client.android;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.TextView;

/**
 * 
 * @author nikos
 *
 */
public class NoisemapActivity extends Activity {
	private static final String TAG = "NoisemapActivity";
	private LocatorAndNoiseMeterThreaded meter;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        //setContentView(R.layout.main);
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        meter = new LocatorAndNoiseMeterThreaded(wifi);
        meter.start();
        tv.setText("sent...");	// TODO: draw data on picture
        setContentView(tv);
    }    
    public void onPause(){
    	meter.pause();
    }
    public void onResume(){
    	if(meter!=null)
    		meter.resume();
    }
}
