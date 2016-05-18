package tw.edu.ncu.ce.nclab.ncutrace;

import java.io.IOException;
import java.text.ParseException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Seconds;

import tw.edu.ncu.ce.nclab.ncutrace.Interpolation.OriginalInterpolation;

public class NCUTrace {

	// 2014-12-15T12:00:00.000+08:00 Trace開始時間為12/15 中午12點(utc+8)
	public final static DateTime STARTING_TIME_OF_NCUTRACE = new DateTime(
			"2014-12-15T12:00:00.000+08:00");
	// meter per sec.
	public final static double MAXWALKINGSPEED = 5.43 * (10 / 36.0);
	public final static double MINWALKINGSPEED = 3.75 * (10 / 36.0);

	public static void main(String[] args) throws IOException, ParseException {
		// DataExtracter step1 = new DataExtracter();
		// step1.startExtractData();
		// step1.timeCheck(); optional

		// 第一步後，可以進行篩選

		// TrackPointInterpolation step2 = new
		// TrackPointInterpolation(step1.getOutPutDirectory());
		TrackPointInterpolation step2 = new TrackPointInterpolation();
		// step2.setInterpolationMethod(new HsuInterpolation());
		step2.setInterpolationMethod(new OriginalInterpolation());
		step2.startInterpolation();

		DateTime startTime = new DateTime(2014, 12, 18, 12, 0, 0, 0);
		System.out.println(startTime);
		DateTime endTime = startTime.plusDays(1);
		System.out.println(endTime);
		TraceFulfillmentAndMerge step3 = new TraceFulfillmentAndMerge(
				step2.getOutPutDirectory());
		step3.setStartingTime(Seconds.secondsBetween(
				NCUTrace.STARTING_TIME_OF_NCUTRACE, startTime).getSeconds());
		step3.setEndingTime(Seconds.secondsBetween(
				NCUTrace.STARTING_TIME_OF_NCUTRACE, endTime).getSeconds());

		step3.startTraceFulfillment();
		step3.startMerge();

	}

}
