package de.picman.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import de.picman.gui.main.GUIControl;

public class ExportPictureClipboardAction extends AbstractPicmanAction {

	private static final long serialVersionUID = -8507573027005563538L;

	public ExportPictureClipboardAction() {
		super("Ablage Exportieren");
		putValue(Action.SMALL_ICON, GUIControl.get().getIconProvider().getImageIcon("icon.export"));
	}

	public void actionPerformed(ActionEvent e) {

	
		
	}
	
}


