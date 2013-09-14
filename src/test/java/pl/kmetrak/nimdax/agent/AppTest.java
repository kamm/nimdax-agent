package pl.kmetrak.nimdax.agent;

import junit.framework.TestCase;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
	NimdaxMonitor nm;
	MonitoredHost host;
	MonitoredVm vm;
	JStatTestData data;

	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		nm = new NimdaxMonitor();
		host = nm.getMonitoredHost();
		vm = nm.getMonitoredVM(host, 3824);
		data = new JStatTestData();
		nm.getData(vm, data);

	}

	public void testApp() {
		assertTrue(data.isDataObtained());
		assertEquals("12", data.getStringSum());
		assertEquals(3, data.getLongSum());
		assertEquals(3., data.getDoubleSum());
		assertEquals(.5, data.getDoubleDiv());
	}
	/*
	 * public void testGetMonitorPackages() throws IOException{ InputStream is =
	 * Jstat.class.getClassLoader().getResourceAsStream(
	 * "sun/tools/jstat/resources/jstat_options"); System.out.println(is);
	 * BufferedReader br = new BufferedReader(new InputStreamReader(is)); String
	 * s; String option=null; String columnName=null; String data=null;
	 * StringBuffer sb = new StringBuffer(); boolean column=false;
	 * while((s=br.readLine())!=null){ if(s.startsWith("option")){
	 * option=s.replaceAll("option (.*?) \\{", "$1");
	 * //System.out.println(option); sb.append(option); sb.append("["); }else
	 * if(s.trim().startsWith("column")){ column=true; }else if(column &&
	 * s.trim().startsWith("data")){ data=(s.replaceAll(".*data (.*?)", "$1"));
	 * }else if(column && s.trim().startsWith("header")){
	 * columnName=(s.replaceAll(".*\\\"\\^*(.*?)\\^*\\\".*", "$1"));
	 * 
	 * }else if(column && s.trim().equals("}")){ sb.append(columnName);
	 * sb.append("=>"); sb.append(data); sb.append(","); column=false; }else
	 * if(!column && s.trim().equals("}")){ option=null; sb.append("]");
	 * System.out.println(sb); sb.setLength(0); } }
	 * 
	 * }
	 */
}
