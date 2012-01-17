package de.picman.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.SwingWorker;

import de.picman.gui.api.PrefsKeys;
import de.picman.gui.components.MainFrame;
import de.picman.gui.dialogs.AboutDialog;
import de.picman.gui.main.GUIControl;

public class DisplayAboutDialogAction extends AbstractPicmanAction {

	private static final long serialVersionUID = -5300114822077255904L;

	public DisplayAboutDialogAction() {
		super("†ber " + GUIControl.get().getProperty(PrefsKeys.GUI_APPNAME));
		putValue(Action.SMALL_ICON, GUIControl.get().getIconProvider().getImageIcon("icon.about"));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		SwingWorker<Object, Void> worker = 
	          new SwingWorker<Object, Void>() {
				public Object doInBackground() {
					MainFrame.getInstance().lockLight();
					AboutDialog dialog = new AboutDialog();
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
