package de.picman.gui.main;

import java.awt.KeyEventPostProcessor;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;

import de.picman.backend.control.ApplicationControl;
import de.picman.backend.db.DbController;
import de.picman.backend.db.User;
import de.picman.gui.actions.TakeScreenShotAction;
import de.picman.gui.api.PrefsKeys;
import de.picman.gui.components.MainFrame;
import de.picman.gui.components.PictureClipboard;
import de.picman.gui.components.PictureClipboardObserver;
import de.picman.gui.components.SearchResult;
import de.picman.gui.components.SearchResultObserver;
import de.picman.gui.dialogs.LoadPicturesFrame;
import de.picman.gui.dialogs.SquidwardLoginDialog;
import de.picman.gui.providers.IconProvider;

public class GUIControl {

	/**
	 * this gets rid of exception for not using native acceleration
	 */
	static {
		System.setProperty("com.sun.media.jai.disableMediaLib", "true");
	}

	private Preferences preferences;
	private Preferences uploadProperties;

	protected static GUIControl me = null;
	protected DbController dbController = null;
	protected SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy:MM:dd HH:mm:ss");
	protected IconProvider iconProvider;

	private GUIControl() {
		super();
		setProperty(PrefsKeys.GUI_APPNAME, "Squidward");
		setProperty(PrefsKeys.GUI_VERSION, "0.9.3a");
		setProperty(PrefsKeys.GUI_UPLOAD_CONTROL, false);
		setProperty(PrefsKeys.GUI_DELETE_ORIGINALS, false);
		setProperty(PrefsKeys.GUI_AUTHOR, "Jens Deters und Ole Rahn");
	}

	public static GUIControl get() {
		if (me == null) {
			me = new GUIControl();
		}
		return me;
	}

