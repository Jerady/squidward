package de.picman.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.looks.LookUtils;
import com.jgoodies.looks.Options;

import de.picman.gui.actions.AbstractPicmanAction;
import de.picman.gui.api.PrefsKeys;
import de.picman.gui.components.CategoriesTree;
import de.picman.gui.components.TreeUtilities;
import de.picman.gui.main.GUIControl;
import de.picman.gui.renderer.AssignedCategoriesTreeCellRenderer;
import de.rahn.bilderdb.control.ApplicationControl;
import de.rahn.bilderdb.db.Category;
import de.rahn.bilderdb.db.DbController;

public class AssignCategoriesDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private CategoriesTree categoriesTree;
	private JTree assignedCategoriesTree;
	
	private JButton cancelButton;
	private JButton applyButton;
	private JPanel buttonPanel;
	
    protected DefaultMutableTreeNode assignedCategoriesRootNode = null;
    
    protected Map<Integer, Category> assignedCategoriesMap;
    protected Map<Integer, DefaultMutableTreeNode> idToNode = new HashMap<Integer, DefaultMutableTreeNode>();
    
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
			
			
			Map<Integer, Category> hashMap = new HashMap<Integer, Category>();
			boolean isUploadControl = GUIControl.get().getPreferences().getBoolean(PrefsKeys.GUI_UPLOAD_CONTROL, false);
			AssignCategoriesDialog dialog = new AssignCategoriesDialog(hashMap, isUploadControl);

			dialog.setModal(true);
			dialog.setLocationByPlatform(true);
			dialog.setVisible(true);
			
		
	}
    
    public AssignCategoriesDialog(Map<Integer, Category> assignedCategoriesMap, boolean addNoChoiceNodes ) {
		this.assignedCategoriesMap = assignedCategoriesMap;
		this.addNoChoiceNodes = addNoChoiceNodes;
		initComponent();
	}
    
	public AssignCategoriesDialog(HashMap<Integer, Category> assignedCategoriesMap ) {
		this(assignedCategoriesMap, false);
	}
	
	
	
	
	protected boolean isAddNoChoiceNodes() {
		return addNoChoiceNodes;
	}

	public void initComponent(){				
				FormLayout layout = new FormLayout(
					    "fill:150dlu, 10dlu, fill:150dlu",
					    "fill:default:grow, 50dlu"
					    );     			

				CellConstraints cc = new CellConstraints();
				PanelBuilder builder = new PanelBuilder(layout);
				builder.setDefaultDialogBorder();
				builder.add(new JScrollPane(getCategoriesTree()),   cc.xy(1,  1));
				builder.add(new JScrollPane(getAssignedCategoriesTree()),   cc.xy(3,1));
				builder.add(getButtonPanel(), cc.xyw(1,2,3));
				
				setDoubleKlickListener();
				
				setModal(true);
				setLayout(new BorderLayout());
				setLocationByPlatform(true);
				add(builder.getPanel(),BorderLayout.CENTER);
				TreeUtilities.expandToToplevelOnly(getCategoriesTree());
				TreeUtilities.expandAll(getAssignedCategoriesTree(),true);
				pack();
				

	}
	
	
	
	private void setDoubleKlickListener() {
		getCategoriesTree().addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2){
					
					TreeSelectionModel selectionModel = getCategoriesTree().getSelectionModel();
					TreePath treePath = selectionModel.getSelectionPath();
					if(treePath == null){
						return;
					}
					
					DefaultMutableTreeNode selectedNode =
						(DefaultMutableTreeNode)treePath.getLastPathComponent();

					Object userObject = selectedNode.getUserObject();
					if(userObject instanceof Category){
						Category selectedCategory =
							(Category)userObject;
						handleAssignedCategory(selectedCategory);
					}
				}
			}
		});
		
		getAssignedCategoriesTree().addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2){
					
					TreeSelectionModel selectionModel = getAssignedCategoriesTree().getSelectionModel();
					TreePath treePath = selectionModel.getSelectionPath();
					if(treePath == null){
						return;
					}
					
					DefaultMutableTreeNode selectedNode =
						(DefaultMutableTreeNode)treePath.getLastPathComponent();

					Object userObject = selectedNode.getUserObject();
					System.out.println(userObject);
					 
					if(userObject instanceof Category){
						Category selectedCategory =
							(Category)userObject;
						handleDeassignedCategory(selectedCategory);
					}
				}
			}
		});
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
				private static final long serialVersionUID = 1645569596397796358L;

				@Override
				public void actionPerformed(ActionEvent arg0) {
					AssignCategoriesDialog.this.dispose();
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
				private static final long serialVersionUID = 2786666429081757050L;

				@Override
				public void actionPerformed(ActionEvent arg0) {
					AssignCategoriesDialog.this.dispose();
				}
			});
			applyButton.setIcon(GUIControl.get().getImageIcon("icon.upload"));
		}
		return applyButton;
	}
	public Map<Integer, Category> getAssignedCategoriesMap() {
		return assignedCategoriesMap;
	}


	private JTree getAssignedCategoriesTree() {
        if (assignedCategoriesTree == null) {
        	assignedCategoriesTree = new JTree(getAssignedCategoryRootNode());
        	assignedCategoriesTree.setCellRenderer(new AssignedCategoriesTreeCellRenderer());
        	assignedCategoriesTree.setDropTarget(new DropTarget(){

				private static final long serialVersionUID = 3975757567845135174L;

				@Override
        		public synchronized void dragEnter(DropTargetDragEvent evt) {
        			try {
        	            Transferable t = evt.getTransferable();
        	    
        	            if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
        	                evt.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
        	                String s = (String)t.getTransferData(DataFlavor.stringFlavor);
        	                
        	                HashMap<Integer, Category> categoryMap = ApplicationControl.getInstance().getDbController().getCategories();
        	                String id = s.split(":")[1].replace(')', ' ').trim();
        	                
        	                Integer index = new Integer(id);
        	                Category category = categoryMap.get(index); 
        	                if(category.isTopLevel())
        	                	evt.rejectDrag();
        	                
        	            } else {
        	                evt.rejectDrag();
        	            }
        	        } catch (IOException e) {
        	            evt.rejectDrag();
        	        } catch (UnsupportedFlavorException e) {
        	            evt.rejectDrag();
        	        }
        		}
        		
        		@Override
        		public synchronized void drop(DropTargetDropEvent evt) {
	
        			try {
        	            Transferable t = evt.getTransferable();
        	    
        	            if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
        	                evt.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
        	                String s = (String)t.getTransferData(DataFlavor.stringFlavor);
        	                evt.getDropTargetContext().dropComplete(true);
        	                
        	                HashMap<Integer, Category> categoryMap = ApplicationControl.getInstance().getDbController().getCategories();
        	                String id = s.split(":")[1].replace(')', ' ').trim();
        	                
        	                Integer index = new Integer(id);
        	                Category category = categoryMap.get(index); 
        	                
        	                handleAssignedCategory(category);
        	                
        	            } else {
        	                evt.rejectDrop();
        	            }
        	        } catch (IOException e) {
        	            evt.rejectDrop();
        	        } catch (UnsupportedFlavorException e) {
        	            evt.rejectDrop();
        	        }
        		}
        		
        	});
        	initAssignTreeNodes();
        }
        return assignedCategoriesTree;
	}
	
	
	private Category getFirstLevelCategory(Category category){
		int parentID = category.getParentCategoryId();
		Category category2check = ApplicationControl.getInstance().getDbController().getCategories().get(parentID);
		while(!category2check.isTopLevel()){
			parentID = category2check.getParentCategoryId();
			category2check = ApplicationControl.getInstance().getDbController().getCategories().get(parentID);
		}
		return category2check;
	}
           
	private void handleDeassignedCategory(Category assignedCategory ){
		System.out.println("remove: " + assignedCategory.getId());
		getAssignedCategoriesMap().remove(assignedCategory.getId());
		removeCategoryNode(getAssignedCategoryRootNode(), assignedCategory);
	}
	
	
	private void removeCategoryNode(DefaultMutableTreeNode node, Category category){
		
		
        if (node.getChildCount() >= 0) {
            for (
			@SuppressWarnings("rawtypes")
			Enumeration e=node.children(); e.hasMoreElements(); ) {
            	DefaultMutableTreeNode n = (DefaultMutableTreeNode)e.nextElement();
        		Object userObject = n.getUserObject();
        		if(userObject instanceof Category){
        			Category cat = (Category)userObject;
        			if(cat.getId() == category.getId()){
        		 		DefaultTreeModel model = (DefaultTreeModel)getAssignedCategoriesTree().getModel();
        		 		model.removeNodeFromParent(n);
        			}else{
                       	removeCategoryNode(n, category);
            		}
        		}
            }
        }
	}
	
	@SuppressWarnings("unchecked")
	private void handleAssignedCategory(Category assignedCategory){
 		// gleich mal checken, ob bereits hinzugefügt	
 		if(isAlreadyAssigned(assignedCategory)){
 			return;
		}

     	// passende Top Level Cat ermitteln
     	Category topLevelCatOfAssignedCat = getFirstLevelCategory(assignedCategory);
 		
 		Enumeration<DefaultMutableTreeNode> childNodes = getAssignedCategoryRootNode().children();
 		DefaultMutableTreeNode parentNode;
 		while(childNodes.hasMoreElements()){
 			parentNode = childNodes.nextElement();
         	Category possibleParentCategory = (Category)parentNode.getUserObject();

         	// oberkategorie suchen
         	if(possibleParentCategory.getId() == topLevelCatOfAssignedCat.getId()){
         		DefaultTreeModel model = (DefaultTreeModel)getAssignedCategoriesTree().getModel();
         		DefaultMutableTreeNode categoryNode = new DefaultMutableTreeNode(assignedCategory);
                model.insertNodeInto(categoryNode,parentNode, parentNode.getChildCount());
                getAssignedCategoriesTree().scrollPathToVisible(new TreePath(categoryNode.getPath()));
                getAssignedCategoriesMap().put(assignedCategory.getId(), assignedCategory);
         	}
 		}
	}
		
	private boolean isAlreadyAssigned(Category category){
		boolean isAlreadyAssigned = getAssignedCategoriesMap().containsKey(category.getId());
		return isAlreadyAssigned;
	}
	
	 private CategoriesTree getCategoriesTree() {
	        if (categoriesTree == null) {
	        	categoriesTree = new CategoriesTree(isAddNoChoiceNodes());
	        }
	        return categoriesTree;
	  }

	    
    protected void initAssignTreeNodes(){
        getAssignedCategoryRootNode().removeAllChildren();
        idToNode.clear();
        DbController controller = ApplicationControl.getInstance().getDbController();
        HashMap<Integer, Category> idToCategory = controller.getCategories();
        ArrayList<Category> categories = new ArrayList<Category>();
        categories.addAll(idToCategory.values());
        Collections.sort(categories);
        
        Category[] cats = categories.toArray(new Category[0]);
        Category tmpCat;
        DefaultMutableTreeNode node;
        // init top level
        for (int i = 0; i < cats.length; i++) {
            node = new DefaultMutableTreeNode(cats[i]);
            tmpCat = cats[i];
            if (tmpCat.isTopLevel()){
                idToNode.put(cats[i].getId(), node);
                getAssignedCategoryRootNode().add(node);
            }
        }
        
        // add assigned cats
        ArrayList<Category> assignedCategories = new ArrayList<Category>();
        assignedCategories.addAll(getAssignedCategoriesMap().values());
        Collections.sort(assignedCategories);
        Category[] assignedCats = assignedCategories.toArray(new Category[0]);
        Category topLevelCat;
        for (int i = 0; i<assignedCats.length; i++) {
        	tmpCat = assignedCats[i];
        	topLevelCat = getFirstLevelCategory(tmpCat);
            if (idToNode.containsKey(topLevelCat.getId())){
                node = new DefaultMutableTreeNode(tmpCat);
                idToNode.get(topLevelCat.getId()).add(node);
            }
        }
        
    }
	    public DefaultMutableTreeNode getAssignedCategoryRootNode() {
	        if (assignedCategoriesRootNode==null){
	        	assignedCategoriesRootNode = new DefaultMutableTreeNode("Zugewiesen");
	        }
	        return assignedCategoriesRootNode;
	    }
	    
	    

}
