import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.JFileChooser;

import org.joda.time.DateTime;
import org.joda.time.Seconds;

/**
 * 把我的足跡節錄下來的Trace檔轉換成 "x y time"，分別是 "TWD97格式的座標(四捨五入至小數點第一位) 開始時間" 
 * 並將檔案輸出到(原資料目錄)_time 的，並附上node對照表與座標最小值
 */
public class Step0 {

	// 2014-12-15T12:00:00.000+08:00 Trace開始時間為12/15 中午12點
	public final static DateTime startingTimeOfNCUTrace = new DateTime(
			"2014-12-15T12:00:00.000+08:00");

	// static double MapedgeX= 121.15; //地圖最低邊緣
	// static double MapedgeY= 24.93; //地圖最低邊緣
	public static void main(String[] args) throws IOException, ParseException {

		String sourceDirectoriesPath = "";

		JFileChooser fc = new JFileChooser();

		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			sourceDirectoriesPath = file.getAbsolutePath() + File.separator;
		} else {
			System.exit(0);
		}

		// 獲得所有參加者的名字(資料夾name)，可能含系統的隱藏檔。
		String userNameListInChinese[] = getFileNameList(sourceDirectoriesPath);

		BufferedWriter[] transBufferedWriter = new BufferedWriter[userNameListInChinese.length]; // 給轉換器用
		BufferedWriter[] transAddTimeBufferedWriter = new BufferedWriter[userNameListInChinese.length]; // 對照組(+時間)

		
		int readlinecount = 0;
		double lon;// 經度
		double lat;// 緯度

		// for each user
		int validUserNameIndex=0;
		for (int usernameIndex = 0; usernameIndex < userNameListInChinese.length; usernameIndex++) {

			File subDirectory = new File(sourceDirectoriesPath
					+ userNameListInChinese[usernameIndex]);
			if (!subDirectory.isDirectory()) {
				continue;
			}

			String trackList[] = getFileNameList(subDirectory.getAbsolutePath());// get裡面的檔案(csv檔)

			System.out.println(userNameListInChinese[usernameIndex]);
			BufferedReader[] br = new BufferedReader[trackList.length];
			transBufferedWriter[usernameIndex] = new BufferedWriter(new FileWriter(
					subDirectory.getAbsolutePath() + "//" + "trans.txt"));
			transAddTimeBufferedWriter[usernameIndex] = new BufferedWriter(
					new FileWriter(subDirectory.getAbsolutePath() + "//"
							+ "trans_time.txt"));

			// For each track file (.csv)
			for (int trackFileNameIndex = 0; trackFileNameIndex < trackList.length; trackFileNameIndex++) {
				
				if (trackList[trackFileNameIndex].endsWith(".csv")) { // 只做.csv檔
					br[trackFileNameIndex] = new BufferedReader(new FileReader(
							subDirectory.getAbsolutePath() + "//"
									+ trackList[trackFileNameIndex]));
				} else {
					continue;
				}
				String str;
				while ((str = br[trackFileNameIndex].readLine()) != null) {
					if (readlinecount < 4) { // 先跳過前四行
						readlinecount++;
						continue;
					}
					String[] linesplit = str.split("\",\""); // 2 3 8有用

					lat = Double.parseDouble(linesplit[2]);
					lon = Double.parseDouble(linesplit[3]);

					DateTime timeOfTracePoint = new DateTime(linesplit[8]);

					// 距離Trace開始時間(基準點)
					int elapseds = Seconds.secondsBetween(
							startingTimeOfNCUTrace, timeOfTracePoint)
							.getSeconds();

					transBufferedWriter[usernameIndex]
							.write(lon + " " + lat + "\n");

					NumberFormat nf = new DecimalFormat(".#"); // 小數點第一位

					TWD97 twd97Location = CoordinateTransform
							.convertWGS84toTWD97(lon, lat);

					transAddTimeBufferedWriter[usernameIndex].write(nf
							.format(twd97Location.getX())
							+ " "
							+ nf.format(twd97Location.getY())
							+ " "
							+ elapseds
							+ "\n");// 儲存經緯度座標與micro
					// secs

				}

				readlinecount = 0;
				System.out.println("file " + trackList[trackFileNameIndex] + " done");
			}
			transBufferedWriter[usernameIndex].flush();
			transBufferedWriter[usernameIndex].close();
			transAddTimeBufferedWriter[usernameIndex].flush();
			transAddTimeBufferedWriter[usernameIndex].close();
		}

	}// main end

	/**
	 * 輸入路徑,輸出當資料夾下的所有檔案列表
	 */
	public static String[] getFileNameList(String folderPath) {

		return new File(folderPath).list();
	}

}
