package de.picman.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.looks.LookUtils;
import com.jgoodies.looks.Options;

import de.picman.gui.main.GUIControl;
import de.picman.gui.panels.DatabaseSettingsPanel;
import de.picman.gui.providers.ResourceAnchor;
import de.rahn.bilderdb.control.ApplicationControl;

public class LoginDialog extends JDialog {

	private static final long serialVersionUID = -9158540156133392000L;
	protected JTextField usernameFld;
	protected JPasswordField passwordFld;
	protected JButton loginBtn;
	protected JButton cancelBtn;
	protected JButton dbConnectionBtn;
	protected ActionListener loginActionListenter;
	private JPanel loginPanel;
	
	
	public LoginDialog() {
		initComponent();
	}
	

	public void paintx(Graphics g) {
		super.paint(g);
		Graphics2D graphics2D = (Graphics2D)g;
		ClassLoader loader = ResourceAnchor.class.getClassLoader();
		Image image = Toolkit.getDefaultToolkit().getImage(loader.getResource("resources/splashscreen.png"));
		graphics2D.drawImage(image, 0, 0, this);
	}
	
	
	
	private void initComponent(){
		setUndecorated(true);
		setLocationByPlatform(true);
		setLayout(new BorderLayout());
		FormLayout layout = new FormLayout(
				// 1           2      3    4     5
			    "right:pref, 3dlu, right:60dlu, 3dlu, 60dlu", // columns
			    // 1	2	 3		4		5	 6    7     8     9
				"pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 10dlu, pref"); 

		
		CellConstraints cc = new CellConstraints();

		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();

		builder.addSeparator("Anmelden",   cc.xyw(1,  1, 5));
		builder.addLabel("Benutzer:",       cc.xy(3, 3 ));
		builder.add(getUserameFld(), cc.xy(5,3));
		builder.addLabel("Passwort:",       cc.xy(3,  5));
		builder.add(getPasswordFld(), cc.xy(5,5));
		builder.addSeparator("",   cc.xyw(1,  7, 5));
		builder.add(getDbConnectionBtn(), cc.xy(1, 9));
		builder.add(getCancelBtn(), cc.xy(3,9));
		builder.add(getLoginBtn(), cc.xy(5,9));
		
		loginPanel = builder.getPanel();
		loginPanel.setOpaque(false);
		add(loginPanel, BorderLayout.CENTER);
		pack();
		if(getUserameFld().getText().length() > 0){
			getPasswordFld().requestFocus();
		}
		
		setAlwaysOnTop(true);
		setLocationRelativeTo(null);
		setTitle("Willkommen bei Squidward!");
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}
	
	public void setLoginPanelVisible(boolean b){
		loginPanel.setVisible(b);
	}
	
	protected ActionListener getLoginActionListener(){
		if(loginActionListenter == null){
			loginActionListenter = new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					String username = getUserameFld().getText();
					String password = new String(getPasswordFld().getPassword());
					GUIControl.get().setProperty("lastuser", username);
					try {

						ApplicationControl.getInstance().login(username, password);
						LoginDialog.this.dispose();
					} catch (Exception e1) {
						ApplicationControl.displayErrorToUser(e1);
					}
				}
			};
		}
		return loginActionListenter;
	}
	
	protected JTextField getUserameFld() {
		if(usernameFld == null){
			usernameFld = new JTextField();
			usernameFld.setText(GUIControl.get().getProperty("lastuser"));
			usernameFld.addKeyListener(new KeyAdapter(){
				@Override
				public void keyTyped(KeyEvent e) {
					getLoginBtn().setEnabled(getPasswordFld().getPassword().length > 0 && getUserameFld().getText().length() > 0);
				}
			});
		}
		return usernameFld;
	}

	
	
	protected JButton getDbConnectionBtn() {
		if(dbConnectionBtn == null){
			dbConnectionBtn = new JButton("");
			dbConnectionBtn.setIcon(GUIControl.get().getImageIcon("icon.edit"));
			dbConnectionBtn.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					JDialog dialog = new JDialog();
					dialog.setModal(true);
					dialog.setLocationByPlatform(true);
					dialog.add(new DatabaseSettingsPanel());
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.pack();
					dialog.setVisible(true);				}
			});

		}
		return dbConnectionBtn;
	}

	protected JPasswordField getPasswordFld() {
		if(passwordFld == null){
			passwordFld = new JPasswordField();
			passwordFld.addKeyListener(new KeyAdapter(){
				public void keyTyped(KeyEvent e) {
					getLoginBtn().setEnabled(getPasswordFld().getPassword().length > 0 && getUserameFld().getText().length() > 0);
				}
			});
			passwordFld.addActionListener(getLoginActionListener());
		}
		return passwordFld;
	}
	

	protected JButton getLoginBtn() {
		if(loginBtn == null){
			loginBtn = new JButton("Anmelden");
			loginBtn.setIcon(GUIControl.get().getImageIcon("icon.yes"));
			loginBtn.addActionListener(
					getLoginActionListener());		
			loginBtn.setEnabled(false);
		}
		return loginBtn;
	}
	
	

	protected JButton getCancelBtn() {
		if(cancelBtn == null){
			cancelBtn = new JButton("Abbruch");
			cancelBtn.setIcon(GUIControl.get().getImageIcon("icon.no"));

			cancelBtn.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			});
		}
		return cancelBtn;
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
		
		LoginDialog loginDialog = new LoginDialog();
		loginDialog.setVisible(true);
		
		
		GUIControl.get().setProperty("lastuser", "Jens");
	}
	
}
