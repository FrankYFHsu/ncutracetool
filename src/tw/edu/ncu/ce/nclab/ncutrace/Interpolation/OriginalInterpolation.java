package tw.edu.ncu.ce.nclab.ncutrace.Interpolation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tw.edu.ncu.ce.nclab.ncutrace.NCUTrace;
import tw.edu.ncu.ce.nclab.ncutrace.data.TWD97;
import tw.edu.ncu.ce.nclab.ncutrace.data.TrackPoint;

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
		double dis = distance(currentPoint.getX(), currentPoint.getY(),
				lastTrackPoint.getX(), lastTrackPoint.getY());

		int timeslot = lastTrackPoint.elapsedTime + 1;

		double randomspeed = generator.nextDouble()
				* (NCUTrace.MAXWALKINGSPEED - NCUTrace.MINWALKINGSPEED)
				+ NCUTrace.MINWALKINGSPEED;
		if (elapsed * randomspeed > dis) {// 如果隨機距離大於實際的距離，就會在原地等待一段時間

			int wait = (int) Math.round(elapsed - (dis / randomspeed));
			for (int i = 1; i < wait; i++) {
				interpolatedTrackPoints
						.add(new TrackPoint(lastTrackPoint.getTWD97_location(),
								(i + lastTrackPoint.elapsedTime)));
				timeslot++;
			}

		}

		double xdelta = (currentPoint.getX() - lastTrackPoint.getX())
				/ (currentPoint.elapsedTime - timeslot + 1);
		double ydelta = (currentPoint.getY() - lastTrackPoint.getY())
				/ (currentPoint.elapsedTime - timeslot + 1);

		for (; timeslot < currentPoint.elapsedTime; timeslot++) {

			TWD97 newTrackPoint = new TWD97(lastTrackPoint.getX() + xdelta,
					lastTrackPoint.getY() + ydelta);

			lastTrackPoint = new TrackPoint(newTrackPoint, timeslot);

			interpolatedTrackPoints.add(lastTrackPoint);

		}

		return interpolatedTrackPoints;
	}

	public static double distance(double X1, double Y1, double X2, double Y2) {

		return Math.sqrt(Math.pow(X1 - X2, 2) + Math.pow(Y1 - Y2, 2));
	}

}
