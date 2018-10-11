package simulator.bean;

import java.util.ArrayList;

public class Server{
	private int serverStatus;
	private ArrayList<Task> buffer = new ArrayList<Task>();
	private Task currentTask;
	
	public int getServerStatus() {
		return serverStatus;
	}
	public void setServerStatus(int serverStatus) {
		this.serverStatus = serverStatus;
	}
	
	public ArrayList<Task> getBuffer() {
		return buffer;
	}
	public void setBuffer(ArrayList<Task> buffer) {
		this.buffer = buffer;
	}
	
	public Task getCurrentTask() {
		return currentTask;
	}
	public void setCurrentTask(Task currentTask) {
		this.currentTask = currentTask;
	}
	
	@Override
	public String toString() {
		if (currentTask == null) {
			return "null";
		}
		return currentTask.toString();
	}
}
