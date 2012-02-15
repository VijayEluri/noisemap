package xifopen.noisemap.client.android.data;

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
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class LocalService extends Service {
	private LocatorAndNoiseMeter meter;
	private static final int NOTIFICATION_ID = 1;
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
    }
    private void _startService() {
    	final WifiManager wifi = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
    	//meter = new LocatorAndNoiseMeterImpl(wifi);
    	meter = new LocatorAndNoiseMeter(){ public void send(){} }; // mock object to allow emulation of the rest of the code
    	timer.scheduleAtFixedRate(
			new TimerTask() {
				public void run() {
					try{
						meter.send();
					} catch(Exception e){
						LocalService.this.notify("Service NoiseMap is down", "To start it again, open the application Noisemap");
						timer.cancel();							// halts all instances of TimerTask
						LocalService.this.stopSelf();			// stops service
					}
				}
			}, 0, 5000);	// 1min
		Log.i(getClass().getSimpleName(), "Service Noisemap started!!!");
    }
    private void _shutdownService() {
    	if (timer != null)
    		timer.cancel();
    	Log.e(getClass().getSimpleName(), "Service Noisemap stopped!!!");
    }
}