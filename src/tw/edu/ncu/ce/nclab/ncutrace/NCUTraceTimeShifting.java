package tw.edu.ncu.ce.nclab.ncutrace;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Scanner;

public class NCUTraceTimeShifting extends TraceArrangement {
	
	public NCUTraceTimeShifting(){};
	
	public NCUTraceTimeShifting(File sourceDirectory) {
		this.sourceDirectory = sourceDirectory;
	}
	
	public static void main(String[] args) throws IOException, ParseException {
		NCUTraceTimeShifting nts = new NCUTraceTimeShifting();
		nts.startShift(43200);
	}
	
	public void startShift(int time){
		
		checkSourceDirectory("_shift");
		
		File[] files = sourceDirectory.listFiles();
		
		File traceFile =null;
		for(File f:files){
			
			if(f.getName().startsWith("NCUtrace")){
				traceFile=f;
				break;
			}
		}
		
		if(traceFile==null){
			System.out.println("null source");
		}else{
			
			try {
				Scanner sc = new Scanner(traceFile);
				
				File shiftedTraceFile =  new File(
						this.outPutDirectory.getAbsolutePath() + File.separator
						+ "shifted_" +traceFile.getName());
				
				PrintWriter pw = new PrintWriter(shiftedTraceFile);
				
				String firstLine = sc.nextLine();
				//0 820800 0 180220.0 0 241956.0 0 0

				String[] infos = firstLine.split(" ");
				
				int endTime = Integer.parseInt(infos[1]);
				endTime = endTime+time;
				
				pw.println(infos[0]+" "+endTime+" "+infos[2]+" "+infos[3]+" "+infos[4]+" "+infos[5]+" 0 0");
				
				while(sc.hasNextLine()){
					//0 1 111218.9 218298.8
					String tracePoint = sc.nextLine();
					String[] tracePoints = tracePoint.split(" ");
					int traceTime = Integer.parseInt(tracePoints[0]);
					
					traceTime = traceTime+time;
					
					pw.println(traceTime+" "+tracePoints[1]+" "+tracePoints[2]+" "+tracePoints[3]);
				}
				
				pw.flush();
				pw.close();
				sc.close();
				
				
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
	}

}
