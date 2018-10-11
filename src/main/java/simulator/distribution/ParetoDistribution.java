package simulator.distribution;

import java.util.Random;

public class ParetoDistribution {
	private Random rng;
	
	private double k;
	
	private double tm;
	
	private double n;
	
	public ParetoDistribution(Random rng, double k, double tm, double n){
		this.rng = rng;
		this.k = k;
		this.tm = tm;
		this.n = n;
	}
	
	public double simple(){
		double lower = tm / Math.pow(n, 1.65);
		double y = this.rng.nextDouble();
		double x;
		while(true){
			x = Math.pow( (k * Math.pow(tm, k)) / (y * Math.pow(n, 1.65*k)), 1/(k+1)) ;
			if (x >= lower) {
				break;
			}else{
				y = this.rng.nextDouble();
				continue;
			}
		}
		return x;
	}
}
