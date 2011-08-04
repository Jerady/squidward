package de.picman.gui.components;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.picman.gui.api.PrefsKeys;
import de.picman.gui.dialogs.PictureViewerDialog;
import de.picman.gui.main.GUIControl;
import de.picman.gui.panels.CategoriesTreePanel;
import de.picman.gui.panels.MainMenuPanel;
import de.picman.gui.panels.MainMenuTreePanel;
import de.picman.gui.panels.PictureClipboardPanel;
import de.picman.gui.panels.PictureDisplayPanel;
import de.picman.gui.panels.PicturePropertiesPanel;
import de.picman.gui.panels.SearchCriteriaPanel;
import de.picman.gui.panels.StatusPanel;
import de.picman.gui.providers.ActionProvider;
import de.rahn.bilderdb.control.ApplicationControl;
import de.rahn.bilderdb.db.Category;
import de.rahn.bilderdb.db.Picture;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = -663347519979662508L;
	private JComponent mainPane;
	private StatusPanel statusPanel;
	private MainMenuTreePanel mainMenuTreePanel;
	private MainMenuPanel mainMenuPanel;
	private PictureDisplayPanel pictureDisplayPanel;
	private CategoriesTreePanel categoriesTreePanel;
	private SearchCriteriaPanel searchCriteriaPanel;
	private PictureClipboardPanel pictureClipboardPanel2;
	private PicturePropertiesPanel picturePropertiesPanel;
	private PictureViewerDialog pictureViewerDialog;
	private WindowBlocker windowBlocker;
	
	protected static MainFrame me = null;

    public static MainFrame getInstance(){
        if (me==null){
            me = new MainFrame();
        }
        return me;
    }

    
	private MainFrame() {
		this.setTitle(GUIControl.get().getProperty(PrefsKeys.GUI_APPNAME)+ " - " + GUIControl.get().getProperty(PrefsKeys.GUI_VERSION));
		this.setSize(1200, 768);
		
		this.setJMenuBar(new Menu());
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		setGlassPane(getWindowBlocker());
		add(getMainPane(), BorderLayout.CENTER);
		getStatusPanel().setCurrentUser(ApplicationControl.getInstance().getCurrentUser());
		setLocationRelativeTo(null);
		setDefaultLookAndFeelDecorated(false);
	}

	
	
	protected WindowBlocker getWindowBlocker() {
		if(windowBlocker == null){
			windowBlocker = new WindowBlocker();
		}
		return windowBlocker;
	}

	public boolean isLocked(){
		return getWindowBlocker().isLocked();
	}
	
	public void lock(){
		getWindowBlocker().block();
	}

	public void lockLight(){
		getWindowBlocker().blockLight();
	}
	
	public void unlockLight(){
		getWindowBlocker().unBlockLight();
	}
	
	public void unlock(){
		getWindowBlocker().unBlock();
	}

	public SearchCriteriaPanel getSearchCriteriaPanel() {
		if(searchCriteriaPanel == null){
			searchCriteriaPanel = new SearchCriteriaPanel();
		}
		return searchCriteriaPanel;
	}


	public PicturePropertiesPanel getPicturePropertiesPanel() {
		if(picturePropertiesPanel == null){
			picturePropertiesPanel = new PicturePropertiesPanel();
		}
		return picturePropertiesPanel;
	}


	public void showMessageToBeImplemented(){
		 JOptionPane.showMessageDialog(this, "Noch nicht implementiert!");	
	}

	public JComponent getMainPane(){
		if(mainPane == null){
			
			FormLayout layout = new FormLayout(
					//   1           2        3        4            5          6          7
				    "fill:default, 0dlu,  fill:0dlu, 0dlu, fill:default:grow, 0dlu,  fill:default",
				    //     1           2                  3              4        5
					"fill:default," +
					"fill:default," +
					"fill:default:grow, " +
					"fill:100dlu," +
					"fill:default");     			

			CellConstraints cc = new CellConstraints();
			PanelBuilder builder = new PanelBuilder(layout);
			builder.add(getFilterPane(), cc.xywh(1,1,1,3));
			
			builder.add(getPictureDisplayPanel(), cc.xywh(5,1,1,3));
			builder.add(getPicturePropertiesPanel(), cc.xywh(7,1,1,4));
			builder.add(getPictureClipboardPanel2(), cc.xyw(1, 4, 5));
			builder.add(getStatusPanel(), cc.xyw(1,5,7));
			
			mainPane = builder.getPanel();
			
			bind();
			
		}
		return mainPane;
	}
	
	private void bind(){
		TransferHandler pictureHandler = new PictureTransferHandler();
		getPictureClipboardPanel2().getItemList().setTransferHandler(pictureHandler);
		getPictureDisplayPanel().getPictureList().setTransferHandler(pictureHandler);
	}
	
	
	private JComponent getFilterPane(){
		JTabbedPane pane = new JTabbedPane();
		pane.setBorder(null);
		pane.add(getMainMenuPanel(),"");
		pane.setIconAt(0, GUIControl.get().getImageIcon("icon.picture"));


		pane.add(getCategoriesTreePanel(),"");
		pane.setIconAt(1, GUIControl.get().getImageIcon("icon.category"));

		pane.add(getSearchCriteriaPanel(),"");
		pane.setIconAt(2, GUIControl.get().getImageIcon("icon.search"));

		pane.setTabPlacement(JTabbedPane.BOTTOM);


//		JXTaskPaneContainer container =  new JXTaskPaneContainer();
//		container.add(getMainMenuPanel());
//		container.add(getCategoriesTreePanel());
//		container.add(getSearchCriteriaPanel());
//		
//		JScrollPane pane = new JScrollPane(container);
//		pane.setBorder(new DropShadowBorder());
		
		return pane;
	}
	
	
	public PictureViewerDialog getPictureViewerDialog() {
		if(pictureViewerDialog == null){
			pictureViewerDialog = new PictureViewerDialog();
		}
		return pictureViewerDialog;
	}


	public PictureClipboardPanel getPictureClipboardPanel2(){
		if(pictureClipboardPanel2 == null){
			pictureClipboardPanel2 = new PictureClipboardPanel("Ablage");
			
			pictureClipboardPanel2.getItemList().addMouseListener(new MouseAdapter(){
		    	public void mouseClicked(MouseEvent e) {
		    		if(e.getClickCount() == 1){
		    			Picture pic = (Picture)pictureClipboardPanel2.getItemList().getSelectedValue();
		    			getPicturePropertiesPanel().setPicture(pic);
		    		}
		    	}
		    });

			pictureClipboardPanel2.getItemList().addKeyListener(new KeyAdapter(){
				@Override
				public void keyReleased(KeyEvent e) {
		    		Picture pic = (Picture)pictureClipboardPanel2.getItemList().getSelectedValue();
		    		getPicturePropertiesPanel().setPicture(pic);
				}
			});

		}
		return pictureClipboardPanel2;	
	}
	
	
	public CategoriesTreePanel getCategoriesTreePanel(){
		if(categoriesTreePanel == null){
			categoriesTreePanel = new CategoriesTreePanel();
			
			categoriesTreePanel.getCategoriesTree().addMouseListener(new MouseAdapter(){
					@Override
					public void mouseClicked(MouseEvent e) {
						if(e.getClickCount() == 2){
							
							DefaultMutableTreeNode selectedNode =
								(DefaultMutableTreeNode)categoriesTreePanel.getCategoriesTree().getSelectionModel().getSelectionPath().getLastPathComponent();

							Object userObject = selectedNode.getUserObject();
							
							if(userObject instanceof Category){
								Category selectedCategory =
									(Category)selectedNode.getUserObject();
								
								setStatusMessage("Suche Bilder zu Kategorie " + selectedCategory.getName());
								MainFrame.getInstance().lock();
								ActionProvider.getSearchPictureByCategoryAction().searchCategory(selectedCategory);
								MainFrame.getInstance().unlock();
							}
							
							
						}
					}
				});
			}
		return categoriesTreePanel;
	}

	public PictureDisplayPanel getPictureDisplayPanel(){
		if(pictureDisplayPanel == null){
			pictureDisplayPanel = new PictureDisplayPanel();
		}
		return pictureDisplayPanel;
	}
	
	
	public StatusPanel getStatusPanel(){
		if(statusPanel == null){
			statusPanel = new StatusPanel();
		}
		return statusPanel;
	}

	
	public MainMenuTreePanel getMainMenuTreePanel(){
		if(mainMenuTreePanel == null){
			mainMenuTreePanel = new MainMenuTreePanel();
		}
		return mainMenuTreePanel;
	}
	
	public MainMenuPanel getMainMenuPanel(){
		if(mainMenuPanel == null){
			mainMenuPanel = new MainMenuPanel();
		}
		return mainMenuPanel;
	}
	
	
	public void setStatusMessage(final String message){
		EventQueue.invokeLater(new Runnable(){
			public void run() {
				MainFrame.this.getStatusPanel().getMessageLabel().setText(message);
			}
		});
	}
	
	@SuppressWarnings("unused")
	private static class MainToolBar extends JToolBar{
		private static final long serialVersionUID = 4005367323567482150L;

		public MainToolBar() {
			add(ActionProvider.getDisplayAllPicturesAction());
			add(ActionProvider.getUploadPictureAction());
			add(ActionProvider.getDisplayExportPicturesDialogAction());
			add(new Separator());
			setRollover(true);
		}
		
	}
	
	
	private static class Menu extends JMenuBar{
		
		private static final long serialVersionUID = 3710830080803304579L;

		public Menu() {
			JMenu dateiMenu = new JMenu(GUIControl.get().getProperty(PrefsKeys.GUI_APPNAME));
			dateiMenu.setFont(dateiMenu.getFont().deriveFont(Font.BOLD));
			dateiMenu.add(new JMenuItem(ActionProvider.getDisplayAboutDialogAction()));
			dateiMenu.add(new JSeparator());
			dateiMenu.add(new JMenuItem(ActionProvider.getBeendenAction()));
			this.add(dateiMenu);

			JMenu picturesMenu = new JMenu("Bild");
			picturesMenu.add(new JMenuItem(ActionProvider.getDisplayAllPicturesAction()));
			picturesMenu.add(new JMenuItem(ActionProvider.getUploadPictureAction()));
			picturesMenu.add(new JSeparator());
			picturesMenu.add(new JMenuItem(ActionProvider.getDeletePictureAction()));
			picturesMenu.add(new JMenuItem(ActionProvider.getRestorePictureAction()));
			this.add(picturesMenu);
			
			ActionProvider.getDeletePictureAction().setEnabled(false);
			ActionProvider.getRestorePictureAction().setEnabled(false);

			JMenu clipboardMenu = new JMenu("Ablage");
			clipboardMenu.add(new JMenuItem(ActionProvider.getClearPictureClipboardAction()));
			clipboardMenu.add(new JMenuItem(ActionProvider.getDisplayExportPicturesDialogAction()));
			this.add(clipboardMenu);

			
			JMenu adminMenu = new JMenu("Administration");
			adminMenu.add(new JMenuItem(ActionProvider.getEditUserAction()));
			adminMenu.add(new JMenuItem(ActionProvider.getEditCategoriesAction()));
			adminMenu.addSeparator();
			adminMenu.add(new JMenuItem(ActionProvider.getDisplayDatabaseSettingsDialogAction()));
			adminMenu.add(new JMenuItem(ActionProvider.getDisplayLogViewerAction()));
			adminMenu.addSeparator();
			adminMenu.add(new JMenuItem(ActionProvider.getSearchDeletedPictureAction()));
			adminMenu.add(new JMenuItem(ActionProvider.getRestorePictureAction()));
			adminMenu.addSeparator();
			adminMenu.add(new JMenuItem(ActionProvider.getGarbageCollectorAction()));
	
			adminMenu.setFont(dateiMenu.getFont().deriveFont(Font.BOLD));
			this.add(adminMenu);

			adminMenu.setVisible(ApplicationControl.getInstance().getCurrentUser().isAdmin());
		
			

			
			
		}
		
	}
	

}
