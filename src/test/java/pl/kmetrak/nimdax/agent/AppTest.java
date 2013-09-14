package pl.kmetrak.nimdax.agent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

import junit.framework.TestCase;
import pl.kmetrak.nimdax.agent.model.JstatData;
import pl.kmetrak.nimdax.agent.model.JstatGCData;
import pl.kmetrak.nimdax.agent.model.JstatTimestampData;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.tools.jstat.Jstat;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
	/**
	 * Rigourous Test :-)
	 */
	public void testApp() {
		assertTrue(true);
		NimdaxMonitor nm = new NimdaxMonitor();
		
		try {
			MonitoredHost host = nm.getMonitoredHost();
			System.out.println(nm.getActiveVms(host));
			MonitoredVm vm = nm.getMonitoredVM(host, 3824);
			JstatTimestampData data = new JstatTimestampData();
			nm.getData(vm, data);
			System.out.println(data.getTimestamp());
			assertEquals("1000",data.getConstant());
			System.out.println(nm.getObservableParameters(vm));
		} catch (URISyntaxException | MonitorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void testGetMonitorPackages() throws IOException{
		InputStream is = Jstat.class.getClassLoader().getResourceAsStream("sun/tools/jstat/resources/jstat_options");
		System.out.println(is);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String s;
		String option=null;
		String columnName=null;
		String data=null;
		StringBuffer sb = new StringBuffer();
		boolean column=false;
		while((s=br.readLine())!=null){
			if(s.startsWith("option")){
				option=s.replaceAll("option (.*?) \\{", "$1");
				//System.out.println(option);
				sb.append(option);
				sb.append("[");
			}else if(s.trim().startsWith("column")){
				column=true;
			}else if(column && s.trim().startsWith("data")){
				data=(s.replaceAll(".*data (.*?)", "$1"));
			}else if(column && s.trim().startsWith("header")){
				columnName=(s.replaceAll(".*\\\"\\^*(.*?)\\^*\\\".*", "$1"));
				
			}else if(column && s.trim().equals("}")){
				sb.append(columnName);
				sb.append("=>");
				sb.append(data);
				sb.append(",");
				column=false;
			}else if(!column && s.trim().equals("}")){
				option=null;
				sb.append("]");
				System.out.println(sb);
				sb.setLength(0);
			}
		}
		
	}
}
