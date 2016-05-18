package tw.edu.ncu.ce.nclab.ncutrace.data;
import java.text.DecimalFormat;
import java.text.NumberFormat;


public class TrackPoint implements Comparable<TrackPoint>{
	
	public double x;
	public double y;
	public int elapsedTime;
	
	public TrackPoint(double x, double y,int time){
		this.x=x;
		this.y=y;
		this.elapsedTime=time;
	}
	
	public TrackPoint(String trackPointInfo){
		String[] infos = trackPointInfo.split(" ");
		
		this.x=Double.parseDouble(infos[0]);
		this.y=Double.parseDouble(infos[1]);
		this.elapsedTime=Integer.parseInt(infos[2]);
	}
	
	public TrackPoint(){
		x=0;
		y=0;
		elapsedTime=0;
	}
	
	public TrackPoint addElapsedTime(int seconds){
		
		return new TrackPoint(x,y,elapsedTime+seconds);
	}
	
	@Override
	public String toString(){
		NumberFormat nf = new DecimalFormat(".#");
		
		return nf.format(x)+" "+nf.format(y)+" "+elapsedTime;
		
	}
	@Override
	public int compareTo(TrackPoint other) {
		
	        return (this.elapsedTime - other.elapsedTime);
	    
	}


}
