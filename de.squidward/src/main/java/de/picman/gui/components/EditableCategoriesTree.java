package de.picman.gui.components;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import de.picman.backend.control.ApplicationControl;
import de.picman.backend.db.Category;
import de.picman.backend.db.DbController;
import de.picman.gui.renderer.CategoriesTreeCellRenderer;

@SuppressWarnings("unused")
public class EditableCategoriesTree extends JTree {

	private static final long serialVersionUID = 4437543310794690344L;
	protected HashMap<Integer, DefaultMutableTreeNode> idToNode = new HashMap<Integer, DefaultMutableTreeNode>();
    protected DefaultMutableTreeNode categoriesRootNode = null;
    protected boolean addNoChoiceNodes;
    protected List<DefaultMutableTreeNode> topLevelNodesList;
    
    public EditableCategoriesTree(boolean addNoChoiceNodes){
    	this.addNoChoiceNodes = addNoChoiceNodes; 
    	initComponent();
    }
    
    
    public EditableCategoriesTree(){
    	this(false);
    }
    
    public boolean isAddNoChoiceNodes() {
		return addNoChoiceNodes;
	}


	public void setAddNoChoiceNodes(boolean addNoChoiceNodes) {
		this.addNoChoiceNodes = addNoChoiceNodes;
		refreshTreeNodes();
	}


	public List<DefaultMutableTreeNode> getTopLevelNodesList() {
    	if(topLevelNodesList == null){
    		topLevelNodesList = new ArrayList<DefaultMutableTreeNode>();
    	}
		return topLevelNodesList;
	}




	protected void initComponent(){
    
    	((DefaultTreeModel)getModel()).setRoot(getRootNode());
    	
    	setExpandsSelectedPaths(true);
        setDragEnabled(true);
//		setBackground(new Color(232,232,232));
		setBorder(null);
        setCellRenderer(new CategoriesTreeCellRenderer());
        
        refreshTreeNodes();
        TreeUtilities.expandToToplevelOnly(this);
    }

    

    
    public void refreshTreeNodes(){
        getRootNode().removeAllChildren();
        idToNode.clear();
        DbController controller = ApplicationControl.getInstance().getDbController();
        HashMap<Integer, Category> idToCategory = controller.getCategories();
        ArrayList<Category> categories = new ArrayList<Category>();
        categories.addAll(idToCategory.values());
        Collections.sort(categories);
        
        Category[] cats = categories.toArray(new Category[0]);
        Category tmp;
        DefaultMutableTreeNode node;
        for (int i = 0; i < cats.length; i++) {
            if (cats[i].isTopLevel()){
                node = new DefaultMutableTreeNode(cats[i]);
                idToNode.put(cats[i].getId(), node);
                getRootNode().add(node);
                getTopLevelNodesList().add(node);
                categories.remove(cats[i]);
            }
        }
        for (int i = 0; !categories.isEmpty() && i<cats.length*10; i++) {
            tmp = categories.get(i%categories.size());

            
            if(!isAddNoChoiceNodes() && tmp.getId() > 998000){
            	continue;
            }
            
            if (idToNode.containsKey(tmp.getParentCategoryId())){
                node = new DefaultMutableTreeNode(tmp);
                idToNode.put(tmp.getId(), node);
                idToNode.get(tmp.getParentCategoryId()).add(node);
                categories.remove(tmp);
                // boese!!:
                i = 0;
            }
        }
        
        
    }
	
    public void expandTreeToCategory(Category category){
    	visitAllNodes(getRootNode(), category);
    }
    
    
    @SuppressWarnings("unchecked")
	public void visitAllNodes(DefaultMutableTreeNode node, Category category) {
       	Object userObject = node.getUserObject();
    	if( userObject instanceof Category){
    		Category cat = (Category)userObject;
    		if(cat.getId() == category.getId()){
    			TreePath path = new TreePath(node.getPath());
    			scrollPathToVisible(path);
    			setSelectionPath(path);
    			return;
    		}
       }
 
    	if (node.getChildCount() >= 0) {
            for (Enumeration<Object> e=node.children(); e.hasMoreElements(); ) {
                DefaultMutableTreeNode n = (DefaultMutableTreeNode)e.nextElement();
                visitAllNodes(n, category);
            }
        }
    }
    
    public DefaultMutableTreeNode getRootNode() {
        if (categoriesRootNode==null){
            categoriesRootNode = new DefaultMutableTreeNode("Kategorien");
        }
        return categoriesRootNode;
    }
}
