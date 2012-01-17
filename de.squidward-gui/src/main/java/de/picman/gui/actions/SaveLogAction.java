package de.picman.gui.actions;

import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.Action;
import javax.swing.JFileChooser;

import de.picman.backend.control.ApplicationControl;
import de.picman.backend.db.DbController;
import de.picman.backend.db.LogEntry;
import de.picman.gui.main.GUIControl;

public class SaveLogAction extends AbstractPicmanAction {

	private static final long serialVersionUID = 4012235685668853108L;

	public SaveLogAction() {
		super("Logdatei Speichern");
		putValue(Action.SMALL_ICON, GUIControl.get().getIconProvider().getImageIcon("icon.log"));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		StringBuilder builder = new StringBuilder();
			try {
				
				DbController controller = ApplicationControl.getInstance().getDbController();
			
				int start = (int)controller.getLogEntryCount()-1000;
				int maxEntries = 1000;
				
				LogEntry[]logEntries = controller.getLogEntries(maxEntries, start);
				for (int i = 0; i < logEntries.length; i++) {
					builder.append(logEntries[i].getLogLevel());
					builder.append(" : ");
					builder.append(GUIControl.get().getDateFormat().format(logEntries[i].getTimestamp()));
					builder.append(" : ");
					builder.append(logEntries[i].getUserName());
					builder.append(" : ");
					builder.append(logEntries[i].getMessage());
					builder.append(" : CatID ");
					builder.append(logEntries[i].getCategoryId());
					builder.append(" : PicID ");
					builder.append(logEntries[i].getPictureId());
					builder.append("\n");
				}
				
			} catch (Exception e1) {
				builder.append(e1.getLocalizedMessage());
			}
			
			JFileChooser chooser = new JFileChooser(System.getProperty("user.home"));
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.showSaveDialog(null);
			
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH-MM-ss");
			String date = format.format(new Date());
			
			String path = chooser.getSelectedFile().getPath()+ "/PicManLogFile-" + date +".log";
			
			 try {
			        BufferedWriter out = new BufferedWriter(new FileWriter(path));
			        out.write(builder.toString());
			        out.close();
			    } catch (IOException e2) {
			    	ApplicationControl.displayErrorToUser(e2);
			    }

			
			
			
		}
	
	
}
