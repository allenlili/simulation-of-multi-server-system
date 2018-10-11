package simulator.core;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import simulator.bean.Preprocessor;
import simulator.bean.Request;
import simulator.bean.Server;
import simulator.bean.Task;

public class Controller {
//	private static Logger logger = Logger.getLogger(Controller.class);
	public boolean trace = true;
	
	/*
	 * ************************
	 * Simulation parameters
	 * ************************
	 */
	// arrival rate
	double lamba = 0.85;
	// uniformly distributed lower
	double lower = 0.05;
	// uniformly distributed upper
	double upper = 0.25;
	// preprocessor service rate mu 1/(n/10)
	double mu;
	// Pareto Distribution argument k
	double k = 2.08;
	// Pareto Distribution argument tm
	double tm = 10.3846;
	// request sequence
	int sequence = 0;
	
	// m servers
	int m;
	// number of subtasks
	double n;
	// simulation time
	double T;
	// scale of data
	int precision;
	// seed
	int seed;
	
	List<Double> simples = new ArrayList<Double>();
	
	/*
	 * ************************
	 * Accounting parameters
	 * ************************
	 */
	// help controller
	Service service;
	// cumulative response time
	double responseTime = 0.0;
	// number of completed customers at the end of the simulation
	int completedRequests = 0;
	// advanced time 
	double masterClock = 0.0;
	// event
	int nextEventType = 0;
	double nextEventTime = 0.0;	
	// nextEventType: arrival request at preProcessor
	double arrivalTimeNextRequest;
	double serviceTimeNextRequest;
	Request newRequest;
	Request currentRequest;
	// nextEventType: request departure from preprocessor
	double departureTimeNextRequest = Double.MAX_VALUE;
	// nextEventType: task departure from server
	double departureTimeNextFirstTask = Double.MAX_VALUE;
	double departureTimeNextTask = Double.MAX_VALUE;
	Task departureFirstTask;
	// create instance of preprocessor
	Preprocessor preprocessor = new Preprocessor();
	// create instance of server list
	ArrayList<Server> servers = new ArrayList<Server>();
	// create instances of request list
	ArrayList<Request> requests = new ArrayList<Request>();
	
	public void initialServerGroup(ArrayList<Server> servers, int m){
		Server server = null;
		for(int i=0; i < m; i++){
			server = new Server();
			servers.add(server);
		}
	}

	// event advance design
	public double simulate(){
		// Initializing parameters 
		mu = 1 / (n / 10.0);
		// compare systems
//		mu = 1 / (7.0 / 10.0);
		service = new Service(seed, precision);
		preprocessor.setStatus(0);
		preprocessor.setBuffer(new ArrayList<Request>());
		initialServerGroup(servers, m);
		arrivalTimeNextRequest = service.getIntervalTime(lamba, lower, upper);
		serviceTimeNextRequest = service.getPreprocessorServiceTime(mu);
		// Start iteration until the end time
		while(masterClock < T){
			// check event type
			getMinTimeEvent();
		    // update master clock
		    masterClock = nextEventTime;
		    // take actions depending on the event type
			if(nextEventType == 0){ 
				// an arrival 
				getArrivalRequest(); 
				// generate a new job and schedule its arrival 
				getArrivalTimeNextRequest(); 
			}
			if(nextEventType == 1){
				// preProcessor processes a request
				processRequest();
				// get the most recent departure time of request
		        getDepartureTimeNextRequest();
			}
			// first task departure
			if(nextEventType == 2){
				// finish the first task and let it leave
				processTask();
				// check if it is the last finished task in a request
				// yes, join and calculate the response time
				checkLastTask();
			}
			// get the most recent task from all servers
			getDepartureTimeFirstTask();
		}
		// calculate result
		double result = new BigDecimal(responseTime/completedRequests).setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue();
//		String info = 
//				"#servers m=" + String.valueOf(m) + ", " + "#tasks n=" + String.valueOf(n) + ", " + 
//				"seed=" + String.valueOf(seed) + ", " + "precision=" + String.valueOf(precision) + ", " +
//				"mean response time = " + String.valueOf(result) + " .";
//		logger.info(info);
		return result;
	}

