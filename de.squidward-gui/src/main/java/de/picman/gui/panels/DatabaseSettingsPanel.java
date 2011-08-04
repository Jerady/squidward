package de.picman.gui.panels;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.looks.LookUtils;
import com.jgoodies.looks.Options;

import de.picman.gui.api.PrefsKeys;
import de.picman.gui.main.GUIControl;

public class DatabaseSettingsPanel extends JPanel {

	private static final long serialVersionUID = -6253909404214477291L;
	private JTextField databaseHostFld;
	private JTextField databaseNameFld;
	private JTextField databasePortFld;
	private JTextField databaseUserFld;
	private JPasswordField databasePasswordFld;
	private JButton testBtn;
	private JLabel testLbl;
	private JButton saveBtn;
	
	public DatabaseSettingsPanel() {

		initComponent();
	
	}
	
	private void initComponent(){
		setOpaque(false);
		setLayout(new BorderLayout());
		
		FormLayout layout = new FormLayout(
			    "right:pref, 3dlu, right:60dlu, 3dlu, 60dlu", // columns
			    // 1	2	 3		4		5	6		7   8     9     10     11	12	  13    14   15        16    17
				"pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 10dlu, pref"); 

		
		CellConstraints cc = new CellConstraints();

		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();

		builder.addSeparator("Datenbank Einstellungen",   cc.xyw(1,  1, 5));
		builder.addLabel("Servername:",     cc.xy(3,3));
		builder.add(getDatabaseHostFld(), 	cc.xy(5,3));
		builder.addLabel("Datenbankname:", 	cc.xy(3,5));
		builder.add(getDatabaseNameFld(), 	cc.xy(5,5));
		builder.addLabel("Port:",       	cc.xy(3,7));
		builder.add(getDatabasePortFld(), 	cc.xy(5,7));
		builder.addLabel("Benutzer:",       cc.xy(3,9));
		builder.add(getDatabaseUserFld(), 	cc.xy(5,9));
		builder.addLabel("Passwort:",       cc.xy(3,11));
		builder.add(getDatabasePasswordFld(), cc.xy(5,11));
		
		
		
		
		builder.add(getTestLbl(),   cc.xy(  3, 13));
		builder.add(getTestBtn(),   cc.xy(  5, 13));
		builder.addSeparator("", cc.xyw(1,15,5));
		builder.add(getSaveBtn(),   cc.xy(  5, 17));
		
		
		add(builder.getPanel(), BorderLayout.CENTER);
	
		String dbHost = GUIControl.get().getProperty(PrefsKeys.GUI_DBHOST);
		String dbName = GUIControl.get().getProperty(PrefsKeys.GUI_DBNAME);
		String dbPort = GUIControl.get().getProperty(PrefsKeys.GUI_DBPORT);
		String dbUser = GUIControl.get().getProperty(PrefsKeys.GUI_DBUSER);
		String dbPassword = GUIControl.get().getProperty(PrefsKeys.GUI_DBPASS);
		
		getDatabaseHostFld().setText(dbHost);
		getDatabaseNameFld().setText(dbName);
		getDatabasePortFld().setText(dbPort);
		getDatabaseUserFld().setText(dbUser);
		getDatabasePasswordFld().setText(dbPassword);

		System.out.println("Host........: " + dbHost);
		System.out.println("DB..........: " + dbName);
		System.out.println("Port........: " + dbPort);
		System.out.println("User........: " + dbUser);
		System.out.println("Password....: " + dbPassword);

	}
	
	
	
	
	private JLabel getTestLbl() {
		if(testLbl == null){
			testLbl = new JLabel();
		}
		return testLbl;
	}

	public JButton getSaveBtn() {
		if(saveBtn == null){
			saveBtn = new JButton("Save");
			saveBtn.setIcon(GUIControl.get().getIconProvider().getImageIcon("icon.yes"));
			saveBtn.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.println("Save Database settings:");
					
					String dbHost = getDatabaseHostFld().getText();
					String dbName = getDatabaseNameFld().getText();
					String dbPort = getDatabasePortFld().getText();
					String dbUser = getDatabaseUserFld().getText();
					String dbPassword = new String(getDatabasePasswordFld().getPassword());
					
					GUIControl.get().setProperty(PrefsKeys.GUI_DBHOST,dbHost);
					GUIControl.get().setProperty(PrefsKeys.GUI_DBNAME,dbName);
					GUIControl.get().setProperty(PrefsKeys.GUI_DBPORT,dbPort);
					GUIControl.get().setProperty(PrefsKeys.GUI_DBUSER,dbUser);
					GUIControl.get().setProperty(PrefsKeys.GUI_DBPASS,dbPassword);

					System.out.println("Host........: " + dbHost);
					System.out.println("DB..........: " + dbName);
					System.out.println("Port........: " + dbPort);
					System.out.println("User........: " + dbUser);
					System.out.println("Password....: " + dbPassword);

					
				
				}
			});
		}
		return saveBtn;
	}

	public JButton getTestBtn() {
		if(testBtn == null){
			testBtn = new JButton("Test");
			testBtn.setIcon(GUIControl.get().getIconProvider().getImageIcon("icon.test"));
			testBtn.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.println("Test Database settings.");
					String dbHost = getDatabaseHostFld().getText();
					String dbName = getDatabaseNameFld().getText();
					String dbPort = getDatabasePortFld().getText();
					String dbUser = getDatabaseUserFld().getText();
					String dbPassword = new String(getDatabasePasswordFld().getPassword());
					
					System.out.println("Host........: " + dbHost);
					System.out.println("DB..........: " + dbName);
					System.out.println("Port........: " + dbPort);
					System.out.println("User........: " + dbUser);
					System.out.println("Password....: " + dbPassword);

					
			        System.out.print("Connecting to: " );
					try{
				        Class.forName("org.postgresql.Driver");
				        String dbUrl = "jdbc:postgresql://"+dbHost+":"+dbPort+"/"+dbName+
				        "?user="+dbUser+"&password="+dbPassword;
				        
				        System.out.println(dbUrl);
				        
				        Connection connection = DriverManager.getConnection(dbUrl);
				        connection.close();
						getTestLbl().setIcon(GUIControl.get().getIconProvider().getImageIcon("icon.good"));
					}
					catch (Exception e1) {
						System.out.println(e1.getMessage());
						getTestLbl().setIcon(GUIControl.get().getIconProvider().getImageIcon("icon.bad"));
					}
					
					
					
				}
			});
		}
		return testBtn;
	}

	
	
	public JTextField getDatabasePortFld() {
		if(databasePortFld == null){
			databasePortFld = new JTextField();
		}
		return databasePortFld;
	}

	public JTextField getDatabaseUserFld() {
		if(databaseUserFld == null){
			databaseUserFld = new JTextField();
		}
		return databaseUserFld;
	}

	public JPasswordField getDatabasePasswordFld() {
		if(databasePasswordFld == null){
			databasePasswordFld = new JPasswordField();
		}
		return databasePasswordFld;
	}

	public JTextField getDatabaseNameFld() {
		if(databaseNameFld == null){
			databaseNameFld = new JTextField();
		}
		return databaseNameFld;
	}
	public JTextField getDatabaseHostFld() {
		if(databaseHostFld == null){
			databaseHostFld = new JTextField();
		}
		return databaseHostFld;
	}
	/**
	 * @param args
	 */
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
		
		JDialog dialog = new JDialog();
		dialog.add(new DatabaseSettingsPanel());
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.pack();
		dialog.setVisible(true);

	}

}
