package th.or.nectec.naparlai;


//import java.util.GregorianCalendar;
//import java.util.SimpleTimeZone;
import android.util.Log;


public class LeafAreaIndexCalculator {
	double LAI;
	double zenithAngleDegree;
	double zenithAngleRadian;
	double avgUpperPAR;
	double avgLowerPAR;
	double K;
	double a;
	double A;
	double X;
	double T;
	double fb;
	double r;
	
	public void setPara(double upper_PAR,double lower_PAR, double zenith_angle_degree,double x){
		
		avgUpperPAR = upper_PAR;
		avgLowerPAR = lower_PAR;
		zenithAngleDegree = zenith_angle_degree; 
		X = x;
		
	}
	
	
	
	public double CalLAI()
	{
		
	    zenithAngleRadian = zenithAngleDegree * (Math.PI/180);
	    a=0.9;
	    A = 0.283+0.785*a-0.159*a*a; 
	    //X = 1;
		K = ((Math.sqrt(Math.pow(X,2)+Math.pow(Math.tan(zenithAngleRadian), 2)))/(X+1.744*Math.pow(X+1.182,-0.733)));
		T = avgLowerPAR/avgUpperPAR;
		
		r = avgUpperPAR/(2550*Math.cos(zenithAngleRadian));
		
		if (r<0.2) {
			r=0.2;
		}else if (r>0.82) {
			r=0.82;
		}
	
		fb = 48.57 + r * (-59.024 + r * 24.835);
		fb = 1.395 + r * (-14.43 + r * fb);
 
		LAI=((1-1/(2*K))*fb-1)*Math.log(T)/(A*(1-0.47*fb));
		
		return (LAI);

	}

	
}


