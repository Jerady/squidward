package de.picman;

import java.awt.FlowLayout;
import java.awt.image.RenderedImage;
import java.io.File;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.media.jai.PlanarImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.picman.gui.main.GUIControl;
import de.rahn.bilderdb.control.ApplicationControl;
import de.rahn.bilderdb.db.Category;
import de.rahn.bilderdb.db.DbController;
import de.rahn.bilderdb.db.Picture;
import de.rahn.bilderdb.db.User;
 
public class FirstTest {

	/**
	* this gets rid of exception for not using native acceleration
	*/
	static
	{
	System.setProperty("com.sun.media.jai.disableMediaLib", "true");
	}
	

	public static void login(){
	try{
		ApplicationControl.getInstance().login("Ole", "geheim");
		User user = ApplicationControl.getInstance().getCurrentUser();
		if(user == null){
			ApplicationControl.displayErrorToUser(new Exception( "User oder Passwort falsch"));
		}
	} catch (Exception e) {
		ApplicationControl.displayErrorToUser(e);
	}
	}	

	public static void main(String[] args) throws URISyntaxException {

		clearThumbnailCache();
			
		
	}
	
	
	public static void clearThumbnailCache(){
		DbController controller = GUIControl.get().getDbController();

		try {
			controller.clearThumbnailCache();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	public static void printCategories(){
		DbController controller = GUIControl.get().getDbController();
		HashMap<Integer, Category> categoryMap = controller.getCategories();
	
		System.out.println(categoryMap);
		
	}
	
	public static void insertPicture() throws Exception{
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        
        RenderedImage fullSize = Picture.loadImageFromFile(new File("C:/TEMP/DSC_3861.jpg"));
        final Picture testPic = new Picture(1, "ein Test", dateFormat.format(new Date(System.currentTimeMillis())), "TESTEST", false, true, true);
        testPic.setPicture(fullSize);
        System.out.print("Uploading image ... ");
        ApplicationControl.getInstance().login("Jens", "geheim");
        final DbController dbControl = ApplicationControl.getInstance().getDbController();
        
		for(int i = 0; i <600;i++){
			dbControl.insertNewPictureInDatabase(testPic);
		}
		System.out.println("done.");
	}
	
	public static void loadPicture() throws Exception{
		
	    System.out.print("Loading image ... ");
        DbController dbControl = ApplicationControl.getInstance().getDbController();
        Picture pic = dbControl.loadPictureFromDatabase(2);
        System.out.println("done:" + pic.getOrigin());

        
        JFrame frame = new JFrame();
        
        JPanel panel = new JPanel();
        
        JLabel labelPreview = new JLabel("Preview");
        JLabel labelThump = new JLabel("Thumbnail");
        PlanarImage imagePreview = PlanarImage.wrapRenderedImage(pic.getPreview());
        PlanarImage imageThump = PlanarImage.wrapRenderedImage(pic.getThumbnail());
        labelPreview.setIcon(new ImageIcon( imagePreview.getAsBufferedImage()));
        labelThump.setIcon(new ImageIcon( imageThump.getAsBufferedImage()));
        
        panel.add(labelPreview);
        panel.add(labelThump);
        panel.setLayout(new FlowLayout());
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
        
	}
	
	
}
