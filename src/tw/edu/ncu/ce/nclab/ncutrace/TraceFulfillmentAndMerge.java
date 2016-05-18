package tw.edu.ncu.ce.nclab.ncutrace;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Scanner;

import javax.swing.JFileChooser;

import tw.edu.ncu.ce.nclab.ncutrace.data.TrackPoint;


public class TraceFulfillmentAndMerge extends ArrangeMethod {

	private int startTime = 0;// defalut
	private int endTime = 86400*14;// default
	private double MinX = Double.MAX_VALUE;
	private double MinY = Double.MAX_VALUE;
	private double MaxX = 0;
	private double MaxY = 0;

	public TraceFulfillmentAndMerge(File sourceDirectory) {
		this.sourceDirectory = sourceDirectory;
	}

	public void setStartingTime(int time) {
		startTime = time;
	}

	public void setEndingTime(int time) {
		endTime = time;
	}

	public void startTraceFulfillment() throws IOException {

		checkSourceDirectory("_All");

		String[] files = getNumericFileName(sourceDirectory);

		boolean firstinputflag = true;
		for (String fileName : files) {

			Scanner sc = new Scanner(new File(sourceDirectory.getAbsolutePath()
					+ File.separator + fileName));

			PrintWriter pw = new PrintWriter(new File(
					outPutDirectory.getAbsolutePath() + File.separator
							+ fileName));
			TrackPoint point = new TrackPoint();
			while (sc.hasNextLine()) {
				String trackPointInfo = sc.nextLine();
				point = new TrackPoint(trackPointInfo);

				if (point.elapsedTime < startTime) {
					continue;
				}
				if (firstinputflag == true) {
					FillStartTime(point, pw);
					firstinputflag = false;
				}
				pw.println(point.toString());

				checkBoundary(point);

				if (point.elapsedTime == endTime) {
					break;
				}
			}
			if (point.elapsedTime < endTime) {
				FillEndTime(point.addElapsedTime(1), pw);
			}
			pw.close();
			sc.close();
			firstinputflag = true;
			System.out.println("Trace: " + fileName + " has fulfilled.");

		}

	}

	public void startMerge() throws IOException {

		sourceDirectory = this.outPutDirectory;

		outPutDirectory = new File(sourceDirectory.getAbsoluteFile()
				+ "_AllInONE");
		outPutDirectory.mkdir();

		String[] files = getNumericFileName(sourceDirectory);

		BufferedReader[] br2 = new BufferedReader[files.length];
		BufferedWriter Rawtrace = new BufferedWriter(new FileWriter(
				outPutDirectory.getAbsolutePath() + File.separator
						+ "NCUtrace_" + startTime + "_" + endTime + ".txt"));

		String[] linesplit;

		double X = 0;
		double Y = 0;
		int time = 0;

		// ==========================file merge=========================

		for (int counter = 0; counter < files.length; counter++) {
			br2[counter] = new BufferedReader(new FileReader(
					sourceDirectory.getAbsolutePath() + File.separator
							+ counter + ".txt"));
		}

		System.out.println("MaxX=" + MaxX + " MinX=" + MinX + " MaxY=" + MaxY
				+ " MinY=" + MinY);
		System.out.println("X=" + Math.ceil(MaxX - MinX) + " Y="
				+ Math.ceil(MaxY - MinY));

		NumberFormat nf = new DecimalFormat(".#");
		Rawtrace.write("0 " + (endTime - startTime) + " 0 "
				+ (Math.ceil(MaxX - MinX) + 100) + " 0 "
				+ (Math.ceil(MaxY - MinY) + 100) + " 0 0\n");

		for (int counter = 0; counter <= (endTime - startTime); counter++) {
			for (int i = 0; i < files.length; i++) {
				String str = br2[i].readLine();
				linesplit = str.split(" ");
				Rawtrace.write(counter + " " + i + " "
						+ nf.format(Double.parseDouble(linesplit[0]) - MinX)
						+ " "
						+ nf.format(Double.parseDouble(linesplit[1]) - MinY)
						+ "\n");
			}
		}

		Rawtrace.flush();
		Rawtrace.close();
		System.out.println("merge finish");

	}

	private void checkBoundary(TrackPoint point) {
		if (MinX > point.x) {
			MinX = point.x;
		}
		if (MinY > point.y) {
			MinY = point.y;
		}
		if (MaxX < point.x) {
			MaxX = point.x;
		}
		if (MaxY < point.y) {
			MaxY = point.y;
		}
	}

	/**
	 * 如果原trace沒有start資料，從第一筆補齊資料
	 * 
	 * @throws IOException
	 */
	public void FillStartTime(TrackPoint p, PrintWriter bw) throws IOException {
		int timestamp = startTime;
		while (timestamp < p.elapsedTime) {
			bw.println(p.x + " " + p.y + " " + timestamp);
			timestamp++;
		}
	}

	public void FillEndTime(TrackPoint p, PrintWriter bw) throws IOException {
		int timestamp = p.elapsedTime;
		while (timestamp <= endTime) {
			bw.println(p.x + " " + p.y + " " + timestamp);
			timestamp++;
		}
	}

}
