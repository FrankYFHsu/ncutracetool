import java.text.DecimalFormat;
import java.text.NumberFormat;


public class TrackPoint implements Comparable<TrackPoint>{
	
	double x;
	double y;
	int elapsedTime;
	
	public TrackPoint(double x, double y,int time){
		this.x=x;
		this.y=y;
		this.elapsedTime=time;
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
