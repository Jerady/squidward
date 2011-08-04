package de.picman.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JOptionPane;

import de.picman.gui.main.GUIControl;

public class ExitAction extends AbstractPicmanAction {
	
	private static final long serialVersionUID = -4566586575755554364L;

	public ExitAction() {
		super("Beenden");
		putValue(Action.SMALL_ICON, GUIControl.get().getIconProvider().getImageIcon("icon.exit"));

	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {

		int answer = JOptionPane.showConfirmDialog(null, "Wirklick beenden?", "?", JOptionPane.YES_NO_OPTION);
		if(answer == JOptionPane.YES_OPTION)			
			System.exit(0);
	}

}
