package de.picman.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.picman.gui.main.GUIControl;
import de.rahn.bilderdb.db.User;



public class StatusPanel extends JPanel {

	private static final long serialVersionUID = 5819841912845289820L;
	private JLabel currentUserLabel;
	private JLabel messageLabel;
	
	
	public StatusPanel() {
		
		FormLayout layout = new FormLayout(
			    "100dlu, 10dlu, 200dlu, 10dlu, fill:default:grow",
			    "pref");     			

		CellConstraints cc = new CellConstraints();
		PanelBuilder builder = new PanelBuilder(layout);
		builder.add(getCurrentUserLabel(), cc.xy(1,1));
		builder.add(getMessageLabel(), cc.xy(3,1));
		setBorder(BorderFactory.createLineBorder(Color.GRAY));
		setLayout(new BorderLayout());
		JPanel panel = builder.getPanel();
		panel.setBackground(new Color(232,232,232));
		add(panel, BorderLayout.CENTER);
	}

	public JLabel getMessageLabel(){
		if(messageLabel == null){
			messageLabel = new JLabel();
		}
		return messageLabel;
	}
	
	private JLabel getCurrentUserLabel() {
		if(currentUserLabel == null){
			currentUserLabel = new JLabel("nicht angemeldet");
			currentUserLabel.setOpaque(false);
			currentUserLabel.setIcon(GUIControl.get().getIconProvider().getImageIcon("icon.user"));
		}
		return currentUserLabel;
	}

	public void setCurrentUser(User user){
		if(user == null){
			getCurrentUserLabel().setText("nicht angemeldet");
		}
		else{
			String role = "";
			if(user.isAdmin()){
				role = " (Administrator)";
				getCurrentUserLabel().setIcon(GUIControl.get().getIconProvider().getImageIcon("icon.admin"));
			}
			else if(user.isReadonly()){
				role = " (Nur lesen)";
				getCurrentUserLabel().setIcon(GUIControl.get().getIconProvider().getImageIcon("icon.readonly"));
			}
			else{
				role = " (Benutzer)";
				getCurrentUserLabel().setIcon(GUIControl.get().getIconProvider().getImageIcon("icon.user"));
			}
			
			getCurrentUserLabel().setText(user.getLogin() + role);
		}
	}
	
	
}
