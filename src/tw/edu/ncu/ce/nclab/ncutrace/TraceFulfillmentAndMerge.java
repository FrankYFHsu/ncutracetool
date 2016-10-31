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

import tw.edu.ncu.ce.nclab.ncutrace.data.TWD97;
import tw.edu.ncu.ce.nclab.ncutrace.data.TrackPoint;

public class TraceFulfillmentAndMerge extends TraceArrangement {

	private int startTime = 0;// defalut
	private int endTime = 86400 * 14;// default
	private double MinX = Double.MAX_VALUE;
	private double MinY = Double.MAX_VALUE;
	private double MaxX = 0;
	private double MaxY = 0;

	private TrackPoint minXPoint = new TrackPoint(new TWD97(Double.MAX_VALUE,0),0);
	private TrackPoint minYPoint = new TrackPoint(new TWD97(0,Double.MAX_VALUE),0);
	private TrackPoint maxXPoint = new TrackPoint(new TWD97(0,0),0);
	private TrackPoint maxYPoint = new TrackPoint(new TWD97(0,0),0);
	
	String tracePrefix;

	public TraceFulfillmentAndMerge(String name,File sourceDirectory) {
		tracePrefix = name;
		this.sourceDirectory = sourceDirectory;
	}

	public TraceFulfillmentAndMerge(String name) {
		tracePrefix = name;
		// Do nothing?
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
				point = new TrackPoint(trackPointInfo,
						TrackPoint.LocationType.TWD97);

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
						+ tracePrefix+"trace_" + startTime + "_" + endTime + ".txt"));

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
		/*
		System.out.println("@NCU area");
		System.out.println("MinXPoint = " + minXPoint);
		System.out.println("MinYPoint = " + minYPoint);
		System.out.println("MaxXPoint = " + maxXPoint);
		System.out.println("MaxYPoint = " + maxYPoint);
		 */
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

	private void checkLocateInNCU(TrackPoint point) {
		double x = point.getX();

		double y = point.getY();

		if (270444 >= x && x >= 268444) {

			if (2763238 >= y && y >= 2761238) {

				if (minXPoint.getX() > point.getX()) {
					minXPoint = point;
				}
				if (minYPoint.getY() > point.getY()) {
					minYPoint = point;
				}
				if (maxXPoint.getX() < point.getX()) {
					maxXPoint = point;
				}
				if (maxYPoint.getY() < point.getY()) {
					maxYPoint = point;
				}

			}

		}

	}

	private void checkBoundary(TrackPoint point) {
		checkLocateInNCU(point);
		if (MinX > point.getX()) {
			MinX = point.getX();

		}
		if (MinY > point.getY()) {
			MinY = point.getY();

		}
		if (MaxX < point.getX()) {
			MaxX = point.getX();

		}
		if (MaxY < point.getY()) {
			MaxY = point.getY();

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
			bw.println(p.getX() + " " + p.getY() + " " + timestamp);
			timestamp++;
		}
	}

	public void FillEndTime(TrackPoint p, PrintWriter bw) throws IOException {
		int timestamp = p.elapsedTime;
		while (timestamp <= endTime) {
			bw.println(p.getX() + " " + p.getY() + " " + timestamp);
			timestamp++;
		}
	}

}
