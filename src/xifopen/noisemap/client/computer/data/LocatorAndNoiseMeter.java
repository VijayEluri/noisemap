package xifopen.noisemap.client.computer.data;

/**
 * A class that implements this interface should:
 * <ul>
 * <li>be called in a separate Thread
 * <li>include a constructor that does Positioning 1)using WiFi 2)inside the Rolex Learning Center
 * <li>have a static url that is loaded by the UI
 * </ul> 
 */
public interface LocatorAndNoiseMeter {
	/**
	 * Takes measurements each second for 5 seconds and sends the average to a server
	 */
	public void send();
}
