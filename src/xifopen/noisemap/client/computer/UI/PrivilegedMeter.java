/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xifopen.noisemap.client.computer.UI;

import java.security.PrivilegedAction;
import xifopen.noisemap.client.computer.data.LocatorAndNoiseMeterImpl;

/**
 *
 * @author nickmeet
 */
public class PrivilegedMeter implements PrivilegedAction<String>{
    private LocatorAndNoiseMeterImpl meter;
    public PrivilegedMeter(LocatorAndNoiseMeterImpl meter){
        this.meter = meter;
    }
    @Override
    public String run() {
        String x;
        try{
            meter.get().progressed(1);
            meter.send();
            meter.get().progressed(100);	
            x = meter.getDB() + " dB\n";    // object of outer class
        }
        catch(RuntimeException e){
            x = e.getMessage();
        }
        return x;
    }
    
}
