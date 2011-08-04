package de.picman.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.picman.gui.main.GUIControl;
import de.rahn.bilderdb.db.Category;

public class CategoryPropertiesPanel extends JPanel {

	private static final long serialVersionUID = -7172802510846527677L;

	private JLabel categoryNameLabel;

	private JCheckBox renameCategorieChkBox;
	private JTextField renameCategorieFld;
	private JButton renameCategorieBtn;
	
	private JCheckBox newCategoryChkBox;
	private JTextField newCategoryFld;
	private JButton newCategoryBtn;
	
	private JCheckBox deleteCategoryChkBox;
	private JButton deleteCategoryBtn;

	private Category category;
	
	public CategoryPropertiesPanel() {
		initComponent();
	}
	
	private void initComponent(){
		setLayout(new BorderLayout());
		
		
		FormLayout layout = new FormLayout(
				//    1       2        3        4     5       6       7
			    "right:pref, 6dlu, right:pref, 3dlu, 120dlu, 3dlu, right:pref", // columns
			    //1      2    3      4    5     6     7     8    9     10     11   12    13   14   15   16   17   18     19
			    "pref, 6dlu, pref, 12dlu, pref, 1dlu, pref, 10dlu, pref, 1dlu, pref, 10dlu,pref,1dlu,pref,6dlu,pref,18dlu, pref");      // rows
		
		CellConstraints cc = new CellConstraints();

		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();

		builder.addSeparator("Kategorie editieren");
		builder.add(getCategoryNameLabel(), cc.xyw(3,3,3));

		builder.addSeparator("Umbenennen", cc.xyw(3,5,5));
		builder.add(getRenameCategorieChkBox(), cc.xy(1,7));
		builder.add(getRenameCategorieFld(), cc.xyw(3,7,3));
		builder.add(getRenameCategorieBtn(), cc.xy(7,7));
		
		builder.addSeparator("Neue Unterkategorie", cc.xyw(3, 9, 5));
		builder.add(getNewCategoryChkBox(), cc.xy(1,11));
		builder.add(getNewCategoryFld(), cc.xyw(3,11,3));
		builder.add(getNewCategoryBtn(), cc.xy(7,11));
		
		builder.addSeparator("Lšschen", cc.xyw(3, 13, 5));
		builder.add(getDeleteCategoryChkBox(), cc.xy(1,15));
		builder.add(getDeleteCategoryBtn(), cc.xyw(3,15,5));
		
		
		add(builder.getPanel(), BorderLayout.CENTER);
		
		
	}
	
	
	