	private void getMinTimeEvent(){
		// nextEventType = 0, arrival event at preprocessor
	    if(arrivalTimeNextRequest < departureTimeNextRequest && arrivalTimeNextRequest < departureTimeNextFirstTask ){
	        nextEventTime = arrivalTimeNextRequest;
	        nextEventType = 0;
	    }
	    // nextEventType = 1, request departure from preprocessor
	    if(departureTimeNextRequest <= arrivalTimeNextRequest && departureTimeNextRequest < departureTimeNextFirstTask ){
	    	nextEventTime = departureTimeNextRequest;
		    nextEventType = 1;
		}
	    // nextEventType = 2, task departure from server 
	    if(departureTimeNextFirstTask <= arrivalTimeNextRequest && departureTimeNextFirstTask <= departureTimeNextRequest){
	    	nextEventTime = departureTimeNextFirstTask;
	    	nextEventType = 2;
		}
	}

	private void getArrivalRequest() {
		if (preprocessor.getStatus() == 1){
			newRequest = new Request(arrivalTimeNextRequest, serviceTimeNextRequest, ++sequence);
			preprocessor.getBuffer().add(newRequest);
			trace("System Time " + masterClock + ": " + "New Request gets into preprocessor buffer: " + newRequest.toString() + ".", trace);
		}else{
			departureTimeNextRequest = new BigDecimal(arrivalTimeNextRequest + serviceTimeNextRequest)
									  .setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue();
			newRequest = new Request(arrivalTimeNextRequest, serviceTimeNextRequest, ++sequence);
			currentRequest = newRequest;
		    preprocessor.setStatus(1);
		    preprocessor.setCurrentRequest(currentRequest);
		    trace("System Time " + masterClock + ": " + "New Request " + newRequest.toString() + " arrives at preprocessor" + ".", trace);
		}
	}

	private void getArrivalTimeNextRequest() {
		arrivalTimeNextRequest = new BigDecimal(masterClock + service.getIntervalTime(lamba, lower, upper))
								.setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue();
		serviceTimeNextRequest = service.getPreprocessorServiceTime(mu);
	}

	private void processRequest() {
		breakRequest(currentRequest,m, n);
		// this method contains logger
		sendTask(masterClock, currentRequest, servers, masterClock, n, k, tm);
		requests.add(currentRequest);
		trace("System Time " + masterClock + ": " + "Preprocessor buffer has processed " + newRequest.toString() + ".", trace);
	}

	public void breakRequest(Request currentRequest, int m, double n){
		Task task = null;
		ArrayList<Integer> selectedNum = service.getNum(m, n);
		for (int i = 0; i < n; i++) {
			task = new Task();
			task.setSequence(i+1);
			task.setParentId(currentRequest.getUuid());
			task.setParentSeq(currentRequest.getSequence());
			task.setServerId(selectedNum.get(i));
			currentRequest.getTasks().add(task);
		}
	}

	public void sendTask(double masterClock, Request currentRequest, ArrayList<Server> servers, double arrivalTimeNextTask, double n, double k, double tm) {
		for (int i = 0; i < currentRequest.getTasks().size(); i++) {
			currentRequest.getTasks().get(i).setTaskArrivalTime(arrivalTimeNextTask);
			currentRequest.getTasks().get(i).setTaskServiceTime(service.getServerServiceTime(k, tm, n));
			// compare two systems, so make n fixed 
//			currentRequest.getTasks().get(i).setTaskServiceTime(service.getServerServiceTime(k, tm, 7));
			if(servers.get(currentRequest.getTasks().get(i).getServerId()).getServerStatus() == 0){
				servers.get(currentRequest.getTasks().get(i).getServerId()).setCurrentTask(currentRequest.getTasks().get(i));
				servers.get(currentRequest.getTasks().get(i).getServerId()).setServerStatus(1);
			}else{
				servers.get(currentRequest.getTasks().get(i).getServerId()).getBuffer().add(currentRequest.getTasks().get(i));
			}
			trace("System Time " + masterClock + ": " + currentRequest.getTasks().get(i).toString() + " has arrived at server" +  currentRequest.getTasks().get(i).getServerId() + "." ,trace);
		}
	}

