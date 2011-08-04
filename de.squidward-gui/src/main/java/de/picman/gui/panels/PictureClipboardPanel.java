package de.picman.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.HashMap;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.GlossPainter;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.painter.PinstripePainter;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.looks.LookUtils;
import com.jgoodies.looks.Options;

import de.picman.gui.actions.AbstractPicmanAction;
import de.picman.gui.components.PictureClipboard;
import de.picman.gui.main.GUIControl;
import de.picman.gui.providers.ActionProvider;
import de.picman.gui.renderer.PictureClipboardListCellRenderer;
import de.rahn.bilderdb.db.Picture;


public class PictureClipboardPanel extends JXTitledPanel {

	private static final long serialVersionUID = -6021607879170919936L;
	private JLabel infoLabel;
	private JList itemList;
	private String name;
	private JButton exportPicturesButton;
	private JButton removeSelectedPictureButton;
	private JButton removeAllPicturesButton;
	private JButton sendMailButton;
	
	
	public PictureClipboardPanel(String name) {
		this.name = " " + name;
		initComponent();
	}
	
	public PictureClipboardPanel(){
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
		PictureClipboardPanel panel = new PictureClipboardPanel("Liste");
		
		testFrame.add(panel);
		testFrame.pack();
		testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		testFrame.setVisible(true);
	}

	@SuppressWarnings("rawtypes")
	private void initComponent(){
		
		setBackground(new Color(232,232,232));
		setBorder(BorderFactory.createEmptyBorder());
		setOpaque(false);
		setCellRenderer(new PictureClipboardListCellRenderer());
		setLayout(new BorderLayout());
		
		FormLayout layout2 = new FormLayout(
				"pref",
				"pref, 3dlu, pref,3dlu, pref,3dlu, pref"
			    );     	
		CellConstraints cc = new CellConstraints();
		PanelBuilder builder2 = new PanelBuilder(layout2);
		builder2.setDefaultDialogBorder();
		builder2.add(getRemoveAllPicturesButton(), cc.xy(1, 1));
		builder2.add(getRemoveSelectedPictureButton(), cc.xy(1, 3));
		builder2.add(getExportPicturesButton(), cc.xy(1, 5));
        builder2.add(getSendMailButton(), cc.xy(1,7));

		
		FormLayout layout = new FormLayout(
			"pref, 3dlu, fill:default:grow",     			
			    "pref, 3dlu, fill:default:grow");
		PanelBuilder builder = new PanelBuilder(layout);
		builder.addSeparator(name,   cc.xyw(1,1,3));
		builder.add(builder2.getPanel(), cc.xy(1,3));
		builder.add(new JScrollPane(getItemList()), cc.xy(3,3));
		
		add(builder.getPanel(), BorderLayout.CENTER);
		updateButtonState();
		
		setTitle("Ablage");
		
		
		GlossPainter gloss = new GlossPainter();
	    PinstripePainter stripes = new PinstripePainter();
	    stripes.setPaint(new Color(1.0f, 1.0f, 1.0f, 0.17f));
	    stripes.setSpacing(5.0);
		    
	    MattePainter matte = new MattePainter(new Color(51, 51, 51));

	    setTitlePainter(new CompoundPainter(matte, stripes, gloss));
	    getTitleFont().deriveFont(Font.BOLD);
		setTitleForeground(Color.WHITE);
	}
	
	public JButton getExportPicturesButton() {
		if(exportPicturesButton == null){
			exportPicturesButton = new JButton(ActionProvider.getDisplayExportPicturesDialogAction());
			exportPicturesButton.setText("Exportieren");
			exportPicturesButton.setHorizontalAlignment(JButton.LEFT);
			ActionProvider.getDisplayExportPicturesDialogAction().setEnabled(true);
		}
		return exportPicturesButton;
	}


	public JButton getRemoveAllPicturesButton() {
		if(removeAllPicturesButton == null){
			removeAllPicturesButton = new JButton(ActionProvider.getClearPictureClipboardAction());
			removeAllPicturesButton.setText("Leeren");
			removeAllPicturesButton.setHorizontalAlignment(JButton.LEFT);
			removeAllPicturesButton.setEnabled(false);
		}
		return removeAllPicturesButton;
	}
	
