package pl.kmetrak.nimdax.agent.model;

import pl.kmetrak.nimdax.agent.annotation.JstatAnnotation;

public class JstatTimestampData extends JstatData{
	
	@JstatAnnotation("sun.os.hrt.ticks,sun.os.hrt.frequency,/")
	long timestamp;

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	@JstatAnnotation("1000")
	String constant;

	public String getConstant() {
		return constant;
	}

	public void setConstant(String constant) {
		this.constant = constant;
	}
	
	
}
