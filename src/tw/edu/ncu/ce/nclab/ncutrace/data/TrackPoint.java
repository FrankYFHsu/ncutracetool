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
	
	public TrackPoint(){
		x=0;
		y=0;
		elapsedTime=0;
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
