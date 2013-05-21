package xifopen.noisemap.client.android.data;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.TimerTask;

import xifopen.noisemap.client.android.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class LocalService extends Service {
	private LocatorAndNoiseMeter meter;
	private static final int NOTIFICATION_ID = 1;
	public static final String Iintent2stop = "I intent to stop";
	private Timer timer = new Timer();
	
    @Override
    public void onCreate() {
    	super.onCreate();
    	_startService();
    }
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        return START_NOT_STICKY;	// in case of stopping, it won't be scheduled for a restart
    }
	public class LocalBinder<S> extends Binder {
	    private String TAG = "LocalBinder";
	    private  WeakReference<S> mService;
	    public LocalBinder(S service){
	        mService = new WeakReference<S>(service);
	    }
	    public S getService() {
	        return mService.get();
	    }
	}
	@Override
    public IBinder onBind(Intent intent) {
    	return new LocalBinder<LocalService>(this);
    }    
    @Override
	public void onDestroy() {
    	super.onDestroy();
    	_shutdownService();
	}
    public void notify(String title, String text){
    	String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		int icon = R.drawable.notification;
		CharSequence tickerText = title;
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, tickerText, when);
		
		Context context = getApplicationContext();
		CharSequence contentTitle = tickerText;
		CharSequence contentText = text;
		Intent notificationIntent = new Intent(this, getClass());
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		mNotificationManager.notify(NOTIFICATION_ID, notification);
		// switching off the button requires communication between the service and the activities
		Intent toActivity = new Intent(Iintent2stop); 
		toActivity.putExtra("status", "down");
        sendBroadcast(toActivity);
    }
    private void _startService() {
    	final WifiManager wifi = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
    	meter = new LocatorAndNoiseMeterImpl(wifi);
    	//meter = new LocatorAndNoiseMeter(){ public void send(){} }; // mock object for debugging to allow emulation of the rest of the code
    	timer.scheduleAtFixedRate(
			new TimerTask() {
				public void run() {
					try{
						meter.send();
					} catch(Exception e){
						Log.e(getClass().getSimpleName(), "Service NoiseMap is down:"+e.getMessage());
						LocalService.this.notify("Service NoiseMap is down", "To start it again, open the application Noisemap");
						timer.cancel();							// halts all instances of TimerTask
						LocalService.this.stopSelf();			// stops service
					}
				}
			}, 0, 5000);	// for the demo, otherwise it should be 5 minutes
		Log.i(getClass().getSimpleName(), "Service Noisemap started");
    }
    private void _shutdownService() {
    	if (timer != null)
    		timer.cancel();
    	Log.e(getClass().getSimpleName(), "Service Noisemap stopped");
    }
}