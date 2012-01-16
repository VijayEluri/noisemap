package xifopen.noisemap.client.computer.data;

import java.util.HashMap;
import java.util.Map;

public class Routers {
	private static final long serialVersionUID = -8760312491181393320L;	
	public HashMap<String, RouterArea> bssid_and_position = new HashMap<String, RouterArea>();
	@Override
	public String toString(){
		String result = "";
		for (Map.Entry<String, RouterArea> entry3 : bssid_and_position.entrySet())
        	result += entry3.getKey()+","+entry3.getValue().toString();
		return result;
	}
	public RouterArea get(String bssid){
		return this.bssid_and_position.get(bssid);
	}
        /* requires xstream jar
        public static void main(String[] args){
            new Routers().save();
        }
        @SuppressWarnings("unchecked")
	public void convert(){
            try{
                Routers r = (Routers) new XStream(new DomDriver()).fromXML(new FileReader("routers.xml"));
                for (String key : r.bssid_and_position.keySet()) {
                    RouterArea area = r.bssid_and_position.get(key);
                    r.bssid_and_position.put(key, area.convertCH1903ToXY());
                }
                String xmlSerializedObj = new XStream(new DomDriver()).toXML(r);
                Writer out = new FileWriter("routerspixels.xml");// data is based on characters, not bytes
                out.write(xmlSerializedObj);
                out.close();
		} catch (FileNotFoundException e) {		e.printStackTrace();
		} catch (IOException e) {				e.printStackTrace();
		} 
	}
	/**
         * Based on PocketCampus
         * Bug: 00:12:44:b1:a4:b0 and 00:12:44:b1:a4:b1 have no position
	 * Changes the format of the coordinates of the Access Points from .dat to .xml.
	 * It does not serialize current object.
	 * Requires from pocketcampus eclipse project called Phone, org.pocketcampus.map: 
	 * CoordinateConverter and Position
	 *
	@Deprecated
	@SuppressWarnings("unchecked")
	public void save(){
		try{
			ObjectInputStream f = new ObjectInputStream(new FileInputStream("nametopos.dat"));
			HashMap<String, Position> name2pos = (HashMap<String, Position>) f.readObject();
			
			ObjectInputStream f2 = new ObjectInputStream(new FileInputStream("mactoname.dat"));
			HashMap<String, String> mac2name = (HashMap<String, String>) f2.readObject();
			
			Routers r = new Routers();
			for (Map.Entry<String, String> entry : mac2name.entrySet()) {	// read only iteration
			    String mac = entry.getKey();	// BSSID
			    String name = entry.getValue();
			    RouterArea ap = null;
			    for (Map.Entry<String, Position> entry2 : name2pos.entrySet()) {
				    String routerID = entry2.getKey();
				    if(name.equals(routerID)){
				    	Position p = entry2.getValue();
				    	ap = new RouterArea(name, p.getLat(), p.getLon(), p.getLevel()).convertCH1903ToXY(); //.convertCH1903ToLatLong();
				    	break;
				    }
				}
			    r.bssid_and_position.put(mac, ap);
			}
			String xmlSerializedObj = new XStream(new DomDriver()).toXML(r);
			Writer out = new FileWriter("routersXY.xml");// data is based on characters, not bytes
			out.write(xmlSerializedObj);
			out.close();
		} catch (FileNotFoundException e) {		e.printStackTrace();
		} catch (IOException e) {				e.printStackTrace();
		} catch (ClassNotFoundException e) {	e.printStackTrace();
		}
	}*/
}
