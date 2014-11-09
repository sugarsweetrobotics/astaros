package net.ysuga.ros;

import java.util.ArrayList;
import java.util.List;

public class ROSNode {

	private String name;
	
	private String packageName;
	
	private String executableName;
	
	private List<ROSTopic> outTopics;
	
	private List<ROSTopic> inTopics;
	
	public ROSNode(String packageName, String name, String type) {
		setName(name);
		setPackageName(packageName);
		if(type.equals("rospy_node")){
			setExecutableName(name + ".py");
		} else {
			setExecutableName(name);
		}
		
		outTopics = new ArrayList<ROSTopic>();
		inTopics  = new ArrayList<ROSTopic>();
	}

	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getPackageName() {
		return packageName;
	}


	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getExecutableName() {
		return executableName;
	}

	public void setExecutableName(String executableName) {
		this.executableName = executableName;
	}

	public List<ROSTopic> getInTopics() {
		return inTopics;
	}
	
	public List<ROSTopic> getOutTopics() {
		return outTopics;
	}
	
	public void addOutTopic(String name2, String name3) {
		// TODO Auto-generated method stub
		
	}

	public void addInTopic(String name2, String name3) {
		// TODO Auto-generated method stub
		
	}
}
