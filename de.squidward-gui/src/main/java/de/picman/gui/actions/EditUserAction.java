package de.picman.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.SwingWorker;

import de.picman.gui.components.MainFrame;
import de.picman.gui.dialogs.UserEditorDialog;
import de.picman.gui.main.GUIControl;

public class EditUserAction extends AbstractPicmanAction {

	private static final long serialVersionUID = 8842800905627347617L;

	public EditUserAction() {
		super("Benutzer");
		putValue(Action.SMALL_ICON, GUIControl.get().getIconProvider().getImageIcon("icon.users"));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		 SwingWorker<Object, Void> worker = 
	          new SwingWorker<Object, Void>() {
	          
			 public Object doInBackground() {
					MainFrame.getInstance().lockLight();

					UserEditorDialog dialog = new UserEditorDialog();
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
