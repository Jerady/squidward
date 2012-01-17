package de.picman.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.SwingWorker;

import de.picman.backend.control.ApplicationControl;
import de.picman.backend.db.Category;
import de.picman.backend.db.DbController;
import de.picman.backend.db.Picture;
import de.picman.gui.components.MainFrame;
import de.picman.gui.components.SearchResult;
import de.picman.gui.main.GUIControl;

public class SearchPictureByCategoryAction extends AbstractPicmanAction {

	private static final long serialVersionUID = -598428206493846215L;
	private Integer searchId = new Integer(0);
	
	public SearchPictureByCategoryAction() {
		super("Suchen");
		putValue(Action.SMALL_ICON, GUIControl.get().getIconProvider().getImageIcon("icon.search"));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		 SwingWorker<Object, Void> worker = 
	          new SwingWorker<Object, Void>() {
	          public Object doInBackground() {
	
				MainFrame.getInstance().lockLight();
		
				DbController controller = ApplicationControl.getInstance().getDbController();
				try {
					Integer[] ids = controller.getIdsOfPicturesInCategory(searchId);
					SearchResult.getInstance().removeAll();
					for(int i = 0; i<ids.length;i++){
						Picture pic = ApplicationControl.getInstance().getMasterMap().get(ids[i]);
						SearchResult.getInstance().addPicture(pic);
					}
				} catch (Exception e1) {
					ApplicationControl.displayErrorToUser(e1);	
					MainFrame.getInstance().unlock();
				}finally{
					MainFrame.getInstance().getPictureDisplayPanel().requestFocus();
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

	public void searchCategory(Category cat){
		searchId(cat.getId());
	}
	
	public void searchId(Integer id){
		this.searchId = id;
		actionPerformed(null);
	}
	
}
