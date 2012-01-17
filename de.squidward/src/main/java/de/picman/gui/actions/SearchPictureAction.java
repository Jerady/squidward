package de.picman.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.SwingWorker;

import de.picman.backend.control.ApplicationControl;
import de.picman.backend.db.Picture;
import de.picman.backend.db.PictureColumn;
import de.picman.backend.db.PictureSearchCriteria;
import de.picman.gui.components.MainFrame;
import de.picman.gui.components.SearchResult;

public class SearchPictureAction extends AbstractPicmanAction {

	private static final long serialVersionUID = 916081019595142758L;
	private PictureSearchCriteria pictureSearchCriteria;
	
	public SearchPictureAction(PictureSearchCriteria pictureSearchCriteria) {
		this.pictureSearchCriteria = pictureSearchCriteria;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		 SwingWorker<Object, Void> worker = 
	          new SwingWorker<Object, Void>() {
	          public Object doInBackground() {
	   
					MainFrame.getInstance().lockLight();
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
						MainFrame.getInstance().getPictureDisplayPanel().requestFocus();
						MainFrame.getInstance().unlockLight();
					}
					  return null;
	          }
	          public void done() {
					MainFrame.getInstance().getPictureDisplayPanel().refresh();
					MainFrame.getInstance().getPicturePropertiesPanel().reset();
					MainFrame.getInstance().unlockLight();
	          }
	       };
	       worker.execute();
	}
	
}
