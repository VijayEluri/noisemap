package xifopen.noisemap.client.android.data;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class LocalService extends Service {
	private LocatorAndNoiseMeter meter;
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
    private void _startService() {
    	final WifiManager wifi = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
    	meter = new LocatorAndNoiseMeterImpl(wifi);
    	//meter = new LocatorAndNoiseMeter(){ public void send(){} }; // mock object to allow emulation of the rest of the code
    	timer.scheduleAtFixedRate(
			new TimerTask() {
				public void run() {
					try{
						meter.send();
					} catch(Exception e){
						timer.cancel();		// halts all instances of TimerTask
						stopSelf();			// stops service
					}
				}
			}, 0, 5000);	// 1min
		Log.i(getClass().getSimpleName(), "Timer started!!!");
    }
    private void _shutdownService() {
    	if (timer != null)
    		timer.cancel();
    	Log.i(getClass().getSimpleName(), "Timer stopped!!!");
    }
}