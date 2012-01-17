package de.picman.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.looks.LookUtils;
import com.jgoodies.looks.Options;

import de.picman.backend.control.ApplicationControl;
import de.picman.backend.db.Category;
import de.picman.gui.actions.AbstractPicmanAction;
import de.picman.gui.components.EditableCategoriesTree;
import de.picman.gui.components.TreeUtilities;
import de.picman.gui.main.GUIControl;
import de.picman.gui.panels.CategoryPropertiesPanel;

public class CategoriesEditorDialog extends JDialog {

	private static final long serialVersionUID = 5506293602046278530L;
	private EditableCategoriesTree categoriesTree;
	private CategoryPropertiesPanel categoryPropertiesPanel;
	private JButton cancelButton;
	private JButton applyButton;
	private JPanel buttonPanel;
	
	
    protected HashMap<Integer, DefaultMutableTreeNode> idToNode = new HashMap<Integer, DefaultMutableTreeNode>();
    
    private boolean addNoChoiceNodes;
    
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
			
			CategoriesEditorDialog dialog = new CategoriesEditorDialog();

			dialog.setModal(true);
			dialog.setLocationByPlatform(true);
			dialog.setVisible(true);
		
	}
    
    public CategoriesEditorDialog() {
		this.addNoChoiceNodes = false;
		initComponent();
	}
    
	
    
    
	

	protected boolean isAddNoChoiceNodes() {
		return addNoChoiceNodes;
	}

	public void initComponent(){				
				FormLayout layout = new FormLayout(
					    "fill:200dlu, 10dlu, fill:180dlu",
					    "fill:default:grow, 6dlu, 20dlu"
					    );     			

				CellConstraints cc = new CellConstraints();
				PanelBuilder builder = new PanelBuilder(layout);
				builder.setDefaultDialogBorder();
				builder.add(new JScrollPane(getCategoriesTree()),   cc.xy(1,  1));
				builder.add(getCategoryPropertiesPanel(),   cc.xy(3,1));
				builder.addSeparator("", cc.xyw(1,2,3));
				builder.add(getButtonPanel(), cc.xy(3,3));
				
				setTitle("Kategorien Editor");
				setModal(true);
				setLayout(new BorderLayout());
				add(builder.getPanel(),BorderLayout.CENTER);
				TreeUtilities.expandToToplevelOnly(getCategoriesTree());
				pack();
				setResizable(false);
				setLocationByPlatform(true);

				
				getCategoryPropertiesPanel().getRenameCategorieBtn().addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						renameCategoryAction();
					}
				});
				
				getCategoryPropertiesPanel().getNewCategoryBtn().addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						newCategoryAction();
					}
				});
				
				getCategoryPropertiesPanel().getDeleteCategoryBtn().addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						deleteCategoryAction();
					}
				});
				
				getCategoryPropertiesPanel().getRenameCategorieFld().addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						renameCategoryAction();
					}
				});
				
				getCategoryPropertiesPanel().getNewCategoryFld().addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						newCategoryAction();
					}
				});

	}
	
	private void deleteCategoryAction(){
		Category category =  getCategoryPropertiesPanel().getCategory();
		String message = "Wirklich " + category.getName() + " und dessen Unterkategorien lšschen?";
		int choice = JOptionPane.showConfirmDialog(getCategoryPropertiesPanel(), message, "ACHTUNG!", JOptionPane.YES_NO_CANCEL_OPTION );
		if(choice == JOptionPane.YES_OPTION){
			try {
				ApplicationControl.getInstance().getDbController().removeCategory(category);
				getCategoriesTree().refreshTreeNodes();
				DefaultTreeModel model = (DefaultTreeModel)getCategoriesTree().getModel();
				model.reload();
				getCategoryPropertiesPanel().update();
				if(category.getParentCategory() != null){
					getCategoriesTree().expandTreeToCategory(category.getParentCategory());
				}
				else{
					getCategoriesTree().setSelectionRow(0);
				}
			} catch (Exception e1) {
				ApplicationControl.displayErrorToUser(e1);
			}
		}
		getCategoryPropertiesPanel().update();
	}
	
	private void renameCategoryAction(){
		String newName = getCategoryPropertiesPanel().getRenameCategorieFld().getText();
		Category category =  getCategoryPropertiesPanel().getCategory();
		category.setName(newName);
		try {
			ApplicationControl.getInstance().getDbController().updateCategory(category);
			getCategoriesTree().refreshTreeNodes();
			DefaultTreeModel model = (DefaultTreeModel)getCategoriesTree().getModel();
			model.reload();
			getCategoryPropertiesPanel().update();
			getCategoriesTree().expandTreeToCategory(category);
		} catch (Exception e1) {
			ApplicationControl.displayErrorToUser(e1);
		}
	}
	
	private void newCategoryAction(){
		String newCategoryName = getCategoryPropertiesPanel().getNewCategoryFld().getText();
		Category parentCategory = getCategoryPropertiesPanel().getCategory();
		Category newCategory = new Category(100, newCategoryName, parentCategory.getId());
		
		try {
        	ApplicationControl.getInstance().getDbController().createCategory(newCategory);
			getCategoriesTree().refreshTreeNodes();
			DefaultTreeModel model = (DefaultTreeModel)getCategoriesTree().getModel();
			model.reload();
			getCategoryPropertiesPanel().update();
			getCategoriesTree().expandTreeToCategory(newCategory);
		} catch (Exception e1) {
			ApplicationControl.displayErrorToUser(e1);
		}
	}
	
	
	

	public JPanel getButtonPanel(){
		if(buttonPanel == null){
			 buttonPanel = new JPanel();
//				buttonPanel.add(getCancelButton());
				buttonPanel.add(getApplyButton());
		}
			
			return buttonPanel;
		}
	
	public JButton getCancelButton(){
		if(cancelButton == null){
			cancelButton = new JButton(
			new AbstractPicmanAction("Abbrechen") {

				private static final long serialVersionUID = -9219135514035675719L;

				@Override
				public void actionPerformed(ActionEvent arg0) {
					CategoriesEditorDialog.this.dispose();
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
				private static final long serialVersionUID = 373666244045691519L;

				@Override
				public void actionPerformed(ActionEvent arg0) {
					CategoriesEditorDialog.this.dispose();
				}
			});
			applyButton.setIcon(GUIControl.get().getImageIcon("icon.upload"));
		}
		return applyButton;
	}


	 private EditableCategoriesTree getCategoriesTree() {
	        if (categoriesTree == null) {
	        	categoriesTree = new EditableCategoriesTree(isAddNoChoiceNodes());
	        	
	        	categoriesTree.addTreeSelectionListener(new TreeSelectionListener(){
	        		
	        		public void valueChanged(TreeSelectionEvent evt) {

	                	TreeSelectionModel selectionModel = 
	                		categoriesTree.getSelectionModel();

	                	if(selectionModel.isSelectionEmpty())
	                		return;
	                	
	                	TreePath selectedPath = selectionModel.getSelectionPath();
	                	Object selectedObject = selectedPath.getLastPathComponent();
	                	DefaultMutableTreeNode selectedNode =
							(DefaultMutableTreeNode)selectedObject;

						Object userObject = selectedNode.getUserObject();
						
						if(userObject instanceof Category){
							Category selectedCategory =
								(Category)selectedNode.getUserObject();
							getCategoryPropertiesPanel().setCategory(selectedCategory);
						}
						if(userObject instanceof String){
							Category category = new Category(-1, userObject.toString(), -1);
							getCategoryPropertiesPanel().setCategory(category);
						}
						
					}
				});
	        }
	        return categoriesTree;
	  }
	    

	 private CategoryPropertiesPanel getCategoryPropertiesPanel(){
			if(categoryPropertiesPanel == null){
				categoryPropertiesPanel = new CategoryPropertiesPanel();
			}
		 return categoryPropertiesPanel;
	 }
}
