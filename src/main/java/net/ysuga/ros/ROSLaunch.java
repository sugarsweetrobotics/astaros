package net.ysuga.ros;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

public class ROSLaunch {
	
	private String name;
	
	private List<ROSNodeInstance>nodeInstances;
	
	public ROSLaunch(String name) {
		setName(name);
		nodeInstances = new ArrayList<ROSNodeInstance>();
	}

	public List<ROSNodeInstance> getNodeInstances() {
		return nodeInstances;
	}
	
	public void addNodeInstance(ROSNodeInstance node) {
		nodeInstances.add(node);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void saveToDirectory(File fullpath) throws IOException {
		File launch = new File(fullpath, getName() + ".launch");
		
		VelocityEngine ve = new VelocityEngine();
		ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		
		ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		ve.init();

		VelocityContext context = new VelocityContext();
		
		context.put("roslaunch", this);

		PrintWriter pw = new PrintWriter(new FileOutputStream(launch));		
		Template template = ve.getTemplate("template/package/launch/roslaunch.launch.vm");
		template.merge(context, pw);
		pw.flush();
	}
}