	public JCheckBox getDeleteCategoryChkBox() {
		if(deleteCategoryChkBox == null){
			deleteCategoryChkBox = new JCheckBox();
			deleteCategoryChkBox.setIcon(GUIControl.get().getImageIcon("icon.remove"));
			deleteCategoryChkBox.setSelectedIcon(GUIControl.get().getImageIcon("icon.add"));
			deleteCategoryChkBox.setEnabled(false);
			deleteCategoryChkBox.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				if(getCategory() == null){
					deleteCategoryChkBox.setSelected(false);
					deleteCategoryChkBox.setEnabled(false);
					return;
				}
				getDeleteCategoryBtn().setEnabled(deleteCategoryChkBox.isSelected());
			}
			});
		}
		return deleteCategoryChkBox;
	}

	public JButton getDeleteCategoryBtn() {
		if(deleteCategoryBtn == null){
			deleteCategoryBtn = new JButton(GUIControl.get().getImageIcon("icon.trash"));
			deleteCategoryBtn.addChangeListener(new ChangeListener(){
				@Override
				public void stateChanged(ChangeEvent e) {
					if(deleteCategoryBtn.isEnabled()){
						deleteCategoryBtn.setBackground(Color.RED);
					}
					else{
						deleteCategoryBtn.setBackground(Color.LIGHT_GRAY);
					}
				}
			});
			
			deleteCategoryBtn.setEnabled(false);
		}
		return deleteCategoryBtn;
	}

	protected JCheckBox getNewCategoryChkBox() {
		if(newCategoryChkBox == null){
			newCategoryChkBox = new JCheckBox();
			newCategoryChkBox.setIcon(GUIControl.get().getImageIcon("icon.remove"));
			newCategoryChkBox.setSelectedIcon(GUIControl.get().getImageIcon("icon.add"));
			newCategoryChkBox.setEnabled(false);
			newCategoryChkBox.addChangeListener(new ChangeListener(){
				@Override
				public void stateChanged(ChangeEvent e) {
					if(getCategory() == null){
						newCategoryChkBox.setSelected(false);
						newCategoryChkBox.setEnabled(false);
						return;
					}
					getNewCategoryFld().setEnabled(newCategoryChkBox.isSelected());
					getNewCategoryFld().setEditable(newCategoryChkBox.isSelected());
					getNewCategoryBtn().setEnabled(newCategoryChkBox.isSelected());
					getNewCategoryFld().requestFocus();
				}
				});
			}
		return newCategoryChkBox;
	}

	public JTextField getNewCategoryFld() {
		if(newCategoryFld == null){
			newCategoryFld = new JTextField();
			newCategoryFld.setEditable(false);
			newCategoryFld.setEnabled(false);
			newCategoryFld.addMouseListener(new MouseAdapter(){
				@Override
				public void mouseClicked(MouseEvent e) {
					getNewCategoryChkBox().setSelected(true);
					newCategoryFld.requestFocus();
				}
			});
		}
		return newCategoryFld;
	}

	public JButton getNewCategoryBtn() {
		if(newCategoryBtn == null){
			newCategoryBtn = new JButton(GUIControl.get().getImageIcon("icon.yes"));
			newCategoryBtn.setEnabled(false);
		}
		return newCategoryBtn;
	}

	public JCheckBox getRenameCategorieChkBox() {
		
		if(renameCategorieChkBox == null){
			renameCategorieChkBox = new JCheckBox();
			renameCategorieChkBox.setIcon(GUIControl.get().getImageIcon("icon.remove"));
			renameCategorieChkBox.setSelectedIcon(GUIControl.get().getImageIcon("icon.add"));
			renameCategorieChkBox.setEnabled(false);
			renameCategorieChkBox.addChangeListener(new ChangeListener(){
				@Override
				public void stateChanged(ChangeEvent e) {
					if(getCategory() == null){
						renameCategorieChkBox.setSelected(false);
						renameCategorieChkBox.setEnabled(false);
						return;
					}
					getRenameCategorieFld().setEnabled(renameCategorieChkBox.isSelected());
					getRenameCategorieFld().setEditable(renameCategorieChkBox.isSelected());
					getRenameCategorieBtn().setEnabled(renameCategorieChkBox.isSelected());
					getRenameCategorieFld().requestFocus();
				}
			});

		}
		
		
		return renameCategorieChkBox;
	}
	
	

	public JButton getRenameCategorieBtn() {
		if(renameCategorieBtn == null){
			renameCategorieBtn = new JButton(GUIControl.get().getImageIcon("icon.yes"));
			
			renameCategorieBtn.setEnabled(false);
		}
		return renameCategorieBtn;
	}

	public JTextField getRenameCategorieFld() {
		if(renameCategorieFld == null){
			renameCategorieFld = new JTextField();
			renameCategorieFld.setEditable(false);
			renameCategorieFld.setEnabled(false);
		}
		return renameCategorieFld;
	}
	
	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
		update();
	}

	
	
	
	
	protected JLabel getCategoryNameLabel() {
		if(categoryNameLabel == null){
			categoryNameLabel = new JLabel(" ");
			Font font = categoryNameLabel.getFont().deriveFont(Font.BOLD, 18);
			categoryNameLabel.setFont(font);
		}
		return categoryNameLabel;
	}


	public void update(){
		String categoryName = "";
		
		if(getCategory() != null){
			categoryName = getCategory().getName();
		}
		
		getRenameCategorieFld().setText(categoryName);
		getCategoryNameLabel().setText(categoryName);
		
		getRenameCategorieChkBox().setSelected(false);
		getNewCategoryChkBox().setSelected(false);
		getDeleteCategoryChkBox().setSelected(false);
		
		
		
		getNewCategoryChkBox().setEnabled(getCategory() != null);
		getRenameCategorieChkBox().setEnabled(getCategory() != null && !(getCategory().getId() == -1));
		getDeleteCategoryChkBox().setEnabled(getCategory() != null && !(getCategory().getId() == -1));
		
	}


}
