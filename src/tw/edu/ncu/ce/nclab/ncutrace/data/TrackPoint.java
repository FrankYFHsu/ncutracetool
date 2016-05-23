package tw.edu.ncu.ce.nclab.ncutrace.data;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import tw.edu.ncu.ce.nclab.ncutrace.CoordinateTransform;

public class TrackPoint implements Comparable<TrackPoint> {

	public enum LocationType {
		TWD97, WGS84DEGREE
	}

	public TWD97 getTWD97_location() {
		return TWD97_location;
	}

	public LonLat getLonlat_location() {
		return Lonlat_location;
	}

	private TWD97 TWD97_location;
	public void setTWD97_location(TWD97 tWD97_location) {
		TWD97_location = tWD97_location;
		Lonlat_location = CoordinateTransform
				.convertTWD97toWGS84(TWD97_location);
	}

	private LonLat Lonlat_location;

	public int elapsedTime;

	public TrackPoint(TWD97 TWD97_location, int time) {
		this.TWD97_location = TWD97_location;
		this.elapsedTime = time;

		Lonlat_location = CoordinateTransform
				.convertTWD97toWGS84(TWD97_location);
	}

	public TrackPoint(LonLat Lonlat_location, int time) {
		this.Lonlat_location = Lonlat_location;
		this.elapsedTime = time;

		TWD97_location = CoordinateTransform
				.convertWGS84toTWD97(Lonlat_location);
	}

	public TrackPoint(String trackPointInfo, LocationType type) {
		String[] infos = trackPointInfo.split(" ");
		if (type == LocationType.TWD97) {

			double x = Double.parseDouble(infos[0]);
			double y = Double.parseDouble(infos[1]);

			TWD97_location = new TWD97(x, y);
			Lonlat_location = CoordinateTransform
					.convertTWD97toWGS84(TWD97_location);

		}

		if (type == LocationType.WGS84DEGREE) {

			double lon = Double.parseDouble(infos[0]);
			double lat = Double.parseDouble(infos[1]);

			Lonlat_location = new LonLat(lon, lat);
			TWD97_location = CoordinateTransform
					.convertWGS84toTWD97(Lonlat_location);

		}

		this.elapsedTime = Integer.parseInt(infos[2]);
	}

	public TrackPoint() {
		TWD97_location = new TWD97(0, 0);
		Lonlat_location = new LonLat(0, 0);
		elapsedTime = 0;
	}

	public TrackPoint addElapsedTime(int seconds) {

		return new TrackPoint(TWD97_location, elapsedTime + seconds);
	}

	public String getLocationInfoWithLonLat() {

		return Lonlat_location.getLongitude() + " "
				+ Lonlat_location.getLatitude() + " " + elapsedTime;
	}

	public String getLocationInfoWithTWD97() {
		NumberFormat nf = new DecimalFormat(".#");

		return nf.format(TWD97_location.getX()) + " " + nf.format(TWD97_location.getY()) + " " + elapsedTime;
	}
	
	

	@Override
	public String toString() {
		
		return getLocationInfoWithTWD97();

	}

	@Override
	public int compareTo(TrackPoint other) {

		return (this.elapsedTime - other.elapsedTime);

	}
	
	public double getX(){
		return this.TWD97_location.getX();
	}
	
	public double getY(){
		return this.TWD97_location.getY();
	}
	
	public double getLon(){
		return this.Lonlat_location.getLongitude();
	}
	
	public double getLat(){
		return this.Lonlat_location.getLatitude();
	}

}
