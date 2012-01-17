package de.picman.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.SwingWorker;

import de.picman.gui.components.MainFrame;
import de.picman.gui.components.PictureClipboard;
import de.picman.gui.dialogs.SendMailDialog;
import de.picman.gui.main.GUIControl;

public class DisplaySendMailDialogAction extends AbstractPicmanAction {

	private static final long serialVersionUID = -4271062508855244462L;

	public DisplaySendMailDialogAction() {
		super("Mail erstellen");
		putValue(Action.SMALL_ICON, GUIControl.get().getIconProvider().getImageIcon("icon.mail"));
		putValue(Action.SHORT_DESCRIPTION, "Mail erstellen");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		 SwingWorker<Object, Void> worker = 
	          new SwingWorker<Object, Void>() {
	          public Object doInBackground() {
				MainFrame.getInstance().lockLight();
				JDialog dialog = new SendMailDialog(PictureClipboard.getInstance().getPictureMap());
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
