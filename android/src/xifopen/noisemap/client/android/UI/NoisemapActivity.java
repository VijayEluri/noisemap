package xifopen.noisemap.client.android.UI;

import xifopen.noisemap.client.android.data.LocalService;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

/**
 * 
 * @author nikos
 *
 */
public class NoisemapActivity extends Activity {
	private static final String TAG = "NoisemapActivity";
	private static final int NOTIFICATION_ID = 1;
	private NoisemapLayout layout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // starts sending data
        startService(new Intent(this, LocalService.class));	// we don't bind it as some devices pause frequently
        // shows web page
        layout = new NoisemapLayout(this);
        if(android.os.Build.VERSION.SDK_INT>=11)
        	layout.addImage();
        else
        	layout.addViewButton();							// on Android 2.3.x (API level 10) or lower, the menu of the embedded...
		this.setContentView(layout);						// ...browser hides the start/stop service button
    }    
    @Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(layout.alter(menu));
	}
    @Override protected void onDestroy() {	// return button on android 2.1 (not hangup button)
    	super.onDestroy();
	    stopService(new Intent(this, LocalService.class));
	}
}
