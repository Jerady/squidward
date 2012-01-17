package de.picman.gui.panels;

import java.awt.BorderLayout;
import java.util.Enumeration;

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import de.picman.gui.main.GUIControl;
import de.picman.gui.providers.ActionProvider;
import de.picman.gui.renderer.MenuTreeCellRenderer;

public class MainMenuTreePanel extends JPanel {

	private static final long serialVersionUID = 8989403641574489158L;
	private JTree menuTree;

	public MainMenuTreePanel() {

		setLayout(new BorderLayout());
		JScrollPane pane = new JScrollPane(getMenuTree());

		add(pane, BorderLayout.CENTER);
	
	}
	
	
	public JTree getMenuTree() {
		if(menuTree == null){
			menuTree = new JTree();
			menuTree.putClientProperty("JTree.lineStyle", "None");
		//	menuTree.setBackground(new Color(232,232,232));
			menuTree.setCellRenderer(new MenuTreeCellRenderer());
			menuTree.setRowHeight(24);
			menuTree.setBorder(null);
			// prevent collapse
			menuTree.setToggleClickCount(0);
			menuTree.setOpaque(false);

			
			
			DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer)menuTree.getCellRenderer();
		    renderer.setLeafIcon(null);
		    renderer.setClosedIcon(GUIControl.get().getImageIcon("icon.treenode.closed"));
		    renderer.setOpenIcon(GUIControl.get().getImageIcon("icon.treenode.opened"));
		    
			DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();

			DefaultMutableTreeNode picturesNode = new DefaultMutableTreeNode("BILDER");
			DefaultMutableTreeNode displayAllPicturesNode = new DefaultMutableTreeNode(ActionProvider.getDisplayAllPicturesAction());
			DefaultMutableTreeNode uploadPicturesNode = new DefaultMutableTreeNode(ActionProvider.getUploadPictureAction());
//			DefaultMutableTreeNode exportPicturesNode = new DefaultMutableTreeNode(ActionProvider.getDisplayExportPicturesDialogAction());
			picturesNode.add(displayAllPicturesNode);
			picturesNode.add(uploadPicturesNode);
//			picturesNode.add(exportPicturesNode);

			DefaultMutableTreeNode editNode = new DefaultMutableTreeNode("BEARBEITEN");
			DefaultMutableTreeNode editUsersNode = new DefaultMutableTreeNode(ActionProvider.getEditUserAction());
			DefaultMutableTreeNode editCategoriesNode = new DefaultMutableTreeNode(ActionProvider.getEditCategoriesAction());
			editNode.add(editUsersNode);
			editNode.add(editCategoriesNode);
			
			
			rootNode.add(picturesNode);
			//rootNode.add(editNode);
			
			menuTree.setRootVisible(false);
			menuTree.setModel( new DefaultTreeModel(rootNode));
			
			
			menuTree.addTreeSelectionListener(new TreeSelectionListener() {
		        public void valueChanged(TreeSelectionEvent evt) {
		            // Get all nodes whose selection status has changed
		            TreePath[] paths = evt.getPaths();
		    
		            // Iterate through all affected nodes
		            for (int i=0; i<paths.length; i++) {
		                if (evt.isAddedPath(i)) {
		                    DefaultMutableTreeNode node = (DefaultMutableTreeNode)evt.getPath().getLastPathComponent();
		                    if(node.getUserObject() instanceof Action){
			                    Action action = (Action)node.getUserObject();
			                    action.actionPerformed(null);
		                    }
		                } else {
		                }
		            }
		            menuTree.setSelectionPath(null);
		        }
		    });

			
			expandAll(menuTree, true);
		}
		return menuTree;
	}
	
	
	public DefaultTreeModel getTreeModel(){
		return (DefaultTreeModel)menuTree.getModel();
	}
	
	// If expand is true, expands all nodes in the tree.
    // Otherwise, collapses all nodes in the tree.
    public void expandAll(JTree tree, boolean expand) {
        TreeNode root = (TreeNode)tree.getModel().getRoot();
    
        // Traverse tree from root
        expandAll(tree, new TreePath(root), expand);
    }
    @SuppressWarnings("unchecked")
	private void expandAll(JTree tree, TreePath parent, boolean expand) {
        // Traverse children
        TreeNode node = (TreeNode)parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration<TreeNode> e=node.children(); e.hasMoreElements(); ) {
                TreeNode n = (TreeNode)e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandAll(tree, path, expand);
            }
        }
    
        // Expansion or collapse must be done bottom-up
        if (expand) {
            tree.expandPath(parent);
        } else {
            tree.collapsePath(parent);
        }

    }
	
	
}
