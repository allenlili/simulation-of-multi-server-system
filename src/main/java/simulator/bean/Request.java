package simulator.bean;

import java.util.ArrayList;
import java.util.UUID;

public class Request{
	private String uuid;
	// the arrival time of the k-th customer in the buffer
	private double arrivalTime;
	// the service time of the k-th customer in the buffer
	private double preProcessorServiceTime;
	// sequence
	private int sequence;
	
	private ArrayList<Task> tasks = new ArrayList<Task>();
	
	public Request(double arrivalTime, double serviceTime, int sequence){
		this.arrivalTime = arrivalTime;
		this.preProcessorServiceTime = serviceTime;
		this.setUuid(UUID.randomUUID().toString());
		this.sequence = sequence;
	}

	public double getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(double arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public double getPreProcessorServiceTime() {
		return preProcessorServiceTime;
	}

	public void setPreProcessorServiceTime(double preProcessorServiceTime) {
		this.preProcessorServiceTime = preProcessorServiceTime;
	}
	
	@Override
	public String toString() {
		return  "(" + "Req" + String.valueOf(sequence) + "," + arrivalTime + "," + preProcessorServiceTime + ")";
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public ArrayList<Task> getTasks() {
		return tasks;
	}

	public void setTasks(ArrayList<Task> tasks) {
		this.tasks = tasks;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
}