package de.picman.gui.actions;

import java.awt.event.ActionEvent;
import java.util.HashMap;

import javax.swing.Action;
import javax.swing.SwingWorker;

import de.picman.gui.components.MainFrame;
import de.picman.gui.dialogs.AssignCategoriesDialog;
import de.picman.gui.main.GUIControl;
import de.rahn.bilderdb.db.Category;

public class DisplayAssignCategoriesDialogAction extends AbstractPicmanAction {

	private static final long serialVersionUID = 6298427657614126165L;
	private HashMap<Integer, Category> map;
	
	public DisplayAssignCategoriesDialogAction(HashMap<Integer, Category> assignedCategoriesMap) {
		super("Kategorien anzeigen");
		this.map = assignedCategoriesMap;
		putValue(Action.SMALL_ICON, GUIControl.get().getIconProvider().getImageIcon("icon.category"));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		 SwingWorker<Object, Void> worker = 
	          new SwingWorker<Object, Void>() {
	          public Object doInBackground() {
					MainFrame.getInstance().lockLight();
						AssignCategoriesDialog dialog = new AssignCategoriesDialog(map);
						dialog.setLocationRelativeTo(MainFrame.getInstance().getContentPane());
						dialog.setModal(true);
						dialog.setVisible(true);
						return null;
				}
					public void done() {
						MainFrame.getInstance().unlockLight();
					}
				};
			worker.execute();
		}

}
