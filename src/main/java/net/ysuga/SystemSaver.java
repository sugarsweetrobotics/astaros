package net.ysuga;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import net.ysuga.ros.ROSLaunch;
import net.ysuga.ros.ROSMsg;
import net.ysuga.ros.ROSNode;
import net.ysuga.ros.ROSNodeInstance;
import net.ysuga.ros.ROSPackage;
import net.ysuga.ros.ROSParam;
import net.ysuga.ros.ROSTopic;

import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IBlock;
import com.change_vision.jude.api.inf.model.IConnector;
import com.change_vision.jude.api.inf.model.IConstraint;
import com.change_vision.jude.api.inf.model.IItemFlow;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IPackage;
import com.change_vision.jude.api.inf.model.IPort;
import com.change_vision.jude.api.inf.model.IValueAttribute;
import com.change_vision.jude.api.inf.model.IValueType;

public class SystemSaver {

	public static void savePackage(IPackage pack, File file) {
		File fullpath = new File(file, pack.getName());
		if (!fullpath.exists()) {
			fullpath.mkdirs();
		}

		ROSPackage p = new ROSPackage(pack.getName(), "0.0.0", "description",
				"user", "user@example.com", "license", "catkin");

		try {
			p.saveToDirectory(fullpath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// For roslaunch
		File launchpath = new File(fullpath, "launch");
		if (!launchpath.exists()) {
			launchpath.mkdirs();
		}
		
		for (INamedElement roslaunch : AstahUtility.getStereoTypedElement(pack,
				"roslaunch")) {
			ROSLaunch launchObj = new ROSLaunch(roslaunch.getName());

			String[] rosnode_stereotypes = {"rospy_node", "roscpp_node"};
			for(String stereotype : rosnode_stereotypes) {
				for (IAttribute part : AstahUtility.getStereoTypedParts((IBlock)roslaunch, stereotype)) {
					ROSNode rosNode = createROSNode(pack, part, stereotype);
					
					File srcpath = new File(fullpath, "src");
					if (!srcpath.exists()) {
						srcpath.mkdirs();
					}
					try {
						rosNode.saveToDirectory(srcpath);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					ROSNodeInstance rosNodeInstance = createROSNodeInstance(
							part, rosNode);
					launchObj.addNodeInstance(rosNodeInstance);
				}
			}

			try {
				launchObj.saveToDirectory(launchpath);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	
	private static ROSNode createROSNode(IPackage pack, IAttribute part, String stereotype) {
		IBlock node = (IBlock)part.getType();
		ROSNode rosNode = new ROSNode(pack.getName(),
				node.getName(), stereotype);

		for(IValueAttribute vp : node.getValueAttributes()) {
			rosNode.addParam(new ROSParam(vp.getType().getFullName("/"), vp.getName(), vp.getInitialValue()));
		}
		
		for(IPort port : node.getPorts()) {
			for(IConnector connector : port.getConnectors()) {
				if(connector instanceof IItemFlow) {
					IValueType v = (IValueType)((IItemFlow)connector).getConveys()[0];
					for(String s : v.getStereotypes()) {
						IValueType topicType = null;
						if (s.equals("rostopic")) {
							topicType = (IValueType)v.getGeneralizations()[0].getSuperType();
						} else {
							if(s.equals("valueType")) {
								continue;
							}
							topicType = v;
						}
						
						if(connector.getPorts()[0].equals(port)) {
							rosNode.addOutTopic(new ROSTopic(port.getName(), new ROSMsg(topicType.getFullName("/"))));
						} else {
							rosNode.addInTopic(new ROSTopic(port.getName(), new ROSMsg(topicType.getFullName("/"))));
						}
						
					}
				}
			}
		}
		return rosNode;
	}

	private static ROSNodeInstance createROSNodeInstance(IAttribute part,
			ROSNode rosNode) {
		ROSNodeInstance rosNodeInstance = new ROSNodeInstance(
				rosNode, part.getName());
		for(IConstraint c : part.getConstraints()){
			String[] vs = c.getName().split("=");
			if(vs.length == 2) {
				rosNodeInstance.addParam(vs[0].trim(), vs[1].trim());
			}
		}
		
		IBlock node = (IBlock)part.getType();
		for(IPort port : node.getPorts()) {
			for(IConnector connector : port.getConnectors()) {
				if(connector instanceof IItemFlow) {
					IValueType v = (IValueType)((IItemFlow)connector).getConveys()[0];
					for(String s : v.getStereotypes()) {
						if (s.equals("rostopic")) {
							rosNodeInstance.addRemap(port.getName(), v.getName());
						}
					}
				}
			}
		}
		return rosNodeInstance;
	}
}
