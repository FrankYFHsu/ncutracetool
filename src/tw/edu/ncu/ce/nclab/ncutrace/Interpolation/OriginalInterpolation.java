package tw.edu.ncu.ce.nclab.ncutrace.Interpolation;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tw.edu.ncu.ce.nclab.ncutrace.NCUTrace;
import tw.edu.ncu.ce.nclab.ncutrace.TrackPoint;

/**
 * Original Interpolation used by Pin-Chun Chiu
 * 
 * @author YuFeng Hsu
 *
 */
public class OriginalInterpolation implements InterpolationMethod {

	Random generator;

	public OriginalInterpolation() {
		this.generator = new Random(0);
	}

	@Override
	public void setRandomGenerator(Random random) {
		this.generator = random;
	}

	@Override
	public List<TrackPoint> getInterpolationMethod(TrackPoint lastTrackPoint,
			TrackPoint currentPoint) {

		List<TrackPoint> interpolatedTrackPoints = new ArrayList<TrackPoint>();

		int elapsed = currentPoint.elapsedTime - lastTrackPoint.elapsedTime;
		double dis = distance(currentPoint.x, currentPoint.y, lastTrackPoint.x,
				lastTrackPoint.y);

		int timeslot = lastTrackPoint.elapsedTime + 1;

		double randomspeed = generator.nextDouble()
				* (NCUTrace.MAXWALKINGSPEED - NCUTrace.MINWALKINGSPEED)
				+ NCUTrace.MINWALKINGSPEED;
		if (elapsed * randomspeed > dis) {// 如果隨機距離大於實際的距離，就會在原地等待一段時間

			int wait = (int) Math.round(elapsed - (dis / randomspeed));
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

	public static double distance(double X1, double Y1, double X2, double Y2) {

		return Math.sqrt(Math.pow(X1 - X2, 2) + Math.pow(Y1 - Y2, 2));
	}

}
