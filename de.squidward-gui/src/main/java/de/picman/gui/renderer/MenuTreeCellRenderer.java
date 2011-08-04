package de.picman.gui.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import de.picman.gui.actions.DisplayAllPicturesAction;
import de.picman.gui.actions.DisplayAssignCategoriesDialogAction;
import de.picman.gui.actions.EditUserAction;
import de.picman.gui.actions.SearchPictureAction;
import de.picman.gui.actions.UploadPictureAction;

public class MenuTreeCellRenderer extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 2424889066080342674L;

	public MenuTreeCellRenderer() {
       
    }

    public Component getTreeCellRendererComponent(
                        JTree tree,
                        Object value,
                        boolean sel,
                        boolean expanded,
                        boolean leaf,
                        int row,
                        boolean hasFocus) {

        super.getTreeCellRendererComponent(
                        tree, value, sel,
                        expanded, leaf, row,
                        hasFocus);
        if (leaf && isActionItem(value)) {
        	
        	ImageIcon icon = (ImageIcon)((Action)getUserObject(value)).getValue(Action.SMALL_ICON);
        	setIcon(icon);
        	setFont(new Font("Arial", Font.PLAIN, 12));
            setForeground(Color.BLACK);
        } else {
        	setFont(new Font("Monospace", Font.BOLD, 14));
            setForeground(new Color(141,141,141));
        } 
        setOpaque(true);
        
        return this;
    }

    protected Object getUserObject(Object value){
        DefaultMutableTreeNode node =
            (DefaultMutableTreeNode)value;
        Object nodeInfo =node.getUserObject();
    	return nodeInfo;
    }
    
    protected boolean isSearchPictureAction(Object value){
        return getUserObject(value) instanceof SearchPictureAction;
    }
    
    protected boolean isUploadPictureAction(Object value){
        return getUserObject(value) instanceof UploadPictureAction;
    }

    protected boolean isEditUserAction(Object value){
        return getUserObject(value) instanceof EditUserAction;
    }
    protected boolean isDisplayCategoriesAction(Object value){
        return getUserObject(value) instanceof DisplayAssignCategoriesDialogAction;
    }
    protected boolean isDisplayAllPicturesAction(Object value){
        return getUserObject(value) instanceof DisplayAllPicturesAction;
    }


    
    protected boolean isActionItem(Object value) {
        return getUserObject(value) instanceof Action;
    }
}
