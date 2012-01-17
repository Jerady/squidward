/*
 * Created on 05.07.2008
 *
 * SVN header information:
 *  $Author$
 *  $Rev$
 *  $Date$
 *  $Id$
 */
package de.picman.backend.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.sun.media.jai.codec.FileCacheSeekableStream;

import de.picman.backend.db.Picture;

public class PreviewPane extends JPanel implements PropertyChangeListener {

    private static final long serialVersionUID = -2345289114340774249L;
    private JLabel label;
    private int maxImgWidth;
    protected Thread lastThread = null;
    
    protected HashMap<String, Icon> pathToImage = new HashMap<String, Icon>();

    public PreviewPane() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(new JLabel("Preview:"), BorderLayout.NORTH);
        label = new PreviewImage();
        label.setBackground(Color.WHITE);
        label.setOpaque(true);
        label.setPreferredSize(new Dimension(300,225));
        maxImgWidth = 195;
        label.setBorder(BorderFactory.createEtchedBorder());
        add(label, BorderLayout.CENTER);
    }
    
    protected class PreviewImage extends JLabel {

        private static final long serialVersionUID = 3114259369757257045L;
        
        @Override
        public void paint(Graphics g1) {
            int width = this.getWidth();
            int height = this.getHeight();
            
            Graphics2D g = (Graphics2D)g1;
            
            if (g==null)
                return;
            
            getBorder().paintBorder(this, g, 1, 1, width-2, height-2);
            
            if (this.getBackground()!=null)
                g.setColor(this.getBackground());
            else 
                g.setColor(Color.WHITE);
            
            getBorder().paintBorder(this, g, 1, 1, width-2, height-2);
            
            g.fillRect(0, 0, width, height);
            
            ImageIcon icon = (ImageIcon)this.getIcon();
            
            if (icon==null)
                return;
            
            int iconHeight = icon.getIconHeight();
            int iconWidth = icon.getIconWidth();
            
            int widthOffset = (Math.max(width, iconWidth) - Math.min(width, iconWidth))/2;
            int heightOffset = (Math.max(height, iconHeight) - Math.min(height, iconHeight))/2;
            
            g.drawImage(icon.getImage(), widthOffset, heightOffset, null);
        }
        
    }

    public void propertyChange(PropertyChangeEvent evt) {

        if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(evt
                .getPropertyName())) {
            final File newFile = (File) evt.getNewValue();
            if (newFile != null) {
                final String path = newFile.getAbsolutePath();
                if (path.toLowerCase().endsWith(".gif") || path.toLowerCase().endsWith(".jpg")
                        || path.toLowerCase().endsWith(".png") || path.toLowerCase().endsWith(".bmp") || path.toLowerCase().endsWith(".tif")) {

                    Thread readImg = new Thread() {

                        @Override
                        public void run() {
                            Icon icon = pathToImage.get(path);
                            if (icon==null){
                                try {
                                    try {
                                        Thread.sleep(50);
                                    } catch (InterruptedException e) {
                                        return;
                                    }
                                    
                                    BufferedImage bi = Picture.readThumbnailFromFile(newFile, label.getSize(), 7);
                                    
                                    if (bi==null){
	                                    FileCacheSeekableStream stream = null;
	                                    try {
	                                        stream = new FileCacheSeekableStream(new FileInputStream(path));
	                                        System.out.println("opening: " + path);
	                                    } catch (IOException e) {
	                                        e.printStackTrace();
	                                    }
	                                    
	                                    if (stream==null)
	                                        return;
	                                    
	                                    RenderedImage image = JAI.create("stream", stream);
	//                                    BufferedImage img = ImageIO.read(newFile);
	                                    float width = image.getWidth();
	                                    float height = image.getHeight();
	//                                    float scale = height / width;
	//                                    width = Math.min(maxImgWidth,width);
	//                                    height = (width * scale); // height should be scaled from new width
	                                    try {
	                                        Thread.sleep(20);
	                                    } catch (InterruptedException e) {
	                                        return;
	                                    }
	                                    
	                                    float maxWidth  = Math.min(maxImgWidth,width);
	                                    float maxHeight = Math.min(height,height*maxImgWidth/width);
	                                    
	                                    ParameterBlock params = new ParameterBlock();
	                                    params.addSource(image);
	                                    params.add(maxWidth/width);         // x scale factor
	                                    params.add(maxHeight/height);         // y scale factor
	                                    params.add(0.0F);         // x translate
	                                    params.add(0.0F);         // y translate
	                                    params.add(Interpolation.getInstance(Interpolation.INTERP_BICUBIC_2));       // interpolation method
	
	                                    /* Create an operator to scale image1. */
	                                    RenderedOp image2 = JAI.create("scale", params);
	                                   
	                                    try {
	                                        Thread.sleep(20);
	                                    } catch (InterruptedException e) {
	                                        return;
	                                    }
	                                    
	                                    bi = image2.createSnapshot().getAsBufferedImage();
	                                    stream.close();                                    
                                    }
                                    
                                    icon = new ImageIcon(bi);
                                    pathToImage.put(path, icon);
                                    
                                } catch (Exception e) {
                                    // couldn't read image.
                                    e.printStackTrace();
                                }
                            }
                            try {
                                Thread.sleep(20);
                            } catch (InterruptedException e) {
                                return;
                            }
                            label.setIcon(icon);
                            repaint();
                        }

                    };
                    if (lastThread != null) {
                        lastThread.interrupt();
                        lastThread = null;
                    }
                    lastThread = readImg;
                    readImg.start();

                } else {
                    label.setIcon(null);
                    repaint();
                }
            }

        }
    }

}