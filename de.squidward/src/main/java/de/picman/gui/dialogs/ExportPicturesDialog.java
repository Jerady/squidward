package de.picman.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
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
import de.picman.gui.api.PrefsKeys;
import de.picman.gui.components.MainFrame;
import de.picman.gui.components.PictureClipboard;
import de.picman.gui.main.GUIControl;

public class ExportPicturesDialog extends JDialog {

	private static final long serialVersionUID = -3674430714408714165L;
	private JButton cancelButton;
	private JButton exportButton;
	private JPanel buttonPanel;
	private JPanel exportPropertiesPanel;
	private JCheckBox overwriteExistingCheckbox;
	private JCheckBox writeXmlForReimportCheckbox;
	private JComboBox typeBox;
	private JComboBox sizeBox;
	private JTextField exportPathTextField;
	private JButton chooseExportPathButton;
	
	public ExportPicturesDialog(HashMap<Integer, Picture> picturesToExportMap) {
		super(MainFrame.getInstance());
		setTitle("Auswahl exportieren");
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
			//builder.setDefaultDialogBorder();

			builder.addSeparator("Zielort",   cc.xyw(1,  1, 8));
			builder.add(getExportPathTextField(), cc.xyw(1,3,5));
			builder.add(getChooseExportPathButton(), cc.xy(7,3));

			builder.addSeparator("", cc.xyw(1,5,8));
			builder.addLabel("Größe:", cc.xy(3, 7));
			builder.add(getSizeBox(), cc.xy(5, 7));
			builder.addLabel("Typ:", cc.xy(3, 9));
			builder.add(getTypeBox(), cc.xy(5, 9));
			builder.add(getOverwriteExistingCheckbox(), cc.xyw(5,11,4));
		    builder.add(getWriteXmlForReimportCheckbox(), cc.xyw(5,13,4));
		    
		    
			exportPropertiesPanel = builder.getPanel();
			
			
		}
		return exportPropertiesPanel;
	}
	
	
	private JCheckBox getOverwriteExistingCheckbox() {
		if(overwriteExistingCheckbox == null){
			overwriteExistingCheckbox = new JCheckBox("bestehende Bilder überschreiben");
			overwriteExistingCheckbox.setSelected(true);
			overwriteExistingCheckbox.setIcon(GUIControl.get().getImageIcon("icon.no"));
			overwriteExistingCheckbox.setSelectedIcon(GUIControl.get().getImageIcon("icon.yes"));
		}
		return overwriteExistingCheckbox;
	}

	private JCheckBox getWriteXmlForReimportCheckbox() {
		if(writeXmlForReimportCheckbox == null){
			writeXmlForReimportCheckbox = new JCheckBox("Re-Importdatei schreiben");
			writeXmlForReimportCheckbox.setSelected(false);
			writeXmlForReimportCheckbox.setIcon(GUIControl.get().getImageIcon("icon.no"));
			writeXmlForReimportCheckbox.setSelectedIcon(GUIControl.get().getImageIcon("icon.yes"));

		}
		return writeXmlForReimportCheckbox;
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
		v.add("Original Größe");
		v.add("Für Mails optimiert");
		return v;
	}

	private JTextField getExportPathTextField() {
		if(exportPathTextField == null){
			File file = new File(GUIControl.get().getProperty(PrefsKeys.GUI_LASTPATH));
			exportPathTextField = new JTextField(file.getPath());
		}
		return exportPathTextField;
	}


	private JButton getChooseExportPathButton() {
		if(chooseExportPathButton == null){
			chooseExportPathButton = new JButton("...");
			chooseExportPathButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					final JFileChooser fileChooser = new JFileChooser();
					fileChooser.setCurrentDirectory(new File(GUIControl
							.get()
							.getProperty(PrefsKeys.GUI_LASTPATH)));
					fileChooser.setMultiSelectionEnabled(true);
					fileChooser.setApproveButtonText("Auswählen");
					fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					fileChooser.showOpenDialog(MainFrame.getInstance());
					File selectedFile = fileChooser.getSelectedFile();
					getExportPathTextField().setText(selectedFile.getAbsolutePath());
					GUIControl.get().setProperty(PrefsKeys.GUI_LASTPATH, getExportPathTextField().getText());
				}
			});
		}
		return chooseExportPathButton;
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
			exportButton = new JButton("Exportieren");
			exportButton.setIcon(GUIControl.get().getImageIcon("icon.export"));
			exportButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
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
				     MainFrame.getInstance().lockLight();

					HashMap<Integer, Picture> picMap = PictureClipboard.getInstance().getPictureMap();
					
					Collection<Picture> pics = picMap.values();
//					String baseDir =  GUIControl.get().getPreferences().get(PrefsKeys.GUI_LASTPATH,
//				    		  System.getProperty("user.dir"));
					String fileName = "picture";
			
				    String message = "Exportiere...";
				    String note = "Bild";
				    String title = "";
				    UIManager.put("ProgressMonitor.progressText", title);
		    
				    int min = 0;
				    int max = picMap.size();
				    size = max;
				    pm = new ProgressMonitor(ExportPicturesDialog.this, message, note, min, max);
				    pm.setMillisToDecideToPopup(0);
				    pm.setMillisToPopup(0);
		
			
					int i = 0;
					for(Picture pic : pics){
						File path = new File(getExportPathTextField().getText());
						boolean isOverwrite = getOverwriteExistingCheckbox().isSelected();
						boolean isWriteXmlForReimport = getWriteXmlForReimportCheckbox().isSelected();
						SupportedImageFiles type = (SupportedImageFiles)getTypeBox().getSelectedItem();
						String picName = fileName + i + "." + type;
						
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
			    	        pm.setNote("Exportiere Bild " + (i+1) + " von " + size +" nach " + path);
			    	    }
						
						try {
							pic.exportToFile(path, picName, isOverwrite, isWriteXmlForReimport, sizeDim, type);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						i++;
					}
					return null;
		          }
			     public void done() {
			       pm.setProgress(size);
			       MainFrame.getInstance().unlockLight();
			     }
			 };
			 worker.execute();
		 ExportPicturesDialog.this.dispose();
	}
	
	private JButton getCancelButton(){
		if(cancelButton == null){
			cancelButton = new JButton("Abbrechen");
			cancelButton.setIcon(GUIControl.get().getImageIcon("icon.cancel"));
			cancelButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					ExportPicturesDialog.this.dispose();
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
		ExportPicturesDialog exportPicturesDialog = new ExportPicturesDialog(null);
		exportPicturesDialog.setVisible(true);
	}
}
