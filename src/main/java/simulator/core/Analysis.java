package simulator.core;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import simulator.util.ExportUtil;

public class Analysis {
	// m number of servers, 1 < n <= m 
	int mMax = 10;
	// number of replications of n
	int numReplications;
	
	Random random = new Random();
	int seed = 0;
	// range of random number 
	int range = 200;
	// simulation time of each replication
	int T = 50000;
	// scale
	int precison = 4;
	
	//file path
	String filePath = "c:/simulator-logs/";
	
	//file type
	String fileExt = ".xlsx";
	
	//number of captured dataPoints
	int numDataPoints = 30000;
	
	public List<String[]> getRandomSets(String[] xArg, String[] yArg, String fileName){
		random.setSeed(seed);
		List<List<Integer>> randomSets1 = new ArrayList<List<Integer>>();
		List<String[]> randomSets2 = new ArrayList<String[]>();
		int randomNum;
		for (int i=0; i < xArg.length; i++) {
			List<Integer> list1 = new ArrayList<Integer>();
			String[] list2 = new String[yArg.length];
			for(int j=0; j < yArg.length; j++){
				randomNum = random.nextInt(range);
				list1.add(randomNum);
				list2[j] = String.valueOf(randomNum); 
			}
			randomSets1.add(list1);
			randomSets2.add(list2);
		}
		ExportUtil exportUtil = new ExportUtil();
		exportUtil.setOutputFile(filePath+fileName+fileExt);
		String sheetName = fileName;
		String title = fileName;
		exportUtil.createWorkBook();
		exportUtil.createSheet(sheetName);
		exportUtil.createTitle(sheetName, 0, 0, 0, yArg.length, title);
		exportUtil.createBody(randomSets2, xArg, yArg);
		return randomSets2;
	}
	
	public List<String[]> getRandomSetsMeanAnalysis(String[] xArg, String[] yArg){
		String fileName = "ma-random sets";
		return getRandomSets(xArg, yArg, fileName);
	}
	
	public List<String[]> getRandomSetsSimulationTimeAnalysis(String[] xArg, String[] yArg){
		String fileName = "st-random sets";
		return getRandomSets(xArg, yArg, fileName);
	}
	
	public void simulationTimeAnalysis(String[] xArg, String[] yArg){
		List<String[]> randomSets = getRandomSetsSimulationTimeAnalysis(xArg, yArg);
		List<String[]> results = new ArrayList<String[]>();
		String[] eachTime = null;
		Integer[] tList = {1000,5000,10000,50000};
		// assume one of this situations, this can be changed to no more than m
		int n = 7;
		List<Double> list = null;
		Controller controller;
		StandardDeviation standardDeviation = new StandardDeviation();
		for (int i = 0; i < tList.length; i++) {
			Integer integer = tList[i];
			String[] randomSet = randomSets.get(i);
			list = new ArrayList<>();
			eachTime = new String[2];
			for (int k = 0; k < randomSet.length; k++) {
				controller = new Controller();
				controller.T = integer;
				controller.trace = false;
				controller.m = mMax;
				controller.n = n;
				controller.seed = Integer.parseInt(randomSet[k]);
				controller.precision = precison;
				list.add(controller.simulate());
			}
			double[] values = transform(list.toArray(new Double[]{}));
			double mean = new BigDecimal(StatUtils.mean(values)).setScale(precison, BigDecimal.ROUND_HALF_UP).doubleValue();
			double sd = new BigDecimal(standardDeviation.evaluate(values)).setScale(precison, BigDecimal.ROUND_HALF_UP).doubleValue();
			eachTime[0] = String.valueOf(mean);
			eachTime[1] = String.valueOf(sd);
			results.add(eachTime);
		}
		String[] xArg1 = {"T=1000","T=5000","T=10000","T=50000"};
		String[] yArg1 = {"mean","standard deviation"};
		String fileName = "simulation time analysis";
		ExportUtil exportUtil = new ExportUtil();
		exportUtil.setOutputFile(filePath+fileName+fileExt);
		String sheetName = fileName;
		String title = fileName;
		exportUtil.createWorkBook();
		exportUtil.createSheet(sheetName);
		exportUtil.createTitle(sheetName, 0, 0, 0, yArg1.length, title);
		exportUtil.createBody(results, xArg1, yArg1);
	}
	
