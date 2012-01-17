/*
 * Created on 05.07.2006
 * 
 */
package de.picman.gui.providers;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class IconProvider {
	
	protected final static Dimension defaultIconSize = new Dimension(24,24);
	
	private Properties iconProperties;

	private String iconPropertiesFileName = null;

	private Map<String, ImageIcon> iconMap = new HashMap<String, ImageIcon>();
	
	
	public IconProvider(String iconPropertiesFileName)
			throws FileNotFoundException, IOException {
		this.setIconPropertiesFileName(iconPropertiesFileName);
		init();
	}

	private void init() throws FileNotFoundException, IOException {

		iconProperties = getResourceProperties();

		@SuppressWarnings("rawtypes")
		Set iconKeys = iconProperties.keySet();
		for (Object object : iconKeys) {
			String key = (String) object;
			String iconPath = iconProperties.getProperty(key);
			ImageIcon icon = getIconFromResource(iconPath);

			if (icon != null && icon.getImageLoadStatus() == MediaTracker.COMPLETE){
				iconMap.put(key, icon);
			} else {
				throw new IllegalArgumentException("Could not load " + iconPath	+ ", status = " + (icon!=null?icon.getImageLoadStatus():"null"));
			}
		}
	}
	
	/**
	 * needed, since this file can now be used by more than one tool...
	 * @param iconPropertiesFileName the iconPropertiesFileName to set
	 */
	public void setIconPropertiesFileName(String iconPropertiesFileName) {
		this.iconPropertiesFileName = iconPropertiesFileName;
	}


	private Properties getResourceProperties() {
		iconProperties = new Properties();
		try {
			ClassLoader classLoader = ResourceAnchor.class.getClassLoader();
			
			InputStream input = classLoader.getResourceAsStream(iconPropertiesFileName);
			if (input != null){
				iconProperties.load(input);
				input.close();
			} else {
				System.err.println(iconPropertiesFileName
								+ " could not be loaded! No Icons or Images will be diplayed.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return iconProperties;
	}

	public ImageIcon getIconFromResource(String path) {
		ImageIcon icon = null;

		System.err.println( "loading >" + path + "<, size: ");

		if (path != null) {

			ClassLoader classLoader = ResourceAnchor.class.getClassLoader();
			
			InputStream in = classLoader.getResourceAsStream(path);

			if (in == null){
				return new ImageIcon(getEmptyGIF());
			}

			Image img = null;
			try {
				BufferedImage bimg = ImageIO.read(in);
				img = bimg.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
//				img = bimg;
				System.err.println( img.getWidth(null) + ", " + img.getHeight(null));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				if (in!=null)
					in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if (img != null) {
				icon = new ImageIcon(img);
			} else {
				icon = new ImageIcon(getEmptyGIF());
			}
		}
		return icon;
	}

	public ImageIcon getImageIcon(String name) {
		ImageIcon icon = null;
		
		if (!iconMap.containsKey(name)){
			String iconPath = getResourceProperties().getProperty(name);
			icon = getIconFromResource(iconPath);

			if (icon != null)
				iconMap.put(name, icon);
		}
		
		if ((icon=iconMap.get(name)) == null)
			return new ImageIcon(getEmptyGIF());
		return icon;
	}
	
	public ImageIcon getImageIcon(String name, Dimension dim) {
		ImageIcon icon = null;
		
		if (!iconMap.containsKey(name)){
			String iconPath = getResourceProperties().getProperty(name);
			icon = getIconFromResource(iconPath);

			if (icon != null)
				iconMap.put(name, icon);
		}
		
		if ((icon=iconMap.get(name)) == null) {
			return new ImageIcon(getEmptyGIF());
		} else if (dim!=null){
			icon = new ImageIcon(icon.getImage().getScaledInstance(dim.width, dim.height, Image.SCALE_SMOOTH));
		}
		
		return icon;
	}

	public byte[] getEmptyGIF() {
		byte[] gif = "494738466137000100010080ff00ffffffff2cff0000000000010001020044020001003b"
				.getBytes();

		return gif;
	}

	public Image getImage(String name) {
		return getImageIcon(name).getImage();
	}
}
