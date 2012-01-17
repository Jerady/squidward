package de.picman.gui.components;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
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
public class CategoriesTree extends JTree {

	private static final long serialVersionUID = 3834101918503532102L;
	protected HashMap<Integer, DefaultMutableTreeNode> idToNode = new HashMap<Integer, DefaultMutableTreeNode>();
    protected DefaultMutableTreeNode categoriesRootNode = null;
    protected boolean addNoChoiceNodes;
    protected List<DefaultMutableTreeNode> topLevelNodesList;
    
    public CategoriesTree(boolean addNoChoiceNodes){
    	this.addNoChoiceNodes = addNoChoiceNodes; 
    	initComponent();
    }
    
    
    public CategoriesTree(){
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
        
        addTreeSelectionListener(new TreeSelectionListener(){
        	@Override
        	public void valueChanged(TreeSelectionEvent e) {
        		TreePath selectedPath = e.getPath();

        		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)selectedPath.getLastPathComponent();
        		Object userObject = selectedNode.getUserObject();
        		if(userObject instanceof Category){
        			Category cat = (Category)userObject;
        			if(cat.isTopLevel()){
        				TreeUtilities.expandToToplevelOnly(CategoriesTree.this);
        				CategoriesTree.this.expandPath(selectedPath);
        			}
        		}
        	}
        });
        
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
        int noChoiceID = 999000;
        for (int i = 0; i < cats.length; i++) {
            if (cats[i].isTopLevel()){
            	
                node = new DefaultMutableTreeNode(cats[i]);
                idToNode.put(cats[i].getId(), node);
                getRootNode().add(node);
                getTopLevelNodesList().add(node);
                categories.remove(cats[i]);
                if(isAddNoChoiceNodes()){
                	node.add(new DefaultMutableTreeNode(new Category(++noChoiceID,"-- keine Zuordnung --",cats[i].getId())));
                }

            }
        }
        for (int i = 0; !categories.isEmpty() && i<cats.length*10; i++) {
            tmp = categories.get(i%categories.size());

            if (idToNode.containsKey(tmp.getParentCategoryId())){
                node = new DefaultMutableTreeNode(tmp);
                idToNode.put(tmp.getId(), node);
                idToNode.get(tmp.getParentCategoryId()).add(node);
                categories.remove(tmp);
                i = 0;
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
