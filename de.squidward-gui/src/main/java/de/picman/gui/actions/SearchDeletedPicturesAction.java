package de.picman.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.SwingWorker;

import de.picman.gui.components.MainFrame;
import de.picman.gui.components.SearchResult;
import de.picman.gui.main.GUIControl;
import de.rahn.bilderdb.control.ApplicationControl;
import de.rahn.bilderdb.db.Picture;
import de.rahn.bilderdb.db.PictureColumn;
import de.rahn.bilderdb.db.PictureSearchCriteria;

public class SearchDeletedPicturesAction extends AbstractPicmanAction {

	private static final long serialVersionUID = -5208289546092014020L;

	public SearchDeletedPicturesAction() {
		super("Papierkorb");
		putValue(Action.SMALL_ICON, GUIControl.get().getIconProvider().getImageIcon("icon.trash"));
	}

	public void actionPerformed(ActionEvent e) {
		 SwingWorker<Object, Void> worker = 
	          new SwingWorker<Object, Void>() {
	          public Object doInBackground() {
	   
					MainFrame.getInstance().lock();
					
					PictureSearchCriteria pictureSearchCriteria = new PictureSearchCriteria();
					pictureSearchCriteria.put(PictureColumn.ISDELETED, true );
				
					try {
						Integer[] ids = ApplicationControl.getInstance().getDbController().searchPictures(pictureSearchCriteria, PictureColumn.ID);
						
						
						
						SearchResult.getInstance().removeAll();
						for(int i = 0; i<ids.length;i++){
							Picture pic = ApplicationControl.getInstance().getMasterMap().get(ids[i]);
							SearchResult.getInstance().addPicture(pic);
						}
						
					} catch (Exception e1) {
						ApplicationControl.displayErrorToUser(e1);	
						MainFrame.getInstance().unlock();
					}
					finally{
						MainFrame.getInstance().getPictureDisplayPanel().refresh();
						MainFrame.getInstance().getPicturePropertiesPanel().reset();
						MainFrame.getInstance().getPictureDisplayPanel().requestFocus();
					}
					  return null;
	          }
	          public void done() {
					MainFrame.getInstance().unlock();
	          }
	       };
	       worker.execute();
	}
}


