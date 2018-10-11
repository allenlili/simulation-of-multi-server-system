package simulator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import simulator.core.Service;
import simulator.util.ExportUtil;

public class Distribution {
	int N = 1000;
	
	// numOfSubTasks
	double n = 7.0;
	// mean arrival rates
	double lamba = 0.85;
	// uniformly distributed lower
	double lower = 0.05;
	// uniformly distributed upper
	double upper = 0.25;
	// preprocessor service rate mu1 n/10
	double mu = 1 / (n / 10.0);
	// Pareto Distribution arg k
	double k = 2.08;
	// Pareto Distribution arg tm
	double tm = 10.3846;
	int seed = 1;
	int precison = 4;
	
	private Service service = new Service(seed, precison);
	
	@Test
	public double getInterarrivalTime(){
		System.out.println("getInterarrivalTime:");
		int i = 0;
		double sum = 0.0;
		double result = 0.0;
		while(i < N){
			result = service.getIntervalTime(lamba , lower, upper);
			i = i + 1;
			sum = sum + result;
		}
		System.out.println("average:" + String.valueOf(new BigDecimal(sum/i).setScale(precison, BigDecimal.ROUND_HALF_UP).doubleValue()));
		return new BigDecimal(sum/i).setScale(precison, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	@Test
	public double getPreprocessorServiceTime(){
		mu = 1 / (n / 10.0);
		System.out.println("getPreprocessorServiceTime:");
		int i = 0;
		double sum = 0.0;
		double result = 0.0;
		while(i < N){
			result = service.getPreprocessorServiceTime(mu);
			i = i + 1;
			sum = sum + result;
		}
		System.out.println("average:" + String.valueOf(new BigDecimal(sum/i).setScale(precison, BigDecimal.ROUND_HALF_UP).doubleValue()));
		return new BigDecimal(sum/i).setScale(precison, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	@Test
	public double getServerServiceTime(){
		System.out.println("getServerServiceTime");
		int i = 0;
		double sum = 0.0;
		double result = 0.0;
		while(i < N){
			result = service.getServerServiceTime(k, tm, n);
			i = i + 1;
			sum = sum + result;
		}
		System.out.println("average:" + String.valueOf(new BigDecimal(sum/i).setScale(precison, BigDecimal.ROUND_HALF_UP).doubleValue()));
		return new BigDecimal(sum/i).setScale(precison, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	public static void main(String[] args) {
		String[]  args1 = {"interval time", "pre-processor time", "server service time"};
		String[]  args2 = {"n=1","n=2","n=3","n=4","n=5","n=6","n=7","n=8","n=9","n=10"};
		List<String[]> results = new ArrayList<String[]>();
		Distribution distribution;
		String[] s1 = new String[args2.length];
		for(int i=0; i<args2.length; i++){
			distribution = new Distribution();
			distribution.n = i+1;
			s1[i] = String.valueOf(distribution.getInterarrivalTime());
		}
		results.add(s1);
		String[] s2 = new String[args2.length];
		for(int i=0; i<args2.length; i++){
			distribution = new Distribution();
			distribution.n = i+1;
			s2[i] = String.valueOf(distribution.getPreprocessorServiceTime());
		}
		results.add(s2);
		String[] s3 = new String[args2.length];
		for(int i=0; i<args2.length; i++){
			distribution = new Distribution();
			distribution.n = i+1;
			s3[i] = String.valueOf(distribution.getServerServiceTime());
		}
		results.add(s3);
		ExportUtil exportUtil = new ExportUtil();
		exportUtil.setOutputFile("c:/simulator-logs/distribution.xlsx");
		exportUtil.createWorkBook();
		exportUtil.createSheet("distribution test");
		exportUtil.createTitle("distribution test", 0, 0, 0, 10, "data distribution");
		exportUtil.createBody(results,args1, args2);
	}
}