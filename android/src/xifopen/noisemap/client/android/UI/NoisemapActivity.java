package xifopen.noisemap.client.android.UI;

import xifopen.noisemap.client.android.data.LocalService;
import xifopen.noisemap.client.android.data.LocatorAndNoiseMeterImpl;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
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
	private BroadcastReceiver ServiceReceiver;
	private LocatorAndNoiseMeterImpl.Locator wifiReceiver;
	private IntentFilter listening2intents;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // starts sending data
        startService(new Intent(this, LocalService.class));	// we don't bind it as some devices pause frequently
        // shows web page
        layout = new NoisemapLayout(this);
        if(android.os.Build.VERSION.SDK_INT>=11){
        	new ReloadWebView(this, 5, layout.addImage());	// 5 seconds refresh rate for the demo
        }
        else												// on Android 2.3.x (API level 10) or lower, the menu of the embedded...
        	layout.addViewButton();							// ...browser hides the start/stop service button
        // registers communication with the service
        this.listening2intents = new IntentFilter(LocalService.Iintent2stop);
        this.ServiceReceiver = new BroadcastReceiver(){
            @Override 
            public void onReceive(Context arg0, Intent fromLocalService) {
            	String status = fromLocalService.getExtras().getString("status"); 
            	if(status.equals("down"))
            		layout.switchButton();
            }
        };
        registerReceiver(ServiceReceiver, listening2intents);
        registerReceiver(wifiReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        // renders layout
		this.setContentView(layout);						
    }    
    @Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(layout.alter(menu));
	}
    @Override protected void onDestroy() {	// return button on android 2.1 (not hangup button)
    	unregisterReceiver(ServiceReceiver);
    	unregisterReceiver(wifiReceiver);
    	super.onDestroy();
	    stopService(new Intent(this, LocalService.class));
	}
}
