package xifopen.noisemap.client.android.data;

import java.util.Calendar;


import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;

@Deprecated
public class LocatorAndNoiseMeterThreaded implements Runnable{
	private static final String TAG = "LocatorAndNoiseMeterThreaded";
	private Thread thread;
	private LocatorAndNoiseMeter meter;
	private boolean isRunning = true;	// Thread.stop(), Thread.resume() are not supported by android
	public LocatorAndNoiseMeterThreaded(WifiManager wifi){
		super();
		meter = new LocatorAndNoiseMeterImpl(wifi);
		//meter = new LocatorAndNoiseMeter(){ public void send(){} }; // mock object to allow emulation of the rest of the code
	}
	public void start(){
		Handler handler = new Handler(); 
	    handler.postDelayed(this, 2000); 
		
        thread = new Thread(this);
        thread.setName("Sound capture started at " + Calendar.getInstance().getTimeInMillis());
        thread.start();
    }
	public void pause(){
		isRunning = false;
	}
	public void resume(){
		isRunning = true;
	}
    public void stop(){
    	pause();	// without the ability to resume
        thread = null;
    }
	@Override
	public void run() {
		for(int i=0; i<60; i++)			// take next measure at least after 1sec for 60 times
			tryRunning(1);
		while(true)						// then take next measure at least after 1min
			tryRunning(6);
	}
	public void tryRunning(long then_sleep_in_sec){
		try{
			if(isRunning)
				meter.send();
			Thread.sleep(then_sleep_in_sec*1000);
		} catch (Exception e) {
			Log.v(TAG, e.getMessage());
		}
	}
}
