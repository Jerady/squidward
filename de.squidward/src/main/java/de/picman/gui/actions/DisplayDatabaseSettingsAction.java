package de.picman.gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.SwingWorker;

import de.picman.gui.components.MainFrame;
import de.picman.gui.main.GUIControl;
import de.picman.gui.panels.DatabaseSettingsPanel;

public class DisplayDatabaseSettingsAction extends AbstractPicmanAction {

	private static final long serialVersionUID = 4871250343754215483L;

	public DisplayDatabaseSettingsAction() {
		super("Datenbank Einstellungen");
		putValue(Action.SMALL_ICON, GUIControl.get().getIconProvider().getImageIcon("icon.edit"));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		 SwingWorker<Object, Void> worker = 
	          new SwingWorker<Object, Void>() {
	          
			 public Object doInBackground() {
					MainFrame.getInstance().lockLight();

					DatabaseSettingsDialog dialog = new DatabaseSettingsDialog();
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


	
	protected class DatabaseSettingsDialog extends JDialog{
		private static final long serialVersionUID = -765218185449805372L;

		public DatabaseSettingsDialog() {
			setTitle("Datenbank Einstellungen");
			DatabaseSettingsPanel panel= new DatabaseSettingsPanel(); 
			panel.getSaveBtn().addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					DatabaseSettingsDialog.this.dispose();
				}
			});
			add(panel);
			setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			pack();
			setLocationByPlatform(true);
			setModal(true);
		}
		
	}

}
