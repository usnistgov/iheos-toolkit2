package gov.nist.toolkit.simcommon.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Simulator  implements Serializable, IsSerializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8914156242225793229L;
	List<SimulatorConfig> configs;
	
	public Simulator() {
		configs = new ArrayList<SimulatorConfig>();
	}
	
	public Simulator(List<SimulatorConfig> configs) {
		this.configs = configs;
	}
	
	public Simulator(SimulatorConfig config) {
		configs = new ArrayList<SimulatorConfig>();
		configs.add(config);
	}
	
	public List<SimulatorConfig> getConfigs() {
		return configs;
	}
	
	public int size() { return configs.size(); }
	
	public SimulatorConfig getConfig(int i) { return configs.get(i); }
	
	public List<SimId> getIds() {
		List<SimId> ids = new ArrayList<SimId>();
		for (SimulatorConfig c : configs)
			ids.add(c.id);
		return ids;
	}

	public String toString() {
		StringBuilder buf = new StringBuilder();

		for (SimulatorConfig conf : configs) buf.append(conf.toString()).append('\n');

		return buf.toString();
	}
}
