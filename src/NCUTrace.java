import java.io.IOException;
import java.text.ParseException;

import org.joda.time.DateTime;

public class NCUTrace {
	

	// 2014-12-15T12:00:00.000+08:00 Trace開始時間為12/15 中午12點(utc+8)
	public final static DateTime STARTING_TIME_OF_NCUTRACE = new DateTime(
			"2014-12-15T12:00:00.000+08:00");
	
	
	public static void main(String[] args) throws IOException, ParseException {
		DataExtracter step1 = new DataExtracter();
		step1.startExtractData();
		step1.timeCheck();
	}
	
	

}
