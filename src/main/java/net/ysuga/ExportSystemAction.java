package net.ysuga;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IModel;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IPackage;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate;
import com.change_vision.jude.api.inf.ui.IWindow;

public class ExportSystemAction implements IPluginActionDelegate {

	public Object run(IWindow window) throws UnExpectedException {
		try {
			AstahAPI api = AstahAPI.getAstahAPI();
			ProjectAccessor projectAccessor = api.getProjectAccessor();
			IModel model = projectAccessor.getProject();
			
			String selectedPackageName = showPackageDialog(window, model);
			
			File file = openSaveFileDialog(window, selectedPackageName);
			if (file != null) {
				IPackage pack = AstahUtility.getPackageFromModel(model, selectedPackageName);
				
				SystemSaver.savePackage(pack, file);
				
			}
			
			

		} catch (ProjectNotFoundException e) {
			String message = "Project is not opened.Please open the project or create new project.";
			JOptionPane.showMessageDialog(window.getParent(), message,
					"Warning", JOptionPane.WARNING_MESSAGE);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(window.getParent(),
					"Unexpected error has occurred.", "Alert",
					JOptionPane.ERROR_MESSAGE);
			throw new UnExpectedException();
		}
		return null;
	}
	
	
	class ButtonAction extends AbstractAction {
		
		private JDialog dialog;

		public ButtonAction(String title, JDialog dialog) {
			super(title);
			this.dialog = dialog;
		}
		@Override
		public void actionPerformed(ActionEvent arg0) {
			dialog.setVisible(false);
		}
		
	}
	
	private String showPackageDialog(IWindow window, IModel model) {
		JDialog dialog = new JDialog(window.getParent(), "Select Package");
		dialog.setLocationRelativeTo(window.getParent());
		dialog.getContentPane().setLayout(new FlowLayout());
		JLabel label = new JLabel("Select ROS Package");
		dialog.getContentPane().add(label);
		Vector<String> alter = new Vector<String>();
		
		for (INamedElement ne : model.getOwnedElements()) {
			if (ne instanceof IPackage) {
				IPackage pack = (IPackage)ne;
				alter.add(pack.getName());
			}
		}
		JComboBox combo = new JComboBox(alter);
		dialog.getContentPane().add(combo);
		JButton button = new JButton(new ButtonAction("OK", dialog));
		dialog.getContentPane().add(button);
		dialog.setSize(300, 100);
		dialog.setModal(true);
		dialog.setVisible(true);
		
		return (String)combo.getSelectedItem();
	}
	

	class SystemFileFilter extends javax.swing.filechooser.FileFilter {
		public boolean accept(java.io.File f) {
			if (f.isDirectory())
				return true;

			return false;
		}

		public java.lang.String getDescription() {
			return "*." + "*";
		}
	}
	
	

		
	public final File openSaveFileDialog(IWindow window, String filename) {
		JFileChooser fileChooser = new JFileChooser(filename);
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setDialogTitle("Select Directory");
		fileChooser.setFileFilter(new SystemFileFilter());
		if (fileChooser.showSaveDialog(window.getParent()) != JFileChooser.APPROVE_OPTION) {
			return null;
		}
		File file = fileChooser.getSelectedFile();
		//if (!file.getName().endsWith(FSM)) {
		//	file = new File(file.getAbsolutePath() + "." + FSM);
		//}

		if (!file.exists()) {
			if (JOptionPane.showConfirmDialog(window.getParent(), "Create Directory?") == JOptionPane.NO_OPTION) {
				return null;
			}
			file.mkdirs();
		}

		return file;
	}

}
