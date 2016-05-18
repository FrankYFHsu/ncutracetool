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
public class DataExtracter extends ArrangeMethod {

	private static final int NUMBER_OF_NO_USE_LINES_IN_CSVFILE = 4;
	private static final int POSITION_OF_LAT_IN_EACHLINE = 2;
	private static final int POSITION_OF_LON_IN_EACHLINE = 3;
	private static final int POSITION_OF_TRACKTIME_IN_EACHLINE = 8;

	private PrintWriter extractInfo;

	public DataExtracter() {
		// Nothing to do?
	}

	public DataExtracter(File source) {
		this.sourceDirectory = source;
	}

	public void startExtractData() {

		checkSourceDirectory("_output");

		try {
			extractInfo = new PrintWriter(
					this.outPutDirectory.getAbsolutePath() + File.separator
							+ "extractInfo.txt");
			extractDataForEachUser();
			extractInfo.close();
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (NumberFormatException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	public void extractDataForEachUser() throws NumberFormatException,
			IOException {

		// 獲得所有參加者的名字(資料夾name)，可能含系統的隱藏檔。
		String userNameListInChinese[] = sourceDirectory.list();
		// For each user
		int validUserNameIndex = 0;
		for (int usernameIndex = 0; usernameIndex < userNameListInChinese.length; usernameIndex++) {

			File subDirectory = new File(this.sourceDirectory.getAbsolutePath()
					+ File.separator + userNameListInChinese[usernameIndex]);
			if (!subDirectory.isDirectory()) {
				continue;
			}
			extractInfo.println(userNameListInChinese[usernameIndex] + " "
					+ validUserNameIndex);
			extractInfo.flush();

			File outPutFile = new File(this.outPutDirectory.getAbsolutePath()
					+ File.separator + validUserNameIndex + ".txt");

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

		String trackList[] = subDirectory.list();// get裡面的檔案(csv檔)

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

	public void timeCheck() throws FileNotFoundException {

		String[] files = getNumericFileName(this.outPutDirectory);

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

	public File getOutPutDirectory() {
		return this.outPutDirectory;
	}

}
