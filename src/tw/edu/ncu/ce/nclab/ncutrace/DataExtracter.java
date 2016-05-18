package tw.edu.ncu.ce.nclab.ncutrace;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import javax.swing.JFileChooser;

import org.joda.time.DateTime;
import org.joda.time.Seconds;

import tw.edu.ncu.ce.nclab.ncutrace.data.TWD97;
import tw.edu.ncu.ce.nclab.ncutrace.data.TrackPoint;

/**
 * 把我的足跡節錄下來的Trace檔轉換成 "x y time"，分別是 "TWD97格式的座標(四捨五入至小數點第一位) 開始時間"
 * 並將檔案輸出到(原資料目錄)_output，並附上node對照表與座標最小值
 */
public class DataExtracter {

	private String sourceDirectoriesPath;
	private String outPutDirectoriesPath;
	private File outPutDirectory;
	private static final int NUMBER_OF_NO_USE_LINES_IN_CSVFILE = 4;
	private static final int POSITION_OF_LAT_IN_EACHLINE = 2;
	private static final int POSITION_OF_LON_IN_EACHLINE = 3;
	private static final int POSITION_OF_TRACKTIME_IN_EACHLINE = 8;
	private double minX;
	private double minY;
	private NumberFormat nf = new DecimalFormat(".#"); // 小數點第一位

	private PrintWriter extractInfo;

	public DataExtracter() {

		sourceDirectoriesPath = "";
		outPutDirectoriesPath = "";
		minX = Double.POSITIVE_INFINITY;
		minY = Double.POSITIVE_INFINITY;
	}

	private void chooseSourceDirectory() {
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			outPutDirectoriesPath = file.getAbsolutePath() + "_output"
					+ File.separator;
			File outPutDirectory = new File(outPutDirectoriesPath);
			outPutDirectory.mkdirs();

			sourceDirectoriesPath = file.getAbsolutePath() + File.separator;
		} else {
			System.exit(0);
		}
	}

	public void extractDataForEachUser() throws NumberFormatException,
			IOException {

		// 獲得所有參加者的名字(資料夾name)，可能含系統的隱藏檔。
		String userNameListInChinese[] = getFileNameList(sourceDirectoriesPath);
		// For each user
		int validUserNameIndex = 0;
		for (int usernameIndex = 0; usernameIndex < userNameListInChinese.length; usernameIndex++) {

			File subDirectory = new File(sourceDirectoriesPath
					+ userNameListInChinese[usernameIndex]);
			if (!subDirectory.isDirectory()) {
				continue;
			}
			extractInfo.println(userNameListInChinese[usernameIndex] + " "
					+ validUserNameIndex);
			extractInfo.flush();

			File outPutFile = new File(outPutDirectoriesPath
					+ validUserNameIndex + ".txt");

			System.out.println(validUserNameIndex + ":"
					+ userNameListInChinese[usernameIndex]);

			PrintWriter userOutPutFile = new PrintWriter(outPutFile);

			extractDataFromTrackFile(subDirectory, userOutPutFile);
			userOutPutFile.close();
			validUserNameIndex++;

		}
	}

	public void extractDataFromTrackFile(File subDirectory,
			PrintWriter userOutPutFile) throws FileNotFoundException {

		String trackList[] = getFileNameList(subDirectory.getAbsolutePath());// get裡面的檔案(csv檔)

		Scanner trackFileScanner;

		ArrayList<TrackPoint> trackPoints = new ArrayList<TrackPoint>();

		// For each track file (.csv)
		for (int trackFileNameIndex = 0; trackFileNameIndex < trackList.length; trackFileNameIndex++) {

			if (trackList[trackFileNameIndex].endsWith(".csv")) {

				trackFileScanner = new Scanner(new File(
						subDirectory.getAbsolutePath() + File.separator
								+ trackList[trackFileNameIndex]));
			} else {
				continue;
			}

			// Skip first 4 lines
			for (int i = 0; i < NUMBER_OF_NO_USE_LINES_IN_CSVFILE; i++) {
				trackFileScanner.nextLine();
			}

			String str;
			while (trackFileScanner.hasNextLine()) {

				str = trackFileScanner.nextLine();
				String[] linesplit = str.split("\",\"");

				double lat = Double
						.parseDouble(linesplit[POSITION_OF_LAT_IN_EACHLINE]);// 緯度(degree)
				double lon = Double
						.parseDouble(linesplit[POSITION_OF_LON_IN_EACHLINE]);// 經度(degree)

				DateTime timeOfTracePoint = new DateTime(
						linesplit[POSITION_OF_TRACKTIME_IN_EACHLINE]);

				// 距離Trace開始時間(基準點)
				int elapseds = Seconds.secondsBetween(
						NCUTrace.STARTING_TIME_OF_NCUTRACE, timeOfTracePoint)
						.getSeconds();

				TWD97 twd97Location = CoordinateTransform.convertWGS84toTWD97(
						lon, lat);

				double x = twd97Location.getX();
				double y = twd97Location.getY();

				checkMinValue(x, y);
				trackPoints.add(new TrackPoint(x, y, elapseds));

			}
			System.out.println("\tFile " + trackList[trackFileNameIndex]
					+ " done");
		}

		// sort trackPoints by the elapsed time
		Collections.sort(trackPoints);

		for (TrackPoint p : trackPoints) {
			userOutPutFile.println(p.toString());
		}

	}

	public void startExtractData() {

		chooseSourceDirectory();

		try {
			extractInfo = new PrintWriter(outPutDirectoriesPath
					+ "extractInfo.txt");
			extractDataForEachUser();
			extractInfo.println("MinX=" + nf.format(minX));
			extractInfo.println("MinY=" + nf.format(minY));
			extractInfo.flush();
			extractInfo.close();
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (NumberFormatException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	public void timeCheck() throws FileNotFoundException {
		outPutDirectory = new File(outPutDirectoriesPath);

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

		String[] files = outPutDirectory.list(trackFilter);

		for (String fileName : files) {

			Scanner sc = new Scanner(new File(outPutDirectory.getAbsolutePath()
					+ File.separator + fileName));
			String st;
			double lastTime = -86400;
			while (sc.hasNextLine()) {
				st = sc.nextLine();
				String[] info = st.split(" ");
				double trackTime = Double.parseDouble(info[2]);
				if (trackTime >= lastTime) {
					lastTime = trackTime;
				} else {
					System.out.println("Error in:" + fileName + " @ "
							+ trackTime);
					break;
				}
			}
			sc.close();

		}

	}

	private void checkMinValue(double x, double y) {
		if (x < minX) {
			minX = x;
		}
		if (y < minY) {
			minY = y;
		}
	}

	public File getOutPutDirectory() {
		return this.outPutDirectory;
	}

	/**
	 * 輸入路徑,輸出當資料夾下的所有檔案列表
	 */
	public static String[] getFileNameList(String folderPath) {

		return new File(folderPath).list();
	}

}
