package pl.kmetrak.nimdax.agent;

import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import pl.kmetrak.nimdax.agent.annotation.JstatAnnotation;
import pl.kmetrak.nimdax.agent.model.JstatData;
import sun.jvmstat.monitor.HostIdentifier;
import sun.jvmstat.monitor.Monitor;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.VmIdentifier;

public class NimdaxMonitor {
	public JstatData getData(MonitoredVm vm, JstatData data) throws MonitorException {
		Class<? extends JstatData> cls = data.getClass();
		for (Field f : cls.getDeclaredFields()) {
			if (f.isAnnotationPresent(JstatAnnotation.class)) {
				String key = ((JstatAnnotation)f.getAnnotation(JstatAnnotation.class)).value();
				try {	
					f.setAccessible(true);	
					f.set(data, getMonitorData(vm, key));
					
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
				
			}
		}
		return data;
	}
	
	private Object getMonitorData(MonitoredVm vm, String key) throws MonitorException{
		if(key.contains(",")){//here we go with RPN
			LinkedList<Long> stack = new LinkedList<>();
			String e[] = key.split(",");
			for(String s:e){
				s=s.trim();
				if(s.matches("[0-9]+")){//constant
					stack.push(Long.parseLong(s));
				}else if("+-/*".contains(s)){//operator
					long val1 = stack.pop();
					long val2 = stack.pop();
					long val=0;
					switch(s){
						case "+":val=val2+val1;break;
						case "-":val=val2-val1;break;
						case "*":val=val2*val1;break;
						case "/":val=val2/val1;break;
					}
					stack.push(val);
				
			
				}
				else{//monitor data
					stack.push((Long)getMonitorData(vm, s));
				}
				
			}
			return stack.pop();
			
		}else{//Plain value
			Monitor m = vm.findByName(key);
			if(m==null){//simply return value - eg. constant numeric value
				return key;
			}else{ //return value from specified monitor
				return m.getValue();
			}
		}
	}
	
	MonitoredHost getMonitoredHost(String hostName) throws URISyntaxException, MonitorException{
		HostIdentifier hostId = new HostIdentifier(hostName);
		MonitoredHost monitoredHost = MonitoredHost
				.getMonitoredHost(hostId);
		return monitoredHost;
	}
	
	
	MonitoredHost getMonitoredHost() throws URISyntaxException, MonitorException{
		return getMonitoredHost(null);
	}
	
	MonitoredVm getMonitoredVM(MonitoredHost monitoredHost, int pid) throws MonitorException, URISyntaxException{
		Set jvms = monitoredHost.activeVms();
		for (Object o : jvms) {
			int lvmid = ((Integer) o).intValue();
			if(lvmid==pid){
				String vmidString = "//" + lvmid + "?mode=r";
				VmIdentifier id = new VmIdentifier(vmidString);
				MonitoredVm vm = monitoredHost.getMonitoredVm(id, 0);
				return vm;
			}
		}
		return null;
	}
	
	Set<Integer> getActiveVms(MonitoredHost monitoredHost) throws MonitorException{
		return monitoredHost.activeVms();
	}
	
	Set<String> getObservableParameters(MonitoredVm monitoredVm) throws MonitorException{
		List<Monitor> list = monitoredVm.findByPattern("");
		Set<String> set = new HashSet<>();
		for(Monitor monitor:list){
			set.add(monitor.getName());
		}
		return set;
	}
}

