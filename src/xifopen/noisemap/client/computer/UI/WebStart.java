package xifopen.noisemap.client.computer.UI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.beans.*;
import java.security.AccessController;
import java.security.PrivilegedAction;
import xifopen.noisemap.client.computer.data.LocatorAndNoiseMeter;
import xifopen.noisemap.client.computer.data.LocatorAndNoiseMeterImpl;

public class WebStart extends JPanel
                              implements ActionListener, 
                                         PropertyChangeListener {

    private JProgressBar progressBar;
    private JButton startButton;
    private JTextArea taskOutput;
    private LocatorAndNoiseMeterThread task;

    public WebStart() {
        super(new BorderLayout());
        startButton = new JButton("Stop");
        startButton.setActionCommand("stop");
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
        
        task = new LocatorAndNoiseMeterThread(taskOutput);
        task.execute();
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
        task.interrupt();
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
