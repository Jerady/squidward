package de.picman.gui.components;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.border.Border;

public class BackgroundImageBorder implements Border {
    private final BufferedImage image;
	int x1,y1;
 
    public BackgroundImageBorder(BufferedImage image) {
        this.image = image;
    }
 
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        x1 =x+ (width-image.getWidth())/2;
        y1 =y+ (height-image.getHeight())/2;
        ((Graphics2D) g).drawRenderedImage(image, AffineTransform.getTranslateInstance(x,y));
    }
 
    public Insets getBorderInsets(Component c) {
        return new Insets(0,0,100,200);
    }
 
    public boolean isBorderOpaque() {
        return true;
    }
}
