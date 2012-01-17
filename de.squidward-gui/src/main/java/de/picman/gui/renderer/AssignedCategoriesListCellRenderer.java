package de.picman.gui.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import de.picman.backend.db.Category;
import de.picman.gui.main.GUIControl;

public class AssignedCategoriesListCellRenderer extends DefaultListCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {

		if(value instanceof Category){
			Category cat = (Category)value;
			
			setIcon(GUIControl.get().getImageIcon("icon.category.blue"));
	    	setFont(new Font("Monospace", Font.BOLD, 12));
	    	setForeground(Color.BLACK);
	        setText(cat.getPathAsString( " / "));
		}
		
		 if(isSelected){
	    	   setBackground(Color.ORANGE);
	       }else{
	    	   setBackground(Color.WHITE);
	       }
		setOpaque(true);
		return this;
	}	
	

}
