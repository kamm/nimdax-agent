package pl.kmetrak.nimdax.agent.model;

import pl.kmetrak.nimdax.agent.JstatData;
import pl.kmetrak.nimdax.agent.annotation.JstatAnnotation;

public class JstatGCData extends JstatData {
	@JstatAnnotation("sun.gc.generation.0.space.1.capacity")
	long s0c;
	@JstatAnnotation("sun.gc.generation.0.space.2.capacity")
	long s1c;
	public long getS0c() {
		return s0c;
	}
	public void setS0c(long s0c) {
		this.s0c = s0c;
	}
	public long getS1c() {
		return s1c;
	}
	public void setS1c(long s1c) {
		this.s1c = s1c;
	}
	
	
	
}
