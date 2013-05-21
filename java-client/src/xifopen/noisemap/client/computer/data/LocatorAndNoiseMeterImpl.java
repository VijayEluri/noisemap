package xifopen.noisemap.client.computer.data;

import xifopen.noisemap.client.computer.UI.LocatorAndNoiseMeterThread;
import xifopen.noisemap.client.computer.UI.WebStart;

/**
 *
 * @author nickmeet
 */
public class LocatorAndNoiseMeterImpl implements LocatorAndNoiseMeter{
    public static String url = "http://craftsrv5.epfl.ch/projects/noisemap";
    private LocatorAndNoiseMeterThread parent;
    private String bssid;
    private double dB;
    public LocatorAndNoiseMeterImpl(LocatorAndNoiseMeterThread parent){
        this.parent = parent;
        this.bssid = new Locator().routerWithHighestSignal(parent);
    }
    public LocatorAndNoiseMeterThread get(){
        return parent;
    }
    @Override
    public void send() {
        this.dB = new VUmeter().getDB(parent);
        new SendData().send(LocatorAndNoiseMeterImpl.url+"/help.php","bssid="+bssid+"&noise="+dB);
    } //"00:24:97:f2:9e:81", new RouterArea("ap-lc-0-j19",49.78766041167251,1.222175058317221,1)
    public double getDB(){
        return dB;
    }
}
