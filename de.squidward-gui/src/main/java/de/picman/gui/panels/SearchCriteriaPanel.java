package de.picman.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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

import de.picman.gui.actions.AbstractPicmanAction;
import de.picman.gui.actions.SearchPictureAction;
import de.picman.gui.dialogs.AssignCategoriesDialog;
import de.picman.gui.main.GUIControl;
import de.picman.gui.renderer.AssignedCategoriesListCellRenderer;
import de.picman.gui.renderer.UserListCellRenderer;
import de.rahn.bilderdb.control.ApplicationControl;
import de.rahn.bilderdb.db.Category;
import de.rahn.bilderdb.db.PictureColumn;
import de.rahn.bilderdb.db.PictureSearchCriteria;
import de.rahn.bilderdb.db.User;

public class SearchCriteriaPanel extends JXTitledPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HashMap<Integer, Category> searchCategories;
	private JTextField descriptionTextfield;
	private JCheckBox searchDescriptionCheckBox;
	private JButton searchButton;
	private JCheckBox searchExemplaryCheckBox;
	private JCheckBox exemplaryCheckBox;
	private JCheckBox searchPublishCheckBox;
	private JCheckBox publishCheckBox;
	private JCheckBox searchBadExampleCheckBox;
	private JCheckBox badExampleCheckBox;
	private JCheckBox searchCategoriesCheckBox;
	private ListPanel searchCategoriesPanel;
	private JCheckBox searchByUserCheckBox;
	private JComboBox userNameComboBox;
	
	
	

	public SearchCriteriaPanel() {
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
				ApplicationControl.getInstance().login("ole", "geheim");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			JFrame testFrame = new JFrame();
			SearchCriteriaPanel panel = new SearchCriteriaPanel();
			
			testFrame.add(panel);
			testFrame.pack();
			testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			testFrame.setVisible(true);
	}

	@SuppressWarnings("rawtypes")
	private void initComponent(){
		
		setBackground(new Color(232,232,232));
		setOpaque(false);
		setLayout(new BorderLayout());
		FormLayout layout = new FormLayout(
				//    1       2        3        4     5
			    "right:pref, 6dlu, right:pref, 3dlu, 120dlu", // columns
			    //1      2    3      4    5     6     7     8    9     10     11   12    13   14   15   16   17   18     19   20   21     22
			    "pref, 6dlu, pref, 0dlu, pref, 6dlu, pref, 6dlu, pref, 6dlu, pref, 6dlu,pref, 6dlu,pref,top:140dlu,pref,6dlu,pref,18dlu, pref");      // rows
		
		CellConstraints cc = new CellConstraints();

		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();

		builder.addSeparator("SUCHE", cc.xyw(1,1,5));
		builder.addSeparator("Beschreibung", cc.xyw(3,3,3));
		builder.add(getSearchDescriptionCheckBox(), cc.xy(1,5));
		builder.add(getSearchDescriptionTextfield(), cc.xyw(3,5,3));
		builder.add(getSearchPublishCheckBox(), cc.xy(1,7));
		builder.add(getPublishCheckBox(), cc.xyw(3,7,3));
		builder.add(getSearchExemplaryCheckBox(), cc.xy(1,9));
		builder.add(getExemplaryCheckBox(), cc.xyw(3,9,3));
		builder.add(getSearchBadExampleCheckBox(), cc.xy(1,11));
		builder.add(getBadExampleCheckBox(), cc.xyw(3,11,3));
		builder.add(getSearchByUserCheckBox(), cc.xy(1,13));
		builder.add(getUserNameComboBox(), cc.xyw(3,13,3));
		builder.add(getSearchCategoriesCheckBox(), cc.xy(1,16));
		builder.add(getSearchCategoriesPanel(), cc.xyw(3,16,3));
		
		
		
		builder.add(getSearchButton(), cc.xyw(1,17,5));
		add(builder.getPanel(), BorderLayout.CENTER);
		
		
		setTitle("Suche");
		
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

	private ListPanel getSearchCategoriesPanel() {
		if(searchCategoriesPanel == null){
			searchCategoriesPanel = new ListPanel("Kategorien");
			searchCategoriesPanel.getItemList().setCellRenderer(new AssignedCategoriesListCellRenderer());
			searchCategoriesPanel.getAddItemButton().setAction(new AbstractPicmanAction(
			""){
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				{
					putValue(Action.SHORT_DESCRIPTION, "hinzufŸgen");
					putValue(Action.SMALL_ICON, GUIControl.get().getIconProvider().getImageIcon("icon.add"));
				}

				@Override
				public void actionPerformed(ActionEvent arg0) {
					EventQueue.invokeLater( new Runnable()
					{
						public void run() {
							getSearchCategoriesCheckBox().setSelected(true);
							
							AssignCategoriesDialog dialog = new AssignCategoriesDialog(getSearchCategories(), false);
							dialog.setModal(true);
							dialog.setVisible(true);
							SearchCriteriaPanel.this.updateSearchCategoriesView();
						} 
					});
				}
			});
			
			searchCategoriesPanel.getRemoveItemButton().setEnabled(false);	
			searchCategoriesPanel.getRemoveItemButton().setAction(new AbstractPicmanAction(
			""){
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;
				{
					putValue(Action.SHORT_DESCRIPTION, "entfernen");
					putValue(Action.SMALL_ICON, GUIControl.get().getIconProvider().getImageIcon("icon.remove"));
				}
					public void actionPerformed(ActionEvent arg0) {
						JList catList = searchCategoriesPanel.getItemList();
						DefaultListModel model = searchCategoriesPanel.getListModel();
						int index = catList.getSelectedIndex();
						if(index>-1){
							Category cat = (Category)catList.getSelectedValue();
							model.remove(catList.getSelectedIndex());
							getSearchCategories().remove(cat.getId());
							catList.setSelectedIndex(index);
						}
						
						if(index >= model.getSize()){
							catList.setSelectedIndex(model.getSize()-1);
						}
					}
				
				
				
			});
			searchCategoriesPanel.setEnabled(false);
		}
		return searchCategoriesPanel;
	}

	private void updateSearchCategoriesView(){
		
		getSearchCategoriesPanel().getListModel().removeAllElements();

		ArrayList<Category> categories = new ArrayList<Category>();
		categories.addAll(getSearchCategories().values());
		Collections.sort(categories);
		
		for(Category cat : categories){
			getSearchCategoriesPanel().getListModel().addElement(cat);
		}
	}
	
	private HashMap<Integer, Category> getSearchCategories() {
		if(searchCategories == null){
			searchCategories = new HashMap<Integer, Category>();
		}
		return searchCategories;
	}
	
	private JTextField getSearchDescriptionTextfield() {
		if(descriptionTextfield == null){
			descriptionTextfield = new JTextField();
			descriptionTextfield.setEnabled(false);
			descriptionTextfield.setEditable(false);
			descriptionTextfield.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					searchPictureAction();
				}
			});
			descriptionTextfield.addMouseListener(new MouseAdapter(){
				@Override
				public void mouseClicked(MouseEvent e) {
					if(e.getClickCount() == 1){
						getSearchDescriptionCheckBox().setSelected(true);
						descriptionTextfield.requestFocus();
					}
				}
			});
		}
		return descriptionTextfield;
	}

	public void reset(){
		getSearchDescriptionCheckBox().setSelected(false);
		getSearchDescriptionTextfield().setText("");
		getSearchPublishCheckBox().setSelected(false);
		getPublishCheckBox().setSelected(false);
		getSearchExemplaryCheckBox().setSelected(false);
		getExemplaryCheckBox().setSelected(false);
		getSearchBadExampleCheckBox().setSelected(false);
		getBadExampleCheckBox().setSelected(false);
		getSearchByUserCheckBox().setSelected(false);
	}
	
	
	private JCheckBox getSearchCategoriesCheckBox() {
		if(searchCategoriesCheckBox == null){
			searchCategoriesCheckBox = new JCheckBox();
			searchCategoriesCheckBox.setIcon(GUIControl.get().getImageIcon("icon.remove"));
			searchCategoriesCheckBox.setSelectedIcon(GUIControl.get().getImageIcon("icon.add"));
			searchCategoriesCheckBox.addChangeListener(new ChangeListener(){
				@Override
				public void stateChanged(ChangeEvent e) {
					getSearchCategoriesPanel().setEnabled(searchCategoriesCheckBox.isSelected());
					refreshSearchButtonState();
				}
			});

		}
		return searchCategoriesCheckBox;
	}



	private JCheckBox getSearchBadExampleCheckBox() {
		if(searchBadExampleCheckBox == null){
			searchBadExampleCheckBox = new JCheckBox();
			searchBadExampleCheckBox.setIcon(GUIControl.get().getImageIcon("icon.remove"));
			searchBadExampleCheckBox.setSelectedIcon(GUIControl.get().getImageIcon("icon.add"));
			searchBadExampleCheckBox.addChangeListener(new ChangeListener(){
				@Override
				public void stateChanged(ChangeEvent e) {
					getBadExampleCheckBox().setEnabled(searchBadExampleCheckBox.isSelected());
					refreshSearchButtonState();
				}
			});
			
		}
		return searchBadExampleCheckBox;
	}

	private JCheckBox getBadExampleCheckBox() {
		if(badExampleCheckBox == null){
			badExampleCheckBox = new JCheckBox("Schlechtes Beispiel");
			badExampleCheckBox.setIcon(GUIControl.get().getImageIcon("icon.no"));
			badExampleCheckBox.setSelectedIcon(GUIControl.get().getImageIcon("icon.yes"));
			badExampleCheckBox.setEnabled(false);
			badExampleCheckBox.setSelected(true);
		}
		return badExampleCheckBox;
	}

	private JCheckBox getSearchPublishCheckBox() {
		if(searchPublishCheckBox == null){
			searchPublishCheckBox = new JCheckBox();
			searchPublishCheckBox.setIcon(GUIControl.get().getImageIcon("icon.remove"));
			searchPublishCheckBox.setSelectedIcon(GUIControl.get().getImageIcon("icon.add"));
			searchPublishCheckBox.addChangeListener(new ChangeListener(){
				@Override
				public void stateChanged(ChangeEvent e) {
					getPublishCheckBox().setEnabled(searchPublishCheckBox.isSelected());
					refreshSearchButtonState();
				}
			});
			
		}
		return searchPublishCheckBox;
	}

	
	
	private JCheckBox getPublishCheckBox() {
		if(publishCheckBox == null){
			publishCheckBox = new JCheckBox("Veršffentlichen");
			publishCheckBox.setIcon(GUIControl.get().getImageIcon("icon.no"));
			publishCheckBox.setSelectedIcon(GUIControl.get().getImageIcon("icon.yes"));
			publishCheckBox.setEnabled(false);
			publishCheckBox.setSelected(true);
		}
		return publishCheckBox;
	}

	private JCheckBox getExemplaryCheckBox() {
		if(exemplaryCheckBox == null){
			exemplaryCheckBox = new JCheckBox("Exemplarisch");
			exemplaryCheckBox.setIcon(GUIControl.get().getImageIcon("icon.no"));
			exemplaryCheckBox.setSelectedIcon(GUIControl.get().getImageIcon("icon.yes"));
			exemplaryCheckBox.setEnabled(false);
			exemplaryCheckBox.setSelected(true);
		}
		return exemplaryCheckBox;
	}

	private JCheckBox getSearchExemplaryCheckBox() {
		if(searchExemplaryCheckBox == null){
			searchExemplaryCheckBox = new JCheckBox();
			searchExemplaryCheckBox.setIcon(GUIControl.get().getImageIcon("icon.remove"));
			searchExemplaryCheckBox.setSelectedIcon(GUIControl.get().getImageIcon("icon.add"));
			searchExemplaryCheckBox.addChangeListener(new ChangeListener(){
				@Override
				public void stateChanged(ChangeEvent e) {
					getExemplaryCheckBox().setEnabled(searchExemplaryCheckBox.isSelected());
					refreshSearchButtonState();
				}
			});
			
		}
		return searchExemplaryCheckBox;
	}

	private JCheckBox getSearchDescriptionCheckBox() {
		if(searchDescriptionCheckBox == null){
			searchDescriptionCheckBox = new JCheckBox();
			searchDescriptionCheckBox.setIcon(GUIControl.get().getImageIcon("icon.remove"));
			searchDescriptionCheckBox.setSelectedIcon(GUIControl.get().getImageIcon("icon.add"));
			searchDescriptionCheckBox.addChangeListener(new ChangeListener(){
				@Override
				public void stateChanged(ChangeEvent e) {
					getSearchDescriptionTextfield().setEnabled(searchDescriptionCheckBox.isSelected());
					getSearchDescriptionTextfield().setEditable(searchDescriptionCheckBox.isSelected());
					refreshSearchButtonState();
				}
			});

		}
		return searchDescriptionCheckBox;
	}
	
	private JComboBox getUserNameComboBox() {
		if(userNameComboBox == null){
			userNameComboBox = new JComboBox();
			userNameComboBox.setEnabled(false);
			userNameComboBox.setRenderer(new UserListCellRenderer());
			
			try {
				User[] users = ApplicationControl.getInstance().getDbController().getAllUsers();
				for (int i = 0; i < users.length; i++) {
					userNameComboBox.addItem(users[i]);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return userNameComboBox;
	}

	
	protected JCheckBox getSearchByUserCheckBox() {
		if(searchByUserCheckBox == null){
			searchByUserCheckBox = new JCheckBox();
			searchByUserCheckBox.setIcon(GUIControl.get().getImageIcon("icon.remove"));
			searchByUserCheckBox.setSelectedIcon(GUIControl.get().getImageIcon("icon.add"));
			searchByUserCheckBox.addChangeListener(new ChangeListener(){
				@Override
				public void stateChanged(ChangeEvent e) {
					getUserNameComboBox().setEnabled(searchByUserCheckBox.isSelected());
					refreshSearchButtonState();
				}
			});

		}
		return searchByUserCheckBox;
	}

	private void refreshSearchButtonState(){
		boolean state = getSearchBadExampleCheckBox().isSelected() || 
		getSearchCategoriesCheckBox().isSelected() || 
		getSearchDescriptionCheckBox().isSelected() || 
		getSearchExemplaryCheckBox().isSelected() ||
		getSearchByUserCheckBox().isSelected() ||
		getSearchPublishCheckBox().isSelected();  

		getSearchButton().setEnabled(state);
	}

	private JButton getSearchButton() {
		if(searchButton == null){
			searchButton = new JButton("Suchen");
			searchButton.setEnabled(false);
			searchButton.setIcon(GUIControl.get().getImageIcon("icon.search"));
			
			searchButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					searchPictureAction();
				}
			});
		}
		
		return searchButton;
	}
	
	private void searchPictureAction(){
		
		PictureSearchCriteria pictureSearchCriteria = new PictureSearchCriteria();
		if(getSearchDescriptionCheckBox().isSelected()){
			pictureSearchCriteria.put(PictureColumn.DESCRIPTION, getSearchDescriptionTextfield().getText());
		}
		if(getSearchExemplaryCheckBox().isSelected()){
			pictureSearchCriteria.put(PictureColumn.ISEXEMPLARY, getExemplaryCheckBox().isSelected());
		}
		if(getSearchPublishCheckBox().isSelected()){
			pictureSearchCriteria.put(PictureColumn.ISPUBLICATION, getPublishCheckBox().isSelected());
		}
		if(getSearchBadExampleCheckBox().isSelected()){
			pictureSearchCriteria.put(PictureColumn.ISBADEXAMPLE, getBadExampleCheckBox().isSelected());
		}
		if(getSearchByUserCheckBox().isSelected()){
			User user = (User)getUserNameComboBox().getSelectedItem();
			pictureSearchCriteria.put(PictureColumn.USERID, user.getId());
		}
		
		if(getSearchCategoriesCheckBox().isSelected()){
			Set<Integer> keys = getSearchCategories().keySet();
			Integer[] catIDs = keys.toArray(new Integer[0]);
			pictureSearchCriteria.put(PictureColumn.CATEGORY, catIDs);
		}
		
		
		new SearchPictureAction(pictureSearchCriteria).actionPerformed(null);
	}
	
	
	
	
}

