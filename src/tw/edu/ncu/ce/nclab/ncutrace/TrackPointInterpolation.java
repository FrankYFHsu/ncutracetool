package tw.edu.ncu.ce.nclab.ncutrace;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JFileChooser;

import tw.edu.ncu.ce.nclab.ncutrace.Interpolation.InterpolationMethod;
import tw.edu.ncu.ce.nclab.ncutrace.Interpolation.OriginalInterpolation;

public class TrackPointInterpolation {
	
	Random generator = new Random(0);
	private File sourceDirectory;
	private File outPutDirectory;
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

	private void chooseSourceDirectory() {
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			sourceDirectory = fc.getSelectedFile();

		} else {
			System.exit(0);
		}
	}

	private void checkSourceDirectory() {
		if (sourceDirectory == null) {
			chooseSourceDirectory();
		}
		outPutDirectory = new File(sourceDirectory.getAbsolutePath() + "_full");
		outPutDirectory.mkdirs();
	}

	public void startInterpolation() throws IOException {

		checkSourceDirectory();

		FilenameFilter trackFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				String lowercaseName = name.toLowerCase();
				if (lowercaseName.matches("\\d+\\.txt")) {
					return true;
				} else {
					return false;
				}
			}
		};

		String[] files = sourceDirectory.list(trackFilter);

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
				String str = sc.nextLine();
				String[] linesplit = str.split(" ");
				double x = Double.parseDouble(linesplit[0]);
				double y = Double.parseDouble(linesplit[1]);
				int time = Integer.parseInt(linesplit[2]);
				if (time < 0) {
					continue;
				}

				if (firstinput) {// 檢測是否為第一筆有效資料(time>=0的第一筆)
					lastdata = new TrackPoint(x, y, time);
					firstinput = false;
					pw.println(lastdata.toString());
					continue;
				}
				if (time - lastdata.elapsedTime > 1) {// 檢測兩點資料之間的連續性 不齊則補足
				
					List<TrackPoint> list = interpolationMethod.getInterpolationMethod(lastdata, new TrackPoint(x,y,time));
					for(TrackPoint p:list){
						pw.println(p.toString());
					}
					
				}
				pw.println(x + " " + y + " " + time);// 寫入並儲存上一筆data
				pw.flush();
				lastdata = new TrackPoint(x, y, time);

			}

		}

	}

}
