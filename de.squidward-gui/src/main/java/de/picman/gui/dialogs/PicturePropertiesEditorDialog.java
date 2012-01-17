package de.picman.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.picman.backend.control.ApplicationControl;
import de.picman.backend.db.DbController;
import de.picman.backend.db.Picture;
import de.picman.backend.db.User;
import de.picman.gui.actions.AbstractPicmanAction;
import de.picman.gui.components.MainFrame;
import de.picman.gui.main.GUIControl;


public class PicturePropertiesEditorDialog extends JDialog {

	private static final long serialVersionUID = -156450643363148288L;
	public final static int CANCEL = 1;
	public final static int OK = 2;
	private int userChoice = 0;
	private JButton applyButton;
	private JButton cancelButton;
	private JPanel buttonPanel;
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
	private DefaultMutableTreeNode assignedCategoriesRootNode;
	private Picture picture;
	
	public PicturePropertiesEditorDialog(Picture picture) {
		super(MainFrame.getInstance());
		setModal(true);
		setTitle("Bildeigenschaften ändern");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		getPictureOriginGroup();
		add(getCommonDataPanel(), BorderLayout.CENTER);
		add(getButtonPanel(), BorderLayout.SOUTH);
		setResizable(false);
		setLocationByPlatform(true);
		setLocationRelativeTo(MainFrame.getInstance().getPicturePropertiesPanel());
		setPicture(picture);
		
		pack();
	}


		private Picture getPicture(){
			return picture;
		}
		
		public void setPicture(Picture picture){
			this.picture = picture;

			SwingWorker<Object, Void> worker = 
		          new SwingWorker<Object, Void>() {
				public Object doInBackground() {
					
					int userId = getPicture().getUserId();
					String userName = "";
					
					try {
						userName = ApplicationControl.getInstance().getDbController().getNameOfUser(userId);
					} catch (Exception e1) {
						ApplicationControl.displayErrorToUser(e1);
					}
					
					getUserNameField().setText(userName);
					getCreationDateField().setText(getPicture().getCreationDate());
					if(getPicture().getOrigin().equals("")){
						getPictureSourceField().setText("-");
					}
					else{
						getPictureSourceField().setText(getPicture().getOrigin());
					}
					
					
					if(getPicture().getDescription().equals("")){
						getDescriptionField().setText("---");	
					}
					else{
						getDescriptionField().setText(getPicture().getDescription());	
					}
			
					getBadExampleCheckBox().setSelected(getPicture().isBadExample());
					getExamplaryCheckBox().setSelected(getPicture().isExemplary());
					getPublishCheckBox().setSelected(getPicture().isPublication());

					return null;
			
						}
				public void done() {
				}
			};
			
			worker.execute();
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
	
	
	
	
	
	public JTextField getDescriptionField() {
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
	
	public JComponent getCommonDataPanel(){
		if(commonDataPanel == null){

		
			FormLayout layout = new FormLayout(
				    "right:pref, 3dlu, 100dlu, 3dlu, pref, 7dlu", // columns
				    "pref, 3dlu, pref, 3dlu, pref, 9dlu, pref, 3dlu, pref, 3dlu, pref,3dlu, pref,3dlu, pref,3dlu, pref,3dlu, pref,5dlu, pref,3dlu, pref,");      // rows
			
			CellConstraints cc = new CellConstraints();

			PanelBuilder builder = new PanelBuilder(layout);
			builder.setDefaultDialogBorder();

			builder.addSeparator("Bildeigenschaften",   cc.xyw(1,  1, 6));
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
			builder.add(getDescriptionField(), cc.xyw(3, 17, 4));
			
			commonDataPanel = builder.getPanel();
			
			
		}
		return commonDataPanel;
	}
	
		
	public JPanel getButtonPanel(){
	if(buttonPanel == null){
		 buttonPanel = new JPanel();
			buttonPanel.add(getCancelButton());
			buttonPanel.add(getApplyButton());
			
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
				private static final long serialVersionUID = -3571879043473320965L;

				@Override
				public void actionPerformed(ActionEvent arg0) {
					userChoice = CANCEL;
					PicturePropertiesEditorDialog.this.dispose();
				}
			});
		cancelButton.setIcon(GUIControl.get().getImageIcon("icon.cancel"));
		}
		return cancelButton;
	}

	public JButton getApplyButton(){
		if(applyButton == null){
			applyButton = new JButton(new AbstractPicmanAction("†bernehmen") {
				private static final long serialVersionUID = 4965041266564747836L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
			
				 SwingWorker<Object, Void> worker = 
			          new SwingWorker<Object, Void>() {
					
			          public Object doInBackground() {
			        	  PicturePropertiesEditorDialog.this.dispose();
			        	  userChoice = OK;
							

				            String columFormat = " %-25s %-40s %-35s %-25s %-25s %-25s %-25s %-25s";
				            StringBuilder sb = new StringBuilder();
				    		sb.append(String.format(columFormat, "UserID","Path", "Description","Insert-Date","Origin","Publish","Exemplary","Bad example")).append("\n");;				    		
				    		try {
								boolean exemplary = getExamplaryCheckBox().isSelected();
								boolean badExample = getBadExampleCheckBox().isSelected();
								boolean publish = getPublishCheckBox().isSelected();
								String description = getDescriptionField().getText();
								String origin = "";
								
								if(publish){
									origin = (String)getPictureSourceBox().getSelectedItem();
									if(origin.equals("neue Quelle")){
							               origin = getPictureSourceField().getText();
							        }
								}

								String creationDate = getCreationDateField().getText();

								getPicture().setDescription(description);
								getPicture().setCreationDate(creationDate);
								getPicture().setOrigin(origin);
								getPicture().setPublication(publish);
								getPicture().setExemplary(exemplary);
								getPicture().setBadExample(badExample);
					            
					            DbController dbControl = ApplicationControl.getInstance().getDbController();
					            dbControl.updatePicture(getPicture());

					            ApplicationControl.getInstance().updateMasterMap();

					            MainFrame.getInstance().getPicturePropertiesPanel().setPicture(getPicture());
					            
							} catch (IOException e) {
								e.printStackTrace();
							} catch (Exception e) {
								e.printStackTrace();
							}
			        	  return null;
			          }
			       };
			       worker.execute();
			}
			});
			applyButton.setFont(applyButton.getFont().deriveFont(Font.BOLD));
			applyButton.setIcon(GUIControl.get().getImageIcon("icon.yes"));
		}	
		return applyButton;
	}


}



	
