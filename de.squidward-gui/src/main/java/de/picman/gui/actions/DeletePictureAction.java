package de.picman.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import de.picman.gui.components.MainFrame;
import de.picman.gui.main.GUIControl;
import de.rahn.bilderdb.control.ApplicationControl;
import de.rahn.bilderdb.db.Picture;

public class DeletePictureAction extends AbstractPicmanAction {

	private static final long serialVersionUID = -3473559680866165037L;

	public DeletePictureAction() {
		super("Bild lšschen");
		putValue(Action.SMALL_ICON, GUIControl.get().getIconProvider().getImageIcon("icon.trash"));
	}

	public void actionPerformed(ActionEvent e) {

		SwingWorker<Object, Void> worker = 
	          new SwingWorker<Object, Void>() {
				public Object doInBackground() {
					MainFrame.getInstance().lock();
					
					Picture pic = MainFrame.getInstance().getPictureDisplayPanel().getSelectedPicture();
					MainFrame.getInstance().setStatusMessage("Lösche Bild id" + pic.getId());

					if(pic != null){
						System.out.println("DeletePictureAction: attempting to delete pic id" + pic.getId() );

						int choice = JOptionPane.showConfirmDialog(MainFrame.getInstance(), "Wirklich löschen?","?", JOptionPane.YES_NO_OPTION);
						if(choice == JOptionPane.YES_OPTION){
								pic.setDeleted(true);
								try {
									System.out.println("DeletePictureAction: deleting pic id" + pic.getId() );
									ApplicationControl.getInstance().getDbController().updatePicture(pic);
									ApplicationControl.getInstance().getMasterMap().remove(pic.getId());
									int index = MainFrame.getInstance().getPictureDisplayPanel().getPictureList().getSelectedIndex();
									MainFrame.getInstance().getPictureDisplayPanel().getPictureList().setSelectedIndex(index);
								} catch (Exception e1) {
									ApplicationControl.displayErrorToUser(e1);
								}
								MainFrame.getInstance().setStatusMessage("Bild id" + pic.getId() + " gelöscht.");
						}
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


