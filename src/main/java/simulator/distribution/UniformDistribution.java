package simulator.distribution;

import java.util.Random;

public class UniformDistribution {
	private Random rng;
	
	private double lower;
	
	private double upper;

	public UniformDistribution(Random rng, double lower, double upper){
		this.rng = rng;
		this.lower = lower;
		this.upper = upper;
	}

	// x = y * upper + (1 - y) * lower, y uniformly distributed in (0,1)
	public double sample() {
		double y = this.rng.nextDouble();
		double x = y * upper + (1 - y) * lower;
		return x;
	}
}
