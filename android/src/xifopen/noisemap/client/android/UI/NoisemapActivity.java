package xifopen.noisemap.client.android.UI;

import xifopen.noisemap.client.android.R;
import xifopen.noisemap.client.android.data.LocalService;
import xifopen.noisemap.client.android.data.LocatorAndNoiseMeterThreaded;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.SurfaceHolder;
import android.widget.Button;
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
	private static final int NOTIFICATION_ID = 1;
	private NoisemapLayout layout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // starts sending data
        startService(new Intent(this, LocalService.class));	// we don't bind it as some devices pause frequently
        // shows web page
        layout = new NoisemapLayout(this);
		layout.addImage();//.addExitButton()
		this.setContentView(layout);
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
