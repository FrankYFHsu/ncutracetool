

public class LonLat {
	
	private double mLatitude;
	private double mLongitude;
	
	public LonLat(double lat, double lon){
		mLatitude=lat;
		mLongitude=lon;
	}
	
	public double getLatitude(){
		return mLatitude;
	}
	public double getLongitude(){
		return mLongitude;
	}
	
	@Override
	public String toString(){
		return mLongitude+","+mLatitude;
	}

}
