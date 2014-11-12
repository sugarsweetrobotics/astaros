package net.ysuga.ros;

public class ROSParam {

	private String type;
	private String name;
	private String defaultValue;
	
	private ROSNode owner;
	
	public ROSParam(String type, String name, String defaultValue) {
		setType(type);
		setName(name);
		setDefaultValue(defaultValue);
	}
	
	public void setOwner(ROSNode node) {
		owner = node;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDefaultValue() {
		if (owner.getStereotype().equals("rospy_node")) {
			if (type.equals("string")) {
				return "\"" + defaultValue + "\"";
			}
		} else if (owner.getStereotype().equals("roscpp_node")) {
			if (type.equals("string")) {
				return "\"" + defaultValue + "\"";
			}			
		}
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
}
