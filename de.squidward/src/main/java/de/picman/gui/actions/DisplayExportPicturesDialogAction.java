package de.picman.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.SwingWorker;

import de.picman.gui.components.MainFrame;
import de.picman.gui.components.PictureClipboard;
import de.picman.gui.dialogs.ExportPicturesDialog;
import de.picman.gui.main.GUIControl;

public class DisplayExportPicturesDialogAction extends AbstractPicmanAction {

	private static final long serialVersionUID = -7696683913197396172L;

	public DisplayExportPicturesDialogAction() {
		super("Bilder exportieren");
		putValue(Action.SMALL_ICON, GUIControl.get().getIconProvider().getImageIcon("icon.export"));
		putValue(Action.SHORT_DESCRIPTION, "Bilder exportieren");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		 SwingWorker<Object, Void> worker = 
	          new SwingWorker<Object, Void>() {
	          public Object doInBackground() {
				MainFrame.getInstance().lockLight();
				JDialog dialog = new ExportPicturesDialog(PictureClipboard.getInstance().getPictureMap());
				dialog.setLocationRelativeTo(MainFrame.getInstance().getContentPane());
				dialog.setModal(true);	
				dialog.setVisible(true);
				MainFrame.getInstance().unlockLight();
				return null;
	          }
	          public void done() {
					MainFrame.getInstance().unlockLight();
	          }
	       };
	       worker.execute();
	}
	


	
	
	 
}
