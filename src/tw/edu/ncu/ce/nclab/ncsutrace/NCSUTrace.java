package tw.edu.ncu.ce.nclab.ncsutrace;

import java.io.IOException;

import tw.edu.ncu.ce.nclab.ncutrace.TraceFulfillmentAndMerge;
import tw.edu.ncu.ce.nclab.ncutrace.TrackPointInterpolation;
import tw.edu.ncu.ce.nclab.ncutrace.Interpolation.HsuInterpolation;

/**
 * Generating the one-format trace file for the ncsu/mobilitymodels dataset (v. 2009-07-23)
 * https://crawdad.cs.dartmouth.edu/ncsu/mobilitymodels/20090723/
 * @author Yu-Feng Hsu
 *
 */
public class NCSUTrace {

	public static void main(String[] args) {

		try {

			NCSUTraceConverter step1 = new NCSUTraceConverter();
			step1.startConverter();
			System.out.println(step1.getNCSUTracePrevix());

			TrackPointInterpolation step2 = new TrackPointInterpolation(
					step1.outPutDirectory);
			step2.setInterpolationMethod(new HsuInterpolation());
			step2.startInterpolation();

			TraceFulfillmentAndMerge step3 = new TraceFulfillmentAndMerge(
					step1.getNCSUTracePrevix(), step2.outPutDirectory);
			
			//設定開始時間
			step3.setStartingTime(0);
			//設定結束時間
			step3.setEndingTime(14400);

			step3.startTraceFulfillment();
			step3.startMerge();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
