package pl.kmetrak.nimdax.agent.model;

import pl.kmetrak.nimdax.agent.JstatData;
import pl.kmetrak.nimdax.agent.annotation.JstatAnnotation;

public class JstatTimestampData extends JstatData{
	
	@JstatAnnotation("sun.os.hrt.ticks,sun.os.hrt.frequency,/")
	double timestamp;

	public double getTimestamp() {
		return timestamp;
	}

	
	
	@JstatAnnotation("1,2,+")
	String constant;

	public String getConstant() {
		return constant;
	}

	
	
}
