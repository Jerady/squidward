package de.picman.gui.components;

import javax.swing.JButton;
import javax.swing.JFrame;

import de.picman.gui.actions.TakeScreenShotAction;

public class DebugDialog {
	public static void main(String[] args) {
		JFrame frame = new JFrame("DEBUG");
		frame.setAlwaysOnTop(true);
		frame.add(new JButton(new TakeScreenShotAction()));
		frame.pack();
		frame.setVisible(true);
	}
}