	public void meanAnalysis(String[] xArg, String[] yArg) throws Exception{
		List<String[]> randomSets = getRandomSetsMeanAnalysis(xArg, yArg);
		Controller controller;
		// mean response time
		List<String[]> results1 = new ArrayList<String[]>();
		String[] replications1;
		// dataPoints
		List<List<String[]>> results2 = new ArrayList<List<String[]>>();
		List<String[]> replications2;
		String[] replication;
		for(int k = 0; k < xArg.length; k++){
			replications1 = new String[yArg.length];
			replications2 = new ArrayList<String[]>();
			for(int i = 0; i < yArg.length; i++){
				controller = new Controller();
				controller.trace = false;
				controller.m = mMax;
				controller.n = k + 1;
				controller.seed = Integer.parseInt(randomSets.get(k)[i]);
				controller.T = T;
				controller.precision = precison;
				replications1[i] = String.valueOf(controller.simulate());
				replication = transformToStringArray(controller.simples.toArray(new Double[]{}));
				replications2.add(replication);
			}
			results1.add(replications1);
			results2.add(replications2);
		}
		
		ExportUtil exportUtil = new ExportUtil();
		
		String fileName = "mean analysis";
		exportUtil.setOutputFile(filePath+fileName+fileExt);
		String sheetName = fileName;
		String title = fileName;
		exportUtil.createWorkBook();
		exportUtil.createSheet(sheetName);
		exportUtil.createTitle(sheetName, 0, 0, 0, yArg.length, title);
		exportUtil.createBody(results1, xArg.length, yArg.length);
		
		exportUtil.createFolder(filePath, "replications");
		exportUtil.export(results2, filePath + "replications" + File.separator, numDataPoints);
	}

	public double[] transform(Double[] values){
		double[] ds = new double[values.length];
		for (int i = 0; i < values.length; i++) {
			ds[i] = values[i];
		}
		return ds;
	}

	public String[] transformToStringArray(Double[] values){
		String[] ds = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			ds[i] = String.valueOf(values[i]);
		}
		return ds;
	}
	
	public void compareSystemsCNR(String[] xArg, String[] yArg, String[] randomSet){
		Controller controller;
		// mean response time
		List<String[]> results = new ArrayList<String[]>();
		String[] means;
		// two system n=7, n=8
		int[] ns = new int[2];
		ns[0] = 7;
		ns[1] = 8;
		for (int i = 0; i < ns.length; i++) {
			means = new String[yArg.length];
			for(int j = 0; j < yArg.length; j++){
				controller = new Controller();
				controller.trace = false;
				controller.m = mMax;
				controller.n = ns[i];
				controller.seed = Integer.parseInt(randomSet[j]);
				controller.T = T;
				controller.precision = precison;
				means[j] = String.valueOf(controller.simulate());
			}
			results.add(means);
		}
		ExportUtil exportUtil = new ExportUtil();
		String fileName = "compare systems CNR";
		exportUtil.setOutputFile(filePath+fileName+fileExt);
		String sheetName = fileName;
		String title = fileName;
		exportUtil.createWorkBook();
		exportUtil.createSheet(sheetName);
		exportUtil.createTitle(sheetName, 0, 0, 0, yArg.length, title);
		exportUtil.createBody(results, xArg, yArg);
	}
	
	public static void main(String[] args) throws Exception {
//		Analysis analysis = new Analysis();
//		String[] xArg = {"T=1000","T=5000","T=10000","T=50000"};
//		String[] yArg = {"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20"};
//		analysis.simulationTimeAnalysis(xArg, yArg);
		
//		String[] xArg1 = {"n=1","n=2","n=3","n=4","n=5","n=6","n=7","n=8","n=9","n=10"};
//		String[] yArg1 = {"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20"};
//		analysis.meanAnalysis(xArg1, yArg1);
		
		//compare two system CNR
//		String[] xArg2 = {"n=7","n=8"};
//		String[] yArg2 = {"n=1","n=2","n=3","n=4","n=5"};
//		String[] yArg2 = {"n=1","n=2","n=3","n=4","n=5","n=6","n=7","n=8","n=9","n=10"};
//		String[] yArg2 = {"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20"};
//		String[] randomSet = {"0","2","98","34","77"};    // randomSet R5
//		String[] randomSet = {"80","4","7","9","10","13","23","66","12","23"};  // randomSet R10
//		String[] randomSet = {"80","4","7","9","10","13","23","66","12","23","22","33","44","55","66","98","31","34","61","23"};    //randomSet R20
//		analysis.compareSystemsCNR(xArg2, yArg2, randomSet);
	}
}