	private void getDepartureTimeNextRequest() {
		// buffer not empty
		if(!preprocessor.getBuffer().isEmpty()){
		    // schedule the next departure event using the first request 
			departureTimeNextRequest = new BigDecimal(masterClock + preprocessor.getBuffer().get(0).getPreProcessorServiceTime())
										.setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue();
		    // remove request from buffer
			currentRequest = preprocessor.getBuffer().remove(0);
			preprocessor.setCurrentRequest(currentRequest);
			preprocessor.setStatus(1);
		// buffer empty
		}else{ 
			departureTimeNextRequest = Double.MAX_VALUE;
			preprocessor.setStatus(0);
			currentRequest = null;
			preprocessor.setCurrentRequest(null);
		}
	}

	private void processTask() {
		// find corresponding server of current task
		int serverId = departureFirstTask.getServerId();
		Server server = servers.get(serverId);
		departureFirstTask.setFinished(true);
		// server buffer not empty 
		if (!server.getBuffer().isEmpty()) {
			server.setCurrentTask(server.getBuffer().remove(0));
			server.setServerStatus(1);
		// server buffer empty
		}else{
			server.setCurrentTask(null);
			server.setServerStatus(0);
		}
		departureTimeNextFirstTask = Double.MAX_VALUE;
		trace("System Time " + masterClock + ": " + departureFirstTask.toString() + " has left " + "server" + serverId + " for join point" +".", trace);
		trace("System Time " + masterClock + ": " + departureFirstTask.toString() + " has arrived at" + " join point" +".", trace);
	}

	private void checkLastTask() {
		// check if all tasks of this request are finished 
		boolean lastTask = false;
		Request request = null;
		for(int i = 0; i < requests.size(); i++){
			request = requests.get(i);
			if(request.getUuid() == departureFirstTask.getParentId()){
				int count = 0;
				for (int j = 0; j < request.getTasks().size(); j++) {
					Task task = request.getTasks().get(j);
					if(task.isFinished()){
						count += 1;
					}
				}
				if (count == request.getTasks().size()) {
					lastTask = true;
				}
				break;
			}
		}
		// if this task is the last finished one of the request, join and accumulate the response time
		if (lastTask) {
			trace("System Time " + masterClock + ": " + 
						"Join point has performed assembling of all subtasks from request " +
						request.toString()  + ".", trace);
			simples.add(masterClock - request.getArrivalTime());
			responseTime = new BigDecimal(responseTime + masterClock - request.getArrivalTime()).setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue();
			completedRequests = completedRequests + 1;
			requests.remove(request);
			trace("System Time " + masterClock + ": " + request.toString() + " leaves " + "system.", trace);
		}
		departureFirstTask = null;
	}

	private void getDepartureTimeFirstTask() {
		for(int i = 0; i < servers.size(); i++){
			Task currentTask = servers.get(i).getCurrentTask();
			if (currentTask == null) {
				continue;
			}else{
				if(currentTask.isTaskCheckedDepartureTime() == false){
					departureTimeNextTask = new BigDecimal(masterClock + currentTask.getTaskServiceTime())
											.setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue();
					currentTask.setTaskDepartureTime(departureTimeNextTask);
					currentTask.setTaskCheckedDepartureTime(true);
				}else{
					departureTimeNextTask = currentTask.getTaskDepartureTime();
				}
				if (departureTimeNextTask < departureTimeNextFirstTask) {
					departureTimeNextFirstTask = departureTimeNextTask;
					departureFirstTask = currentTask;
				}
			}
		}
	}
	
	public void trace(String content, boolean onoff){
	    Logger logger = Logger.getLogger(Controller.class);
		if(onoff){
			logger.trace(content);
		}
	}
}
