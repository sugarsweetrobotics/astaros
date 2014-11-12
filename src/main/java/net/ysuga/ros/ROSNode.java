package net.ysuga.ros;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

public class ROSNode {

	private String name;
	
	private String packageName;
	
	private String executableName;
	
	private List<ROSTopic> outTopics;
	
	private List<ROSTopic> inTopics;
	
	private List<ROSParam> params;
	
	private List<ROSTopic> importTopic;
	
	private String stereotype;
	
	public ROSNode(String packageName, String name, String type) {
		setName(name);
		setPackageName(packageName);
		this.setStereotype(type);
		if(type.equals("rospy_node")){
			setExecutableName(name + ".py");
		} else {
			setExecutableName(name);
		}
		
		outTopics = new ArrayList<ROSTopic>();
		inTopics  = new ArrayList<ROSTopic>();
		params    = new ArrayList<ROSParam>();
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
	
	public List<ROSTopic> getImportTopic() {
		List<ROSTopic> arrayList = new ArrayList<ROSTopic>();
		for (ROSTopic t : inTopics) {
			if(!arrayList.contains(t)) {
				arrayList.add(t);
			}
		}
		
		for (ROSTopic t : outTopics) {
			if(!arrayList.contains(t)) {
				arrayList.add(t);
			}
		}
		
		return arrayList;
	}
	
	public List<ROSParam> getParams() {
		return params;
	}
	
	public void addOutTopic(ROSTopic topic) {
		outTopics.add(topic);
	}

	public void addInTopic(ROSTopic topic) {
		inTopics.add(topic);
	}
	
	public void addParam(ROSParam param) {
		param.setOwner(this);
		params.add(param);
	}

	public void saveToDirectory(File fullpath) throws FileNotFoundException {
		
		if (getStereotype().equals("rospy_node")) {
			saveRospyNode(fullpath);
		} else if (getStereotype().equals("roscpp_node")) {
			saveRoscppNode(fullpath);
		}
	}
	
	private void saveRospyNode(File fullpath) throws FileNotFoundException {
		File src = new File(fullpath, this.getExecutableName());
		
		VelocityEngine ve = new VelocityEngine();
		ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		ve.init();

		VelocityContext context = new VelocityContext();
		
		context.put("node", this);

		
		PrintWriter pw = new PrintWriter(new FileOutputStream(src));		
		Template template = ve.getTemplate("template/package/src/rospy_node.py.vm");
		template.merge(context, pw);
		pw.flush();
		
	}
	
	private void saveRoscppNode(File fullpath) {
		File launch = new File(fullpath, getName() + ".cpp");
	}

	public String getStereotype() {
		return stereotype;
	}

	private void setStereotype(String stereotype) {
		this.stereotype = stereotype;
	}

}
