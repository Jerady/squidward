package de.picman.gui.actions;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import de.picman.backend.control.ApplicationControl;
import de.picman.backend.db.DbController;
import de.picman.backend.db.LogEntry;
import de.picman.gui.components.MainFrame;
import de.picman.gui.main.GUIControl;

public class DisplayLogViewerAction extends AbstractPicmanAction {

	private static final long serialVersionUID = 7137762930323586902L;

	public DisplayLogViewerAction() {
		super("Protokoll");
		putValue(Action.SMALL_ICON, GUIControl.get().getIconProvider().getImageIcon("icon.log"));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		 SwingWorker<Object, Void> worker = 
	          new SwingWorker<Object, Void>() {
	          
			 public Object doInBackground() {
				MainFrame.getInstance().lockLight();
				LogViewerDialog dialog = new LogViewerDialog();
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


	
	private class LogViewerDialog extends JDialog{

		private static final long serialVersionUID = 4847140927050631855L;

		public LogViewerDialog() {
			super(MainFrame.getInstance());
			setTitle("Datenbank Protokoll");
			setLayout(new BorderLayout());
			
			JTextArea textArea = new JTextArea();
			textArea.setEditable(false);
			
			try {
				
				DbController dbController = ApplicationControl.getInstance().getDbController();
				long numLogEntries = dbController.getLogEntryCount();
				int lastIndex = (int)numLogEntries;
				int startingFromIndex = lastIndex-1000;
				
				LogEntry[]logEntries = dbController.getLogEntries(1000, startingFromIndex);
				for (int i = 0; i < logEntries.length; i++) {
					textArea.append(logEntries[i].getLogLevel());
					textArea.append(" : ");
					textArea.append(GUIControl.get().getDateFormat().format(logEntries[i].getTimestamp()));
					textArea.append(" : ");
					textArea.append(logEntries[i].getUserName());
					textArea.append(" : ");
					textArea.append(logEntries[i].getMessage());
					textArea.append(" : CatID ");
					textArea.append(Integer.toString(logEntries[i].getCategoryId()));
					textArea.append(" : PicID ");
					textArea.append(Integer.toString(logEntries[i].getPictureId()));
					textArea.append("\n");
				}
				
			} catch (Exception e) {
				textArea.append(e.getLocalizedMessage());
			}
			
			
			JScrollPane scrollPane = new JScrollPane(textArea);
			add(scrollPane,BorderLayout.CENTER);
			
			JButton saveLogBtn = new JButton(new SaveLogAction());
			add(saveLogBtn, BorderLayout.SOUTH);
			setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			setSize(800,400);
			setLocationByPlatform(true);
		}
		
	}

}
