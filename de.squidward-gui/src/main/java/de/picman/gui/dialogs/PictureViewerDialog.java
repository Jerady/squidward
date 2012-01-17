package de.picman.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.picman.backend.control.ApplicationControl;
import de.picman.backend.db.Picture;

public class PictureViewerDialog extends JDialog{

	private static final long serialVersionUID = 4194852973780935805L;
	private JPanel picturePanel;
	private JLabel pictureLabel;
	private JScrollPane scrollPane;
	
	public PictureViewerDialog() {
		initComponent();
	}
	
	private void initComponent(){
		setTitle("Bild im Original");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setSize(new Dimension(1000,800));
		setLocationByPlatform(true);
		setLayout(new BorderLayout());
		add(getPicturePanel(), BorderLayout.CENTER);
	}
	
	
	private JPanel getPicturePanel(){
		if(picturePanel == null){
			picturePanel = new JPanel();
			scrollPane = new JScrollPane(getPictureLabel());
			scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);	
			picturePanel.setLayout(new BorderLayout());
			picturePanel.add(scrollPane, BorderLayout.CENTER);
		}
		return picturePanel;
	}
	
	private JLabel getPictureLabel(){
		if(pictureLabel == null){
			pictureLabel = new JLabel();
		}
		return pictureLabel;
	}
	
	
	public void showPicture(Picture picture){
		if(picture == null)
			return;
		try {
			ImageIcon icon = new ImageIcon(picture.getPreviewAsBufferedImage());
			getPictureLabel().setIcon(icon);
			setTitle("Bild im Original: " + picture.getDescription());
			pack();
		} catch (Exception e) {
			ApplicationControl.displayErrorToUser(e);
		}
	}
	
	public void reset(){
		getPictureLabel().setIcon(null);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		PictureViewerDialog dialog = new PictureViewerDialog();
		dialog.setVisible(true);
		
	    try {
			ApplicationControl.getInstance().login("Ole", "geheim");
	    	File pic = new File("C:/EclipseWorkspace/PicMan/DSC_3571.jpg");
			RenderedImage fullSize = Picture.loadImageFromFile(pic);
			Picture testPic = new Picture(1, "ein Test", "", "Venedig", false, true, true);
			testPic.setPicture(fullSize);
	        dialog.showPicture(testPic);		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

       
	}

}
