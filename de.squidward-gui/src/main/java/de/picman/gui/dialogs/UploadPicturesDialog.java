package de.picman.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.tree.DefaultMutableTreeNode;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDirectory;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.looks.LookUtils;
import com.jgoodies.looks.Options;

import de.picman.gui.actions.AbstractPicmanAction;
import de.picman.gui.api.PrefsKeys;
import de.picman.gui.components.CategoriesTree;
import de.picman.gui.components.MainFrame;
import de.picman.gui.main.GUIControl;
import de.picman.gui.panels.ListPanel;
import de.picman.gui.panels.PreviewPane;
import de.picman.gui.renderer.AssignedCategoriesListCellRenderer;
import de.picman.gui.renderer.PicutureListCellRenderer;
import de.rahn.bilderdb.control.ApplicationControl;
import de.rahn.bilderdb.db.Category;
import de.rahn.bilderdb.db.DbController;
import de.rahn.bilderdb.db.Picture;
import de.rahn.bilderdb.db.User;


public class UploadPicturesDialog extends JDialog {

	private static final long serialVersionUID = 6761560932404353369L;

	private HashMap<Integer, Category> assignedCategories;
	
	public final static int CANCEL = 1;
	public final static int OK = 2;
	private int userChoice = 0;
	private JButton uploadButton;
	private JButton cancelButton;
	private JPanel buttonPanel;
	private ListPanel selectPicturesPanel;
	private JPanel chooseCategoriesPanel;
	private JComponent mainPane;
	private JPanel commonDataPanel;
	private JComboBox pictureSourceBox;
	private JTextField pictureSourceField;
	private JCheckBox publishCheckBox;
	private JCheckBox examplaryCheckBox;
	private JCheckBox badExampleCheckBox;
	private JRadioButton lpvSourceRadioButton;
	private JRadioButton unknownSourceRadioButton;
	private JTextField userNameField;
	private JTextField creationDateField;
	private JLabel pictureCreditsLabel;
	private JTextField descriptiontField;
	private ButtonGroup pictureOriginGroup;
	private JLabel countSelectionLabel;
	private ListPanel categoriesPanel;
	private JCheckBox deleteOriginals;
	private DefaultMutableTreeNode assignedCategoriesRootNode;
	
	public UploadPicturesDialog() {
		super(MainFrame.getInstance());
		setModal(true);
		setTitle("Bild(er) in die Datenbank laden");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		getPictureOriginGroup();
		add(getMainPane(), BorderLayout.CENTER);
		add(getButtonPanel(), BorderLayout.SOUTH);
		setResizable(false);
		pack();
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
					ApplicationControl.getInstance().login("Jens","geheim" );
				} catch (Exception e) {
					ApplicationControl.displayErrorToUser(e);
				}
				
				CategoriesTree tree = new CategoriesTree();
				tree.getTopLevelNodesList();
				
				UploadPicturesDialog dialog = new UploadPicturesDialog();

