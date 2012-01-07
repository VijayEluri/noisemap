package xifopen.noisemap.client;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.beans.*;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class WebStart extends JPanel
                              implements ActionListener, 
                                         PropertyChangeListener {

    private JProgressBar progressBar;
    private JButton startButton;
    private JTextArea taskOutput;
    private Task task;

    public class Task extends SwingWorker<Void, Void> {
        private int percent = 0;
        public void progressed(int percent){
            this.percent += percent;
            if(this.percent>=100)
                this.percent = 100;
            setProgress(this.percent);
        }
        private String result = "";
        /*
         * Main task. Executed in background thread.
         */
        @Override
        public Void doInBackground(){
            result = AccessController.doPrivileged(new PrivilegedAction<String>() { // useless but kept for convenience
                @Override
                public String run() {	// all java code is client-side, so there is no obvious security issue
                    String x;
                    double db = getNoise(Task.this);    // object of outer class
                    x = db + " dB\n";
                    if(db==1)
                        x = "Error: Please check if your microphone is mute or if you have plugged in your headphones.";
                    return x;
                }
                public double getNoise(WebStart.Task t){
                    double dB = -1;
                    t.progressed(1);
                    //"00:24:97:f2:9e:81", new RouterArea("ap-lc-0-j19",49.78766041167251,1.222175058317221,1)
                    dB = new VUmeter().getDB(t);
                    String bssid = new Locator().routerWithHighestSignal(t);
                    SendData.send("http://craftsrv5.epfl.ch/projects/noisemap/help.php","bssid="+bssid+"&noise="+dB);
                    t.progressed(100);	
                    return dB;
                }
            });
            return null;
        }
        public Void doIt(){
            //getNoiseAndPosition(this);
            this.setProgress(0);
            return null;
        }

        /*
         * Executed in event dispatch thread
         */
        @Override
        public void done() {
            Toolkit.getDefaultToolkit().beep();
            startButton.setEnabled(true);
            taskOutput.append(result);
        }
        
    }
    public WebStart() {
        super(new BorderLayout());
        startButton = new JButton("Start!");
        startButton.setActionCommand("start");
        startButton.addActionListener(this);
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true); // so height stays the same whether or not the string is shown
        progressBar.setVisible(false);
        taskOutput = new JTextArea(5, 20);
        taskOutput.setMargin(new Insets(5,5,5,5));
        taskOutput.setEditable(false);

        JPanel panel = new JPanel();
        panel.add(startButton);
        panel.add(progressBar);
        add(panel, BorderLayout.PAGE_START);
        add(new JScrollPane(taskOutput), BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }
    /**
     * Invoked when the user presses the start button.
     */
    @Override
    public void actionPerformed(ActionEvent evt) {
        progressBar.setIndeterminate(true);
        progressBar.setVisible(true);
        startButton.setEnabled(false);
        //Instances of javax.swing.SwingWorker are not reusuable, so
        //we create new instances as needed.
        task = new Task();
        task.addPropertyChangeListener(this);
        task.execute();
    }
    /**
     * Invoked when task's progress property changes.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("progress")) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setIndeterminate(false);
            progressBar.setValue(progress);
            //taskOutput.append(String.format(
            //            "Completed %d%% of task.\n", progress));
        }
    }
    /**
     * Create the GUI and show it. As with all GUI code, this must run
     * on the event-dispatching thread.
     */
    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Noise Level Meter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JComponent newContentPane = new WebStart();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);
        frame.pack();
        frame.setVisible(true); //Display the window
    }
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
