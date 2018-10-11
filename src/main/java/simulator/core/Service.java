package simulator.core;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Random;
import simulator.bean.Server;
import simulator.distribution.*;

public class Service {
	
	protected int precision;
	
	protected Random rng;
	
	protected ArrayList<Server> servers;
	
	public Service(int seed, int precision){
		rng = new Random();
		rng.setSeed(seed);
		this.precision = precision;
	}
	
	public ArrayList<Integer> getNum(int m, double n){
		ArrayList<Integer> selectedNum = new ArrayList<Integer>();
		int num = 0;
		while(true){
			num = rng.nextInt(m);
			if (selectedNum.isEmpty()) {
				selectedNum.add(num);
				continue;
			}
			boolean flag = false;
			for(int j = 0; j < selectedNum.size(); j++){
				if (num == selectedNum.get(j)) {
					flag = true;
					break;
				}
			}
			if (selectedNum.size() >= n) {
				break;
			}
			if(!flag){
				selectedNum.add(num);
			}
		}
		return selectedNum;
	}
	
	public double getIntervalTime(double lamba, double lower, double upper){
		ExponentialDistribution exponentialDistribution = new ExponentialDistribution(rng, lamba);
		double a1k = exponentialDistribution.sample();
		UniformDistribution uniformDistribution = new UniformDistribution(rng, lower, upper);
		double a2k = uniformDistribution.sample();
		double intervalTime = a1k + a2k;
		BigDecimal bg = new BigDecimal(intervalTime);
        intervalTime = bg.setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue();
		return intervalTime;
	}
	
	public double getPreprocessorServiceTime(double mu){
		ExponentialDistribution exponentialDistribution = new ExponentialDistribution(rng, mu);
		double preprocessorServiceTime = exponentialDistribution.sample();
		BigDecimal bg = new BigDecimal(preprocessorServiceTime);
		preprocessorServiceTime = bg.setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue();
		return preprocessorServiceTime;
	}
	
	public double getServerServiceTime(double k, double tm, double n){
		ParetoDistribution paretoDistribution = new ParetoDistribution(rng, k, tm, n);
		double serverServiceTime = paretoDistribution.simple();
		BigDecimal bg = new BigDecimal(serverServiceTime);
		serverServiceTime = bg.setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue();
		return serverServiceTime;
	}
}





