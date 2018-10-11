package simulator.bean;

import java.util.ArrayList;

public class Preprocessor{
	
	private ArrayList<Request> buffer;
	
	private int status;
	
	private Request currentRequest;
	
	public Preprocessor(){
		
	}
			
	public ArrayList<Request> getBuffer() {
		return buffer;
	}
	
	public void setBuffer(ArrayList<Request> buffer) {
		this.buffer = buffer;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Request getCurrentRequest() {
		return currentRequest;
	}

	public void setCurrentRequest(Request currentRequest) {
		this.currentRequest = currentRequest;
	}
	
	@Override
	public String toString() {
		if (currentRequest == null) {
			return "null";
		}
		return currentRequest.toString();
	}
}
