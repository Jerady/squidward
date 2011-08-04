package de.picman.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.looks.LookUtils;
import com.jgoodies.looks.Options;

import de.picman.gui.main.GUIControl;


public class ListPanel extends JPanel {

	private static final long serialVersionUID = -7521053312169545220L;
	private JButton addItemButton;
	private JButton removeItemButton;
	private JLabel infoLabel;
	private JList itemList;
	private String name;
	
	public ListPanel(String name) {
		this.name = name;
		createPanel();
		addDefaultActions();
	}
	
	public ListPanel(){
		this("");
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
		
		
		JFrame testFrame = new JFrame();
		ListPanel panel = new ListPanel("Liste");
		panel.addDefaultActions();
		
		testFrame.add(panel);
		testFrame.pack();
		testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		testFrame.setVisible(true);
	}

	private void createPanel(){
		FormLayout layout = new FormLayout(
			    "fill:default:grow",
			    "pref, 3dlu, fill:default:grow, 3dlu, pref");     			

		CellConstraints cc = new CellConstraints();
		PanelBuilder builder = new PanelBuilder(layout);
		
		builder.addSeparator(name,   cc.xy(1,  1));
		builder.add(new JScrollPane(getItemList()), cc.xy(1,3));

		FormLayout layout2 = new FormLayout(
			    "pref, 3dlu, pref,3dlu, pref,3dlu, pref",
			    "pref");     	
		PanelBuilder builder2 = new PanelBuilder(layout2);
		
		builder2.add(getAddItemButton(), cc.xy(1, 1));
		builder2.add(getRemoveItemButton(), cc.xy(3, 1));
        builder2.addLabel("", cc.xy(5,1) );
        builder2.add(getInfoLabel(), cc.xy(7,1));
        
		builder.add(builder2.getPanel(), cc.xy(1, 5));
		
		setLayout(new BorderLayout());
		add(builder.getPanel(), BorderLayout.CENTER);
	}
	
	public void addDefaultActions(){
		getAddItemButton().setAction(new AbstractAction(""){

			private static final long serialVersionUID = 2272899413422461655L;

			{
				putValue(Action.SHORT_DESCRIPTION, "hinzufŸgen");
				putValue(Action.SMALL_ICON, GUIControl.get().getIconProvider().getImageIcon("icon.add"));
			}

			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int index = getListModel().getSize()+1;
				getListModel().addElement("Item No. " + index);
			}
		});

	}
	
	public JButton getAddItemButton() {
		if(addItemButton == null){
			addItemButton = new JButton("+");
			addItemButton.setPreferredSize(new Dimension(25,25));
			addItemButton.setFont(getDefaultFont());

		}
		return addItemButton;
	}

	public void setCellRenderer(DefaultListCellRenderer renderer){
		getItemList().setCellRenderer(renderer);
	}

	public void setEnabled(boolean state){
		getAddItemButton().setEnabled(state);
		getRemoveItemButton().setEnabled(state);
		getItemList().setEnabled(state);
	}
	
	public JButton getRemoveItemButton() {
		if(removeItemButton == null){
			removeItemButton = new JButton(
					new AbstractAction(""){
						private static final long serialVersionUID = -9146418155257679226L;
						{
							putValue(Action.SHORT_DESCRIPTION, "entfernen");
							putValue(Action.SMALL_ICON, GUIControl.get().getIconProvider().getImageIcon("icon.remove"));
						}
						@Override
						public void actionPerformed(ActionEvent arg0) {
							DefaultListModel model = getListModel();
							int index = getItemList().getSelectedIndex();
							if(index>-1){
								model.remove(getItemList().getSelectedIndex());
								getItemList().setSelectedIndex(index);
							}
							
							if(index >= model.getSize()){
								getItemList().setSelectedIndex(model.getSize()-1);
							}
						}
				}		
			
			);
			removeItemButton.setPreferredSize(new Dimension(25,25));
			removeItemButton.setEnabled(false);
			removeItemButton.setFont(getDefaultFont());
		}
		return removeItemButton;
	}

	public Font getDefaultFont(){
		return new Font("Monospace", Font.BOLD, 14);
	}
	
	public JLabel getInfoLabel() {
		if(infoLabel == null){
			infoLabel = new JLabel("0");
			infoLabel.setForeground(new Color(240,240,240));
			infoLabel.setFont(getDefaultFont());
		}
		return infoLabel;
	}

	public JList getItemList() {
		if(itemList == null){
			itemList = new JList();
			itemList.setDropMode(DropMode.INSERT);
			DefaultListModel listModel = new DefaultListModel();
			itemList.setModel(listModel);
			itemList.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
			itemList.addListSelectionListener(new ListSelectionListener(){
				@Override
				public void valueChanged(ListSelectionEvent e) {
					getRemoveItemButton().setEnabled(!itemList.isSelectionEmpty());
				}
			});
			getListModel().addListDataListener(new ListDataListener(){
				@Override
				public void contentsChanged(ListDataEvent e) {
				}
				@Override
				public void intervalAdded(ListDataEvent e) {
					updateButtonState();
				}
				@Override
				public void intervalRemoved(ListDataEvent e) {
					updateButtonState();
				}
				private void updateButtonState(){
					getInfoLabel().setText(String.valueOf(getListModel().getSize()));
				}
				
			});
			
			
			
		}
		return itemList;
	}
	
	public DefaultListModel getListModel(){
		return (DefaultListModel)itemList.getModel();
	}
	
	
	
	
	
	
}
