package de.picman.gui.actions;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.SwingWorker;

import de.picman.gui.api.PrefsKeys;
import de.picman.gui.main.GUIControl;
import de.rahn.bilderdb.control.ApplicationControl;

public class TakeScreenShotAction extends AbstractAction {

	private static final long serialVersionUID = -6652536539106423269L;

	public TakeScreenShotAction() {
		super("Screenshot erstellen");
//		putValue(Action.SMALL_ICON, GUIControl.getInstance().getIconProvider().getImageIcon("icon.users"));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		 SwingWorker<Object, Void> worker = 
	          new SwingWorker<Object, Void>() {
			 public Object doInBackground() {
					//Get the screen size  
				 Toolkit toolkit = Toolkit.getDefaultToolkit();  
				 Dimension screenSize = toolkit.getScreenSize();  
				 Rectangle rectangle = new Rectangle(0, 0, screenSize.width, screenSize.height);  
				 
				 Robot robot;
				try {
					robot = new Robot();
					 BufferedImage image = robot.createScreenCapture(rectangle);  
		
					final JFileChooser fileChooser = new JFileChooser();
					fileChooser.setCurrentDirectory(new File(GUIControl.get().getProperty(PrefsKeys.GUI_LASTPATH)));
					fileChooser.setMultiSelectionEnabled(true);
					fileChooser.setApproveButtonText("Screenshot Speichern");
					fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					fileChooser.setDialogTitle("Aktuellen Screenshot speichern:");
					int choice = fileChooser.showSaveDialog(null);
					
					if(choice == JFileChooser.CANCEL_OPTION)
						return null;
						
					File selectedFile = fileChooser.getSelectedFile();
					String file = selectedFile.getAbsolutePath()+"/screenshot_"+ System.currentTimeMillis()+".png";
					GUIControl.get().setProperty((PrefsKeys.GUI_LASTPATH),selectedFile.getAbsolutePath());

					ImageIO.write(image, "png", new File(file));  			 
				} catch (Exception e) {
					ApplicationControl.displayErrorToUser(e);
					e.printStackTrace();
				}  
				
					return null;
	          }
	          public void done() {
	          }
	       };
	       worker.execute();	
	}

	public static void main(String[] args) {
		new TakeScreenShotAction().actionPerformed(null);
	}
	
}
