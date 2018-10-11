package simulator.bean;

public class Task{
	private String parentId;
	private int parentSeq;
	private int sequence;
	private int serverId;
	private double taskArrivalTime;
	private double taskServiceTime;
	private double taskDepartureTime;
	private boolean isFinished;
	private boolean taskCheckedDepartureTime;
	
	@Override
	public String toString() {
		return  "(" + "Task" + String.valueOf(sequence) + " of Req" + String.valueOf(parentSeq) + "," + taskArrivalTime + "," + taskServiceTime + "," + isFinished + ")";
	}
	
	public Task() {
		this.taskCheckedDepartureTime = false;
	}
	
	public String getParentId() {
		return parentId;
	}
	
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	
	public int getServerId() {
		return serverId;
	}
	
	public void setServerId(int serverId) {
		this.serverId = serverId;
	}
	
	public double getTaskArrivalTime() {
		return taskArrivalTime;
	}
	
	public void setTaskArrivalTime(double taskArrivalTime) {
		this.taskArrivalTime = taskArrivalTime;
	}
	
	public double getTaskServiceTime() {
		return taskServiceTime;
	}
	
	public void setTaskServiceTime(double taskServiceTime) {
		this.taskServiceTime = taskServiceTime;
	}
	
	public double getTaskDepartureTime() {
		return taskDepartureTime;
	}
	
	public void setTaskDepartureTime(double taskDepartureTime) {
		this.taskDepartureTime = taskDepartureTime;
	}
	
	public boolean isFinished() {
		return isFinished;
	}
	
	public void setFinished(boolean isFinished) {
		this.isFinished = isFinished;
	}
	
	public boolean isTaskCheckedDepartureTime() {
		return taskCheckedDepartureTime;
	}
	
	public void setTaskCheckedDepartureTime(boolean taskCheckedDepartureTime) {
		this.taskCheckedDepartureTime = taskCheckedDepartureTime;
	}

	public int getParentSeq() {
		return parentSeq;
	}

	public void setParentSeq(int parentSeq) {
		this.parentSeq = parentSeq;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
}