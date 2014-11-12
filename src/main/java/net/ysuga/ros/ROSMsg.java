package net.ysuga.ros;

public class ROSMsg {

	private String typename;
	
	private String packageName;
	
	private String name;
	
	public ROSMsg(String typename) {
		setTypename(typename);
	}
	
	public String getTypename() {
		return typename;
	}
	
	public void setTypename(String typename) {
		this.typename = typename;
		
		String[] tokens = typename.split("/");
		packageName = tokens[0].trim();
		name = tokens[1].trim();
	}
	
	public String getPackageName() {
		return packageName;
	}
	
	public String getName() {
		return name;
	}
	
}
