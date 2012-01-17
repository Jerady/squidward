package de.picman.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.looks.LookUtils;
import com.jgoodies.looks.Options;

import de.picman.backend.control.ApplicationControl;
import de.picman.backend.db.Picture;
import de.picman.gui.main.GUIControl;


public class PicturePropertiesEditorPanel extends JPanel {

	private static final long serialVersionUID = -1434919434542028170L;
	private Picture picture;
	private JTextField userNameField;
	private JTextField creationDateField;
	private JTextField descriptiontField;
	private JTextField pictureSourceField;
	private JLabel publishField;
	private JLabel exemplaryField;
	private JLabel badExampleField;
	private JLabel pictureField;
	
	
	public PicturePropertiesEditorPanel() {
		initComponent();
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
		
		
		JFrame testFrame = new JFrame();
		PicturePropertiesEditorPanel panel = new PicturePropertiesEditorPanel();
		
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
			    "right:pref, 3dlu, 100dlu, 3dlu, 20dlu", // columns
			    // 1     2     3     4     5    6      7    8    9     10     1    12     13  14    15     16   17   18  19  20          22    23             24    25 
			    "pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref,3dlu, pref,3dlu, pref, 3dlu,pref,3dlu,50dlu,3dlu,pref, 3dlu,  fill:default, 3dlu, pref ");      // rows
		
		CellConstraints cc = new CellConstraints();

		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();

		builder.addSeparator("Bildeigenschaften",   cc.xyw(1,  1, 5));
		builder.addLabel("Besitzer:",       cc.xy(1,  3));
		builder.add(getUserNameField(),         cc.xy(3,  3));
		builder.addLabel("Aufnahmedatum:",       cc.xy(1,  5));
		builder.add(getCreationDateField(),         cc.xy(3,  5));
		builder.addLabel("Darf veröffentlicht werden:",       cc.xy(1, 7));
		builder.add(getPublishField(),       cc.xy(3,  7));
		builder.addLabel("Quelle:",       cc.xy(1,  9));
		builder.add(getPictureSourceField(),  cc.xy(3,  9));
		builder.addLabel("Exemplarisch:",       cc.xy(1,  11));
		builder.add(getExemplaryField(),  cc.xy(3,  11));
		builder.addLabel("Schlechtes Beispiel:",       cc.xy(1,  13));
		builder.add(getBadExampleField(),  cc.xy(3,  13));
		builder.addLabel("Beschreibung:",     cc.xy(1,15 ));
		builder.add(getDescriptiontField(), cc.xy(3, 15));
		
		add(builder.getPanel(), BorderLayout.CENTER);
	}
	
	
	
	

	private JLabel getPictureField() {
		if(pictureField == null){
			pictureField = new JLabel();
			
		}
		return pictureField;
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

	private JTextField getUserNameField() {
		if(userNameField == null){
			userNameField = new JTextField();
			userNameField.setEditable(false);
			userNameField.setEnabled(false);
			setTextFieldProperties(userNameField);
		}
		return userNameField;
	}

	private JTextField getCreationDateField() {
		if(creationDateField == null){
			creationDateField = new JTextField();
			creationDateField.setEditable(false);
			creationDateField.setEnabled(false);
			setTextFieldProperties(creationDateField);
		}
		return creationDateField;
	}

	private JTextField getDescriptiontField() {
		if(descriptiontField == null){
			descriptiontField = new JTextField();
			descriptiontField.setEditable(false);
			descriptiontField.setEnabled(false);
			setTextFieldProperties(descriptiontField);
		}
		return descriptiontField;
	}

	private JTextField getPictureSourceField() {
		if(pictureSourceField == null){
			pictureSourceField = new JTextField();
			pictureSourceField.setEditable(false);
			pictureSourceField.setEnabled(false);
			setTextFieldProperties(pictureSourceField);
		}
		return pictureSourceField;
	}
	
	private void setTextFieldProperties(JComponent field){
		field.setFont(field.getFont().deriveFont(Font.BOLD));

	}
	
	public void setPicture(Picture picture){
		this.picture = picture;

		SwingWorker<Object, Void> worker = 
	          new SwingWorker<Object, Void>() {
			public Object doInBackground() {
				ImageIcon yesIcon = GUIControl.get().getIconProvider().getImageIcon("icon.yes");
				ImageIcon noIcon = GUIControl.get().getIconProvider().getImageIcon("icon.no");
				ImageIcon badIcon = GUIControl.get().getIconProvider().getImageIcon("icon.bad");
				ImageIcon goodIcon = GUIControl.get().getIconProvider().getImageIcon("icon.good");
				
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
		
				
				getBadExampleField().setText((getPicture().isBadExample())?"Ja":"Nein");
				getBadExampleField().setIcon((getPicture().isBadExample())?goodIcon:badIcon);
				getExemplaryField().setText((getPicture().isExemplary())?"Ja":"Nein");
				getExemplaryField().setIcon((getPicture().isExemplary())?goodIcon:badIcon);
				getPublishField().setText((getPicture().isPublication())?"Ja":"Nein");
				getPublishField().setIcon((getPicture().isPublication())?yesIcon:noIcon);
				
				BufferedImage image = null;
				try {
					image = getPicture().getPreviewAsBufferedImage();
				} catch (Exception e) {
					ApplicationControl.displayErrorToUser(e);
				}
				getPictureField().setIcon(new ImageIcon(image));
				
				
				getUserNameField().setEditable(true);
				getUserNameField().setEnabled(true);
				getCreationDateField().setEditable(true);
				getCreationDateField().setEnabled(true);
				getPictureSourceField().setEditable(true);
				getPictureSourceField().setEnabled(true);
				getDescriptiontField().setEditable(true);
				getDescriptiontField().setEnabled(true);

				
				
				
				return null;
		
					}
			public void done() {
			}
		};
		
		worker.execute();
	}
	
	public void reset(){
		getUserNameField().setText("");
		getUserNameField().setEditable(false);
		getUserNameField().setEnabled(false);
		getCreationDateField().setText("");
		getCreationDateField().setEditable(false);
		getCreationDateField().setEnabled(false);
		getPictureSourceField().setText("");
		getPictureSourceField().setEditable(false);
		getPictureSourceField().setEnabled(false);
		getDescriptiontField().setText("");
		getDescriptiontField().setEditable(false);
		getDescriptiontField().setEnabled(false);

		getBadExampleField().setText("");
		getBadExampleField().setIcon(null);
		getExemplaryField().setText("");
		getExemplaryField().setIcon(null);
		getPublishField().setText("");
		getPublishField().setIcon(null);
		getPictureField().setIcon(null);
	}
	
	private Picture getPicture(){
		return picture;
	}
}
