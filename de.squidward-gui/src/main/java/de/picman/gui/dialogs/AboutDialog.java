package de.picman.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.looks.LookUtils;
import com.jgoodies.looks.Options;

import de.picman.gui.api.PrefsKeys;
import de.picman.gui.main.GUIControl;
import de.picman.gui.providers.ResourceAnchor;
import de.rahn.bilderdb.control.ApplicationControl;

public class AboutDialog extends JDialog {

	private static final long serialVersionUID = 5356221893449191712L;
	private JTextArea releaseNotesArea;
	private JTextArea usedLibraryArea;
	private JTextArea databaseInfoArea;
	private String aboutFilePath = "de/picman/resources/release_notes.txt";
	private String libraryFilePath = "de/picman/resources/library_notes.txt";
		
	public AboutDialog() {
		initComponent();
		loadReleaseNotesFile();
		loadLibraryNotesFile();
		fetchDatabaseInformation();
	}
	
	private void fetchDatabaseInformation(){
		try {

			getDatabaseInfoArea().append("DATENBANK INFORMATIONEN\n");
			getDatabaseInfoArea().append("--------------------------------------\n\n");
			getDatabaseInfoArea().append("BILDER\n");
			getDatabaseInfoArea().append("              Bilder in der DB......: ");
			getDatabaseInfoArea().append(Long.toString(ApplicationControl.getInstance().getDbController().getPictureCount()));
			getDatabaseInfoArea().append("\n");

			getDatabaseInfoArea().append("\n");

			getDatabaseInfoArea().append("BENUTZER\n");
			getDatabaseInfoArea().append("              Administratoren.......: ");
			getDatabaseInfoArea().append(Long.toString(ApplicationControl.getInstance().getDbController().getAdminCount()));
			getDatabaseInfoArea().append("\n");
			getDatabaseInfoArea().append("              Benuzter..............: ");
			getDatabaseInfoArea().append(Long.toString(ApplicationControl.getInstance().getDbController().getRegularUserCount()));
			getDatabaseInfoArea().append("\n");
			getDatabaseInfoArea().append("              'Nur lesen'-Benutzer..: ");
			getDatabaseInfoArea().append(Long.toString(ApplicationControl.getInstance().getDbController().getReadOnlyUserCount()));
			getDatabaseInfoArea().append("\n");
			getDatabaseInfoArea().append("\n");

			
			getDatabaseInfoArea().append("KATEGORIEN\n");
			getDatabaseInfoArea().append("              Top level............:  ");
			getDatabaseInfoArea().append(Long.toString(ApplicationControl.getInstance().getDbController().getTopLevelCategoryCount()));
			getDatabaseInfoArea().append("\n");
			getDatabaseInfoArea().append("              Sub Kategorien.......:  ");
			getDatabaseInfoArea().append(Long.toString(ApplicationControl.getInstance().getDbController().getCategoryCount()-
					ApplicationControl.getInstance().getDbController().getTopLevelCategoryCount()));
			getDatabaseInfoArea().append("\n");
			getDatabaseInfoArea().append("\n");

			getDatabaseInfoArea().append("DATENBANK LOG\n");
			getDatabaseInfoArea().append("              Log Einträge.........:  ");
			getDatabaseInfoArea().append(Long.toString(ApplicationControl.getInstance().getDbController().getLogEntryCount()));
			getDatabaseInfoArea().append("\n");
			getDatabaseInfoArea().append("\n");
		
		
		} catch (Exception e) {
			ApplicationControl.displayErrorToUser(e);
		}

		
	}
	
	private void loadReleaseNotesFile(){
		try {
			ClassLoader classLoader = ResourceAnchor.class.getClassLoader();
			BufferedReader reader = new BufferedReader(new InputStreamReader(classLoader.getResourceAsStream(aboutFilePath)));
			String line = "";
			while ((line = reader.readLine()) != null) {
				getReleaseNotesArea().append(line);
				getReleaseNotesArea().append("\n");
			}
			reader.close();
			
		} catch (Exception e) {
			ApplicationControl.displayErrorToUser(e);
		}
	}
	
