package de.picman.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import de.picman.gui.components.PictureClipboard;
import de.picman.gui.main.GUIControl;

public class ClearPictureClipboardAction extends AbstractPicmanAction {

	private static final long serialVersionUID = -7157376265557473641L;

	public ClearPictureClipboardAction() {
		super("Ablage leeren");
		putValue(Action.SMALL_ICON, GUIControl.get().getIconProvider().getImageIcon("icon.trash"));
		putValue(Action.SHORT_DESCRIPTION, "Ablage leeren");

	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		PictureClipboard.getInstance().removeAll();
	}
	


	
	
	 
}
