
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;

public class Step2 {

	/**
	 * 將轉換後的TWD97附上與Trace開始時間之秒數(trans_time.txt) 並四捨五入至小數點第一位 輸出到
	 * 20141215_twd97_time 資料夾底下，並附上node對照表與 XY座標地圖最低邊緣
	 */
	// static double MapedgeX= 121.15; //地圖最低邊緣
	// static double MapedgeY= 24.93; //地圖最低邊緣
	static double MinX = Double.MAX_VALUE;
	static double MinY = Double.MAX_VALUE;
	static int DistributionDays = 30;

	public static void main(String[] args) throws IOException, ParseException {

		String path = "D:\\NCUtrace\\20141215_trans_twd97\\"; // check point
		String outputpath = "D:\\NCUtrace\\20141215_twd97_time\\";

		// XXX
		File f = new File(outputpath);
		f.mkdir();

		int NumOfParticipant = getFileList(path).length;
		String PersonalFileName[] = new String[NumOfParticipant];
		PersonalFileName = getFileList(path);
		NumberFormat nf = new DecimalFormat(".#"); // 小數點第一位

		BufferedWriter Record = new BufferedWriter(new FileWriter(outputpath
				+ "Record.txt"));
		BufferedWriter[] bw = new BufferedWriter[NumOfParticipant];
		BufferedReader[] TimeReader = new BufferedReader[NumOfParticipant];
		BufferedReader[] TwdReader = new BufferedReader[NumOfParticipant];

		// 採樣回數的distribution
		int Record_times[][] = new int[NumOfParticipant][DistributionDays];
		for (int i = 0; i < NumOfParticipant; i++) {
			for (int j = 0; j < DistributionDays; j++) {
				Record_times[i][j] = 0;
			}
		}

		String str_timeReader;
		String str_twdReader;
		double X;
		double Y;

		for (int counter = 0; counter < NumOfParticipant; counter++) {
			TimeReader[counter] = new BufferedReader(new FileReader(path
					+ PersonalFileName[counter] + "\\" + "trans_time.txt"));
			TwdReader[counter] = new BufferedReader(new FileReader(path
					+ PersonalFileName[counter] + "\\" + "trans_twd97.txt"));
			bw[counter] = new BufferedWriter(new FileWriter(outputpath
					+ counter + "_" + "twd97_time.txt"));
			Record.write(PersonalFileName[counter] + " " + counter + "\n");
			while ((str_timeReader = TimeReader[counter].readLine()) != null) {
				str_twdReader = TwdReader[counter].readLine();
				String[] linesplit_time = str_timeReader.split(" ");
				String[] linesplit_twd = str_twdReader.split(" ");

				X = Double.parseDouble(linesplit_twd[0]);
				Y = Double.parseDouble(linesplit_twd[1]);
				bw[counter].write(nf.format(X) + " " + nf.format(Y) + " "
						+ linesplit_time[2] + "\n");
				BoundaryCheck(Double.parseDouble(nf.format(X).toString()),
						Double.parseDouble(nf.format(Y).toString()));

				// 採樣回數的distribution
				if (Integer.parseInt(linesplit_time[2]) >= 0) {
					Record_times[counter][(int) (Integer
							.parseInt(linesplit_time[2]) / 86400.0)]++;
				}
			}
			bw[counter].flush();
			bw[counter].close();
			TimeReader[counter].close();
			TwdReader[counter].close();
			System.out.println("file " + PersonalFileName[counter] + " done");
		}

		Record.write("MinX=" + MinX + "\n" + "MinY=" + MinY + "\n\n");

		// 採樣回數的distribution
		int SumofDay[] = new int[DistributionDays];
		Arrays.fill(SumofDay, 0);

		for (int i = 0; i < NumOfParticipant; i++) {
			for (int j = 0; j < DistributionDays; j++) {
				Record.write(Record_times[i][j] + " ");
				SumofDay[j] = SumofDay[j] + Record_times[i][j];
			}
			Record.write("\n");

		}

		// 採樣回數的distribution
		for (int i = 0; i < DistributionDays; i++) {
			Record.write("Day" + (i + 1) + " " + SumofDay[i] + "\n");
		}

		Record.flush();
		Record.close();

	}// main end

	/**
	 * 輸入路徑,輸出當資料夾下的所有檔案列表
	 */
	public static String[] getFileList(String folderPath) {
		File folder = new File(folderPath);
		String[] list = folder.list();
		return list;
	}

	/**
	 * 查本次X與Y軸的最低界線
	 * 
	 * @param X
	 * @param Y
	 */
	public static void BoundaryCheck(double X, double Y) {
		if (MinX > X) {
			MinX = X;
		}
		if (MinY > Y) {
			MinY = Y;
		}
	}

}
