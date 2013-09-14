package pl.kmetrak.nimdax.agent;

import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import pl.kmetrak.nimdax.agent.annotation.JstatAnnotation;
import sun.jvmstat.monitor.HostIdentifier;
import sun.jvmstat.monitor.Monitor;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.VmIdentifier;

public class NimdaxMonitor {
	public JstatData getData(MonitoredVm vm, JstatData data)
			throws MonitorException {
		Class<? extends JstatData> cls = data.getClass();
		try {
			for (Field f : cls.getDeclaredFields()) {
				if (f.isAnnotationPresent(JstatAnnotation.class)) {
					String key = ((JstatAnnotation) f
							.getAnnotation(JstatAnnotation.class)).value();

					f.setAccessible(true);
					if (f.getType().equals(Long.class)
							|| f.getType().equals(long.class)) {
						f.set(data, getLongMonitorData(vm, key));
					} else if (f.getType().equals(Double.class)
							|| f.getType().equals(double.class)) {
						f.set(data, getDoubleMonitorData(vm, key));
					} else if (f.getType().equals(String.class)) {
						f.set(data, getStringMonitorData(vm, key));
					}

				}
			}
			data.setDataObtained(true);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return data;
	}

	private Long getLongMonitorData(MonitoredVm vm, String key)
			throws MonitorException {
		if (key.contains(",")) {// here we go with RPN
			LinkedList<Long> stack = new LinkedList<>();
			String e[] = key.split(",");
			for (String s : e) {

				s = s.trim();
				if (s.matches("[0-9]+")) {// constant
					stack.push(Long.parseLong(s));
				} else if ("+-/*".contains(s)) {// operator
					long val1 = stack.pop();

					long val2 = stack.pop();
					long val = 0;
					switch (s) {
					case "+":
						val = val2 + val1;
						break;
					case "-":
						val = val2 - val1;
						break;
					case "*":
						val = val2 * val1;
						break;
					case "/":
						val = val2 / val1;
						break;
					}
					stack.push(val);

				} else {// monitor data
					stack.push(getLongMonitorData(vm, s));
				}

			}
			return stack.pop();

		} else {// Plain value
			Monitor m = vm.findByName(key);
			if (m == null) {// simply return value - eg. constant numeric value
				return Long.parseLong(key);
			} else { // return value from specified monitor
				return (Long) m.getValue();
			}
		}
	}

	private Double getDoubleMonitorData(MonitoredVm vm, String key)
			throws MonitorException {
		if (key.contains(",")) {// here we go with RPN
			LinkedList<Double> stack = new LinkedList<>();
			String e[] = key.split(",");
			for (String s : e) {

				s = s.trim();
				if ("+-/*".contains(s)) {// operator
					double val1 = stack.pop();

					double val2 = stack.pop();
					double val = 0;
					switch (s) {
					case "+":
						val = val2 + val1;
						break;
					case "-":
						val = val2 - val1;
						break;
					case "*":
						val = val2 * val1;
						break;
					case "/":
						val = val2 / val1;
						break;
					}
					stack.push(val);

				} else if (s.matches("[0-9.E-]+")) {// constant
					stack.push(Double.parseDouble(s));
				} else {// monitor data
					stack.push(getDoubleMonitorData(vm, s));
				}

			}
			return stack.pop();

		} else {// Plain value
			Monitor m = vm.findByName(key);
			if (m == null) {// simply return value - eg. constant numeric value
				return Double.parseDouble(key);
			} else { // return value from specified monitor
				return Double.parseDouble("" + m.getValue());
			}
		}
	}

	private String getStringMonitorData(MonitoredVm vm, String key)
			throws MonitorException {
		if (key.contains(",")) {// here we go with RPN
			LinkedList<String> stack = new LinkedList<>();
			String e[] = key.split(",");
			for (String s : e) {
				s = s.trim();
				if ("+".contains(s)) {// operator
					String val1 = stack.pop();
					String val2 = stack.pop();
					String val = "";
					switch (s) {
					case "+":
						val = val2 + val1;
						break;
					}
					stack.push(val);

				} else {// monitor data
					stack.push(getStringMonitorData(vm, s));
				}

			}
			return stack.pop();

		} else {// Plain value
			Monitor m = vm.findByName(key);
			if (m == null) {// simply return value - eg. constant numeric value
				return key;
			} else { // return value from specified monitor
				return (String) m.getValue();
			}
		}
	}

	MonitoredHost getMonitoredHost(String hostName) throws URISyntaxException,
			MonitorException {
		HostIdentifier hostId = new HostIdentifier(hostName);
		MonitoredHost monitoredHost = MonitoredHost.getMonitoredHost(hostId);
		return monitoredHost;
	}

	MonitoredHost getMonitoredHost() throws URISyntaxException,
			MonitorException {
		return getMonitoredHost(null);
	}

	MonitoredVm getMonitoredVM(MonitoredHost monitoredHost, int pid)
			throws MonitorException, URISyntaxException {
		Set jvms = monitoredHost.activeVms();
		for (Object o : jvms) {
			int lvmid = ((Integer) o).intValue();
			if (lvmid == pid) {
				String vmidString = "//" + lvmid + "?mode=r";
				VmIdentifier id = new VmIdentifier(vmidString);
				MonitoredVm vm = monitoredHost.getMonitoredVm(id, 0);
				return vm;
			}
		}
		return null;
	}

	Set<Integer> getActiveVms(MonitoredHost monitoredHost)
			throws MonitorException {
		return monitoredHost.activeVms();
	}

	Set<String> getObservableParameters(MonitoredVm monitoredVm)
			throws MonitorException {
		List<Monitor> list = monitoredVm.findByPattern("");
		Set<String> set = new HashSet<>();
		for (Monitor monitor : list) {
			set.add(monitor.getName());
		}
		return set;
	}
	
	private static Field getField(Class clazz, String fieldName) throws NoSuchFieldException {
	    try {
	      return clazz.getDeclaredField(fieldName);
	    } catch (NoSuchFieldException e) {
	      Class superClass = clazz.getSuperclass();
	      if (superClass == null) {
	        throw e;
	      } else {
	        return getField(superClass, fieldName);
	      }
	    }
	  }
}
