package de.picman.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.util.HashMap;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.looks.LookUtils;
import com.jgoodies.looks.Options;

import de.picman.backend.control.ApplicationControl;
import de.picman.backend.db.User;
import de.picman.gui.actions.AbstractPicmanAction;
import de.picman.gui.main.GUIControl;
import de.picman.gui.panels.ListPanel;
import de.picman.gui.renderer.UserListCellRenderer;

public class UserEditorDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private ListPanel userListPanel;
	private JButton cancelButton;
	private JButton applyButton;
	private JPanel buttonPanel;
	private JPanel userPropertiesPanel;
	
	private JTextField loginNameFld;
	private JTextField foreNameFld;
	private JTextField sureNameFld;
	private JPasswordField passwordFld;
	private JSlider privilegesSlider;
	private User user;
	
	private JButton saveBtn;
	
    protected HashMap<Integer, DefaultMutableTreeNode> idToNode = new HashMap<Integer, DefaultMutableTreeNode>();
    
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
				ApplicationControl.getInstance().login("Jens","geheim" );
			} catch (Exception e) {
				ApplicationControl.displayErrorToUser(e);
			}
			
			UserEditorDialog dialog = new UserEditorDialog();

			dialog.setModal(true);
			dialog.setLocationByPlatform(true);
			dialog.setVisible(true);
		
	}
    
    public UserEditorDialog() {
		initComponent();
	}

	public void initComponent(){				
				FormLayout layout = new FormLayout(
					    "fill:150dlu, 10dlu, fill:180dlu",
					    "fill:default:grow, 6dlu, 20dlu"
					    );     			

				CellConstraints cc = new CellConstraints();
				PanelBuilder builder = new PanelBuilder(layout);
				builder.setDefaultDialogBorder();
				builder.add(getUserListPanel(),   cc.xy(1,  1));
				builder.add(getUserPropertiesPanel(),   cc.xy(3,1));
				builder.addSeparator("", cc.xyw(1,2,3));
				builder.add(getButtonPanel(), cc.xy(3,3));
				
				setTitle("Benutzerkonten editieren");
				setModal(true);
				setLayout(new BorderLayout());
				add(builder.getPanel(),BorderLayout.CENTER);
				pack();
				setResizable(false);
				loadUser();
	}
	
	


	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected JSlider getPrivilegesSlider() {
		if(privilegesSlider == null){
			privilegesSlider = new JSlider(JSlider.VERTICAL,0,2,0);
			privilegesSlider.setMajorTickSpacing(1);
			privilegesSlider.setPaintTicks(true);
			privilegesSlider.setPaintLabels(true);
			privilegesSlider.setSnapToTicks(true);

			// Retrieve current table
		    java.util.Dictionary table = privilegesSlider.getLabelTable();
		    
		    
		    JLabel viewOnlyLabel = new JLabel("Nur suchen", GUIControl.get().getImageIcon("icon.readonly"),0);
		    JLabel userLabel = new JLabel("Normaler Benutzer", GUIControl.get().getImageIcon("icon.user"),0);
		    JLabel adminLabel = new JLabel("Administator", GUIControl.get().getImageIcon("icon.admin"), 0);

		    table.put(new Integer(0), viewOnlyLabel);
		    table.put(new Integer(1), userLabel);
		    table.put(new Integer(2), adminLabel);
		    
		    privilegesSlider.setLabelTable(table);
		    privilegesSlider.setEnabled(false);
		    privilegesSlider.setInverted(true);
		}
		return privilegesSlider;
	}

	protected JTextField getLoginNameFld() {
		if(loginNameFld == null){
			loginNameFld = new JTextField();
			loginNameFld.setEditable(false);
			loginNameFld.setEnabled(false);
		}
		return loginNameFld;
	}

	
	
	protected JPasswordField getPasswordFld() {
		if(passwordFld == null){
			passwordFld = new JPasswordField();
			passwordFld.setEditable(false);
			passwordFld.setEnabled(false);
		}
		return passwordFld;
	}

	protected JTextField getFullNameFld() {
		if(foreNameFld == null){
			foreNameFld = new JTextField();
			foreNameFld.setEditable(false);
			foreNameFld.setEnabled(false);
		}
		return foreNameFld;
	}

	protected JTextField getSureNameFld() {
		if(sureNameFld == null){
			sureNameFld = new JTextField();
		}
		return sureNameFld;
	}

	
	
	protected User getUser() {
		return user;
	}

	protected JButton getSaveBtn() {
		if(saveBtn == null){
			saveBtn = new JButton();
			saveBtn.setAction(new AbstractPicmanAction("Speichern"){
				private static final long serialVersionUID = -3157006627934564878L;
				{
					putValue(Action.SHORT_DESCRIPTION, "Speichern");
					putValue(Action.SMALL_ICON, GUIControl.get().getIconProvider().getImageIcon("icon.yes"));
				}
				@Override
				public void actionPerformed(ActionEvent arg0) {
					EventQueue.invokeLater( new Runnable()
					{
						public void run() {
							getUser().setFullname(getFullNameFld().getText());
							getUser().setLogin(getLoginNameFld().getText());
							getUser().setAdmin((getPrivilegesSlider().getValue()==2));
							getUser().setReadonly((getPrivilegesSlider().getValue()==0));
							String password = new String(getPasswordFld().getPassword());
							getUser().setPassword(password);
							try {
								if(user.getId() == -1){
									//neuer User
									ApplicationControl.getInstance().getDbController().createUser(user);
								}else{
									ApplicationControl.getInstance().getDbController().updateUser(user);
								}
								loadUser();
							} catch (Exception e) {
								ApplicationControl.displayErrorToUser(e);
							}
						} 
					});
				}
			});
			saveBtn.setEnabled(false);

		}
		return saveBtn;
	}

	protected ListPanel getUserListPanel() {
		if(userListPanel == null){
			userListPanel = new ListPanel("Benutzer");
			userListPanel.getItemList().setCellRenderer(new UserListCellRenderer());
			userListPanel.getItemList().addListSelectionListener(new ListSelectionListener(){
				@Override
				public void valueChanged(ListSelectionEvent e) {
					Object object = userListPanel.getItemList().getSelectedValue();
					if(object instanceof User){
						User user = (User)object;
						setUser(user);
					}
				}
			});
			
			userListPanel.getAddItemButton().setAction(new AbstractPicmanAction(""){
				private static final long serialVersionUID = -4013138855102063354L;
				{
					putValue(Action.SHORT_DESCRIPTION, "");
					putValue(Action.SMALL_ICON, GUIControl.get().getIconProvider().getImageIcon("icon.add"));
				}
				@Override
				public void actionPerformed(ActionEvent arg0) {
					EventQueue.invokeLater( new Runnable()
					{
						public void run() {
							newUserAction();
						} 
					});
				}
			});
			
			userListPanel.getRemoveItemButton().setAction(new AbstractPicmanAction(""){
				private static final long serialVersionUID = -1912292437698356945L;
				{
					putValue(Action.SHORT_DESCRIPTION, "");
					putValue(Action.SMALL_ICON, GUIControl.get().getIconProvider().getImageIcon("icon.remove"));
				}
				@Override
				public void actionPerformed(ActionEvent arg0) {
					EventQueue.invokeLater( new Runnable()
					{
						public void run() {
							reset();
						} 
					});
				}
			});
		}
		return userListPanel;
	}

	private void newUserAction(){
		
		getUserListPanel().getItemList().clearSelection();
		
		getLoginNameFld().setText("");
		getLoginNameFld().setEditable(true);
		getLoginNameFld().setEnabled(true);

		getFullNameFld().setText("");
		getFullNameFld().setEditable(true);
		getFullNameFld().setEnabled(true);

		getPasswordFld().setText("");
		getPasswordFld().setEditable(true);
		getPasswordFld().setEnabled(true);
		
		getLoginNameFld().requestFocus();
		getSaveBtn().setEnabled(true);
		getPrivilegesSlider().setEnabled(true);
		
		User user = new User(-1,"");
		setUser(user);
		
	}
	
	protected JPanel getUserPropertiesPanel() {
		if(userPropertiesPanel == null){
			FormLayout layout = new FormLayout(
				    "right:pref, 3dlu, 100dlu, 3dlu, 20dlu", // columns
				    // 1     2     3     4     5    6      7    8    9     10     11    12     13  14    15     16   17   18  19  20          22    23             24    25 
				    "pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, 40dlu,3dlu, pref,3dlu, pref, 3dlu,pref,3dlu,50dlu,3dlu,pref, 3dlu,  fill:default, 3dlu, pref ");      // rows
			
			CellConstraints cc = new CellConstraints();
			PanelBuilder builder = new PanelBuilder(layout);

			builder.addSeparator("Benutzerdaten",   cc.xyw(1,  1, 5));
			builder.addLabel("Anmeldename:",       cc.xy(1,  3));
			builder.add(getLoginNameFld(),         cc.xy(3,  3));
			builder.addLabel("Voller Name:",       cc.xy(1,  5));
			builder.add(getFullNameFld(),         cc.xy(3,  5));
			builder.addLabel("Passwort",       cc.xy(1,  7));
			builder.add(getPasswordFld(),         cc.xy(3,  7));

			builder.addSeparator("Benutzerrolle", cc.xyw(1,9,5));
			builder.add(getPrivilegesSlider(), cc.xy(3,11));
			builder.addSeparator("", cc.xyw(1,13,5));
			builder.add(getSaveBtn(),         cc.xy(3,  17));

			
			userPropertiesPanel = builder.getPanel();
		}
		return userPropertiesPanel;
	}

	public JPanel getButtonPanel(){
		if(buttonPanel == null){
			 buttonPanel = new JPanel();
				buttonPanel.add(getCancelButton());
				buttonPanel.add(getApplyButton());
		}
			
			return buttonPanel;
		}
	
	public JButton getCancelButton(){
		if(cancelButton == null){
			cancelButton = new JButton(
			new AbstractPicmanAction("Abbrechen") {
				private static final long serialVersionUID = -2972647202378943016L;

				@Override
				public void actionPerformed(ActionEvent arg0) {
					UserEditorDialog.this.dispose();
				}
			});
		cancelButton.setIcon(GUIControl.get().getImageIcon("icon.cancel"));
		}
		return cancelButton;
	}
	
	
	public JButton getApplyButton(){
		if(applyButton == null){
			applyButton = new JButton(
			new AbstractPicmanAction("Fertig") {
				private static final long serialVersionUID = -7238473160709698661L;

				@Override
				public void actionPerformed(ActionEvent arg0) {
					UserEditorDialog.this.dispose();
				}
			});
			applyButton.setIcon(GUIControl.get().getImageIcon("icon.upload"));
		}
		return applyButton;
	}

	private void setUser(User user){
		if(user == null){
			return;
		}
		this.user = user;
		getLoginNameFld().setText(user.getLogin());
		getLoginNameFld().setEditable(true);
		getLoginNameFld().setEnabled(true);

		getFullNameFld().setText(user.getFullname());
		getFullNameFld().setEditable(true);
		getFullNameFld().setEnabled(true);

		getPasswordFld().setText(user.getPassword());
		getPasswordFld().setEditable(true);
		getPasswordFld().setEnabled(true);
	
		getLoginNameFld().requestFocus();
		getSaveBtn().setEnabled(true);

		getPrivilegesSlider().setEnabled(true);

		if(user.isAdmin()){
			getPrivilegesSlider().setValue(2);
		}
		else if(user.isReadonly()){
			getPrivilegesSlider().setValue(0);
		}
		else{
			getPrivilegesSlider().setValue(1);
		}
		
		
	}
	
	private void reset(){
		getLoginNameFld().setText("");
		getLoginNameFld().setEditable(false);
		getLoginNameFld().setEnabled(false);
		getFullNameFld().setText("");
		getFullNameFld().setEditable(false);
		getFullNameFld().setEnabled(false);
		getPasswordFld().setText("");
		getPasswordFld().setEditable(false);
		getPasswordFld().setEnabled(false);
		getSaveBtn().setEnabled(false);
		getUserListPanel().getItemList().clearSelection();
		getPrivilegesSlider().setEnabled(false);
	}

	private void loadUser(){
		try {
			getUserListPanel().getListModel().removeAllElements();
			User[] users = ApplicationControl.getInstance().getDbController().getAllUsers();
			for (int i = 0; i < users.length; i++) {
				getUserListPanel().getListModel().addElement(users[i]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		
	
	}

	
}
