package de.picman.gui.renderer;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JList;

import de.picman.gui.utils.ImageUtils;
import de.rahn.bilderdb.db.Picture;

public class PictureDisplayCellRenderer extends DefaultListCellRenderer {

	private static final long serialVersionUID = -8893951647157426077L;
	
	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
			if(value instanceof Picture){
				if(isSelected){
					setForeground(Color.WHITE);
					setBackground(new Color(60,60,60));
				}
				else{
					setForeground(Color.GRAY);
					setBackground(Color.DARK_GRAY);
				}

				Picture picture = (Picture)value;
				BufferedImage imagePreview;
				try {
					imagePreview = picture.getThumbnailAsBufferedImage();
			        imagePreview = ImageUtils.createReflection(imagePreview);
					if(picture.isDeleted()){
						BufferedImage imageTrans = new BufferedImage(imagePreview.getWidth(), imagePreview.getHeight(),
				                BufferedImage.TRANSLUCENT);
				        Graphics2D g = imageTrans.createGraphics();  
				        float transperancy = 0.2f;
				        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transperancy));  
				        g.drawImage(imagePreview, null, 0, 0);  
				        g.dispose();  
						setIcon(new ImageIcon( imageTrans));
					}
					else{
						setIcon(new ImageIcon( imagePreview));
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				
				setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));			
		}
		
		return this;
	}

}
