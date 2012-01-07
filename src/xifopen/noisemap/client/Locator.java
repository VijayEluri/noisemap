package xifopen.noisemap.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Locator {

    public String routerWithHighestSignal(WebStart.Task t) {
        String nbssid = null;	// Or:We could make training data to deal with walls and reflections
        try {
            List<Map<String, String>> measurements = new ArrayList<Map<String, String>>();
            for (int i = 0; i < 3; i++) {
                Thread.sleep(400);
                if (t != null) {
                    t.progressed(80 / 3);        // random value
                }
                measurements.add(detect());
                if (i != 0 && !measurements.get(i).isEmpty())
                    for (String bssid : measurements.get(i).keySet())
                        if (!measurements.get(0).containsKey(bssid))
                            measurements.get(0).put(bssid, measurements.get(i).get(bssid));
            }
            int max = 0;
            for (Map.Entry<String, String> entry : measurements.get(0).entrySet()) {
                int value = Integer.parseInt(entry.getValue());
                if (value > max) {
                    max = value;
                    nbssid = entry.getKey();
                }
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(Locator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return nbssid;
    }

    private Map<String, String> detect() {
        // TODO: currently parses only Windows 7 output
        Map<String, String> bssid_strength = new HashMap<String, String>();

        List<String> r = null;
        while (true) {				// workaround as sometimes the epfl SSID is not detected
            r = getBSSID();
            if (!r.isEmpty()) {
                List<String> list = new ArrayList<String>();
                List<String> epfl = null;
                List<String> publicepfl = null;
                for (int i = 4; i != r.size(); i++) {
                    String line = r.get(i);
                    if (line.equals("")) {
                        String header = list.get(0);
                        if (header.contains("public-epfl")) {
                            publicepfl = list;
                        } else if (header.contains("epfl")) {
                            epfl = list;
                        }
                        list = new ArrayList<String>();
                    } else {
                        list.add(line);
                    }
                }
                if (epfl == null) {
                    continue;
                } else {
                    epfl.addAll(publicepfl);
                    for (int i = 0; i != epfl.size(); i++) {
                        String line = epfl.get(i);
                        if (line.contains("BSSID")) {
                            String bssid = line.split("\\s+")[4];	// '+' means 'any' and \\s is equal to [ \\t\\n\\x0B\\f\\r]
                            i++;
                            String signal_strength = epfl.get(i).split("\\s+")[3];
                            signal_strength = signal_strength.substring(0, signal_strength.length() - 1);	// omits '%'
                            bssid_strength.put(bssid, signal_strength);
                        }
                    }
                    break;
                }
            }
        }
        return bssid_strength;
    }

    /**
     * Serves as a cross-platform wrapper around getting the list of detected BSSIDs along with their signal strength
     * @return
     */
    private List<String> getBSSID() {
        List<String> result = null;
        String os = System.getProperty("os.name").toLowerCase();
        boolean isWindows = os.indexOf("win") >= 0;
        boolean isMac = os.indexOf("mac") >= 0;
        if (isWindows) {	// check on windows 7, then on vista and xp			// cmd is necessary for pipes
            result = exec("cmd /c netsh wlan show networks mode=bssid"); // OR:netsh wlan show int | findstr "BSSID"
        }/*
        else if(isMac){
        // /System/Library/PrivateFrameworks/Apple80211.framework/Versions/Current/Resources/airport -I or -s
        }
        else{
        throw new Exception("Unsupported Operating System:"+os);
        }
         */
        return result;
    }

    /**
     * 
     * @param command
     * @return null if command has non zero exit code, otherwise it returns the lines of stdout
     * @throws IOException
     * @throws InterruptedException
     */
    private List<String> exec(String command) {
        List<String> lines = new ArrayList<String>();
        Runtime rt = Runtime.getRuntime();
        Process pr;
        try {
            pr = rt.exec(command);
            BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line = null;
            while ((line = input.readLine()) != null) {
                lines.add(line);
            }
            if (pr.waitFor() != 0) {
                lines = null;
            }
        } catch (IOException e) {
            System.out.println("command: " + command);
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("command: " + command);
            e.printStackTrace();
        }
        return lines;
    }
}
