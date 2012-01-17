package de.picman.gui.components;

import java.awt.EventQueue;
import java.util.Observable;
import java.util.Observer;


public class SearchResultObserver implements Observer {
	
	@Override
	public void update(Observable arg0, Object arg1) {
		EventQueue.invokeLater( new Runnable()
		{
			public void run() {
				MainFrame.getInstance().getPictureDisplayPanel().refresh();
				MainFrame.getInstance().getPicturePropertiesPanel().reset();
			} 
		});
	}

}
