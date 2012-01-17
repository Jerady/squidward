/*
 * Created on 05.07.2008
 *
 */
package de.picman.backend.control;

import java.awt.Dimension;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;

import de.picman.backend.db.Category;
import de.picman.backend.db.DbController;
import de.picman.backend.db.Picture;
import de.picman.backend.db.User;

/**
 * 
 * @author Ole Rahn
 * 
 */
public class ApplicationControl {

    protected static ApplicationControl instance = null;
    
    protected DbController dbController = null;

    protected DbController maintenanceDbController = null;
    protected Map<Integer, Picture> masterPictureMap = null;
    protected boolean masterMapInitiated = false;
    protected long lastMasterMapUpdate = 0;
    protected int mapUpdateInterval = 5 * 60 * 1000;
    protected Thread mapUpdater = null;
    
    protected Preferences preferences = null;
    
    protected User currentUser = null;

    private ApplicationControl() {
        super();
    }
    
    protected DbController getMaintenanceDbController() {
        if (maintenanceDbController==null){
            maintenanceDbController = new DbController(getPreferences().get("dbhost", "localhost"),getPreferences().get("dbport", "5432"),getPreferences().get("dbuser", "postgres"),getPreferences().get("dbpass", "postgres"));
        }
        return maintenanceDbController;
    }
    
    /**
     * force immediate update of the master map, e.g. after uploading new pictures to the database.
     * @throws Exception
     */
    public synchronized void updateMasterMap() throws Exception{
        int sizeBefore = getMasterMap().size();
        long before = System.currentTimeMillis();
        this.getMaintenanceDbController().updateMap(mapUpdateInterval/1000, getMasterMap());
        lastMasterMapUpdate = System.currentTimeMillis();
        logFine("updated master map in "+(lastMasterMapUpdate-before)+"ms, size before: " + sizeBefore + ", after update: " + getMasterMap().size());
        System.out.println("updated master map in "+(lastMasterMapUpdate-before)+"ms, size before: " + sizeBefore + ", after update: " + getMasterMap().size());
    }
    
    public synchronized void clearPictures() throws Exception{
        Picture[] pics = getMasterMap().values().toArray(new Picture[0]);
        
        for (int i = 0; i < pics.length; i++) {
            pics[i].clearPicture();
        }
        
        Runtime.getRuntime().gc();
    }
    
    /**
     * 
     * @return true after initial filling of the master map was finished
     */
    public boolean isMasterMapInitiated() {
        return masterMapInitiated;
    }

    /**
     * method to receive THE master map of picture IDs to {@link Picture} objects. The map will be updated
     * automatically in a separate {@link Thread} every {@link #mapUpdateInterval} milliseconds (default 5min).
     * The map needs to be initialized once in the beginning by a call to the method {@link #initiatePictureMasterMap()}.
     * This method will implicitly be called in case the {@link #getMasterMap()} method is called for the first time.<br>
     * A good way would be to start a thread executing the {@link #getMasterMap()} method, as 
     * a first action on the start of the application.<br>
     * {@link #isMasterMapInitiated()} gives will tell you if the initialization of the master map was finished.
     * The master map is implicitly used by {@link DbController} methods, e.g. {@link DbController#searchPicturesAndGetObjectFromMap(de.rahn.bilderdb.db.PictureSearchCriteria, de.rahn.bilderdb.db.PictureColumn)} 
     * @return
     * @throws Exception
     */
    public Map<Integer, Picture> getMasterMap() throws Exception{
        if (masterPictureMap==null){
            initiatePictureMasterMap();
        }
        return this.masterPictureMap;
    }
    
    public Map<Integer, Picture> getMasterMapClone() throws Exception{
        if (masterPictureMap==null){
            initiatePictureMasterMap();
        }
        HashMap<Integer, Picture> clone = new HashMap<Integer, Picture>();
        clone.putAll(this.masterPictureMap);
        
        return clone;
    }
    
    protected void initiatePictureMasterMap() throws Exception{
        if (masterPictureMap==null){
            this.masterPictureMap = Collections.synchronizedMap(new HashMap<Integer, Picture>());
            this.masterPictureMap = this.getMaintenanceDbController().loadAllPictures();
            masterMapInitiated = true;
            lastMasterMapUpdate = System.currentTimeMillis();
            
            this.mapUpdater = new Thread(){
                @Override
                public void run() {
                    while(true){
                        try {
                            Thread.sleep(mapUpdateInterval);
                        } catch (InterruptedException e) {
                            return;
                        }
                        try {
                            updateMasterMap();
                        } catch (Exception e) {
                            displayErrorToUser(e);
                        }
                    }
                }
            };
            this.mapUpdater.start();
        }
    }
    
