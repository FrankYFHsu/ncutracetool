package tw.edu.ncu.ce.nclab.ncutrace.data;

public class TWD97 {
	private double mX;
	private double mY;
	
	public TWD97(double x, double y){
		mX=x;
		mY=y;
	}
	
	public double getX(){
		return mX;
	}
	public double getY(){
		return mY;
	}
	
	@Override
	public String toString(){
		return mY+","+mX;
	}

}
