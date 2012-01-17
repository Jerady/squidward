package de.picman.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.GlossPainter;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.painter.PinstripePainter;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.picman.gui.components.CategoriesTree;

public class CategoriesTreePanel extends JXTitledPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private CategoriesTree categoriesTree;
	
	public CategoriesTreePanel() {
		super("KATEGORIEN");
		initComponent();
	}
	
	@SuppressWarnings("rawtypes")
	private void initComponent(){
		setLayout(new BorderLayout());
		
		FormLayout layout = new FormLayout(
				//    1     
			    "140dlu", // columns
			    "pref, 3dlu, fill:default:grow");      // rows
		

		CellConstraints cc = new CellConstraints();

		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();

		builder.addSeparator("KATEGORIEN",   cc.xy(1,1));
		builder.add(new JScrollPane(getCategoriesTree()), cc.xy(1, 3));
		
		add(builder.getPanel());
		

		setTitle("Kategorien");
		
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
	
	public CategoriesTree getCategoriesTree(){
		if(categoriesTree == null){
			categoriesTree = new CategoriesTree();
		
		}
		return categoriesTree;
	}
	
}
