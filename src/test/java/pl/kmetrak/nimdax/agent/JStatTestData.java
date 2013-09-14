package pl.kmetrak.nimdax.agent;

import pl.kmetrak.nimdax.agent.JstatData;
import pl.kmetrak.nimdax.agent.annotation.JstatAnnotation;

public class JStatTestData extends JstatData {

	@JstatAnnotation("1,2,+")
	String stringSum;

	public String getStringSum() {
		return stringSum;
	}
	
	@JstatAnnotation("1,2,+")
	long longSum;

	public long getLongSum() {
		return longSum;
	}
	
	@JstatAnnotation("1,2,+")
	double doubleSum;

	public double getDoubleSum() {
		return doubleSum;
	}
	
	@JstatAnnotation("1,2,/")
	double doubleDiv;

	public double getDoubleDiv() {
		return doubleDiv;
	}
	
	
}
