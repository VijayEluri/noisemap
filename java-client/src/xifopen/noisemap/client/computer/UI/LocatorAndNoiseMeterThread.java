package xifopen.noisemap.client.computer.UI;

import java.security.AccessController;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import xifopen.noisemap.client.computer.data.LocatorAndNoiseMeterImpl;

/**
 *
 * @author nickmeet
 */
public class LocatorAndNoiseMeterThread extends SwingWorker<Void, Void> {
    private int percent = 0;
    private JTextArea taskOutput;
    private String result = "";
    private boolean isPaused = false;

    public LocatorAndNoiseMeterThread(JTextArea taskOutput){
        this.taskOutput = taskOutput;
    }
    public void pause(){
        isPaused = true;
    }
    public void progressed(int percent){
        this.percent += percent;
        if(this.percent>=100)
            this.percent = 100;
        setProgress(this.percent);
    }
    /*
     * Main task. Executed in background thread.
     */
    @Override
    public Void doInBackground(){
        final LocatorAndNoiseMeterImpl meter = new LocatorAndNoiseMeterImpl(this); 
        while(true){
            if(!isPaused){
                result = AccessController.doPrivileged(new PrivilegedMeter(meter));
                taskOutput.append(result);
            }
            wait1sec();
        }        
    }
    private void wait1sec(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {}
    }
    public Void doIt(){
        this.setProgress(0);
        return null;
    }
    /*
     * Executed in event dispatch thread
     */
    @Override
    public void done() {
        //taskOutput.append(result);
    }

    void resume() {
        isPaused = false;
    }
}