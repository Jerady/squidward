package de.picman.gui.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import de.picman.gui.main.GUIControl;
import de.rahn.bilderdb.db.User;

public class UserListCellRenderer extends DefaultListCellRenderer {
	
	private static final long serialVersionUID = 1L;

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		
		if(value instanceof User){
			User user = (User)value;
		
			if(isSelected){
				setBackground(Color.ORANGE);
			}
			else{
				setBackground(Color.WHITE);
			}
			setText(user.getFullname());
			setForeground(Color.BLACK);
			if(user.isAdmin()){
				setIcon(GUIControl.get().getImageIcon("icon.admin"));
			}else if(user.isReadonly()){
					setIcon(GUIControl.get().getImageIcon("icon.readonly"));
			}else{
				setIcon(GUIControl.get().getImageIcon("icon.user"));
				
			}
		}
		return this;
	}

}
