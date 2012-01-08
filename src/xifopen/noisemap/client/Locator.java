package xifopen.noisemap.client;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
        Map<String, String> result = null;
        String os = System.getProperty("os.name").toLowerCase();
        boolean isWindows = os.indexOf("win") >= 0;
        boolean isMac = os.indexOf("mac") >= 0;
        if (isWindows)
            result = detectOnWindows();
        else if(isMac)
            result = detectOnOSX();
        else
            throw new UnsupportedOperationException(os);
        return result;
    }
    /**
     * Serves as a cross-platform wrapper around getting the list of detected BSSIDs along with their signal strength
     * @return 
     */
    private Map<String, String> detectOnOSX() {
        // TODO: check it on several versions of OSX
        Map<String, String> bssid_strength = new HashMap<String, String>();

        boolean tryagain = true;
        List<String> r = null;
        for(int tried=0; tryagain && tried<3; tried++){	// workaround as sometimes the epfl SSID is not detected
            String cmd = "";
            cmd += "/System/Library/PrivateFrameworks/Apple80211.framework";
            cmd += "/Versions/Current/Resources/";
            cmd += "airport -I or -s";
            r = exec(cmd);
            /* for simulation
            FileReader fileReader;
            try {
                fileReader = new FileReader("output.txt");
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String lin = null;
                while ((lin = bufferedReader.readLine()) != null) {
                    r.add(lin.trim());
                }
                bufferedReader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            } */           
            if (!r.isEmpty())
                for (int i = 1; i != r.size(); i++) {
                    String[] words = r.get(i).split("\\s+");
                    if(words.length>=3){
                        String SSID = words[0];
                        String BSSID = words[1];
                        String RSSI = words[2];
                        if (SSID.equals("public-epfl") || SSID.equals("epfl")){  // we avoid other possible networks that contain "epfl"
                            bssid_strength.put(BSSID, RSSI);
                            tryagain = false;
                        }
                    }
                }
        }
        return bssid_strength;
    }
    private Map<String, String> detectOnWindows() {
        // TODO: currently parses only Windows 7 output, check also windows Vista and XP
        Map<String, String> bssid_strength = new HashMap<String, String>();

        boolean tryagain = true;
        List<String> r = null;
        for(int tried=0; tryagain && tried<3; tried++){	// workaround as sometimes the epfl SSID is not detected
            r = exec("cmd /c netsh wlan show networks mode=bssid");  // OR:netsh wlan show int | findstr "BSSID"
            if (!r.isEmpty()) {     // cmd is necessary for pipes
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
                    tryagain = false;
                }
            }
        }
        return bssid_strength;
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