				dialog.setModal(true);
				dialog.setLocationByPlatform(true);
				dialog.setVisible(true);

				
				
		}
	    
		private void updateButtonState(){
			
			Set<String> topLevelSet = new HashSet<String>();
			Collection<Category> categories = getAssignedCategories().values();
	
			for(Category cat : categories){
				topLevelSet.add(cat.getTopLevelCategory().getName());
			}
			
			CategoriesTree tree = new CategoriesTree();
			
			int numberOfToplevelCats = tree.getTopLevelNodesList().size();
			int numberOfAssignedTopLevelCats = topLevelSet.size();
			
			
			boolean isUploadControl = GUIControl.get().getPreferences().getBoolean(PrefsKeys.GUI_UPLOAD_CONTROL, false);
			if(isUploadControl){
				getUploadButton().setEnabled(
						(numberOfAssignedTopLevelCats == numberOfToplevelCats) && 
						(selectPicturesPanel.getListModel().getSize()>0)
				);
			}
			else{
				getUploadButton().setEnabled(selectPicturesPanel.getListModel().getSize()>0);
			}
		}

	   
	   
	private void updateAssignedCategoriesView(){
		
		getCategoriesPanel().getListModel().removeAllElements();

		ArrayList<Category> categories = new ArrayList<Category>();
		categories.addAll(getAssignedCategories().values());
		Collections.sort(categories);
		
		for(Category cat : categories){
			getCategoriesPanel().getListModel().addElement(cat);
		}

		updateButtonState();
		
	}
	
	public DefaultMutableTreeNode getAssignedCategoryRootNode() {
        if (assignedCategoriesRootNode==null){
        	assignedCategoriesRootNode = new DefaultMutableTreeNode("Zugewiesen");
        }
        return assignedCategoriesRootNode;
    }
	
	
	
	public JTextField getPictureSourceField() {
		if(pictureSourceField == null){
			pictureSourceField = new JTextField();
			pictureSourceField.setEnabled(false);
		}
		return pictureSourceField;
	}

	public JComboBox getPictureSourceBox() {
		if(pictureSourceBox == null){
			pictureSourceBox = new JComboBox(GUIControl.get().getPictureSourceList());
			pictureSourceBox.addItem("neue Quelle");
			pictureSourceBox.setEnabled(getPublishCheckBox().isSelected());
			pictureSourceBox.addActionListener(
					new ActionListener(){
						@Override
						public void actionPerformed(ActionEvent e) {
							getPictureSourceField().setEnabled(((String)pictureSourceBox.getSelectedItem()).equals("neue Quelle"));
							getPictureSourceField().requestFocus();
						}
					}
			
			);
			
		}
		return pictureSourceBox;
	}

	public HashMap<Integer, Category> getAssignedCategories() {
		if(assignedCategories == null){
			assignedCategories = new HashMap<Integer, Category>();
		}
		return assignedCategories;
	}


	public JLabel getCountSelectionLabel() {
		if(countSelectionLabel == null){
			countSelectionLabel = new JLabel("0");
		}
		return countSelectionLabel;
	}



	public ButtonGroup getPictureOriginGroup() {
		if(pictureOriginGroup == null){
			pictureOriginGroup = new ButtonGroup();
			pictureOriginGroup.add(getLpvSourceRadioButton());
			getLpvSourceRadioButton().setSelected(true);
			pictureOriginGroup.add(getUnknownSourceRadioButton());
		}
		return pictureOriginGroup;
	}

	public JCheckBox getDeleteOriginals(){
		if(deleteOriginals == null){
			deleteOriginals = new JCheckBox("Löschen (nach dem Laden)");
			deleteOriginals.setIcon(GUIControl.get().getImageIcon("icon.no"));
			deleteOriginals.setSelectedIcon(GUIControl.get().getImageIcon("icon.yes"));
			deleteOriginals.setEnabled(GUIControl.get().getPreferences().getBoolean(PrefsKeys.GUI_DELETE_ORIGINALS, false));
			
		}
		return deleteOriginals;
	}


	public JCheckBox getExamplaryCheckBox(){
		if(examplaryCheckBox == null){
			examplaryCheckBox = new JCheckBox("exemplarisch");
			examplaryCheckBox.setIcon(GUIControl.get().getImageIcon("icon.no"));
			examplaryCheckBox.setSelectedIcon(GUIControl.get().getImageIcon("icon.yes"));
		}
		return examplaryCheckBox;
	}

	public JCheckBox getBadExampleCheckBox(){
		if(badExampleCheckBox == null){
			badExampleCheckBox = new JCheckBox("Negativbeispiel");
			badExampleCheckBox.setIcon(GUIControl.get().getImageIcon("icon.no"));
			badExampleCheckBox.setSelectedIcon(GUIControl.get().getImageIcon("icon.yes"));
		}
		return badExampleCheckBox;
	}
	
	public JTextField getUserNameField(){
		if(userNameField == null){
			userNameField = new JTextField();
			userNameField.setEditable(false);
			User user = ApplicationControl.getInstance().getCurrentUser();
			if(user == null){
				userNameField.setText("login first!");
			}
			else{
				userNameField.setText(user.getFullname());
			}
		}
		return userNameField;
	}
	
	public JLabel getPicutureCreditsLabel(){
		if(pictureCreditsLabel==null){
			pictureCreditsLabel = new JLabel("Bildnachweis:");
			pictureCreditsLabel.setHorizontalAlignment(JLabel.LEFT);
			pictureCreditsLabel.setEnabled(false);
		}
		return pictureCreditsLabel;
	}
	
	public JTextField getCreationDateField(){
		if(creationDateField == null){
			creationDateField = new JTextField();
			creationDateField.setEditable(true);
			creationDateField.setText("wird aus dem Bild gelesen");
			creationDateField.setEditable(false);
		}
		return creationDateField;
	}
	
	
	public JCheckBox getPublishCheckBox(){
		if(publishCheckBox == null){
			publishCheckBox = new JCheckBox("Veröffentlichen");
			publishCheckBox.setIcon(GUIControl.get().getIconProvider().getImageIcon("icon.no"));
			publishCheckBox.setSelectedIcon(GUIControl.get().getIconProvider().getImageIcon("icon.yes"));

			publishCheckBox.addItemListener(new ItemListener(){
				@Override
				public void itemStateChanged(ItemEvent e) {
					boolean isPublish = publishCheckBox.isSelected();
					boolean isNewSource = ((String)pictureSourceBox.getSelectedItem()).equals("neue Quelle");

					getPictureSourceBox().setEnabled(isPublish);
					getPictureSourceField().setEnabled(isPublish && isNewSource);
					getPictureSourceField().requestFocus(isPublish && isNewSource);
					getPicutureCreditsLabel().setEnabled(isPublish);
				}
			});
		}
		return publishCheckBox;
	}
	
	
	
	public ListPanel getCategoriesPanel() {
		if(categoriesPanel == null){
			categoriesPanel = new ListPanel("Kategorien");
			categoriesPanel.getItemList().setCellRenderer(new AssignedCategoriesListCellRenderer());
			categoriesPanel.getAddItemButton().setAction(new AbstractPicmanAction(
			""){
				private static final long serialVersionUID = -2686790389962380235L;

				{
					putValue(Action.SHORT_DESCRIPTION, "hinzufügen");
					putValue(Action.SMALL_ICON, GUIControl.get().getIconProvider().getImageIcon("icon.add"));
				}

				@Override
				public void actionPerformed(ActionEvent arg0) {
					EventQueue.invokeLater( new Runnable()
					{
						public void run() {
							AssignCategoriesDialog dialog = new AssignCategoriesDialog(getAssignedCategories(), true);
							dialog.setModal(true);
							dialog.setVisible(true);
							UploadPicturesDialog.this.updateAssignedCategoriesView();
						} 
					});
				}
			});
			
			categoriesPanel.getRemoveItemButton().setAction(new AbstractPicmanAction(
			""){
				private static final long serialVersionUID = 3431514097371067639L;
				{
					putValue(Action.SHORT_DESCRIPTION, "entfernen");
					putValue(Action.SMALL_ICON, GUIControl.get().getIconProvider().getImageIcon("icon.remove"));
				}
					public void actionPerformed(ActionEvent arg0) {
						JList catList = categoriesPanel.getItemList();
						DefaultListModel model = categoriesPanel.getListModel();
						int index = catList.getSelectedIndex();
						if(index>-1){
							Category cat = (Category)catList.getSelectedValue();
							model.remove(catList.getSelectedIndex());
							getAssignedCategories().remove(cat.getId());
							catList.setSelectedIndex(index);
						}
						
						if(index >= model.getSize()){
							catList.setSelectedIndex(model.getSize()-1);
						}
						updateButtonState();
					}
				
				
				
			});
			categoriesPanel.getRemoveItemButton().setEnabled(false);	
		}
		return categoriesPanel;
	}



	public JTextField getDesciptionField() {
		if(descriptiontField == null){
			descriptiontField = new JTextField();
		}
		return descriptiontField;
	}

	public JRadioButton getUnknownSourceRadioButton(){
		if(unknownSourceRadioButton == null){
			unknownSourceRadioButton = new JRadioButton("nicht bekannt");
			unknownSourceRadioButton.setEnabled(false);
		}
		return unknownSourceRadioButton;
	}

	public JRadioButton getLpvSourceRadioButton(){
		if(lpvSourceRadioButton == null){
			lpvSourceRadioButton = new JRadioButton("LPV");
			lpvSourceRadioButton.setEnabled(false);
		}
		return lpvSourceRadioButton;
	}

	public JComponent getMainPane(){
		if(mainPane == null){
			
			FormLayout layout = new FormLayout(
				    "fill:300dlu, 10dlu, fill:default:grow",
				    "fill:default,fill:default:grow");     			

			CellConstraints cc = new CellConstraints();
			PanelBuilder builder = new PanelBuilder(layout);
			builder.setDefaultDialogBorder();
			
			builder.add(getSelectPicturesPanel(),   cc.xywh(1, 1, 1, 2));
			builder.add(getCommonDataPanel(),   cc.xy(3,  1));
			builder.add(getCategoriesPanel(),   cc.xy(3,  2));
			mainPane = builder.getPanel();
		}
		return mainPane;
	}
	
	public JComponent getCommonDataPanel(){
		if(commonDataPanel == null){

		
			FormLayout layout = new FormLayout(
				    "right:pref, 3dlu, 100dlu, 3dlu, pref, 7dlu", // columns
				    "pref, 3dlu, pref, 3dlu, pref, 9dlu, pref, 3dlu, pref, 3dlu, pref,3dlu, pref,3dlu, pref,3dlu, pref,3dlu, pref,5dlu, pref,3dlu, pref,");      // rows
			
			CellConstraints cc = new CellConstraints();

			PanelBuilder builder = new PanelBuilder(layout);
			//builder.setDefaultDialogBorder();

			builder.addSeparator("Allgemeine Angaben",   cc.xyw(1,  1, 6));
			builder.addLabel("Besitzer:",       cc.xy(1,  3));
			builder.add(getUserNameField(),         cc.xy(3,  3));
			builder.addLabel("Aufnahmedatum:",       cc.xy(1,  5));
			builder.add(getCreationDateField(),         cc.xy(3,  5));
			builder.add(getPublishCheckBox(),       cc.xy(3,  7));
			builder.add(getPicutureCreditsLabel(),       cc.xy(1,  9));
			builder.add(getPictureSourceBox(),  cc.xy(3,  9));
			builder.add(getPictureSourceField(),  cc.xy(3,  11));
			builder.add(getExamplaryCheckBox(),  cc.xy(3,  13));
			builder.add(getBadExampleCheckBox(),  cc.xy(3,  15));
			builder.addLabel("Beschreibung:",     cc.xy(1,17 ));
			builder.add(getDesciptionField(), cc.xyw(3, 17, 4));
			builder.addSeparator("Originale Bilder",   cc.xyw(1,  19, 6));
			builder.add(getDeleteOriginals(),  cc.xy(3,  21));
			
			commonDataPanel = builder.getPanel();
			
			
		}
		return commonDataPanel;
	}
	
	public JPanel getChooseCategoriesPanel(){
		if(chooseCategoriesPanel == null){
			chooseCategoriesPanel = new JPanel();
			chooseCategoriesPanel.setBorder(new TitledBorder("Kategorien"));
		}
		return chooseCategoriesPanel;
	}
	
	public ListPanel getSelectPicturesPanel(){
		if(selectPicturesPanel == null){
			selectPicturesPanel = new ListPanel("Bilder-Auswahl");
			selectPicturesPanel.setCellRenderer(new PicutureListCellRenderer());

			selectPicturesPanel.getListModel().addListDataListener(new ListDataListener(){
				@Override
				public void contentsChanged(ListDataEvent e) {
				}
				@Override
				public void intervalAdded(ListDataEvent e) {
					updateButtonState();
				}
				@Override
				public void intervalRemoved(ListDataEvent e) {
					updateButtonState();
				}
				
			});
			
			selectPicturesPanel.getAddItemButton().setAction(
					new AbstractPicmanAction(
					"") {
						
						private static final long serialVersionUID = 3836109620790483193L;
						{
							putValue(Action.SHORT_DESCRIPTION, "hinzufügen");
							putValue(Action.SMALL_ICON, GUIControl.get().getIconProvider().getImageIcon("icon.add"));
						}
				@Override
				public void actionPerformed(ActionEvent arg0) {
					final JFileChooser fileChooser = new JFileChooser();

					PreviewPane previewPane = new PreviewPane();
					fileChooser.setCurrentDirectory(new File(GUIControl
							.get()
							.getProperty(PrefsKeys.GUI_LASTPATH)));
					fileChooser
							.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
					fileChooser.setMultiSelectionEnabled(true);
					fileChooser.setAccessory(previewPane);
					fileChooser.addPropertyChangeListener(previewPane);
					fileChooser.setApproveButtonText("Hinzufügen");
					fileChooser.setFileFilter(GUIControl.getPicturesFileFilter());
					fileChooser.showOpenDialog(MainFrame.getInstance());
					File[] selectedFiles = fileChooser.getSelectedFiles();
					if (selectedFiles != null) {
						
						for (File file : selectedFiles) {
						
						final String selectedFileStr = file.getAbsolutePath();
						GUIControl.get().setProperty(
								PrefsKeys.GUI_LASTPATH,
								fileChooser.getSelectedFile().getAbsolutePath());

						final JLabel imageLabel = new JLabel(selectedFileStr);
						final File selectedFile = file;
						DefaultListModel model = selectPicturesPanel.getListModel();
						model.addElement(imageLabel);
						
						Thread loadImageThread = new Thread(){
							@Override
							public void run() {
								Dimension dim = new Dimension(150, 50);
								try {
									BufferedImage thumb;
									thumb = Picture.readThumbnailFromFile(selectedFile, dim, 5);
									imageLabel.setIcon(new ImageIcon(thumb));
								} catch (Exception e) {
									ApplicationControl.displayErrorToUser(e);
								}
								
								
								EventQueue.invokeLater( new Runnable()
								{
									public void run() {
										getSelectPicturesPanel().getItemList().updateUI();
									} 
								});
								
							}
						};
						loadImageThread.start();
						}
					}
				}
			});
		}
		return selectPicturesPanel;
	}
	
	public JPanel getButtonPanel(){
	if(buttonPanel == null){
		 buttonPanel = new JPanel();
			buttonPanel.add(getCancelButton());
			buttonPanel.add(getUploadButton());
			
	}
		
		return buttonPanel;
	}
	
	public int getUserChoice() {
		return userChoice;
	}

	
	public JButton getCancelButton(){
		if(cancelButton == null){
			cancelButton = new JButton(
			new AbstractPicmanAction("Abbrechen") {
				private static final long serialVersionUID = -5777708231053835186L;

				@Override
				public void actionPerformed(ActionEvent arg0) {
					userChoice = CANCEL;
					UploadPicturesDialog.this.dispose();
				}
			});
		cancelButton.setIcon(GUIControl.get().getImageIcon("icon.cancel"));
		}
		return cancelButton;
	}

	public JButton getUploadButton(){
		if(uploadButton == null){
			uploadButton = new JButton(new AbstractPicmanAction("Bilder Hochladen") {
			private static final long serialVersionUID = 7092974175594686726L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
			
				 SwingWorker<Object, Void> worker = 
			          new SwingWorker<Object, Void>() {
					private ProgressMonitor pm;
					private int size;
					
			          public Object doInBackground() {
			        	  
			        	  MainFrame.getInstance().lock();
			        	  
			        	  UploadPicturesDialog.this.dispose();
			        	  userChoice = OK;
							
						    size = getSelectPicturesPanel().getListModel().getSize(); // 4

				            String columFormat = " %-25s %-40s %-35s %-25s %-25s %-25s %-25s %-25s";
				            StringBuilder sb = new StringBuilder();
				    		sb.append(String.format(columFormat, "UserID","Path", "Description","Insert-Date","Origin","Publish","Exemplary","Bad example")).append("\n");;

				    		
				    	    String message = "Lade...";
				    	    String note = "Bild";
				    	    String title = "";
				    	    UIManager.put("ProgressMonitor.progressText", title);
				    	    
				    	    int min = 0;
				    	    int max = size;
				    	    pm = new ProgressMonitor(UploadPicturesDialog.this, message, note, min, max);
				    	    pm.setMillisToDecideToPopup(0);
				    	    pm.setMillisToPopup(0);
				    	    
				    		
						    // Get all item objects
						    for (int i=0; i<size; i++) {
						        Object item = getSelectPicturesPanel().getListModel().getElementAt(i);
						        if(item instanceof JLabel){
						        	JLabel picture = (JLabel)item;			        	
						            RenderedImage fullSize;
									try {
									
										int userID = ApplicationControl.getInstance().getCurrentUser().getId();
										boolean exemplary = getExamplaryCheckBox().isSelected();
										boolean badExample = getBadExampleCheckBox().isSelected();
										boolean publish = getPublishCheckBox().isSelected();
										String description = getDesciptionField().getText();
										String origin = "";
										
										if(publish){
											origin = (String)getPictureSourceBox().getSelectedItem();
											if(origin.equals("neue Quelle")){
									               origin = getPictureSourceField().getText();
									        }
										}
										String pathToFile = picture.getText();

										boolean cancelled = pm.isCanceled();
							    	    
							    	    if (cancelled) {
							    	        return null;
							    	    } else {
							    	        pm.setProgress(i);
							    	        pm.setNote("Lade Bild " + (i+1) + " von " + size +" in die Datenbank");
							    	    }

							    	    
							    	    File pictureFile = new File(pathToFile);
							    	    String creationDate = GUIControl.get().getDateFormat().format(new Date(pictureFile.lastModified()));
							    	    
							    	    if(pathToFile.toLowerCase().endsWith("jpeg")|| pathToFile.toLowerCase().endsWith("jpg")){
								    		Metadata metadata = JpegMetadataReader.readMetadata(pictureFile);
											Directory exifDirectory = metadata.getDirectory(ExifDirectory.class);
											creationDate = exifDirectory.getString(ExifDirectory.TAG_DATETIME);
							    	    }
										
							    		sb.append(String.format(columFormat, userID,pathToFile,description,creationDate,origin,publish,exemplary,badExample)).append("\n");;
							           
							    		Picture pic = new Picture(userID, description, creationDate, origin, publish, exemplary, badExample);
							    	    fullSize = Picture.loadImageFromFile(pictureFile);
							            pic.setPicture(fullSize);
							            
							            
							            
							            DbController dbControl = ApplicationControl.getInstance().getDbController();
							            dbControl.insertNewPictureInDatabase(pic);
							            
							            // assign categories:
							            
							    		ArrayList<Category> categories = new ArrayList<Category>();
							    		categories.addAll(getAssignedCategories().values());
							    		Collections.sort(categories);
							    		
							    		for(Category cat : categories){
							    			// continue bei "-- keine --"
							    			if(cat.getId() > 999000)
							    				continue;
							    			dbControl.assignPictureToCategory(pic, cat);
							    		}
										ApplicationControl.getInstance().updateMasterMap();
							        	 MainFrame.getInstance().unlock();
									} catch (Exception e) {
										ApplicationControl.displayErrorToUser(e);
									} finally{
							        	  MainFrame.getInstance().unlock();

									}
						        }
						    }
			        	  return null;
			          }
			          public void done() {
			            pm.setProgress(size);
			          }
			       };
			       worker.execute();
			}
			});
			uploadButton.setEnabled(false);
			uploadButton.setFont(uploadButton.getFont().deriveFont(Font.BOLD));
			uploadButton.setIcon(GUIControl.get().getImageIcon("icon.upload"));
		}	
		return uploadButton;
	}


}



	
