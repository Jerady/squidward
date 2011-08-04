package de.picman.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.UIManager;

import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.GlossPainter;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.painter.PinstripePainter;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.looks.LookUtils;
import com.jgoodies.looks.Options;

import de.picman.gui.providers.ActionProvider;
import de.rahn.bilderdb.control.ApplicationControl;


public class MainMenuPanel extends JXTitledPanel {

	private static final long serialVersionUID = -8067433958241990501L;
	private JButton addPicturesButton;
	private JButton displayAllPicturesButton;
	
	public MainMenuPanel() {
		initComponent();
	}
	
	public static void main(String[] args) {
		try { 
			String laf = 
			    LookUtils.IS_OS_WINDOWS_XP 
			        ? Options.getCrossPlatformLookAndFeelClassName() 
			        : Options.getSystemLookAndFeelClassName(); 
			    UIManager.setLookAndFeel( laf ); 
			} catch ( Exception e ) { 
			    System.err.println( "Can't set look & feel:" + e ); 
			}
		
		try {
			ApplicationControl.getInstance().login("jens", "geheim");
		} catch (Exception e) {
			e.printStackTrace();
		}
			
		JFrame testFrame = new JFrame();
		MainMenuPanel panel = new MainMenuPanel();
		
		testFrame.add(panel);
		testFrame.pack();
		testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		testFrame.setVisible(true);
	}
	
	
	@SuppressWarnings("rawtypes")
	public void initComponent() {
		
		setBackground(new Color(232,232,232));
		setOpaque(false);
		setLayout(new BorderLayout());
		FormLayout layout = new FormLayout(
		//    1       2        3        4     5
	    "right:pref, 6dlu, right:pref, 3dlu, 130dlu", // columns
	    "pref, 3dlu, pref, 3dlu,pref, 3dlu");      // rows
	

		CellConstraints cc = new CellConstraints();

		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();

		
		
		builder.addSeparator("BILDER",   cc.xyw(1,1,5));
		builder.add(getDisplayAllPicturesButton(), cc.xyw(3,3,3));
		builder.add(getAddPicturesButton(), cc.xyw(3,5,3));
		
		add(builder.getPanel(), BorderLayout.CENTER);

		
		setTitle("Bilder");

		
		setBorder(BorderFactory.createEmptyBorder());
		
		GlossPainter gloss = new GlossPainter();
	    PinstripePainter stripes = new PinstripePainter();
	    stripes.setPaint(new Color(1.0f, 1.0f, 1.0f, 0.17f));
	    stripes.setSpacing(5.0);
		    
	    MattePainter matte = new MattePainter(new Color(51, 51, 51));

	    setTitlePainter(new CompoundPainter(matte, stripes, gloss));
	    getTitleFont().deriveFont(Font.BOLD);
		setTitleForeground(Color.WHITE);
				
	}


	private JButton getAddPicturesButton() {
		if(addPicturesButton==null){
			addPicturesButton = new JButton(ActionProvider.getUploadPictureAction());
			addPicturesButton.setHorizontalAlignment(JButton.LEFT);
		}
		return addPicturesButton;
	}
	private JButton getDisplayAllPicturesButton() {
		if(displayAllPicturesButton==null){
			displayAllPicturesButton = new JButton(ActionProvider.getDisplayAllPicturesAction());
			displayAllPicturesButton.setHorizontalAlignment(JButton.LEFT);
		}
		return displayAllPicturesButton;
	}
	
	

	
	
}
