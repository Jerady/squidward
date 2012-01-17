package de.picman.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.picman.backend.control.ApplicationControl;
import de.picman.backend.db.Category;
import de.picman.backend.db.DbController;
import de.picman.backend.db.Picture;
import de.picman.backend.db.User;
import de.picman.gui.components.MainFrame;
import de.picman.gui.dialogs.AssignCategoriesDialog;
import de.picman.gui.dialogs.PicturePropertiesEditorDialog;
import de.picman.gui.main.GUIControl;
import de.picman.gui.providers.ActionProvider;
import de.picman.gui.renderer.AssignedCategoriesListCellRenderer;


public class PicturePropertiesPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Picture picture;
	private JLabel userNameField;
	private JLabel creationDateField;
	private JLabel insertedDateField;
	private JTextArea descriptiontField;
	private JLabel pictureSourceField;
	private JLabel publishField;
	private JLabel exemplaryField;
	private JLabel badExampleField;
	private JLabel pictureField;
	private JButton deletePictureButton;
	private JButton openInEditorButton;
	private JList assignedCategoriesList;
	private JButton editPicturePropertiesButton;
	private JButton editAssignedCategoriesButton;
	
	
	public PicturePropertiesPanel() {
		initComponent();
	}
	
	public static void main(String[] args) {
		GUIControl.setLookAndFeel();
		
		JFrame testFrame = new JFrame();
		PicturePropertiesPanel panel = new PicturePropertiesPanel();
		
		testFrame.add(panel);
		testFrame.pack();
		testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		testFrame.setVisible(true);
	}

	private void initComponent(){
		
		setBackground(new Color(232,232,232));
		setOpaque(false);
		setLayout(new BorderLayout());
		
		FormLayout layout = new FormLayout(
			    "right:pref, 3dlu, 80dlu, 3dlu, 20dlu", // columns
			    // 1     2     3     4     5    6      7    8    9     10    11    12    13  14     15   16   17     18  19    20    21   22   23    24    25       26            27   28 
			    "pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu,pref, 3dlu, pref, 3dlu, pref,3dlu, pref,3dlu, pref, 3dlu,pref,3dlu,100dlu,3dlu,pref, 3dlu,  fill:default, 3dlu, pref ");      // rows
		
		CellConstraints cc = new CellConstraints();

		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();

		builder.addSeparator("Bildeigenschaften",   cc.xyw(1,  1, 3));
		builder.add(getEditPicturePropertiesButton(), cc.xy(5,1));
		builder.addLabel("Besitzer:",       cc.xy(1,  3));
		builder.add(getUserNameField(),         cc.xy(3,  3));
		builder.addLabel("Aufnahmedatum:",       cc.xy(1,  5));
		builder.add(getCreationDateField(),         cc.xy(3,  5));
		builder.addLabel("Eingefügt am:",       cc.xy(1,  7));
		builder.add(getInsertedDateField(),         cc.xy(3,  7));
		builder.addLabel("Darf veröffentlicht werden:",       cc.xy(1, 9));
		builder.add(getPublishField(),       cc.xy(3,  9));
		builder.addLabel("Quelle:",       cc.xy(1,  11));
		builder.add(getPictureSourceField(),  cc.xy(3,  11));
		builder.addLabel("Exemplarisch:",       cc.xy(1,  13));
		builder.add(getExemplaryField(),  cc.xy(3,  13));
		builder.addLabel("Schlechtes Beispiel:",       cc.xy(1,  15));
		builder.add(getBadExampleField(),  cc.xy(3,  15));
		
		
		JLabel descLabel = new JLabel("Beschreibung:");
		descLabel.setVerticalAlignment(JLabel.TOP);
		builder.add(descLabel,     cc.xy(1,17 ));
		
		JScrollPane descScrollPane = new JScrollPane(getDescriptiontField());
		builder.add(descScrollPane, cc.xy(3, 17));
	
		
		builder.addSeparator("Kategorien",   cc.xyw(1,  19, 3));
		builder.add(getEditAssignedCategoriesButton(), cc.xy(5,19));
		builder.add(new JScrollPane( getAssignedCategoriesList()), cc.xyw(1,21,5));

		
		builder.addSeparator("Bildvorschau",   cc.xyw(1,  23, 3));
		builder.add(getOpenInEditorButton(), cc.xy(5,23));
		builder.add(getPictureField(), cc.xyw(1, 25,5));
		
		User currentUser = ApplicationControl.getInstance().getCurrentUser();
		
		if(currentUser!=null){
			if(currentUser.isAdmin()){
				builder.add(getDeletePictureButton(), cc.xy(3,27));
			}
		}
		add(builder.getPanel(), BorderLayout.CENTER);
	}
	
	
	
	protected JList getAssignedCategoriesList() {
		if(assignedCategoriesList == null){
			assignedCategoriesList = new JList();
			assignedCategoriesList.setCellRenderer(new AssignedCategoriesListCellRenderer());
			DefaultListModel listModel = new DefaultListModel();
			assignedCategoriesList.setModel(listModel);
			assignedCategoriesList.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
		}
		
		return assignedCategoriesList;
	}

	private JLabel getPictureField() {
		if(pictureField == null){
			pictureField = new JLabel();
			
		}
		return pictureField;
	}

	protected JComponent getEditPicturePropertiesPanel(){
		FormLayout layout = new FormLayout(
			    "fill:default:grow, right:pref", // columns
			    // 1   
			    "pref");
		CellConstraints cc = new CellConstraints();

		PanelBuilder builder = new PanelBuilder(layout);
		builder.add(getEditPicturePropertiesButton(), cc.xy(2, 1));
		JPanel panel = builder.getPanel();
		panel.setOpaque(true);
		return panel;
	}

	
	protected JButton getEditPicturePropertiesButton() {
		if(editPicturePropertiesButton == null){
			editPicturePropertiesButton = new JButton();
			editPicturePropertiesButton.setEnabled(false);
			editPicturePropertiesButton.setIcon(GUIControl.get().getIconProvider().getImageIcon("icon.edit"));
			editPicturePropertiesButton.setToolTipText("Bildeigenschaften ändern");
			editPicturePropertiesButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					 SwingWorker<Object, Void> worker = 
				          new SwingWorker<Object, Void>() {
				          public Object doInBackground() {
							MainFrame.getInstance().lockLight();
							
							PicturePropertiesEditorDialog dialog = new PicturePropertiesEditorDialog(getPicture());
							dialog.setLocationRelativeTo(PicturePropertiesPanel.this.getEditPicturePropertiesButton());
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
					});
				}
		return editPicturePropertiesButton;
	}

	protected JButton getEditAssignedCategoriesButton() {
		if(editAssignedCategoriesButton == null){
			editAssignedCategoriesButton = new JButton();
			editAssignedCategoriesButton.setEnabled(false);
			editAssignedCategoriesButton.setIcon(GUIControl.get().getIconProvider().getImageIcon("icon.edit"));
			editAssignedCategoriesButton.setToolTipText("Zugeordnete Kategorien ändern");
			editAssignedCategoriesButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					 SwingWorker<Object, Void> worker = 
				          new SwingWorker<Object, Void>() {
				          public Object doInBackground() {
							MainFrame.getInstance().lockLight();
							
							Integer[] catIds = new Integer[0];
							try{
								catIds = ApplicationControl.getInstance().getDbController().getIdsOfCategoriesContainingPictures(getPicture());
							} catch (Exception e2) {
								ApplicationControl.displayErrorToUser(e2);
								MainFrame.getInstance().unlock();
								MainFrame.getInstance().getPictureDisplayPanel().requestFocus();
							}

							Map<Integer, Category> categories = ApplicationControl.getInstance().getDbController().getCategories();
							Map<Integer, Category> assignedCategories = new HashMap<Integer, Category>();
							
							for (int i = 0; i < catIds.length; i++) {

								Category cat = categories.get(catIds[i]);
								if(cat==null)
									continue;
								assignedCategories.put(catIds[i], cat);
							}
							try {

								DbController dbControl = ApplicationControl.getInstance().getDbController();
					            // remove assigned categories:
					    		Set<Integer> entrySet = assignedCategories.keySet();
					    		for(Integer id : entrySet){
									dbControl.removePictureFromCategory(getPicture(), assignedCategories.get(id));
					    		}
					    		
								AssignCategoriesDialog dialog = new AssignCategoriesDialog(assignedCategories, false);
								dialog.setLocationRelativeTo(PicturePropertiesPanel.this.getEditAssignedCategoriesButton());
								dialog.setModal(true);
								dialog.setVisible(true);
								
					            // re-assign categories:
					    		entrySet = assignedCategories.keySet();
					    		for(Integer id : entrySet){
										dbControl.assignPictureToCategory(getPicture(), assignedCategories.get(id));
										System.out.println("PicturePropertiesPanel: Assigning category ID" + id + " to picture ID" + getPicture().getId());
					    		}
								ApplicationControl.getInstance().updateMasterMap();
								setPicture(getPicture());
							} catch (Exception e) {
								ApplicationControl.displayErrorToUser(e);
								MainFrame.getInstance().unlock();
							}
							finally{
								MainFrame.getInstance().getPictureDisplayPanel().requestFocus();
							}
				  return null;
			          }
			          public void done() {
							MainFrame.getInstance().unlockLight();
			          }
			       };
			       worker.execute();
					}
				});
		}
		return editAssignedCategoriesButton;
	}
	
	private JButton getOpenInEditorButton(){
		if(openInEditorButton == null){
			openInEditorButton = new JButton("");
			openInEditorButton.setIcon(GUIControl.get().getIconProvider().getImageIcon("icon.search"));
			openInEditorButton.setToolTipText("Bild in externem Editor ansehen/bearbeiten");
			openInEditorButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					 SwingWorker<Object, Void> worker = 
				          new SwingWorker<Object, Void>() {
				          public Object doInBackground() {
				   
								MainFrame.getInstance().lockLight();
													try {
								File pic = getPicture().getAsTempFile(null);
								Desktop.getDesktop().edit(pic);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							finally{
								MainFrame.getInstance().unlockLight();
							}
							  return null;
			          }
			          public void done() {
			          }
			       };
			       worker.execute();
				}
			});
		}
		openInEditorButton.setEnabled(false);
		return openInEditorButton;
	}
	
	private JButton getDeletePictureButton(){
		if(deletePictureButton == null){
			deletePictureButton = new JButton(ActionProvider.getDeletePictureAction());
			deletePictureButton.setEnabled(false);
		}
		return deletePictureButton;
	}
	
	private JLabel getPublishField() {
		if(publishField == null){
			publishField = new JLabel();
			setTextFieldProperties(publishField);
		}
		return publishField;
	}

	private JLabel getExemplaryField() {
		if(exemplaryField == null){
			exemplaryField = new JLabel();
			setTextFieldProperties(exemplaryField);
		}
		return exemplaryField;
	}

	private JLabel getBadExampleField() {
		if(badExampleField == null){
			badExampleField = new JLabel();
			setTextFieldProperties(badExampleField);
		}
		return badExampleField;
	}

	private JLabel getUserNameField() {
		if(userNameField == null){
			userNameField = new JLabel();
			setTextFieldProperties(userNameField);
		}
		return userNameField;
	}

	private JLabel getCreationDateField() {
		if(creationDateField == null){
			creationDateField = new JLabel();
			setTextFieldProperties(creationDateField);
		}
		return creationDateField;
	}

	private JLabel getInsertedDateField() {
		if(insertedDateField == null){
			insertedDateField = new JLabel();
			setTextFieldProperties(insertedDateField);
		}
		return insertedDateField;
	}
	
	private JTextArea getDescriptiontField() {
		if(descriptiontField == null){
			descriptiontField = new JTextArea();
			descriptiontField.setPreferredSize(new Dimension(120,100));
			setTextFieldProperties(descriptiontField);
			descriptiontField.setEditable(false);
			descriptiontField.setLineWrap(true);
		}
		return descriptiontField;
	}

	private JLabel getPictureSourceField() {
		if(pictureSourceField == null){
			pictureSourceField = new JLabel();
			setTextFieldProperties(pictureSourceField);
		}
		return pictureSourceField;
	}
	
	private void setTextFieldProperties(JComponent field){
		field.setFont(field.getFont().deriveFont(Font.BOLD));

	}
	
	public void setPicture(Picture picture){
		
		this.picture = picture;
		reset();
		
		
		SwingWorker<Object, Void> worker = 
	          new SwingWorker<Object, Void>() {
			public Object doInBackground() {
				
				// Update Picture Properties
				int userId = getPicture().getUserId();
				String userName = "";
				
				try {
					userName = ApplicationControl.getInstance().getDbController().getNameOfUser(userId);
				} catch (Exception e1) {
					ApplicationControl.displayErrorToUser(e1);
				}
				
				getUserNameField().setText(userName);
				getCreationDateField().setText(getPicture().getCreationDate());
				getInsertedDateField().setText(GUIControl.get().getDateFormat().format(getPicture().getSavingDate()));
				if(getPicture().getOrigin().equals("")){
					getPictureSourceField().setText("---");
				}
				else{
					getPictureSourceField().setText(getPicture().getOrigin());
				}
				
				
				if(getPicture().getDescription().equals("")){
					getDescriptiontField().setText("---");	
				}
				else{
					getDescriptiontField().setText(getPicture().getDescription());	
				}
				
				ImageIcon yesIcon = GUIControl.get().getIconProvider().getImageIcon("icon.yes");
				ImageIcon noIcon = GUIControl.get().getIconProvider().getImageIcon("icon.no");
				ImageIcon badIcon = GUIControl.get().getIconProvider().getImageIcon("icon.bad");
				ImageIcon goodIcon = GUIControl.get().getIconProvider().getImageIcon("icon.good");

				getBadExampleField().setText((getPicture().isBadExample())?"Ja":"Nein");
				getBadExampleField().setIcon((getPicture().isBadExample())?goodIcon:badIcon);
				getExemplaryField().setText((getPicture().isExemplary())?"Ja":"Nein");
				getExemplaryField().setIcon((getPicture().isExemplary())?goodIcon:badIcon);
				getPublishField().setText((getPicture().isPublication())?"Ja":"Nein");
				getPublishField().setIcon((getPicture().isPublication())?yesIcon:noIcon);
				getDeletePictureButton().setEnabled(!getPicture().isDeleted());
				getOpenInEditorButton().setEnabled(true);
				
				getEditPicturePropertiesButton().setEnabled(!ApplicationControl.getInstance().getCurrentUser().isReadonly());
				getEditAssignedCategoriesButton().setEnabled(!ApplicationControl.getInstance().getCurrentUser().isReadonly());
				getDeletePictureButton().setEnabled(!ApplicationControl.getInstance().getCurrentUser().isReadonly());

				// Update Preview Image
				BufferedImage tumb = null;
				try {
					Dimension dim = ApplicationControl.getInstance().getPicturePreviewDimension();
					int width = dim.width;
					int height = dim.height;
				
					tumb = getPicture().getScaledThumbnailAsBufferedImage(width, height);
					
					
					
					Graphics2D graphics2D = (Graphics2D)tumb.getGraphics();
					graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						        RenderingHints.VALUE_ANTIALIAS_ON);
					Font font = new Font("Arial", Font.BOLD, 18);
					graphics2D.setColor(new Color(100,100,100));
					graphics2D.fillRect(0, 0, width, 30);
					graphics2D.setFont(font);
					graphics2D.setColor(new Color(255,140,0));
					graphics2D.drawString("Lade Details..." , 20, 20);
					getPictureField().setIcon(new ImageIcon(tumb));
				} catch (Exception e3) {
					e3.printStackTrace();
				}

				BufferedImage image = null;
				try {
					image = getPicture().getPreviewAsBufferedImage();
				} catch (Exception e) {
					ApplicationControl.displayErrorToUser(e);
				}
				getPictureField().setIcon(new ImageIcon(image));
				
				// Update Picture Categories
				DefaultListModel model = (DefaultListModel)getAssignedCategoriesList().getModel();
				model.removeAllElements();
				
				Integer[] catIds = new Integer[0];
				try{
					catIds = ApplicationControl.getInstance().getDbController().getIdsOfCategoriesContainingPictures(getPicture());
				} catch (Exception e2) {
					ApplicationControl.displayErrorToUser(e2);
				}
				Map<Integer, Category> categories = ApplicationControl.getInstance().getDbController().getCategories();
				for (int i = 0; i < catIds.length; i++) {

					Category cat = categories.get(catIds[i]);
					if(cat==null)
						continue;
					model.addElement(cat);
				}
				
				return null;
			}
			public void done() {
			}
		};
		worker.execute();
//		while(!worker.isDone()){
//			if(!MainFrame.getInstance().isLocked()){
//				MainFrame.getInstance().lock();
//			}
//			
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
			
		
	}
	
	public void reset(){
		getUserNameField().setText("");
		getCreationDateField().setText("");
		getInsertedDateField().setText("");
		getPictureSourceField().setText("");
		getDescriptiontField().setText("");
		getBadExampleField().setText("");
		getBadExampleField().setIcon(null);
		getExemplaryField().setText("");
		getExemplaryField().setIcon(null);
		getPublishField().setText("");
		getPublishField().setIcon(null);
		getPictureField().setIcon(null);
		getDeletePictureButton().setEnabled(false);
		getOpenInEditorButton().setEnabled(false);
		DefaultListModel model = (DefaultListModel)getAssignedCategoriesList().getModel();
		model.removeAllElements();
		
		getEditAssignedCategoriesButton().setEnabled(false);
		getEditPicturePropertiesButton().setEnabled(false);
	}
	
	private Picture getPicture(){
		return picture;
	}
}
