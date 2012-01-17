package de.picman.gui.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JList;

import de.picman.backend.db.Picture;

public class PictureClipboardListCellRenderer extends DefaultListCellRenderer {

	private static final long serialVersionUID = -8859631096884245754L;

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {

		if(value instanceof Picture){
		
			if(isSelected){
				setForeground(Color.WHITE);
				setBackground(Color.GRAY);
			}
			else{
				setForeground(Color.GRAY);
				setBackground(Color.DARK_GRAY);
			}
			
			Picture picture = (Picture)value;
			BufferedImage imagePreview;
			try {
				imagePreview = picture.getThumbnailAsBufferedImage();
		        setIcon(new ImageIcon( imagePreview));
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));			
		}
		
		return this;
	}

}
