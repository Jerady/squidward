package de.picman.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.looks.LookUtils;
import com.jgoodies.looks.Options;

import de.picman.backend.control.ApplicationControl;
import de.picman.backend.db.Picture;
import de.picman.backend.db.SupportedImageFiles;
import de.picman.gui.components.MainFrame;
import de.picman.gui.components.PictureClipboard;
import de.picman.gui.main.GUIControl;

public class SendMailDialog extends JDialog {

	private static final long serialVersionUID = -5912639457338250380L;
	private JButton cancelButton;
	private JButton exportButton;
	private JPanel buttonPanel;
	private JPanel exportPropertiesPanel;
	
	private JComboBox typeBox;
	private JComboBox sizeBox;
	
	public SendMailDialog(HashMap<Integer, Picture> picturesToExportMap) {
		super(MainFrame.getInstance());
		setTitle("Mail erstellen");
		initComponent();
	
	}
	
	
	public void initComponent(){				
		FormLayout layout = new FormLayout(
			    "fill:default:grow",
			    "fill:default:grow, 3dlu, 20dlu"
			    );     			

		CellConstraints cc = new CellConstraints();
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();

		builder.add(getExportPropertiesPanel(), cc.xy(1,1));
		builder.addSeparator("", cc.xy(1,2));
		builder.add(getButtonPanel(), cc.xy(1,3));
		
		
		setModal(true);
		setLayout(new BorderLayout());
		add(builder.getPanel(),BorderLayout.CENTER);
		pack();

	}
	
	
	public JComponent getExportPropertiesPanel(){
		if(exportPropertiesPanel == null){

		
			FormLayout layout = new FormLayout(
					// 1         2     3       4      5      6     7      8
				    "right:pref, 3dlu, left:pref,3dlu, 100dlu, 3dlu, pref, 7dlu", // columns
					// 1    2     3     4      5     6    7      8    9     10    11    12    13    14
					"pref, 6dlu, pref, 18dlu, pref, 6dlu, pref, 6dlu, pref, 18dlu, pref, 6dlu, pref, 6dlu");
				    
			CellConstraints cc = new CellConstraints();

			PanelBuilder builder = new PanelBuilder(layout);

			builder.addLabel("Grš§e:", cc.xy(3, 1));
			builder.add(getSizeBox(), cc.xy(5, 1));
			builder.addLabel("Typ:", cc.xy(3, 3));
			builder.add(getTypeBox(), cc.xy(5, 3));
		    
		    
			exportPropertiesPanel = builder.getPanel();
			
			
		}
		return exportPropertiesPanel;
	}
	

	private JComboBox getTypeBox() {
		if(typeBox == null){
			typeBox = new JComboBox(SupportedImageFiles.values());
			typeBox.setSelectedItem(SupportedImageFiles.PNG);
		}
		return typeBox;
	}

	

	private JComboBox getSizeBox() {
		if(sizeBox == null){
			sizeBox = new JComboBox(getSizeChoice());
		}
		return sizeBox;
	}

	
	private Vector<String> getSizeChoice(){
		Vector<String> v = new Vector<String>();
		v.add("Original Grš§e");
		v.add("FŸr Mails optimiert");
		return v;
	}

	public JPanel getButtonPanel(){
		if(buttonPanel == null){
			 buttonPanel = new JPanel();
				buttonPanel.add(getCancelButton());
				buttonPanel.add(getExportButton());
		}
			
			return buttonPanel;
		}
	
	private JButton getExportButton(){
		if(exportButton == null){
			exportButton = new JButton("Mail erstellen");
			exportButton.setIcon(GUIControl.get().getImageIcon("icon.mail"));
			exportButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					SendMailDialog.this.dispose();
					exportPicturesAction();
				}
			});
		}
		return exportButton;
	}
	
	
	private void exportPicturesAction(){
		 SwingWorker<Object, Void> worker = 
			  new SwingWorker<Object, Void>() {
				private ProgressMonitor pm;
				private int size;
				
		          public Object doInBackground() {

					HashMap<Integer, Picture> picMap = PictureClipboard.getInstance().getPictureMap();
					
					Collection<Picture> pics = picMap.values();
					String baseDir = System.getProperty("java.io.tmpdir");
					String fileName = "picture";
			
				    String message = "Erstelle Mail...";
				    String note = "Bild";
				    String title = "";
				    UIManager.put("ProgressMonitor.progressText", title);
		    
				    int min = 0;
				    int max = picMap.size();
				    size = max;
				    pm = new ProgressMonitor(SendMailDialog.this, message, note, min, max);
				    pm.setMillisToDecideToPopup(0);
				    pm.setMillisToPopup(0);
		
			
					int i = 0;
					List<String> pictureList = new ArrayList<String>();
					String name = "";
					for(Picture pic : pics){
						File tempPath = new File(baseDir);
						boolean isOverwrite = true;
						boolean isWriteXmlForReimport = false;
						SupportedImageFiles type = (SupportedImageFiles)getTypeBox().getSelectedItem();
						String picName = fileName + i;
						
						String sizeStr = (String)getSizeBox().getSelectedItem();
				
						Dimension sizeDim = null;
						
						if(sizeStr.equals(getSizeChoice().get(0))){			// original
							sizeDim = null;
						}else if(sizeStr.equals(getSizeChoice().get(1))){   // mails
							sizeDim = new Dimension(500,200);
						}
						
						boolean cancelled = pm.isCanceled();
			    	    
			    	    if (cancelled) {
			    	        return null;
			    	    } else {
			    	        pm.setProgress(i);
			    	        pm.setNote("Bild " + (i+1) + " von " + size);
			    	    }
						
						try {
							 name = pic.exportToFile(tempPath, picName, isOverwrite, isWriteXmlForReimport, sizeDim, type).getAbsolutePath();
							pictureList.add(name);
						
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						i++;
					}
					
//					try {
//						System.out.println("SendMailDialog: erstelle Mail");
//						Message mail = new Message();
//						mail.setAttachments(pictureList);
//						mail.setBody("");
//
//					} catch (IOException e) {
//						ApplicationControl.displayErrorToUser(e);
//					   return null;
//		          	}
					return null;
	          }					
			     public void done() {
			       pm.setProgress(size);
			       SendMailDialog.this.dispose();
			     }
			 };
			 worker.execute();
		 
		 
		 
	}
	
	private JButton getCancelButton(){
		if(cancelButton == null){
			cancelButton = new JButton("Abbrechen");
			cancelButton.setIcon(GUIControl.get().getImageIcon("icon.cancel"));
			cancelButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					SendMailDialog.this.dispose();
				}
			});
		}
		return cancelButton;
	}
	
	public static void main(String[] args) {
		
		try { 
			String laf = 
			    LookUtils.IS_OS_WINDOWS_XP 
			        ? Options.getCrossPlatformLookAndFeelClassName() 
			        : Options.getSystemLookAndFeelClassName(); 
			    UIManager.setLookAndFeel( laf ); 
			} catch ( Exception e ) { 
			    System.err.println( "Can't set look & feel:" + e ); 
			}
			
			try {
				ApplicationControl.getInstance().login("Jens", "geheim");
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		SendMailDialog sendMailDialog = new SendMailDialog(null);
		sendMailDialog.setVisible(true);
	}
}
