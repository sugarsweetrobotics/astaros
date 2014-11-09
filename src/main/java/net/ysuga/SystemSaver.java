package net.ysuga;

import java.io.File;
import java.io.IOException;

import net.ysuga.ros.ROSLaunch;
import net.ysuga.ros.ROSNode;
import net.ysuga.ros.ROSNodeInstance;
import net.ysuga.ros.ROSPackage;

import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IBlock;
import com.change_vision.jude.api.inf.model.IConnector;
import com.change_vision.jude.api.inf.model.IConstraint;
import com.change_vision.jude.api.inf.model.IItemFlow;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IPackage;
import com.change_vision.jude.api.inf.model.IPort;
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
					
					ROSNode rosNode = new ROSNode(pack.getName(),
							part.getName(), stereotype);
					ROSNodeInstance rosNodeInstance = createROSNodeInstance(
							part, (IBlock)part.getType(), rosNode);
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

	private static ROSNodeInstance createROSNodeInstance(IAttribute a,
			IBlock node, ROSNode rosNode) {
		ROSNodeInstance rosNodeInstance = new ROSNodeInstance(
				rosNode, a.getName());
		for(IConstraint c : a.getConstraints()){
			String[] vs = c.getName().split("=");
			if(vs.length == 2) {
				rosNodeInstance.addParam(vs[0].trim(), vs[1].trim());
			}
		}
		for(IPort port : node.getPorts()) {
			for(IConnector connector : port.getConnectors()) {
				if(connector instanceof IItemFlow) {
					IValueType v = (IValueType)((IItemFlow)connector).getConveys()[0];
					for(String s : v.getStereotypes()) {
						if (s.equals("rostopic")) {
							rosNodeInstance.addRemap(port.getName(), v.getName());
							IValueType topicType = (IValueType)v.getGeneralizations()[0].getSuperType();
							if(connector.getPorts().equals(port)) {
								rosNode.addOutTopic(topicType.getName(), connector.getName());
							} else {
								rosNode.addInTopic(topicType.getName(), connector.getName());
							}
							
						}
					}
				}
			}
		}
		return rosNodeInstance;
	}
}
