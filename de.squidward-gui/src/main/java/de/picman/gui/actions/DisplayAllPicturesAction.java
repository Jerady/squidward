package de.picman.gui.actions;

import java.awt.event.ActionEvent;
import java.util.Map;
import java.util.Set;

import javax.swing.Action;
import javax.swing.SwingWorker;

import de.picman.gui.components.MainFrame;
import de.picman.gui.components.SearchResult;
import de.picman.gui.main.GUIControl;
import de.rahn.bilderdb.control.ApplicationControl;
import de.rahn.bilderdb.db.Picture;

public class DisplayAllPicturesAction extends AbstractPicmanAction {

	private static final long serialVersionUID = -3378037930581811236L;

	public DisplayAllPicturesAction() {
		super("");
		
		String label = "Alle anzeigen";
		
		putValue(Action.NAME, label);
		putValue(Action.SMALL_ICON, GUIControl.get().getIconProvider().getImageIcon("icon.picture"));
	}
	


	public void actionPerformed(ActionEvent e) {
		SwingWorker<Object, Void> worker = 
	          new SwingWorker<Object, Void>() {
			
				private int size = 0;
				private long startTime = 0l;
				
				public Object doInBackground() {
					MainFrame.getInstance().lock();
					MainFrame.getInstance().setStatusMessage("Lade alle Bilder aus der Datenbank...");
					MainFrame.getInstance().getSearchCriteriaPanel().reset();
					
					startTime = System.currentTimeMillis();

					try {
						SearchResult.getInstance().removeAll();
						
						Map<Integer, Picture> masterMap = ApplicationControl.getInstance().getMasterMap();
						
						Set<Integer> masterSet = masterMap.keySet();
						Integer[] picIDs = masterSet.toArray(new Integer[0]);
												
						for(int i = 0; i<picIDs.length;i++){
							Picture pic = ApplicationControl.getInstance().getMasterMap().get(picIDs[i]);
							SearchResult.getInstance().addPicture(pic);
						}
						size = picIDs.length;
					} catch (Exception e1) {
						ApplicationControl.displayErrorToUser(e1);	
						MainFrame.getInstance().unlock();
					}
					return null;
				}
					public void done() {
						MainFrame.getInstance().getPictureDisplayPanel().refresh();
						MainFrame.getInstance().getPicturePropertiesPanel().reset();
						MainFrame.getInstance().unlock();
						MainFrame.getInstance().setStatusMessage("Laden der "+ size + " Bilder hat " + (System.currentTimeMillis()-startTime) + "ms gedauert.");
					}
				};
			worker.execute();
			
	}

	
}
