package org.pocketcampus.map;
 


import java.io.Serializable;


public class Position implements Serializable {
	private static final long serialVersionUID = -8760312491181393320L;
	private final double lat;
	private final double lon;
	private final int level;
	
	public Position(double lat, double lon, int level) {
		this.lat = lat;
		this.lon = lon;
		this.level = level;
	}
	
	public double getLat() {
		return lat;
	}

	public double getLon() {
		return lon;
	}
	
	public int getLevel() {
		return level;
	}
	
	@Override
	public String toString() {
		return "("+lat+", "+lon+", "+level+")";
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	
	/*public int distanceTo(Position pos) {
		GeoPoint startGP = new GeoPoint(lat, lon);
		GeoPoint endGP = new GeoPoint(pos.getLat(), pos.getLon());
		return startGP.distanceTo(endGP);
	}*/
}

