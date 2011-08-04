package de.picman.gui.components;

import java.awt.EventQueue;
import java.util.Observable;
import java.util.Observer;

import de.picman.gui.providers.ActionProvider;

public class PictureClipboardObserver implements Observer {

	
	
	
	@Override
	public void update(Observable arg0, Object arg1) {
		
		boolean export = (PictureClipboard.getInstance().getPictureMap().size() > 0);
		ActionProvider.getDisplayExportPicturesDialogAction().setEnabled(export);
		
		EventQueue.invokeLater( new Runnable()
		{
			public void run() {
	    		MainFrame.getInstance().getPictureClipboardPanel2().refresh();
			} 
		});
	}

}
