/*
 * Created on 05.07.2008
 */
package de.picman.backend.ui.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import de.picman.backend.control.ApplicationControl;
import de.picman.backend.ui.PreviewPane;

/**
 * 
 * @author Ole Rahn
 * 
 */
public class ChooseSourceDirectoryAction extends AbstractAction {

	private static final long serialVersionUID = 3449895621512785299L;

	private static final FileFilter fileFilter = new FileFilter() {

		@Override
		public boolean accept(File f) {
			if (f.isDirectory())
				return true;
			if (f.getAbsolutePath().toLowerCase().endsWith(".png"))
				return true;
			if (f.getAbsolutePath().toLowerCase().endsWith(".gif"))
				return true;
			if (f.getAbsolutePath().toLowerCase().endsWith(".jpg"))
				return true;
			if (f.getAbsolutePath().toLowerCase().endsWith(".bmp"))
				return true;
			if (f.getAbsolutePath().toLowerCase().endsWith(".tif"))
				return true;
			return false;
		}

		@Override
		public String getDescription() {
			return "Bilder und Verzeichnisse";
		}

	};

	public ChooseSourceDirectoryAction(Component parent) {
		super("Quell-Verzeichnis auswhlen");
		this.parent = parent;
	}

	protected Component parent = null;

	public void actionPerformed(ActionEvent arg0) {
		final JFileChooser chooser = new JFileChooser();

		PreviewPane previewPane = new PreviewPane();
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		chooser.setMultiSelectionEnabled(true);
		chooser.setAccessory(previewPane);
		chooser.addPropertyChangeListener(previewPane);
		chooser.setApproveButtonText("OK");
		chooser.setFileFilter(fileFilter);
		if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
			File[] files = chooser.getSelectedFiles();
			System.out.println("user clicked OK, " + files.length
					+ " selected ...");
			processFileArray(files);
		}
		// chooser.showDialog(BilderDbMainFrame.getInstance(),
		// "Bitte whlen Sie ein Quell-Verzeichnis aus.");

	}

	@SuppressWarnings("deprecation")
	protected void processFileArray(File[] files) {
		File file;
		for (int i = 0; i < files.length; i++) {
			file = files[i];
			if (file.canRead() && !file.isDirectory()) {
				try {
					System.out.print("processing file: "
							+ file.getAbsolutePath() + " ...");
					ApplicationControl
							.getInstance()
							.getDbController()
							.uploadImage(file, "ein hart codiertes Beispiel", 1);
					System.out.println("... done!");
				} catch (Exception e) {
					ApplicationControl.displayErrorToUser(e);
				}
			} else if (file.canRead() && file.isDirectory()) {
				// File[] newFiles = file.listFiles(fileFilter);
				// processFileArray(newFiles);
			}
		}
	}

}
