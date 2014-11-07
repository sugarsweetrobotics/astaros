package net.ysuga;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.io.BufferedReader;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectEvent;
import com.change_vision.jude.api.inf.project.ProjectEventListener;
import com.change_vision.jude.api.inf.ui.IPluginExtraTabView;
import com.change_vision.jude.api.inf.ui.ISelectionListener;

public class RoscoreTabView extends JPanel implements IPluginExtraTabView,
		ProjectEventListener {

	private Timer timer;

	private JTextArea textArea;

	private JScrollPane scrollPane;

	private BufferedReader bufferedReader;

	public RoscoreTabView() {
		initComponents();

	}

	private void initTimer() {
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				checkRoscore();
			}

		}, 500, 500);
	}

	private void initComponents() {
		setLayout(new BorderLayout());
		add(createLabelPane(), BorderLayout.CENTER);
		addProjectEventListener();
	}

	private void addProjectEventListener() {
		try {
			AstahAPI api = AstahAPI.getAstahAPI();
			ProjectAccessor projectAccessor = api.getProjectAccessor();
			projectAccessor.addProjectEventListener(this);
		} catch (ClassNotFoundException e) {
			e.getMessage();
		}
	}

	private Container createLabelPane() {
		textArea = new JTextArea("waiting roscore");
		scrollPane = new JScrollPane(textArea);
		return scrollPane;
	}

	private void checkRoscore() {
		ProcessManager pm = ProcessManager.init();
		Process p = pm.getRoscore();
		if (p != null) {

			boolean flag = false;
			JScrollBar sb = scrollPane.getVerticalScrollBar();
			if (sb.getValue() == sb.getMaximum()) {
				flag = true;
			}
			String newText = pm.getRoscoreOutput();
			textArea.setText(newText);

			if (flag) {
				sb.setValue(sb.getMaximum());
			}
		}
	}

	@Override
	public void projectChanged(ProjectEvent e) {
	}

	@Override
	public void projectClosed(ProjectEvent e) {
	}

	@Override
	public void projectOpened(ProjectEvent e) {
	}

	@Override
	public void addSelectionListener(ISelectionListener listener) {
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public String getDescription() {
		return "Show Hello World here";
	}

	@Override
	public String getTitle() {
		return "roscore_process";
	}

	public void activated() {
		initTimer();
	}

	public void deactivated() {
	}
}