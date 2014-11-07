package net.ysuga;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ProcessManager {

	private static ProcessManager instance;

	private Process roscore;
	private ProcessBuilder pb;
	private Thread roscore_redirect_thread;
	private StringBuilder roscore_output;

	public String getRoscoreOutput() {
		return roscore_output.toString();
	}

	public static ProcessManager init() {
		if (instance == null) {
			instance = new ProcessManager();
		}

		return instance;
	}

	private ProcessManager() {

	}

	public Process getRoscore() {
		return roscore;
	}

	class RedirectRoscoreThread extends Thread {
		public void run() {

			try {

				BufferedReader br = new BufferedReader(new InputStreamReader(
						roscore.getInputStream(), "JISAutoDetect" + ""));
				BufferedReader er = new BufferedReader(new InputStreamReader(
						roscore.getErrorStream(), "UTF-8"));

				while (true) {
					if (br.ready()) {
						roscore_output.append(br.readLine() + "\n");
					}

					if (er.ready()) {
						String line = er.readLine();
						// roscore_output.append(line + "\n");
					}

					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						String line = "InterruptedException:" + e.getMessage();
						roscore_output = roscore_output.append(line + "\n");
					}
				}
			} catch (IOException e) {

			}
		}
	}

	public Process launchRoscore() {
		if (roscore == null) {
			roscore_output = new StringBuilder();
			List<String> arg = new ArrayList<String>();
			arg.add("python");
			arg.add("-u");
			arg.add("/opt/ros/indigo/bin/roscore");
			pb = new ProcessBuilder(arg);
			// pb.redirectErrorStream(true);
			try {
				roscore = pb.start();
			} catch (IOException e) {
				return null;
			}

			roscore_redirect_thread = new RedirectRoscoreThread();
			roscore_redirect_thread.start();

		}

		return roscore;
	}

	public static String rosmsg_show(String fullname) {
		StringBuilder output = new StringBuilder();
		List<String> arg = new ArrayList<String>();
		arg.add("python");
		arg.add("-u");
		arg.add("/opt/ros/indigo/bin/rosmsg");
		arg.add("show");
		arg.add(fullname);
		ProcessBuilder pb = new ProcessBuilder(arg);
		// pb.redirectErrorStream(true);
		try {
			Process p = pb.start();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			p.waitFor();
			while (true) {
				String line = br.readLine();
				if (line == null) {
					break;
				} else {
					output.append(line + "\n");
				}

			}

		} catch (IOException e) {
			return null;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output.toString();

	}
}
