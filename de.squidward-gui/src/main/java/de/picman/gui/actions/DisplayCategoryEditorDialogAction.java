package de.picman.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.SwingWorker;

import de.picman.gui.components.MainFrame;
import de.picman.gui.dialogs.CategoriesEditorDialog;
import de.picman.gui.main.GUIControl;

public class DisplayCategoryEditorDialogAction extends AbstractPicmanAction {

	private static final long serialVersionUID = -6967533586661655521L;

	public DisplayCategoryEditorDialogAction() {
		super("Kategorien Editor");
		putValue(Action.SMALL_ICON, GUIControl.get().getIconProvider().getImageIcon("icon.edit"));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		 SwingWorker<Object, Void> worker = 
	          new SwingWorker<Object, Void>() {
	          public Object doInBackground() {
	   
					MainFrame.getInstance().lockLight();

					CategoriesEditorDialog dialog = new CategoriesEditorDialog();
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
