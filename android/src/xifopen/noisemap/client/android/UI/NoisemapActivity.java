package xifopen.noisemap.client.android.UI;

import xifopen.noisemap.client.android.data.LocalService;
import xifopen.noisemap.client.android.data.LocatorAndNoiseMeterThreaded;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

/**
 * 
 * @author nikos
 *
 */
public class NoisemapActivity extends Activity {
	private static final String TAG = "NoisemapActivity";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // starts sending data
        startService(new Intent(this, LocalService.class));	// we don't bind it as some devices pause frequently
        // shows web page
        NoisemapLayout layout = new NoisemapLayout(this);
		layout.addImage();//.addExitButton()
		this.setContentView(layout);
    }    
    @Override protected void onDestroy() {	// return button on android 2.1 (not hangup button)
    	super.onDestroy();
	    stopService(new Intent(this, LocalService.class));
	}
}
