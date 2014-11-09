package net.ysuga.ros;

public class ROSTopic {
	
	private ROSMsg type;
	
	private String name;
	
	public ROSTopic(String name, ROSMsg type) {
		setName(name);
		setType(type);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ROSMsg getType() {
		return type;
	}

	public void setType(ROSMsg type) {
		this.type = type;
	}

}