    public static void main(String[] args){
    	try {
			ApplicationControl.getInstance().login("Ole", "geheim");
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public Preferences getPreferences(){
        if (preferences==null){
            preferences = Preferences.systemRoot().node("picman");
            try {
				initPreferences();
			} catch (BackingStoreException e) {
				e.printStackTrace();
			}
        }
        return preferences;
    }
    
    protected void initPreferences() throws BackingStoreException{
        if (getPreferences()!=null){
            if (getPreferences().get("firstrun", null)==null){
                getPreferences().putBoolean("firstrun", false);
                getPreferences().put("dbhost", "192.168.0.2");
                getPreferences().put("dbuser", "postgres");
                getPreferences().put("dbpass", "postgres");
                getPreferences().put("dbname", "picturedb");
                getPreferences().put("dbport", "5432");
                getPreferences().put("samplefilesdir", "D://Entwicklung//BilderDb//beispiel_bilder");
                getPreferences().put("sampleexportdir", "/home/orahn/Desktop/picture_export_test");
                getPreferences().put("loglevel", "INFO");
                getPreferences().sync();
            }
        }
    }
    
    public String getSamplePicDirectory(){
    	return getPreferences().get("samplefilesdir", null);
    }
    
    public String getSampleExportDirectory(){
        return getPreferences().get("sampleexportdir", null);
    }
    
    public Level getLogLevel(){
    	return Level.parse(getPreferences().get("loglevel", "INFO"));
    }
    
    public Dimension getPicturePreviewDimension(){
        return new Dimension(320,240);
    }
    
    public Dimension getPictureThumbnailDimension(){
        return new Dimension(128,96);
    }
    
    /**
     * a simple way to notify a user that something went wrong: will raise a message dialog,
     * print the stack trace to the console and log the exception to the database
     * @param e exception that occurred
     */
    public final static void displayErrorToUser(Exception e){
        ApplicationControl.getInstance().logSevere("An exception occurred and was shown to user:\n" + exceptionToString(e));
        JOptionPane.showMessageDialog(null, e.toString() , "Ein Fehler trat auf.", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
    
    protected final static String exceptionToString(Exception e){
        String result = e.toString() + "\n";
        
        if (getInstance().getLogLevel().intValue() <= Level.INFO.intValue()) {
            StackTraceElement[] traceLines =  e.getStackTrace();
            StackTraceElement stackTraceElement;
            
            for (int i = 0; i < traceLines.length; i++) {
                stackTraceElement = traceLines[i];
                result += "\n" + stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() + "(" + stackTraceElement.getFileName() + ":" + stackTraceElement.getLineNumber() + ")";
            }
        }
        
        return result;
    }

    /**
     * @param username user name entered by User
     * @param password password entered by User
     * @return a {@link User} object, in case the given combination of 
     * user name and password is found in the database. Otherwise NULL is returned.
     * @throws Exception 
     */
    public User login(String username, String password) throws Exception{
    	if (this.currentUser!=null){
    		logInfo("about to loging, even though already logged in.");
    	}
        this.currentUser = getDbController().attemptLogin(username, password);
        if (getCurrentUser()!=null){
            logInfo("user logged in.");
        }
        return getCurrentUser();
    }
    
    public User getCurrentUser() {
        return currentUser;
    }

    public static ApplicationControl getInstance(){
        if (instance==null){
           instance = new ApplicationControl();
        }
        return instance;
    }


    public DbController getDbController() {
        if (dbController==null){
            dbController = new DbController(getPreferences().get("dbhost", "localhost"),getPreferences().get("dbport", "5432"),getPreferences().get("dbuser", "postgres"),getPreferences().get("dbpass", "postgres"));
        }
        return dbController;
    }

    public void logFine(Picture picture, Category category, String message){
        getDbController().logFine(picture, category, message);
    }

    public void logFine(Picture picture, String message) {
        getDbController().logFine(picture, message);
    }

    public void logFine(String message) {
        getDbController().logFine(message);
    }

    public void logInfo(Picture picture, Category category, String message){
        getDbController().logInfo(picture, category, message);
    }

    public void logInfo(Picture picture, String message) {
        getDbController().logInfo(picture, message);
    }

    public void logInfo(String message) {
        getDbController().logInfo(message);
    }

    public void logSevere(Picture picture, Category category, String message){
        getDbController().logSevere(picture, category, message);
    }

    public void logSevere(Picture picture, String message) {
        getDbController().logSevere(picture, message);
    }

    public void logSevere(String message) {
        getDbController().logSevere(message);
    }

    public void logWarn(Picture picture, Category category, String message){
        getDbController().logWarn(picture, category, message);
    }

    public void logWarn(Picture picture, String message){
        getDbController().logWarn(picture, message);
    }

    public void logWarn(String message){
        getDbController().logWarn(message);
    }

    /**
     * @return true/false - enables/disables special debug mode e.g. with extended logging
     */
    public boolean isDebug() {
        return false;
    }
    
    
}
