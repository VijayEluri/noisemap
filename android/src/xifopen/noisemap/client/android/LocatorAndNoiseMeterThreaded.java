package xifopen.noisemap.client.android;

import java.util.Calendar;

import android.net.wifi.WifiManager;

public class LocatorAndNoiseMeterThreaded implements Runnable{
	private Thread thread;
	private LocatorAndNoiseMeter meter;
	private boolean isRunning = true;
	public LocatorAndNoiseMeterThreaded(WifiManager wifi){
		super();
		//meter = new LocatorAndNoiseMeter(wifi);
		meter = new LocatorAndNoiseMeter(){ public void send(){} }; // mock object to allow emulation of the rest of the code
	}
	public void start() {
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
    public void stop() {
    	pause();	// without the ability to resume
        thread = null;
    }
	@Override
	public void run() {
		while(true){
			if(isRunning)
				meter.send();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
		}
	}
}
