package tw.edu.ncu.ce.nclab.ncutrace.data;


public class LonLat {
	
	private double mLatitude;//經度
	private double mLongitude;//緯度
	
	public LonLat(double lon, double lat){
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