	public IconProvider getIconProvider() {
		if (iconProvider == null) {
			try {
				iconProvider = new IconProvider(
						"de/picman/resources/icon.properties");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return iconProvider;
	}

	public ImageIcon getImageIcon(String key) {
		return getIconProvider().getImageIcon(key);
	}

	public DbController getDbController() {
		if (dbController == null) {

			String dbHost = getProperty(PrefsKeys.GUI_DBHOST);
			String dbPort = getProperty(PrefsKeys.GUI_DBPORT);
			String dbUser = getProperty(PrefsKeys.GUI_DBUSER);
			String dbPassword = getProperty(PrefsKeys.GUI_DBPASS);

			dbController = new DbController(dbHost, dbPort, dbUser, dbPassword);
		}
		return dbController;
	}

	public SimpleDateFormat getDateFormat() {
		return dateFormat;
	}

	protected void showLoginDialog() {
		new SquidwardLoginDialog();
		User user = ApplicationControl.getInstance().getCurrentUser();

		if (user != null) {
			showGUI();
		}
	}

	protected void showGUI() {
		SwingWorker<Object, Void> worker = new SwingWorker<Object, Void>() {
			public Object doInBackground() {
				try {
					LoadPicturesFrame loadPicturesFrame = new LoadPicturesFrame();
					loadPicturesFrame.setVisible(true);
					loadPicturesFrame.setWaiting(true);
					PictureClipboard.getInstance().addObserver(
							new PictureClipboardObserver());
					SearchResult.getInstance().addObserver(
							new SearchResultObserver());
					MainFrame.getInstance().setVisible(true);
					MainFrame.getInstance().lock();
					MainFrame.getInstance().setStatusMessage(
							ApplicationControl.getInstance().getMasterMap()
									.size()
									+ " Bilder geladen");
					loadPicturesFrame.setVisible(false);
					loadPicturesFrame.setWaiting(false);
					MainFrame.getInstance().unlock();

				} catch (Exception e) {
					ApplicationControl.displayErrorToUser(e);
				}
				return null;
			}

			public void done() {
				MainFrame.getInstance().unlock();
			}
		};
		worker.execute();

	}

	public String getProperty(String key) {
		return getPreferences().get(key, "");
	}

	public void setProperty(String key, String value) {
		getPreferences().put(key, value);
		savePreferences();
	}

	public void setProperty(String key, boolean value) {
		getPreferences().putBoolean(key, value);
		savePreferences();
	}

	public Preferences getPreferences() {
		if (preferences == null) {
			preferences = Preferences.systemRoot().node("picman");
			if (getPreferences().getBoolean("firstrun", true))
				initiatePreferences();

		}
		return preferences;
	}

	public Preferences getUploadProperties() {
		if (uploadProperties == null) {
			uploadProperties = Preferences.systemRoot().node("picmanupload");
			initDefaultUploadPreferences();
		}
		return uploadProperties;
	}

	private void initDefaultUploadPreferences() {
		getUploadProperties().putBoolean("exemplary", false);
		getUploadProperties().putBoolean("badExample", false);
		getUploadProperties().putBoolean("publish", false);
		getUploadProperties().putBoolean("description", false);
	}

	private void initiatePreferences() {
		getPreferences().putBoolean(PrefsKeys.GUI_FIRSTRUN, false);
		getPreferences().put(PrefsKeys.GUI_APPNAME, "Squidward");
		getPreferences().put(PrefsKeys.GUI_AUTHOR, "Jens Deters & Ole Rahn");
		getPreferences().put(PrefsKeys.GUI_VERSION, "0.9.2 (LPV)");
		getPreferences().put(PrefsKeys.GUI_LASTPATH,
				System.getProperty("user.dir"));
		getPreferences().put(PrefsKeys.GUI_LASTUSER, "");
		getPreferences().putBoolean(PrefsKeys.GUI_UPLOAD_CONTROL, true);
		getPreferences().putBoolean(PrefsKeys.GUI_DELETE_ORIGINALS, false);

		// getPreferences().put(PrefsKeys.GUI_DBHOST, "localhost");
		// getPreferences().put(PrefsKeys.GUI_DBPORT, "5432");
		// getPreferences().put(PrefsKeys.GUI_DBUSER, "postgres");
		// getPreferences().put(PrefsKeys.GUI_DBPASS, "postgres");
		savePreferences();
	}

	public void savePreferences() {
		try {
			getPreferences().sync();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	public String[] getPictureSourceList() {
		String[] list = new String[0];
		try {
			list = ApplicationControl.getInstance().getDbController()
					.getAllOriginsThatExistInTheDatabase();
		} catch (Exception e) {
			ApplicationControl.displayErrorToUser(e);
		}
		return list;
	}

	public static void setLookAndFeel() {
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
//					UIManager.put("nimbusBase", new Color(40, 40, 40));
//					UIManager.put("nimbusBlueGrey", new Color(60, 60, 70));
//					UIManager.put("control", new Color(100, 100, 100));
//					// UIManager.put("text", new Color(240,240,240));
//					UIManager.put("controlText", new Color(240, 240, 240));
//					UIManager.put("infoText", new Color(240, 240, 240));
//					UIManager.put("menuText", new Color(240, 240, 240));
//					UIManager.put("textForeground", new Color(240, 240, 240));
//					UIManager.put("TextField.foreground", new Color(0, 0, 0));
//					UIManager.put("PasswordField.foreground", new Color(0, 0, 0));
//					UIManager.put("List.foreground", new Color(0, 0, 0));
//					UIManager.put("Label.foreground", new Color(0, 0, 0));
//					UIManager.put("MenuItem.foreground",  new Color(240, 240, 240));
//					UIManager.put("MenuBar.foreground",  new Color(240, 240, 240));
//					UIManager.put("Menu[Enabled].textForeground",  new Color(240, 240, 240));
//					UIManager.put("TextPane.foreground", new Color(0, 0, 0));
//					UIManager.put("TextArea.foreground", new Color(0, 0, 0));
//					UIManager.put("FileChooser.foreground", new Color(0, 0, 0));
//					UIManager.put("ComboBox.background", new Color(0, 0, 0));
					break;
				}
			}
		} catch (UnsupportedLookAndFeelException e) {
			// handle exception
		} catch (ClassNotFoundException e) {
			// handle exception
		} catch (InstantiationException e) {
			// handle exception
		} catch (IllegalAccessException e) {
			// handle exception
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		setLookAndFeel();

		KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.addKeyEventPostProcessor(new KeyEventPostProcessor() {

					public boolean postProcessKeyEvent(KeyEvent e) {
						if (e.getID() == KeyEvent.KEY_PRESSED
								&& e.getKeyCode() == KeyEvent.VK_ESCAPE) {
							new TakeScreenShotAction().actionPerformed(null);
						}
						return true;
					}
				});

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				GUIControl.get().showLoginDialog();
			}
		});

	}

	public static FileFilter getPicturesFileFilter() {

		FileFilter fileFilter = new FileFilter() {
			@Override
			public boolean accept(File f) {
				if (f.isDirectory())
					return true;
				if (f.getAbsolutePath().toLowerCase().endsWith(".png"))
					return true;
				if (f.getAbsolutePath().toLowerCase().endsWith(".gif"))
					return true;
				if (f.getAbsolutePath().toLowerCase().endsWith(".jpg"))
					return true;
				if (f.getAbsolutePath().toLowerCase().endsWith(".bmp"))
					return true;
				if (f.getAbsolutePath().toLowerCase().endsWith(".tif"))
					return true;
				return false;
			}

			@Override
			public String getDescription() {
				return "Bilder und Verzeichnisse";
			}

		};
		return fileFilter;
	}

}
