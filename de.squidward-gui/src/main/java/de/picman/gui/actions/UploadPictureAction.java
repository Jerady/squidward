package de.picman.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.SwingWorker;

import de.picman.gui.components.MainFrame;
import de.picman.gui.dialogs.UploadPicturesDialog;
import de.picman.gui.main.GUIControl;

public class UploadPictureAction extends AbstractPicmanAction {

	private static final long serialVersionUID = 2166467094417387244L;

	public UploadPictureAction() {
		super("In die Datenbank laden");
		putValue(Action.SMALL_ICON, GUIControl.get().getIconProvider().getImageIcon("icon.upload"));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		SwingWorker<Object, Void> worker = 
	          new SwingWorker<Object, Void>() {
	          public Object doInBackground() {
				MainFrame.getInstance().lockLight();
				UploadPicturesDialog dialog = new UploadPicturesDialog();
				dialog.setLocationRelativeTo(MainFrame.getInstance().getContentPane());
				dialog.setModal(true);
				dialog.setVisible(true);
				return null;
	          }
	          public void done() {
					MainFrame.getInstance().unlockLight();
	          }
	       };
	       worker.execute();
	}

}
