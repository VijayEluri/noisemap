package xifopen.noisemap.client.computer.data;


public class RouterArea {
	private String name;	// WiFi beacons labeling convention: ap-[building acronym]-[level]-[AP number]
	private double latitude;
	private double longitude;
	private int floor;
	private int db;

	public RouterArea(String name, double lat, double lon, int level) {
		this.name = name;
		this.latitude = lat;
		this.longitude = lon;
		this.floor = level;
	}
	/**
	 * for assignments by reference
	 */
	public RouterArea(){
		
	}
        public String getName() {
		return name;
	}
	public double getLat() {
		return latitude;
	}
	public double getLon() {
		return longitude;
	}
	public int getLevel() {
		return floor;
	}
	public int getDB(){
		return db;
	}
	public void setDB(int db){
		this.db = db;
	}
	@Override
	public String toString(){
		return getName()+","+getLat()+","+getLon()+","+getLevel()+"\n";
	}
        /**
         * Converts CH1903 coordinates to pixel coordinates of a 408*558 image
         * based on reference WIFIs of the Rolex Learning Center
         * @return latitude is height and longitude is width
         */
        public RouterArea convertCH1903ToXY(){
            double width = 558;                 // image dimensions
            double height = 408;
            // 533286.2/152284.0 gives 47/24 but should give ~= 510,140 (154m,41.8m)
            double x0 = 533131;                 // top left corner of the image
            double y0 = 152325;
            double x_end = 533298;              // lon of right-most router
            double y_end = 152204;              // lat of bottom-most router
            double xunit = width/(x_end-x0);
            double yunit = height/(y0-y_end);
            
            double x = Math.ceil((this.latitude-x0)*xunit);
            double y = height - Math.ceil((this.longitude-y_end)*yunit);    // CH1903 lat is in inverse order
            
            this.latitude = x;
            this.longitude = y;
            return this;
        }
/**
 * 
 * @param x northing in EPSG:4326
 * @param y easting in EPSG:4326
 * @param floor the level(not modified)
 * @return A Position in standard lat/long coordinates (WGS84)
 */
	public RouterArea convertEPSG4326ToLatLong(){
		double x = this.latitude;
		double y = this.longitude;
		double a = 6378137;
		double lat = (Math.PI/2.0 - 2.0 * Math.atan(Math.exp(-y / a)));
		double lon = adjust_lon(x/a);

		//convert to degree
		this.latitude = lat/Math.PI*180;
		this.longitude = lon/Math.PI*180;
		return this;
	}

	/**
	 * @param lat Latitude in WGS84
	 * @param lon Longitude in WGS84
	 * @param floor the level(not modified)
	 * @return A Position in standard EPSG:4326
	 */
	public RouterArea convertLatLongToEPSG4326(){
		double x = this.latitude;
		double y = this.longitude;
		double a = 6378137;
		
		//convert to radian
		x = x*Math.PI/180;
		y = y*Math.PI/180;
		
		this.latitude = a*adjust_lon(y);
		this.longitude = a*Math.log(Math.tan(Math.PI/4.0 + 0.5*x));
		return this;
	}
	
	/**
	 * 
	 * @param x northing in CH1903
	 * @param y easting in CH1903
	 * @param floor the level(not modified)
	 * @return A Position in standard lat/long coordinates (WGS84)
	 */
	public RouterArea convertCH1903ToLatLong(){	
		double x = this.latitude;
		double y = this.longitude;
		double y_aux = (y - 600000)/1000000;
		double x_aux = (x - 200000)/1000000;

		double lat = 16.9023892
		+  3.238272 * x_aux
		-  0.270978 * Math.pow(y_aux,2)
		-  0.002528 * Math.pow(x_aux,2)
		-  0.0447   * Math.pow(y_aux,2) * x_aux
		-  0.0140   * Math.pow(x_aux,3);

		this.latitude = lat * 100/36;

		double lng = 2.6779094
		+ 4.728982 * y_aux
		+ 0.791484 * y_aux * x_aux
		+ 0.1306   * y_aux * Math.pow(x_aux,2)
		- 0.0436   * Math.pow(y_aux,3);

		this.longitude = lng * 100/36;
		return this;
	}
	
	private double adjust_lon(double x) {
		x = (Math.abs(x) < Math.PI) ? x: (x - (Double.compare(x, 0)*2*Math.PI) );
		return x;
	}
}