	private JButton getSendMailButton(){
		if(sendMailButton == null){
			sendMailButton = new JButton(ActionProvider.getDisplaySendMailDialogAction());
			sendMailButton.setHorizontalAlignment(JButton.LEFT);
		}
		return sendMailButton;
	}
	

	public void setCellRenderer(DefaultListCellRenderer renderer){
		getItemList().setCellRenderer(renderer);
	}
	
	public JButton getRemoveSelectedPictureButton() {
		if(removeSelectedPictureButton == null){
			removeSelectedPictureButton = new JButton(
					new AbstractPicmanAction("Entfernen"){
						
						private static final long serialVersionUID = 4145550933936010163L;

						{
							putValue(Action.SHORT_DESCRIPTION, "Bild löschen");
							putValue(Action.SMALL_ICON, GUIControl.get().getIconProvider().getImageIcon("icon.remove"));
						}
						
						@Override
						public void actionPerformed(ActionEvent arg0) {
							DefaultListModel model = getListModel();
							int index = getItemList().getSelectedIndex();
							if(index>-1){
								Picture selectedPic = (Picture)getItemList().getSelectedValue();
								PictureClipboard.getInstance().removePicture(selectedPic);
								getItemList().setSelectedIndex(index);
							}
							if(index >= model.getSize()){
								getItemList().setSelectedIndex(model.getSize()-1);
							}
						}
				}		
			
			);
			removeSelectedPictureButton.setPreferredSize(new Dimension(25,25));
			removeSelectedPictureButton.setEnabled(false);
			removeSelectedPictureButton.setHorizontalAlignment(JButton.LEFT);
		}
		return removeSelectedPictureButton;
	}

	public Font getDefaultFont(){
		return new Font("Monospace", Font.BOLD, 14);
	}
	
	public JLabel getInfoLabel() {
		if(infoLabel == null){
			infoLabel = new JLabel("0");
			infoLabel.setForeground(Color.DARK_GRAY);
			infoLabel.setFont(getDefaultFont());
			infoLabel.setVisible(false);
		}
		return infoLabel;
	}

	public JList getItemList() {
		if(itemList == null){
			itemList = new JList();
			itemList.setBackground(Color.DARK_GRAY);
			itemList.setDropMode(DropMode.INSERT);
			
			itemList.setAutoscrolls(true);
			itemList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
			itemList.setVisibleRowCount(1);
			DefaultListModel listModel = new DefaultListModel();
			itemList.setModel(listModel);
			itemList.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
			itemList.addListSelectionListener(new ListSelectionListener(){
				@Override
				public void valueChanged(ListSelectionEvent e) {
					getRemoveSelectedPictureButton().setEnabled(!itemList.isSelectionEmpty());
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
			});
			
			
			
		}
		return itemList;
	}
	
	
	
	private void updateButtonState(){
		getInfoLabel().setText(String.valueOf(getListModel().getSize()));
		ActionProvider.getClearPictureClipboardAction().setEnabled(!PictureClipboard.getInstance().getPictureMap().isEmpty());
		
		int index = getItemList().getSelectedIndex();
		getRemoveSelectedPictureButton().setEnabled(index>0);
		
		getSendMailButton().setEnabled(!PictureClipboard.getInstance().getPictureMap().isEmpty());
		ActionProvider.getDisplayExportPicturesDialogAction().setEnabled(!PictureClipboard.getInstance().getPictureMap().isEmpty());
	}
	
	public DefaultListModel getListModel(){
		return (DefaultListModel)itemList.getModel();
	}
	
	
	
	public void refresh(){
		getListModel().removeAllElements();
		HashMap<Integer, Picture> picMap = PictureClipboard.getInstance().getPictureMap();
		Collection<Picture> pictures = picMap.values();
		for(Picture pic: pictures){
			DefaultListModel model = getListModel();
			model.addElement(pic);
		}
		updateButtonState();
	}
	
	
}