	private void loadLibraryNotesFile(){
		try {
			ClassLoader classLoader = ResourceAnchor.class.getClassLoader();
			BufferedReader reader = new BufferedReader(new InputStreamReader(classLoader.getResourceAsStream(libraryFilePath)));
			String line = "";
			while ((line = reader.readLine()) != null) {
				getUsedLibraryArea().append(line);
				getUsedLibraryArea().append("\n");
			}
			reader.close();
			
		} catch (Exception e) {
			ApplicationControl.displayErrorToUser(e);
		}
	}
	
	protected JTextArea getUsedLibraryArea() {
		if(usedLibraryArea==null){
			usedLibraryArea = new JTextArea();
			usedLibraryArea.setEditable(false);
			usedLibraryArea.setLineWrap(true);
			usedLibraryArea.setOpaque(true);
			usedLibraryArea.setBackground(Color.DARK_GRAY);
			usedLibraryArea.setForeground(Color.WHITE);
		}
		return usedLibraryArea;
	}

	protected JTextArea getReleaseNotesArea() {
		if(releaseNotesArea==null){
			releaseNotesArea = new JTextArea();
			releaseNotesArea.setEditable(false);
			releaseNotesArea.setLineWrap(true);
			releaseNotesArea.setOpaque(true);
			releaseNotesArea.setBackground(Color.DARK_GRAY);
			releaseNotesArea.setForeground(Color.WHITE);
		}
		return releaseNotesArea;
	}

	protected JTextArea getDatabaseInfoArea() {
		if(databaseInfoArea==null){
			databaseInfoArea = new JTextArea();
			databaseInfoArea.setEditable(false);
			databaseInfoArea.setLineWrap(true);
			databaseInfoArea.setOpaque(true);
			databaseInfoArea.setFont(new Font("Monospaced",Font.PLAIN,12));
			databaseInfoArea.setBackground(Color.DARK_GRAY);
			databaseInfoArea.setForeground(Color.WHITE);
		}
		return databaseInfoArea;
	}

	private void initComponent(){
		setModal(true);
		setSize(new Dimension(600,600));
		setLocationByPlatform(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("Über " + GUIControl.get().getProperty(PrefsKeys.GUI_APPNAME) +"...");
		setLayout(new BorderLayout());
		
		FormLayout layout = new FormLayout(
			    "right:pref, 3dlu, 350dlu", // columns
			    // 1     2     3     4     5    6      7    8        9       10     1    12     13  14    15     16   17   18  19  20          22    23             24    25 
			    "pref, 3dlu, pref, 3dlu, pref, 3dlu,  pref, 3dlu, fill:default:grow");
			    
		CellConstraints cc = new CellConstraints();

		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		
		
		builder.addSeparator("Über " + GUIControl.get().getProperty(PrefsKeys.GUI_APPNAME),   cc.xyw(1,  1, 3));
		builder.addLabel("Version:",       cc.xy(1,  3));
		builder.addLabel(GUIControl.get().getProperty(PrefsKeys.GUI_VERSION),         cc.xy(3,  3));
		builder.addLabel("(c) Copyright 2009 Jens Deters (GUI) und Ole Rahn (Backend + Datenbank)",   cc.xyw(1, 5,3));

		
		builder.addSeparator("", cc.xyw(1,7,3));
		builder.add(getNotesPane(), cc.xyw(1,9,3));
		
		
		add(builder.getPanel(), BorderLayout.CENTER);
	}
	
	
	private JTabbedPane getNotesPane(){
		JTabbedPane pane = new JTabbedPane();
		pane.add(new JScrollPane(getReleaseNotesArea()), "Release Notes");
		pane.add(new JScrollPane(getDatabaseInfoArea()),"Database Information");
		pane.add(new JScrollPane(getUsedLibraryArea()), "Used Libraries");
		return pane;
	}
	
	
	/**
	 * @param args
	 */
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

		AboutDialog aboutDialog = new AboutDialog();
		aboutDialog.setVisible(true);
		
	}

}
