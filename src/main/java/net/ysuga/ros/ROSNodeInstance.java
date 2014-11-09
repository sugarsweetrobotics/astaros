package net.ysuga.ros;

import java.util.HashMap;
import java.util.Map;

public class ROSNodeInstance {

	private String name;
	
	private ROSNode node;
	
	private Map<String, String> remaps; 
	
	private Map<String, String> params; 
	
	
	public ROSNodeInstance(ROSNode node, String name) {
		setNode(node);
		setName(name);
		params = new HashMap<String, String>();
		remaps = new HashMap<String, String>();
	}
	
	public Map<String, String> getParams() {
		return params;
	}
	
	public void addParam(String name, String value) {
		params.put(name, value);
	}
	
	
	public Map<String, String> getRemaps() {
		return remaps;
	}
	
	public void addRemap(String from, String to) {
		remaps.put(from, to);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public ROSNode getNode() {
		return node;
	}
	
	public void setNode(ROSNode node) {
		this.node = node;
	}
}
