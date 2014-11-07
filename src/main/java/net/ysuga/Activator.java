package net.ysuga;


import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;



public class Activator implements BundleActivator {
	
	public void start(BundleContext context) {
	}

	public void stop(BundleContext context) {
		ProcessManager pm = ProcessManager.init();
		Process p = pm.getRoscore();
		if(p != null) {
			p.destroy();
		}
	}
	
}
