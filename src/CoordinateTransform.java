/**
 * The modified code from http://sask989.blogspot.tw/2012/05/wgs84totwd97.html
 * 
 * @author YuFeng Hsu
 * 
 */
public class CoordinateTransform {

	private final static double a = 6378137.0;// 地球赤道半徑(Equatorial Radius)公尺
	private final static double b = 6356752.3142451;// 兩極半徑(Polar Radius)公尺
	private final static double lon0 = 121 * Math.PI / 180;// 中央經線 (弧度)
	private final static double k0 = 0.9999;// 中央經線尺度比
	private final static int dx = 250000;// 橫座標平移量
	private final static int dy = 0;
	private final static double e = 1 - Math.pow(b, 2) / Math.pow(a, 2);
	private final static double e2 = (1 - Math.pow(b, 2) / Math.pow(a, 2))
			/ (Math.pow(b, 2) / Math.pow(a, 2));

	public final static int DMS_TYPE = 1;
	public final static int DEGREE_TYPE = 2;

	public static void main(String[] args) {

		// Test
		String twd97 = CoordinateTransform.convertWGS84toTWD97(121.227228,
				24.953063).toString();

		String lonlat = CoordinateTransform.convertTWD97toWGS84(
				272945.1380794972, 2760597.9480111464,
				CoordinateTransform.DEGREE_TYPE);

		System.out.println(twd97);
		System.out.println(lonlat);

	}

	/**
	 * 將WGS84經緯度(度:分:秒)轉成TWD97坐標
	 * 
	 * @param lonD
	 *            經度(度)
	 * @param lonM
	 *            經度(分)
	 * @param lonS
	 *            經度(秒)
	 * @param latD
	 *            緯度(度)
	 * @param latM
	 *            緯度(分)
	 * @param latS
	 *            緯度(秒)
	 * @return TWD97坐標"x,y"
	 */
	public static TWD97 convertWGS84toTWD97(int lonD, int lonM, int lonS,
			int latD, int latM, int latS) {
		double lon_degree = (double) (lonD) + (double) lonM / 60
				+ (double) lonS / 3600;
		double lat_degree = (double) (latD) + (double) latM / 60
				+ (double) latS / 3600;
		return WGS84toTWD97Calculation(lon_degree, lat_degree);
	}

	/**
	 * 將WGS84經緯度(角度)轉成TWD97坐標
	 * 
	 * @param lon_degree
	 *            經度(角度)
	 * @param lat_degree
	 *            緯度(角度)
	 * @return TWD97坐標"x,y"
	 */
	public static TWD97 convertWGS84toTWD97(double lon_degree,
			double lat_degree) {
		return WGS84toTWD97Calculation(lon_degree, lat_degree);
	}

	/**
	 * 將TWD97坐標轉成WGS84經緯度
	 * 
	 * @param XValue
	 *            TWD97坐標x
	 * @param YValue
	 *            TWD97坐標y
	 * @param type
	 *            經緯度的表示方法，CoordinateTransform.DMS_TYPE為度分秒，CoordinateTransform.
	 *            DEGREE_TYPE為角度。
	 * @return WGS84經緯度
	 */
	public static String convertTWD97toWGS84(double XValue, double YValue,
			int type) {

		String lonlat = "";

		lonlat = TWD97toWGS84Calculation(XValue, YValue);

		if (type == DEGREE_TYPE) {
			return lonlat;
		} else if (type == DMS_TYPE) {
			return convertDegreetoDMS(lonlat);

		}else{
			//Default, using degree
			return lonlat;
		}
		
	}

	private static String convertDegreetoDMS(String degree) {
		String[] answer = degree.split(",");
		int lonDValue = (int) Double.parseDouble(answer[0]);
		int lonMValue = (int) ((Double.parseDouble(answer[0]) - lonDValue) * 60);
		double lonSValue = (((Double.parseDouble(answer[0]) - lonDValue) * 60) - lonMValue) * 60;

		int latDValue = (int) Double.parseDouble(answer[1]);
		int latMValue = (int) ((Double.parseDouble(answer[1]) - latDValue) * 60);
		double latSValue = (((Double.parseDouble(answer[1]) - latDValue) * 60) - latMValue) * 60;

		return lonDValue + "º " + lonMValue + "' " + lonSValue + "\","
				+ latDValue + "º " + latMValue + "' " + latSValue + "\"";

	}

