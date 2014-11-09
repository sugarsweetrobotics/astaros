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

public class ROSPackage {

	private String name;
	private String version;
	private String description;
	private String maintainer_name;
	private String maintainer_email;
	private String license;
	private String buildtool_depend;

	private List<String> dependencies;
	
	public void addDependency(String name) {
		dependencies.add(name);
	}
	
	public List<String> getDependencies() {
		return dependencies;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMaintainer_name() {
		return maintainer_name;
	}

	public void setMaintainer_name(String maintainer_name) {
		this.maintainer_name = maintainer_name;
	}

	public String getMaintainer_email() {
		return maintainer_email;
	}

	public void setMaintainer_email(String maintainer_email) {
		this.maintainer_email = maintainer_email;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public String getBuildtool_depend() {
		return buildtool_depend;
	}

	public void setBuildtool_depend(String buildtool_depend) {
		this.buildtool_depend = buildtool_depend;
	}
	
	public ROSPackage(String name, String version, String description,
			String maintainer_name, String maintainer_email, String license,
			String buildtool_depend) {
		this.setName(name);
		this.setVersion(version);
		this.setDescription(description);
		this.setMaintainer_name(maintainer_name);
		this.setMaintainer_email(maintainer_email);
		this.setLicense(license);
		this.setBuildtool_depend(buildtool_depend);
		this.dependencies = new ArrayList<String>();
	}

	public void saveToDirectory(File fullpath) throws IOException {
		File xmlPackage = new File(fullpath, "package.xml");
		File cmakeLists = new File(fullpath, "CMakeLists.txt");
		
		VelocityEngine ve = new VelocityEngine();
		ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		
		ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		ve.init();

		VelocityContext context = new VelocityContext();
		
		context.put("package", this);

		PrintWriter xmlpw = new PrintWriter(new FileOutputStream(xmlPackage));		
		Template xmltemplate = ve.getTemplate("template/package/package.xml.vm");
		xmltemplate.merge(context, xmlpw);
		xmlpw.flush();

		PrintWriter cmakepw = new PrintWriter(new FileOutputStream(cmakeLists));		
		Template cmaketemplate = ve.getTemplate("template/package/CMakeLists.txt.vm");
		cmaketemplate.merge(context, cmakepw);
		cmakepw.flush();
	
	}
	

	/*
	 * public void saveToFile(File file) throws IOException {
	 * 
	 * InputStream is = getClass().getResourceAsStream("package.xml");
	 * BufferedReader br = new BufferedReader( new
	 * InputStreamReader(getClass().getResourceAsStream("package.xml")));
	 * 
	 * PrintWriter pw = new PrintWriter(new FileOutputStream(file));
	 * 
	 * while(true) { String line = br.readLine(); if(line == null) { break; }
	 * pw.println(line); }
	 * 
	 * pw.close();
	 * 
	 * }
	 */
	/*
	 * private void addChildTextElement(Document doc, Element root, String name,
	 * String value) { Element n = doc.createElement(name); root.appendChild(n);
	 * Node nm = doc.createTextNode(value); n.appendChild(nm); }
	 * 
	 * private void addChildTextElementWithAttribute(Document doc, Element root,
	 * String name, String value, String attrName, String attrValue) { Element n
	 * = doc.createElement(name); root.appendChild(n); n.setAttribute(attrName,
	 * attrValue); Node nm = doc.createTextNode(value); n.appendChild(nm); }
	 * 
	 * public void saveToFile(File file) { DocumentBuilderFactory factory =
	 * DocumentBuilderFactory.newInstance(); DocumentBuilder builder; try {
	 * builder = factory.newDocumentBuilder(); Document doc =
	 * builder.newDocument(); factory.setValidating(true);
	 * 
	 * Element root = doc.createElement("package"); doc.appendChild(root);
	 * 
	 * addChildTextElement(doc, root, "name", name); addChildTextElement(doc,
	 * root, "version", version); addChildTextElement(doc, root, "description",
	 * description); addChildTextElementWithAttribute(doc, root, "maintainer",
	 * maintainer_name, "email", maintainer_email); addChildTextElement(doc,
	 * root, "license", license); addChildTextElement(doc, root,
	 * "buildtool_depend", buildtool_depend);
	 * 
	 * TransformerFactory tff = TransformerFactory.newInstance(); Transformer tf
	 * = tff.newTransformer(); tf.setOutputProperty(OutputKeys.ENCODING,
	 * "utf-8"); tf.setOutputProperty(OutputKeys.INDENT, "yes");
	 * 
	 * FileOutputStream fo = new FileOutputStream(file); StreamResult result =
	 * new StreamResult(fo);
	 * 
	 * tf.transform(new DOMSource(root), result);
	 * 
	 * } catch (ParserConfigurationException e) { // TODO Auto-generated catch
	 * block e.printStackTrace(); } catch (TransformerConfigurationException e)
	 * { // TODO Auto-generated catch block e.printStackTrace(); } catch
	 * (FileNotFoundException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } catch (TransformerException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); }
	 * 
	 * }
	 */


}
