package de.picman.gui.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import de.picman.gui.main.GUIControl;
import de.rahn.bilderdb.db.Category;

public class CategoriesTreeCellRenderer extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = -7489574643512311437L;

	public CategoriesTreeCellRenderer() {
       
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

        DefaultMutableTreeNode node =
            (DefaultMutableTreeNode)value;   
        if(node.isRoot()) {
    		setIcon(GUIControl.get().getImageIcon("icon.category.toplevel"));
        	setFont(new Font("Monospace", Font.BOLD, 14));
            setForeground(Color.BLACK);
    	}
        if(getUserObject(value) instanceof Category){
        	Category category = (Category)getUserObject(value);
        	setText(category.getName());
        	
        	if( category.isTopLevel()){
        		setIcon(GUIControl.get().getImageIcon("icon.category.toplevel"));
            	setFont(new Font("Monospace", Font.BOLD, 12));
                setForeground(Color.BLACK);
        	}
        	else{
        		setIcon(GUIControl.get().getImageIcon("icon.category.blue"));
            	setFont(new Font("Arial", Font.PLAIN, 12));
                setForeground(Color.BLACK);
        	}
        }
        
       if(sel){
    	   setBackground(Color.ORANGE);
       }else{
    	   setBackground(Color.WHITE);
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
    
}
