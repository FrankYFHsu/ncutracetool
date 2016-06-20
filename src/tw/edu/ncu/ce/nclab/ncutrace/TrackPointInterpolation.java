package tw.edu.ncu.ce.nclab.ncutrace;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import tw.edu.ncu.ce.nclab.ncutrace.Interpolation.InterpolationMethod;
import tw.edu.ncu.ce.nclab.ncutrace.Interpolation.OriginalInterpolation;
import tw.edu.ncu.ce.nclab.ncutrace.data.TrackPoint;

public class TrackPointInterpolation extends TraceArrangement{
	
	Random generator = new Random(0);

	private InterpolationMethod interpolationMethod;

	public TrackPointInterpolation(File sourceDirectory) {
		this.sourceDirectory = sourceDirectory;
	}

	public TrackPointInterpolation() {
		interpolationMethod=new OriginalInterpolation();
	}
	
	public void setInterpolationMethod(InterpolationMethod im){
		interpolationMethod=im;
		interpolationMethod.setRandomGenerator(generator);
	}

	public void setRandomSeed(long seed) {
		generator.setSeed(seed);
		interpolationMethod.setRandomGenerator(generator);//Currently, this is no need
	}



	public void startInterpolation() throws IOException {

		checkSourceDirectory("_full");

		String[] files = getNumericFileName(sourceDirectory);

		for (String fileName : files) {
			System.out.println(fileName);
			Scanner sc = new Scanner(new File(sourceDirectory.getAbsolutePath()
					+ File.separator + fileName));

			PrintWriter pw = new PrintWriter(new File(
					outPutDirectory.getAbsolutePath() + File.separator
							+ fileName));

			boolean firstinput = true;

			TrackPoint lastdata = new TrackPoint();

			while (sc.hasNextLine()) {
				String trackPointInfo = sc.nextLine();
				String[] linesplit = trackPointInfo.split(" ");
				double x = Double.parseDouble(linesplit[0]);
				double y = Double.parseDouble(linesplit[1]);
				int time = Integer.parseInt(linesplit[2]);
				if (time < 0) {
					continue;
				}

				if (firstinput) {// 檢測是否為第一筆有效資料(time>=0的第一筆)
					lastdata = new TrackPoint(trackPointInfo,TrackPoint.LocationType.TWD97);
					firstinput = false;
					pw.println(lastdata.toString());
					continue;
				}
				if (time - lastdata.elapsedTime > 1) {// 檢測兩點資料之間的連續性 不齊則補足
				
					List<TrackPoint> list = interpolationMethod.getInterpolationMethod(lastdata, new TrackPoint(trackPointInfo,TrackPoint.LocationType.TWD97));
					for(TrackPoint p:list){
						pw.println(p.toString());
					}
					
				}
				pw.println(x + " " + y + " " + time);// 寫入並儲存上一筆data
				pw.flush();
				lastdata = new TrackPoint(trackPointInfo,TrackPoint.LocationType.TWD97);

			}

		}

	}

}
