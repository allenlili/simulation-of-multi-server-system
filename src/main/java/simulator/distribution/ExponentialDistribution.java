package simulator.distribution;

import java.util.Random;

public class ExponentialDistribution {
	private Random rng;
	
	private double rate;

	public ExponentialDistribution(Random rng, double rate){
		this.rng = rng;
		this.rate = rate;
	}

	// x = -log(1-y)/rate, y which is uniformly distributed in (0,1).
	public double sample() {
		double y = this.rng.nextDouble();
		double x = -Math.log(1-y) / this.rate;
		return x;
	}
}
