package tw.edu.ncu.ce.nclab.ncutrace.Interpolation;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tw.edu.ncu.ce.nclab.ncutrace.NCUTrace;
import tw.edu.ncu.ce.nclab.ncutrace.data.TrackPoint;

/**
 * Interpolation modified by YuFeng Hsu
 * 不同點在於只有速度小於定義的最小值，才會有原地等待，而非每段都會有原地等待。
 * @author YuFeng Hsu
 *
 */
public class HsuInterpolation implements InterpolationMethod {

	Random generator;

	public HsuInterpolation() {
		this.generator = new Random(0);
	}

	@Override
	public List<TrackPoint> getInterpolationMethod(TrackPoint lastTrackPoint,
			TrackPoint currentPoint) {

		List<TrackPoint> interpolatedTrackPoints = new ArrayList<TrackPoint>();
		
		double dis = distance(currentPoint.x, currentPoint.y, lastTrackPoint.x,
				lastTrackPoint.y);
		int duration = currentPoint.elapsedTime - lastTrackPoint.elapsedTime;
		
		double speed = 1.0*dis/duration;
		
		int timeslot = lastTrackPoint.elapsedTime + 1;
		
		if(speed< NCUTrace.MINWALKINGSPEED){
			//隨機原地等待一段時間
			double randomspeed = generator.nextDouble()
					* (NCUTrace.MAXWALKINGSPEED - NCUTrace.MINWALKINGSPEED)
					+ NCUTrace.MINWALKINGSPEED;
			
			int wait = (int) Math.round(duration - (dis / randomspeed));
			for (int i = 1; i < wait; i++) {
				interpolatedTrackPoints.add(new TrackPoint(lastTrackPoint.x,
						lastTrackPoint.y, (i + lastTrackPoint.elapsedTime)));
				timeslot++;
			}
			
		}
		
		double xdelta = (currentPoint.x - lastTrackPoint.x)
				/ (currentPoint.elapsedTime - timeslot + 1);
		double ydelta = (currentPoint.y - lastTrackPoint.y)
				/ (currentPoint.elapsedTime - timeslot + 1);

		for (; timeslot < currentPoint.elapsedTime; timeslot++) {
			lastTrackPoint.x = lastTrackPoint.x + xdelta;
			lastTrackPoint.y = lastTrackPoint.y + ydelta;
			interpolatedTrackPoints.add(new TrackPoint(lastTrackPoint.x,
					lastTrackPoint.y, timeslot));

		}

		return interpolatedTrackPoints;
	}

	@Override
	public void setRandomGenerator(Random random) {
		this.generator = random;

	}

	public static double distance(double X1, double Y1, double X2, double Y2) {

		return Math.sqrt(Math.pow(X1 - X2, 2) + Math.pow(Y1 - Y2, 2));
	}

}