	private static TWD97 WGS84toTWD97Calculation(double lon_degree,
			double lat_degree) {

		double lon = (lon_degree - Math.floor((lon_degree + 180) / 360) * 360)
				* Math.PI / 180;
		double lat = lat_degree * Math.PI / 180;

		double V = a / Math.sqrt(1 - e * Math.pow(Math.sin(lat), 2));
		double T = Math.pow(Math.tan(lat), 2);
		double C = e2 * Math.pow(Math.cos(lat), 2);
		double A = Math.cos(lat) * (lon - lon0);
		double M = a
				* ((1.0 - e / 4.0 - 3.0 * Math.pow(e, 2) / 64.0 - 5.0 * Math
						.pow(e, 3) / 256.0)
						* lat
						- (3.0 * e / 8.0 + 3.0 * Math.pow(e, 2) / 32.0 + 45.0 * Math
								.pow(e, 3) / 1024.0)
						* Math.sin(2.0 * lat)
						+ (15.0 * Math.pow(e, 2) / 256.0 + 45.0 * Math
								.pow(e, 3) / 1024.0) * Math.sin(4.0 * lat) - (35.0 * Math
						.pow(e, 3) / 3072.0) * Math.sin(6.0 * lat));
		// x
		double x = dx
				+ k0
				* V
				* (A + (1 - T + C) * Math.pow(A, 3) / 6 + (5 - 18 * T
						+ Math.pow(T, 2) + 72 * C - 58 * e2)
						* Math.pow(A, 5) / 120);
		// y
		double y = dy
				+ k0
				* (M + V
						* Math.tan(lat)
						* (Math.pow(A, 2) / 2
								+ (5 - T + 9 * C + 4 * Math.pow(C, 2))
								* Math.pow(A, 4) / 24 + (61 - 58 * T
								+ Math.pow(T, 2) + 600 * C - 330 * e2)
								* Math.pow(A, 6) / 720));

		return new TWD97(x,y);
	}

	private static String TWD97toWGS84Calculation(double x, double y) {
		x -= dx;
		y -= dy;

		// Calculate the Meridional Arc
		double M = y / k0;

		// Calculate Footprint Latitude
		double mu = M
				/ (a * (1.0 - e / 4.0 - 3 * Math.pow(e, 2) / 64.0 - 5 * Math
						.pow(e, 3) / 256.0));
		double e1 = (1.0 - Math.sqrt(1.0 - e)) / (1.0 + Math.sqrt(1.0 - e));

		double J1 = (3 * e1 / 2 - 27 * Math.pow(e1, 3) / 32.0);
		double J2 = (21 * Math.pow(e1, 2) / 16 - 55 * Math.pow(e1, 4) / 32.0);
		double J3 = (151 * Math.pow(e1, 3) / 96.0);
		double J4 = (1097 * Math.pow(e1, 4) / 512.0);

		double fp = mu + J1 * Math.sin(2 * mu) + J2 * Math.sin(4 * mu) + J3
				* Math.sin(6 * mu) + J4 * Math.sin(8 * mu);

		// Calculate Latitude and Longitude
		double C1 = e2 * Math.pow(Math.cos(fp), 2);
		double T1 = Math.pow(Math.tan(fp), 2);
		double R1 = a * (1 - e)
				/ Math.pow((1 - e * Math.pow(Math.sin(fp), 2)), (3.0 / 2.0));
		double N1 = a / Math.pow((1 - e * Math.pow(Math.sin(fp), 2)), 0.5);

		double D = x / (N1 * k0);

		// Latitude
		double Q1 = N1 * Math.tan(fp) / R1;
		double Q2 = (Math.pow(D, 2) / 2.0);
		double Q3 = (5 + 3 * T1 + 10 * C1 - 4 * Math.pow(C1, 2) - 9 * e2)
				* Math.pow(D, 4) / 24.0;
		double Q4 = (61 + 90 * T1 + 298 * C1 + 45 * Math.pow(T1, 2) - 3
				* Math.pow(C1, 2) - 252 * e2)
				* Math.pow(D, 6) / 720.0;
		double lat = fp - Q1 * (Q2 - Q3 + Q4);

		// Longitude
		double Q5 = D;
		double Q6 = (1 + 2 * T1 + C1) * Math.pow(D, 3) / 6;
		double Q7 = (5 - 2 * C1 + 28 * T1 - 3 * Math.pow(C1, 2) + 8 * e2 + 24 * Math
				.pow(T1, 2)) * Math.pow(D, 5) / 120.0;
		double lon = lon0 + (Q5 - Q6 + Q7) / Math.cos(fp);

		double lat_degree = (lat * 180) / Math.PI;
		double lon_degree = (lon * 180) / Math.PI;

		return lon_degree + "," + lat_degree;
	}
}
