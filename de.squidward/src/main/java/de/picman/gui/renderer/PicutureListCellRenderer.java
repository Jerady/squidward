package de.picman.gui.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

public class PicutureListCellRenderer extends DefaultListCellRenderer {
	
	private static final long serialVersionUID = -1321973800349308150L;

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		
		if(value instanceof JLabel){
			JLabel label = (JLabel)value;
		
			if(isSelected){
				setForeground(Color.WHITE);
				setBackground(Color.GRAY);
			}
			else{
				setForeground(Color.GRAY);
				setBackground(Color.DARK_GRAY);
			}
			setBorder(BorderFactory.createEtchedBorder());
			setText(label.getText());
			setIcon(label.getIcon());
			
			repaint();
			
		}
		return this;
	}

}
