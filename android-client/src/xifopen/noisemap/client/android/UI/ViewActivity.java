package xifopen.noisemap.client.android.UI;

import xifopen.noisemap.client.android.data.LocalService;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

public class ViewActivity extends Activity{
	private static final String TAG = "ViewActivity";
	private static final int NOTIFICATION_ID = 1;
	private NoisemapLayout layout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // shows web page
        layout = new NoisemapLayout(this);
       	layout.addImage();
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
