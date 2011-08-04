package de.picman.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import de.picman.gui.main.GUIControl;
import de.rahn.bilderdb.control.ApplicationControl;

public class StartGarbageCollectorAction extends AbstractPicmanAction {
	
	private static final long serialVersionUID = -291946601971918763L;

	public StartGarbageCollectorAction() {
		super("Speicher Aufräumen");
		putValue(Action.SMALL_ICON, GUIControl.get().getIconProvider().getImageIcon("icon.restore"));

	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		try {
			ApplicationControl.getInstance().clearPictures();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
