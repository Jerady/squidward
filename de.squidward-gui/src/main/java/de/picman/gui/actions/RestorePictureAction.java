package de.picman.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import de.picman.gui.components.MainFrame;
import de.picman.gui.main.GUIControl;
import de.picman.gui.providers.ActionProvider;
import de.rahn.bilderdb.control.ApplicationControl;
import de.rahn.bilderdb.db.Picture;

public class RestorePictureAction extends AbstractPicmanAction {

	private static final long serialVersionUID = 5893769622814031969L;

	public RestorePictureAction() {
		super("Bild wiederherstellen");
		putValue(Action.SMALL_ICON, GUIControl.get().getIconProvider().getImageIcon("icon.restore"));
	}
	
	public void actionPerformed(ActionEvent e) {
		Picture pic = MainFrame.getInstance().getPictureDisplayPanel().getSelectedPicture();
		if(pic != null){
			pic.setDeleted(false);
			try {
				ApplicationControl.getInstance().getDbController().updatePicture(pic);
			} catch (Exception e1) {
				ApplicationControl.displayErrorToUser(e1);
			}
		}		
		ActionProvider.getSearchDeletedPictureAction().actionPerformed(null);
	}
}


