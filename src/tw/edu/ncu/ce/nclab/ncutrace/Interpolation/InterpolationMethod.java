package tw.edu.ncu.ce.nclab.ncutrace.Interpolation;
import java.util.List;
import java.util.Random;

import tw.edu.ncu.ce.nclab.ncutrace.data.TrackPoint;

public interface InterpolationMethod {

	public List<TrackPoint> getInterpolationMethod(TrackPoint lastTrackPoint,
			TrackPoint currentPoint);

	public void setRandomGenerator(Random random);
}
