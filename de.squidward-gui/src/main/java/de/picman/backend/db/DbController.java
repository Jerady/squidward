/*
 * Created on 06.07.2008
 *
 */
package de.picman.backend.db;

import java.awt.Dimension;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.jdom.JDOMException;

import de.picman.backend.control.ApplicationControl;

/**
 * 
 * @author Ole Rahn
 * 
 */
public class DbController {

	protected String host, user, passwd, port;
	protected Connection connection = null;

	protected boolean localCaching = true;
	protected File localCacheDirectory = null;

	protected Level logLevel = Level.OFF;

	/**
	 * @param host
	 * @param port
	 * @param user
	 * @param passwd
	 */
	public DbController(String host, String port, String user, String passwd) {
		super();
		this.host = host;
		this.port = port;
		this.user = user;
		this.passwd = passwd;
		this.logLevel = ApplicationControl.getInstance().getLogLevel();
	}

	protected void loadDriver() throws ClassNotFoundException {
		Class.forName("org.postgresql.Driver");
		driverLoaded = true;
	}

	protected String createDbString() {
		return "jdbc:postgresql://"
				+ host
				+ ":"
				+ port
				+ "/"
				+ ApplicationControl.getInstance().getPreferences()
						.get("dbname", "pictureDb");
	}

	private boolean driverLoaded = false;

	public Connection getConnection() throws Exception {

		if (connection == null || connection.isClosed()) {
			if (!driverLoaded) {
				loadDriver();
			}
			String url = createDbString();
			connection = null;
			connection = DriverManager.getConnection(url, user, passwd);
			connection.setAutoCommit(false);
		}
		return connection;
	}

	public boolean isLocalCaching() {
		return localCaching;
	}

	public File getThumnailCacheFile(Picture pic) {
		return getThumnailCacheFile(pic.getId());
	}

	protected File getThumnailCacheFile(int picId) {
		return new File(getLocalCacheDirectory(), thumbnailFilenamePrefix
				+ picId + thumbnailFilenamePostfix);
	}

	public File getLocalCacheDirectory() {
		if (localCacheDirectory == null) {
			localCacheDirectory = new File(System.getProperty("java.io.tmpdir")
					+ File.separator
					+ host
					+ "_"
					+ port
					+ "_"
					+ ApplicationControl.getInstance().getPreferences()
							.get("dbname", "pictureDb") + File.separator);

			if (!localCacheDirectory.exists()) {
				localCacheDirectory.mkdirs();
			}

			if (!localCacheDirectory.exists()
					|| !localCacheDirectory.canWrite()) {
				throw new IllegalStateException(
						"Local image caching can not be activated, because the local caching directory ("
								+ localCacheDirectory.getAbsolutePath()
								+ ") can not be created or is not writeable.");
			}

		}
		return localCacheDirectory;
	}

	public void closeConnection() throws SQLException {
		if (connection != null && connection.isClosed() == false) {
			connection.close();
			connection = null;
		}
	}

	public ResultSet doSelect(String sql) throws Exception {
		Statement st = getConnection().createStatement();
		// st.setFetchSize(100);
		ResultSet rs = st.executeQuery(sql);
		return rs;
	}

	public int doInsertUpdateDelete(String sql, boolean commit)
			throws Exception {

		Statement st = getConnection().createStatement();

		int result = -1;
		result = st.executeUpdate(sql);

		if (commit) {
			getConnection().commit();
		}

		return result;
	}

	public void updateMap(int seconds, Map<Integer, Picture> idToPic)
			throws Exception {
		Integer[] ids = getIdsOfRecentlyUpdatedPictures(seconds);
		loadPicturesIntoMap(ids, idToPic);
		removeDeletedPicturesFromMap(idToPic);
	}

	public void removeDeletedPicturesFromMap(Map<Integer, Picture> idToPic)
			throws Exception {
		Integer[] ids = idToPic.keySet().toArray(new Integer[0]);

		if (ids.length == 0) {
			logFine("map emtpy, nothing to delete.");
			return;
		}

		StringBuffer listOfIdsInMap = new StringBuffer();
		for (int i = 0; i < ids.length; i++) {
			if (i != 0) {
				listOfIdsInMap.append(",");
			}
			listOfIdsInMap.append("(" + ids[i] + ")");
		}

		String sql = "SELECT id FROM (VALUES "
				+ listOfIdsInMap.toString()
				+ ") AS pics(id) WHERE id NOT IN (SELECT id FROM pictures "
				+ (ApplicationControl.getInstance().getCurrentUser()
						.canSeeHiddenPictures() ? "" : "WHERE deleted = FALSE")
				+ ");";

		ResultSet result = doSelect(sql);
		ArrayList<Integer> toBeRemoved = new ArrayList<Integer>();
		while (result.next()) {
			toBeRemoved.add(result.getInt(1));
		}
		ids = toBeRemoved.toArray(new Integer[0]);

		for (int i = 0; i < ids.length; i++) {
			idToPic.remove(ids[i]);
		}
		logFine(ids.length + " pictures deleted from map.");
	}

	public Integer[] getIdsOfRecentlyUpdatedPictures(int seconds)
			throws Exception {
		String sql = "SELECT id FROM pictures WHERE "
				+ (ApplicationControl.getInstance().getCurrentUser()
						.canSeeHiddenPictures() ? "" : " deleted = FALSE AND")
				+ " saving_date > (now() - interval '" + seconds + " second');";
		ResultSet result = doSelect(sql);
		ArrayList<Integer> ids = new ArrayList<Integer>();
		while (result.next()) {
			ids.add(result.getInt(1));
		}
		result.close();
		return ids.toArray(new Integer[0]);
	}

	public void loadPicturesIntoMap(Integer[] ids, Map<Integer, Picture> idToPic)
			throws Exception {
		for (int i = 0; i < ids.length; i++) {
			idToPic.put(ids[i], loadPictureFromDatabase(ids[i]));
		}
	}

	protected int getLastId() throws Exception {
		ResultSet result = doSelect("select lastval();");
		if (!result.next()) {
			throw new IllegalStateException(
					"Can not find newly uploaded picture in database!");
		}
		return result.getInt(1);
	}

	/**
	 * creates a database entry for the given {@link Category} object
	 * 
	 * @param cat
	 * @throws Exception
	 */
	public void createCategory(Category cat) throws Exception {
		if (ApplicationControl.getInstance().getCurrentUser().isReadonly()) {
			throw new IllegalStateException(
					"Can not perform Insert,Update,Delete - you are a read-only user!");
		}
		String sql = "INSERT INTO categories (name, parentcategory) VALUES ('"
				+ cat.getName()
				+ "',"
				+ (cat.getParentCategoryId() > 0 ? cat.getParentCategoryId()
						: "NULL") + ");";
		logInfo(null, cat,
				"attemting to create a category: " + sql.replace("'", "\\'"));
		doInsertUpdateDelete(sql, true);
		cat.setId(getLastId());
		logInfo(null, cat, "successfully created category.");
	}

	/**
	 * updates the database entry for the given {@link Category} object
	 * 
	 * @param cat
	 * @throws Exception
	 */
	public void updateCategory(Category cat) throws Exception {
		if (ApplicationControl.getInstance().getCurrentUser().isReadonly()) {
			throw new IllegalStateException(
					"Can not perform Insert,Update,Delete - you are a read-only user!");
		}
		String sql = "UPDATE categories SET name = '"
				+ cat.getName()
				+ "', parentcategory = "
				+ (cat.getParentCategoryId() > 0 ? cat.getParentCategoryId()
						: "NULL") + " WHERE id = " + cat.getId() + ";";
		logInfo(null, cat,
				"attemting to update a category: " + sql.replace("'", "\\'"));
		doInsertUpdateDelete(sql, true);
		logInfo(null, cat, "successfully updated category.");
	}

	/**
	 * removes database entry for the given {@link Category} object
	 * 
	 * @param cat
	 * @throws Exception
	 */
	public void removeCategory(Category cat) throws Exception {
		if (ApplicationControl.getInstance().getCurrentUser().isReadonly()) {
			throw new IllegalStateException(
					"Can not perform Insert,Update,Delete - you are a read-only user!");
		}
		String sql = "DELETE FROM categories WHERE id = " + cat.getId() + ";";
		logInfo(null, cat, "attemting to delete a category: " + sql);
		doInsertUpdateDelete(sql, true);
		logInfo(null, cat, "successfully deleted category: " + sql);
	}

	/**
	 * creates a database entry for the given {@link User} object
	 * 
	 * @param user
	 * @param password
	 * @throws Exception
	 */
	public void createUser(User user) throws Exception {
		if (ApplicationControl.getInstance().getCurrentUser().isReadonly()) {
			throw new IllegalStateException(
					"Can not perform Insert,Update,Delete - you are a read-only user!");
		}
		String sql = "INSERT INTO users (name, password, fullname, privileges) VALUES ('"
				+ user.getLogin()
				+ "','"
				+ user.getPassword()
				+ "','"
				+ user.getFullname() + "', " + user.getPrivileges() + ");";
		logInfo("attemting to create a user: " + sql.replace("'", "\\'"));
		doInsertUpdateDelete(sql, true);
		user.setId(getLastId());
		logInfo("successfully created user: " + sql.replace("'", "\\'"));
	}

	/**
	 * updates database entry for the given {@link User} object
	 * 
	 * @param user
	 * @param password
	 *            may be null, if password is not be changed
	 * @throws Exception
	 */
	public void updateUser(User user) throws Exception {
		if (ApplicationControl.getInstance().getCurrentUser().isReadonly()) {
			throw new IllegalStateException(
					"Can not perform Insert,Update,Delete - you are a read-only user!");
		}
		String sql = "UPDATE users SET name = '"
				+ user.getLogin()
				+ (user.getPassword() != null ? "', password = '"
						+ user.getPassword() : "") + "', fullname = '"
				+ user.getFullname() + "', privileges = "
				+ user.getPrivileges() + " WHERE id = " + user.getId() + ";";
		logInfo("attemting to update a user: " + sql.replace("'", "\\'"));
		doInsertUpdateDelete(sql, true);
		logInfo("successfully updated user: " + sql.replace("'", "\\'"));
	}

	/**
	 * deletes database entry for the given {@link User} object, sets all
	 * references to it's ID to <code>NULL</code>.
	 * 
	 * @param user
	 * @throws Exception
	 */
	public void removeUser(User user) throws Exception {
		if (ApplicationControl.getInstance().getCurrentUser().isReadonly()) {
			throw new IllegalStateException(
					"Can not perform Insert,Update,Delete - you are a read-only user!");
		}
		String sql = "UPDATE log SET user_id = NULL WHERE user_id = "
				+ user.getId() + ";";
		logInfo("attemting to delete references to user in table log: " + sql);
		doInsertUpdateDelete(sql, false);
		logInfo("successfully deleted references to user in table log.");

		sql = "UPDATE pictures SET user_id = NULL WHERE user_id = "
				+ user.getId() + ";";
		logInfo("attemting to delete references to user in table pictures: "
				+ sql);
		doInsertUpdateDelete(sql, false);
		logInfo("successfully deleted references to user in table pictures.");

		sql = "DELETE FROM users WHERE id = " + user.getId() + ";";
		logInfo("attemting to delete a user: " + sql);
		doInsertUpdateDelete(sql, false);
		logInfo("successfully deleted user.");

		logInfo("commiting changes.");
		getConnection().commit();
		logInfo("done commiting changes.");
	}

	/**
	 * assign a category to a picture, remove with
	 * {@link #removePictureFromCategory(Picture, Category)}
	 * 
	 * @param pic
	 * @param cat
	 * @throws Exception
	 */
	public void assignPictureToCategory(Picture pic, Category cat)
			throws Exception {
		if (ApplicationControl.getInstance().getCurrentUser().isReadonly()) {
			throw new IllegalStateException(
					"Can not perform Insert,Update,Delete - you are a read-only user!");
		}
		String sql = "INSERT INTO pictures_to_categories (picture_id, category_id) VALUES ( "
				+ pic.getId() + ",  " + cat.getId() + ");";
		logInfo(pic, cat, "attemting to assign a category to a picture: " + sql);
		doInsertUpdateDelete(sql, true);
		logInfo(pic, cat, "successfully assigned a category to a picture.");
	}

	/**
	 * remove a picture from a category, add with
	 * {@link #assignPictureToCategory(Picture, Category)}
	 * 
	 * @param pic
	 * @param cat
	 * @throws Exception
	 */
	public void removePictureFromCategory(Picture pic, Category cat)
			throws Exception {
		if (ApplicationControl.getInstance().getCurrentUser().isReadonly()) {
			throw new IllegalStateException(
					"Can not perform Insert,Update,Delete - you are a read-only user!");
		}
		String sql = "DELETE FROM pictures_to_categories WHERE picture_id = "
				+ pic.getId() + " AND category_id = " + cat.getId() + ";";
		logInfo(pic, cat, "attemting to remove a picture from a category: "
				+ sql);
		doInsertUpdateDelete(sql, true);
		logInfo(pic, cat, "successfully removed a picture from a category.");
	}

	/**
	 * NOT TO BE USED ANYMORE!
	 * 
	 * @deprecated
	 * @param file
	 * @param description
	 * @param category
	 * @throws Exception
	 */
	public void uploadImage(File file, String description, int category)
			throws Exception {
		if (ApplicationControl.getInstance().getCurrentUser().isReadonly()) {
			throw new IllegalStateException(
					"Can not perform Insert,Update,Delete - you are a read-only user!");
		}
		// TODO: category!
		logInfo("attemting to upload picture with deprecated method");
		FileInputStream fis = new FileInputStream(file);
		PreparedStatement pstmt = getConnection().prepareStatement(
				"INSERT INTO pictures (description, picture) VALUES ( \'"
						+ description + "\',  ?) ;");
		pstmt.setBinaryStream(1, fis, (int) file.length());
		pstmt.executeUpdate();
		pstmt.close();
		fis.close();
		logInfo("successfully uploaded picture with deprecated method");
	}

	/**
	 * update the data of a picture, must be a picture that already exists in
	 * the database.
	 * 
	 * @param pic
	 *            the picture object with the new data
	 * @throws Exception
	 */
	public void updatePicture(Picture pic) throws Exception {
		if (ApplicationControl.getInstance().getCurrentUser().isReadonly()) {
			throw new IllegalStateException(
					"Can not perform Insert,Update,Delete - you are a read-only user!");
		}
		String sql = "UPDATE pictures SET description = '"
				+ pic.getDescription() + "', " + "saving_date = NOW(), "
				+ "publication = " + Boolean.toString(pic.isPublication())
				+ ", " + "origin = '" + pic.getOrigin() + "', "
				+ "exemplary = " + Boolean.toString(pic.isExemplary()) + ", "
				+ "bad_example = " + Boolean.toString(pic.isBadExample())
				+ ", " + "creation_date = '" + pic.getCreationDate() + "', "
				+ "deleted = " + Boolean.toString(pic.isDeleted()) + ", "
				+ "user_id = "
				+ ApplicationControl.getInstance().getCurrentUser().getId()
				+ " " + "WHERE id = " + pic.getId() + ";";
		logInfo(pic, null,
				"attemting to update picture: " + sql.replace("'", "\\'"));
		doInsertUpdateDelete(sql, true);
		logInfo(pic, null, "succesfully updated picture.");
	}

	public enum ImageTypes {
		preview, thumbnail, picture;
	}

	/**
	 * @deprecated Upload the bitmap data of a picture, used by {@link Picture}
	 *             objects. After upload, add categories using
	 *             {@link #assignPictureToCategory(Picture, Category)}
	 * @param file
	 * @param id
	 * @param type
	 * @throws Exception
	 */
	public void uploadImage(File file, int id, ImageTypes type)
			throws Exception {
		if (ApplicationControl.getInstance().getCurrentUser().isReadonly()) {
			throw new IllegalStateException(
					"Can not perform Insert,Update,Delete - you are a read-only user!");
		}
		logFine("attemting to upload " + type);
		FileInputStream fis = new FileInputStream(file);
		PreparedStatement pstmt = getConnection().prepareStatement(
				"UPDATE pictures SET " + type.toString() + " = ? WHERE id = "
						+ id + " ;");
		pstmt.setBinaryStream(1, fis, (int) file.length());
		pstmt.executeUpdate();
		pstmt.close();
		fis.close();
		logFine("successfully uploaded " + type);
	}

	/**
	 * "soft-delete" of a picture - the picture remains in the database, but
	 * will not be shown anymore.
	 * 
	 * @param pic
	 * @throws Exception
	 */
	public void markPictureAsDeleted(Picture pic) throws Exception {
		if (ApplicationControl.getInstance().getCurrentUser().isReadonly()) {
			throw new IllegalStateException(
					"Can not perform Insert,Update,Delete - you are a read-only user!");
		}
		logInfo(pic, null, "attemting to mark a picture \"deleted\"");
		pic.setDeleted(true);
		updatePicture(pic);
		logInfo(pic, null, "successfully marked a picture \"deleted\"");
	}

	/**
	 * 
	 * @param criteria
	 *            definition of search criteria
	 * @param orderBy
	 *            column to order result by
	 * @return an array of {@link Picture} IDs, to be used with a map of
	 *         IDs/Objects in order to prevent unnecessary network bandwidth and
	 *         database load, especially in case multiple queries are fired in a
	 *         row.
	 * @throws Exception
	 */
	public Integer[] searchPictures(PictureSearchCriteria criteria,
			PictureColumn orderBy) throws Exception {
		String sql = criteria.generateQuery(orderBy);
		logFine("searching for pictures, SQL: " + sql.replace("'", "\\'"));
		System.out.println("Query SQL: " + sql);
		ArrayList<Integer> ids = new ArrayList<Integer>();
		ResultSet result = doSelect(sql);
		while (result.next()) {
			ids.add(result.getInt(1));
		}
		result.close();
		logFine("found " + ids.size() + " pictures matching serach criteria.");
		return ids.toArray(new Integer[0]);
	}

	/**
	 * comfort method to get {@link Picture} objects matching the search
	 * criteria from a map e.g. by {@link #loadAllPictures()}.
	 * 
	 * @param criteria
	 * @param orderBy
	 * @param idToPic
	 *            map e.g. by {@link #loadAllPictures()}
	 * @return
	 * @throws Exception
	 */
	public Picture[] searchPicturesAndGetObjectFromMap(
			PictureSearchCriteria criteria, PictureColumn orderBy,
			Map<Integer, Picture> idToPic) throws Exception {
		Integer[] resultingIds = searchPictures(criteria, orderBy);
		ArrayList<Picture> pics = new ArrayList<Picture>();

		for (int i = 0; i < resultingIds.length; i++) {
			if (idToPic.containsKey(resultingIds[i])) {
				pics.add(idToPic.get(resultingIds[i]));
			} else {
				logWarn("a picture with the ID "
						+ resultingIds[i]
						+ " was returned as query result, but was not found in map - picture will be skipped.");
			}
		}

		return pics.toArray(new Picture[0]);
	}

	/**
	 * comfort method to get {@link Picture} objects matching the search
	 * criteria from the master map.
	 * 
	 * @param criteria
	 * @param orderBy
	 * @return
	 * @throws Exception
	 */
	public Picture[] searchPicturesAndGetObjectFromMap(
			PictureSearchCriteria criteria, PictureColumn orderBy)
			throws Exception {
		Map<Integer, Picture> idToPic = ApplicationControl.getInstance()
				.getMasterMap();
		Integer[] resultingIds = searchPictures(criteria, orderBy);
		ArrayList<Picture> pics = new ArrayList<Picture>();

		for (int i = 0; i < resultingIds.length; i++) {
			if (idToPic.containsKey(resultingIds[i])) {
				pics.add(idToPic.get(resultingIds[i]));
			} else {
				logWarn("a picture with the ID "
						+ resultingIds[i]
						+ " was returned as query result, but was not found in map - picture will be skipped.");
			}
		}

		return pics.toArray(new Picture[0]);
	}

	/**
	 * 
	 * @param pic
	 *            the Picture to export
	 * @param baseDirectory
	 *            directory to export the picture to
	 * @param baseFileName
	 *            base file name, maybe null
	 * @param overwrite
	 *            in case the destination file exists, should it be overwritten?
	 * @param writeXmlForReimport
	 *            should an XML file containing data for reimport be written?
	 * @param dim
	 *            the dimensions to fit the exported picture in - if null the
	 *            original size will be used
	 * @param type
	 *            should the picture be exported as PNG or as TIFF?
	 * @throws Exception
	 */
	public File exportPicture(Picture pic, File baseDirectory,
			String baseFileName, boolean overwrite,
			boolean writeXmlForReimport, Dimension dim, SupportedImageFiles type)
			throws Exception {
		return pic.exportToFile(baseDirectory, baseFileName, overwrite,
				writeXmlForReimport, dim, type);
	}

	/**
	 * see {@link Picture#importPictureFromFile(File, boolean)}
	 * 
	 * @param file
	 * @param reuseIds
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 * @throws JDOMException
	 */
	public Picture importPictureFromPicmanFiles(File file, boolean reuseIds)
			throws JDOMException, IOException, ParseException {
		if (ApplicationControl.getInstance().getCurrentUser().isReadonly()) {
			throw new IllegalStateException(
					"Can not perform Insert,Update,Delete - you are a read-only user!");
		}
		return Picture.importPictureFromFile(file, reuseIds);
	}

	/**
	 * 
	 * @param pics
	 *            the Pictures to export
	 * @param baseDirectory
	 *            directory to export the picture to
	 * @param baseFileName
	 *            base file name, maybe null
	 * @param overwrite
	 *            in case the destination file exists, should it be overwritten?
	 * @param dim
	 *            the dimensions to fit the exported picture in - if null the
	 *            original size will be used
	 * @param type
	 *            should the picture be exported as PNG or as TIFF?
	 * @throws Exception
	 */
	public void exportPictures(Picture[] pics, File baseDirectory,
			String baseFileName, boolean overwrite,
			boolean writeXmlForReimport, Dimension dim, SupportedImageFiles type)
			throws Exception {
		if (!baseDirectory.isDirectory())
			throw new IllegalArgumentException(
					"given File object is not a directory");
		else if (!baseDirectory.canWrite())
			throw new IllegalArgumentException(
					"given directory is not writeable");

		logFine("about to export " + pics.length
				+ " pictures to local file system: "
				+ baseDirectory.getAbsolutePath());

		for (int i = 0; i < pics.length; i++) {
			exportPicture(pics[i], baseDirectory, baseFileName, overwrite,
					writeXmlForReimport, dim, type);
		}

		logFine("done exporting pictures to file system.");
	}

	/**
	 * comfort method for faster development, for production better use
	 * {@link #searchPictures(PictureSearchCriteria, PictureColumn)} in
	 * combination with a map generated by {@link #loadAllPictures()} to speed
	 * up queries.
	 * 
	 * @param criteria
	 * @param orderBy
	 * @return
	 * @throws Exception
	 */
	public Picture[] searchPicturesAndCastToArray(
			PictureSearchCriteria criteria, PictureColumn orderBy)
			throws Exception {
		HashMap<Integer, Picture> idToPic = loadAllPictures();
		return searchPicturesAndGetObjectFromMap(criteria, orderBy, idToPic);
	}

	/**
	 * get total number of log entries in the database
	 * 
	 * @return
	 * @throws Exception
	 */
	public long getLogEntryCount() throws Exception {
		String sql = "SELECT count(*) from log;";
		ResultSet result = doSelect(sql);
		result.next();
		return result.getLong(1);
	}

	/**
	 * get total number of picture entries in the database
	 * 
	 * @return
	 * @throws Exception
	 */
	public long getPictureCount() throws Exception {
		String sql = "SELECT count(*) from pictures;";
		ResultSet result = doSelect(sql);
		result.next();
		return result.getLong(1);
	}

	/**
	 * get total number of category entries in the database
	 * 
	 * @return
	 * @throws Exception
	 */
	public long getCategoryCount() throws Exception {
		String sql = "SELECT count(*) from categories;";
		ResultSet result = doSelect(sql);
		result.next();
		return result.getLong(1);
	}

	/**
	 * get total number of category entries in the database, that have no parent
	 * ID
	 * 
	 * @return
	 * @throws Exception
	 */
	public long getTopLevelCategoryCount() throws Exception {
		String sql = "SELECT count(*) from categories WHERE parentcategory IS NULL;";
		ResultSet result = doSelect(sql);
		result.next();
		return result.getLong(1);
	}

	/**
	 * 
	 * @param maxNumberOfEntries
	 *            (max.) number of log records to load from the DB
	 * @param startingFrom
	 *            offset for the first log record to load, e.g. to implement a
	 *            per
	 * @return
	 * @throws Exception
	 */
	public LogEntry[] getLogEntries(int maxNumberOfEntries, int startingFrom)
			throws Exception {
		String sql = "SELECT *, (SELECT name FROM users WHERE id = user_id) AS user FROM log ORDER BY timestamp LIMIT "
				+ maxNumberOfEntries + " OFFSET " + startingFrom + ";";

		logFine("retrieving log records from database: " + sql);
		ResultSet result = doSelect(sql);

		ArrayList<LogEntry> logEntries = new ArrayList<LogEntry>();

		LogEntry tmp;

		while (result.next()) {
			tmp = new LogEntry(result.getInt("id"), result.getInt("user_id"),
					result.getInt("picture_id"), result.getInt("category_id"),
					result.getString("user"), result.getString("message"),
					result.getString("level"), new java.sql.Date(result
							.getTimestamp("timestamp").getTime()));
			logEntries.add(tmp);
		}
		result.close();
		logFine("retrieved " + logEntries.size()
				+ " log records from database.");

		return logEntries.toArray(new LogEntry[0]);
	}

	/**
	 * Retrieve the full name of the user with the given ID
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public String getNameOfUser(int id) throws Exception {
		String sql = "SELECT fullname FROM users WHERE id = " + id + ";";

		logFine("looking up full name of user with ID:" + id + " from db: "
				+ sql);
		ResultSet result = doSelect(sql);
		if (!result.next()) {
			logFine("no user name found.");
			return "unbekannt";
		}
		String name = result.getString(1);

		logFine("retrieved user name \"" + name + "\".");

		return name;
	}

	/**
	 * 
	 * @return a string array containing a set of all origins that exist in the
	 *         database, so far.
	 * @throws Exception
	 */
	public String[] getAllOriginsThatExistInTheDatabase() throws Exception {
		String sql = "SELECT origin FROM pictures GROUP BY origin ORDER BY origin;";

		logFine("retrieving existing origins from database: " + sql);
		ResultSet result = doSelect(sql);

		ArrayList<String> origins = new ArrayList<String>();

		while (result.next()) {
			origins.add(result.getString(1));
		}

		result.close();

		logFine("retrieved " + origins.size()
				+ " existing origins from database.");

		return origins.toArray(new String[0]);
	}

	/**
	 * remove of a picture - the picture and all references to it will be
	 * deleted from tha database.
	 * 
	 * @param pic
	 * @throws Exception
	 */
	public void removePicture(Picture pic) throws Exception {
		if (ApplicationControl.getInstance().getCurrentUser().isReadonly()) {
			throw new IllegalStateException(
					"Can not perform Insert,Update,Delete - you are a read-only user!");
		}
		String sql = "DELETE FROM pictures_to_categories WHERE picture_id = "
				+ pic.getId() + ";";
		String sql2 = "DELETE FROM pictures WHERE id = " + pic.getId() + ";";

		logInfo(pic, null, "attemting to remove a picture from the database.");

		logFine(pic, null, "removing picture from the categories: " + sql);
		doInsertUpdateDelete(sql, false);

		logFine(pic, null, "removing picture: " + sql2);
		doInsertUpdateDelete(sql2, false);

		logFine(pic, null, "commiting changes");
		getConnection().commit();

		logInfo(pic, null, "successfully removed a picture from the database.");
	}

	/**
	 * Loads all {@link Picture} objects of a certain category - full-size
	 * images need to be loaded, separately!
	 * 
	 * @param catId
	 * @return a HashMap of Picture IDs and Picture objects contained in the
	 *         given Category
	 * @throws Exception
	 */
	public HashMap<Integer, Picture> loadPicturesOfCategory(int catId)
			throws Exception {
		String sql = "SELECT id, picture_id, category_id FROM pictures JOIN pictures_to_categories ON pictures.id = pictures_to_categories.picture_id WHERE category_id = "
				+ catId + " order by description;";

		logFine("attemting to load id/picture map from database, SQL: " + sql);
		ResultSet result = doSelect(sql);

		int id;
		HashMap<Integer, Picture> idToPic = new HashMap<Integer, Picture>();
		Map<Integer, Picture> masterMap = ApplicationControl.getInstance()
				.getMasterMap();

		while (result.next()) {
			id = result.getInt(1);
			idToPic.put(id, masterMap.get(id));
		}
		result.close();
		logFine("successfully loaded id/picture map from database, SQL: " + sql);
		return idToPic;

	}

	/**
	 * Loads all {@link Picture} IDs of a certain category
	 * 
	 * @param catId
	 * @return an array of IDs - to be used with a HashMap of {@link Picture}
	 *         IDs and objects
	 * @throws Exception
	 */
	public Integer[] getIdsOfPicturesInCategory(int catId) throws Exception {
		String sql = "SELECT id, picture_id, category_id FROM pictures JOIN pictures_to_categories ON pictures.id = pictures_to_categories.picture_id WHERE category_id = "
				+ catId + " order by description;";

		logFine("attemting to load picture IDs from database, SQL: " + sql);
		ResultSet result = doSelect(sql);

		ArrayList<Integer> ids = new ArrayList<Integer>();

		while (result.next()) {
			ids.add(result.getInt(DbConstants.FIELD_ID));

		}
		result.close();
		logFine("successfully loaded picture IDs from database, SQL: " + sql);
		return ids.toArray(new Integer[0]);
	}

	/**
	 * Loads all sub-{@link Category} IDs of a certain category
	 * 
	 * @param catId
	 * @return an array of IDs - to be used with a HashMap of {@link Category}
	 *         IDs and objects
	 * @throws Exception
	 */
	public Integer[] getIdsOfCategoriesInCategory(int catId) throws Exception {
		String sql = "SELECT id FROM categories WHERE parentcategory = "
				+ catId + " order by name;";

		logFine("attemting to load category IDs from database, SQL: " + sql);
		ResultSet result = doSelect(sql);

		ArrayList<Integer> ids = new ArrayList<Integer>();

		while (result.next()) {
			ids.add(result.getInt(1));
		}
		result.close();
		logFine("successfully loaded category IDs from database, SQL: " + sql);
		return ids.toArray(new Integer[0]);
	}

	/**
	 * 
	 * @param pic
	 * @return array of IDs of {@link Category}s that contain the given
	 *         {@link Picture}.
	 * @throws Exception
	 */
	public Integer[] getIdsOfCategoriesContainingPictures(Picture pic)
			throws Exception {
		String sql = "SELECT category_id FROM pictures_to_categories WHERE picture_id = "
				+ pic.getId() + ";";

		logFine(pic,
				"attemting to load category IDs for picture from database, SQL: "
						+ sql);
		ResultSet result = doSelect(sql);

		ArrayList<Integer> ids = new ArrayList<Integer>();

		while (result.next()) {
			ids.add(result.getInt(1));
		}
		result.close();
		logFine(pic,
				"successfully loaded category IDs for picture from database, SQL: "
						+ sql);
		return ids.toArray(new Integer[0]);
	}

	protected final static String thumbnailFilenamePrefix = "thumb_";
	protected final static String thumbnailFilenamePostfix = ".png";

	protected ArrayList<Integer> getIdsOfPicturesInlocalCache() {
		ArrayList<Integer> cachedImages = new ArrayList<Integer>();

		String[] fileNames = getLocalCacheDirectory().list(
				new FilenameFilter() {
					public boolean accept(File dir, String name) {
						return name.startsWith(thumbnailFilenamePrefix)
								&& name.endsWith(thumbnailFilenamePostfix);
					}
				});

		String name = null, idStr;
		for (int i = 0; i < fileNames.length; i++) {
			name = fileNames[i];

			idStr = name.replace(thumbnailFilenamePrefix, "");
			idStr = idStr.replace(thumbnailFilenamePostfix, "");

			try {
				cachedImages.add(Integer.parseInt(idStr));
			} catch (Exception e) {
				logFine("Can not parse cache file name \"" + fileNames[i]
						+ "\"");
			}

		}

		return cachedImages;
	}

	/**
	 * IF POSSIBLE, USE {@link ApplicationControl#getMasterMap()}, INSTEAD!!<br>
	 * Loads all {@link Picture} objects from the database (use with caution) -
	 * full-size images need to be loaded, separately!
	 * 
	 * @return a HashMap of Picture IDs and Picture objects
	 * @throws Exception
	 */
	public HashMap<Integer, Picture> loadAllPictures() throws Exception {
		logFine("attemting to load id/picture map from database.");

		Picture[] pics = null;

		if (!isLocalCaching()) {
			pics = loadAllPicturesAsArray();
		} else {
			try {
				logFine("local caching is used - checking local temp. directory \""
						+ getLocalCacheDirectory().getAbsolutePath() + "\".");
				ArrayList<Integer> idsOfAllPicturesInDb = loadIdsOfAllPictures();
				ArrayList<Integer> idsOfLocallyCachedFiles = getIdsOfPicturesInlocalCache();

				// TODO: find out which pictures to load from DB, load and
				// write, locally
				Picture[] picsNotCached = loadAllPicturesAsArray(true,
						idsOfLocallyCachedFiles);

				// TODO: load cached pictures from DB (text, only)
				ArrayList<Integer> idsOfPicsNotCached = new ArrayList<Integer>();
				idsOfPicsNotCached.addAll(idsOfAllPicturesInDb);
				idsOfPicsNotCached.removeAll(idsOfLocallyCachedFiles);
				Picture[] picsCached = loadAllPicturesAsArray(false,
						idsOfPicsNotCached);

				// TODO: find out which pictures to delete, locally and delete
				ArrayList<Integer> idsOfPicsToDeleteFromCache = new ArrayList<Integer>();
				idsOfPicsToDeleteFromCache.addAll(idsOfLocallyCachedFiles);
				idsOfPicsToDeleteFromCache.removeAll(idsOfAllPicturesInDb);
				deleteCachedPictures(idsOfPicsToDeleteFromCache);

				ArrayList<Picture> picArray = new ArrayList<Picture>();

				for (int i = 0; i < picsNotCached.length; i++) {
					picArray.add(picsNotCached[i]);
				}
				for (int i = 0; i < picsCached.length; i++) {
					picArray.add(picsCached[i]);
				}

				pics = picArray.toArray(new Picture[0]);

			} catch (Exception localCache) {
				// logWarn(localCache.getMessage());
				pics = loadAllPicturesAsArray();
			}

		}

		HashMap<Integer, Picture> idToPic = new HashMap<Integer, Picture>(
				pics.length);

		for (int i = 0; i < pics.length; i++) {
			idToPic.put(pics[i].getId(), pics[i]);
		}
		logFine("successfully loaded id/picture map from database.");
		return idToPic;

	}

	protected void deleteCachedPictures(ArrayList<Integer> ids) {
		Integer[] idToDelete = ids.toArray(new Integer[0]);
		File toBeDeleted;
		for (int i = 0; i < idToDelete.length; i++) {
			toBeDeleted = getThumnailCacheFile(idToDelete[i]);
			try {
				if (toBeDeleted.exists()) {
					toBeDeleted.delete();
				}
			} catch (Exception e) {
				logFine("can not delete local cache file: "
						+ toBeDeleted.toString());
			}
		}
	}

	/**
	 * extracts {@link Picture} objects from a picture map, sorts them and fills
	 * them into an array.
	 * 
	 * @param idToPic
	 * @return
	 */
	public final static Picture[] convertPictureMapToArray(
			Map<Integer, Picture> idToPic) {
		ArrayList<Picture> pics = new ArrayList<Picture>();
		pics.addAll(idToPic.values());
		Collections.sort(pics);
		return pics.toArray(new Picture[0]);
	}

	/**
	 * IF POSSIBLE, USE {@link ApplicationControl#getMasterMap()} AND
	 * {@link #convertPictureMapToArray(Map)}, INSTEAD!!<br>
	 * 
	 * @return array of all pictures from the database
	 * @throws Exception
	 */
	protected Picture[] loadAllPicturesAsArray() throws Exception {
		return loadAllPicturesAsArray(true, null);
	}

	protected String createConditionString(ArrayList<String> conditions) {
		String conditionStr = "";
		if (conditions.size() > 0) {
			conditionStr += " WHERE ";
			String[] conds = conditions.toArray(new String[0]);
			for (int i = 0; i < conds.length; i++) {
				if (i > 0) {
					conditionStr += " AND ";
				}
				conditionStr += " (" + conds[i] + ") ";
			}
		}
		return conditionStr;
	}

	/**
	 * IF POSSIBLE, USE {@link ApplicationControl#getMasterMap()} AND
	 * {@link #convertPictureMapToArray(Map)}, INSTEAD!!<br>
	 * 
	 * @return array of all pictures from the database
	 * @throws Exception
	 */
	protected Picture[] loadAllPicturesAsArray(boolean inclThumbnails,
			ArrayList<Integer> excludeIds) throws Exception {
		ArrayList<String> conditions = new ArrayList<String>();

		if (ApplicationControl.getInstance().getCurrentUser()
				.canSeeHiddenPictures()) {
			conditions.add("deleted = FALSE");
		}

		if (excludeIds != null && !excludeIds.isEmpty()) {
			Integer[] idsToExclude = excludeIds.toArray(new Integer[0]);
			String condStr = "";
			for (int i = 0; i < idsToExclude.length; i++) {
				if (i > 0) {
					condStr += ",";
				}
				condStr += idsToExclude[i];
			}
			conditions.add("id NOT IN (" + condStr + ")");
		}

		String sql = "SELECT id, " + "description, "
				+ (inclThumbnails ? "thumbnail, " : "") + "saving_date, "
				+ "publication, " + "origin, " + "exemplary, "
				+ "bad_example, " + "creation_date, " + "user_id, "
				+ "deleted " + "FROM pictures "
				+ createConditionString(conditions) + "ORDER BY description;";
		logFine("attempting to load picture array from database, SQL: " + sql);
		// System.out.println(sql);

		ResultSet result = doSelect(sql);
		ArrayList<Picture> pics = new ArrayList<Picture>();
		int i = 0;
		for (; result.next(); i++) {
			if (inclThumbnails) {
				pics.add(new Picture(result.getInt("id"), result
						.getInt("user_id"), result.getString("description"),
						result.getString("creation_date"), result
								.getDate("saving_date"), result
								.getString("origin"), Picture
								.loadImageFromStream(result
										.getBinaryStream("thumbnail")), result
								.getBoolean("publication"), result
								.getBoolean("exemplary"), result
								.getBoolean("bad_example"), result
								.getBoolean("deleted")));
			} else {
				pics.add(new Picture(result.getInt("id"), result
						.getInt("user_id"), result.getString("description"),
						result.getString("creation_date"), result
								.getDate("saving_date"), result
								.getString("origin"), null, result
								.getBoolean("publication"), result
								.getBoolean("exemplary"), result
								.getBoolean("bad_example"), result
								.getBoolean("deleted")));
			}
		}
		result.close();
		logFine("successfully loaded picture[" + i + "] from database");
		return pics.toArray(new Picture[0]);
	}

	/**
	 * @return array of all picture IDs from the database
	 * @throws Exception
	 */
	protected ArrayList<Integer> loadIdsOfAllPictures() throws Exception {
		String sql = "SELECT id FROM pictures "
				+ (ApplicationControl.getInstance().getCurrentUser()
						.canSeeHiddenPictures() ? "" : "WHERE deleted = FALSE")
				+ " ORDER BY description;";
		logFine("attemting to load picture IDs from database, SQL: " + sql);

		ResultSet result = doSelect(sql);
		ArrayList<Integer> ids = new ArrayList<Integer>();
		int i = 0;
		for (; result.next(); i++) {
			ids.add(result.getInt("id"));
		}
		result.close();
		logFine("successfully loaded " + i + " picture IDs from database");
		return ids;
	}

	/**
	 * IF POSSIBLE, GET PICTURE FROM {@link ApplicationControl#getMasterMap()},
	 * INSTEAD!!<br>
	 * Loads the picture object with the requested ID from the database.
	 * Attention: Even though the thumbnail and the picture object itself is
	 * loaded the preview and full size image are loaded on demand.
	 * 
	 * @param id
	 * @return {@link Picture} object with the given ID.
	 * @throws Exception
	 */
	public Picture loadPictureFromDatabase(int id) throws Exception {
		logFine("attempting to load picture " + id);
		ResultSet result = doSelect("SELECT id, description, thumbnail, saving_date, publication, origin, exemplary, bad_example, creation_date, user_id, deleted FROM "
				+ DbConstants.TABLE_NAME_PICTURES
				+ " WHERE "
				+ DbConstants.FIELD_ID + " = " + id + ";");
		if (!result.next())
			throw new IllegalArgumentException("Kein Bild mit der ID " + id
					+ " in der Datenbank gefunden.");
		Picture pic = new Picture(
				id,
				result.getInt("user_id"),
				result.getString("description"),
				result.getString("creation_date"),
				result.getDate("saving_date"),
				result.getString("origin"),
				Picture.loadImageFromStream(result.getBinaryStream("thumbnail")),
				result.getBoolean("publication"), result
						.getBoolean("exemplary"), result
						.getBoolean("bad_example"), result
						.getBoolean("deleted"));
		result.close();
		logFine(pic, "successfully loaded picture");
		return pic;
	}

	/**
	 * The given <code>Picture</code> object will be inserted into the database.
	 * The full-size image needs to be present, a preview and a thumbnail will
	 * be created, automatically. <br>
	 * <b>NOTE:the database ID will be filled into <code>toBeInserted</code></b>
	 * 
	 * @param toBeInserted
	 *            the new Picture object to be inserted into the database
	 */
	public void insertNewPictureInDatabase(Picture toBeInserted)
			throws Exception {
		if (ApplicationControl.getInstance().getCurrentUser().isReadonly()) {
			throw new IllegalStateException(
					"Can not perform Insert,Update,Delete - you are a read-only user!");
		}
		if (ApplicationControl.getInstance().getCurrentUser().isReadonly()) {
			throw new IllegalStateException(
					"Can not perform Insert,Update,Delete - you are a read-only user!");
		}

		logInfo(toBeInserted, "attempting to insert new picture in database");

		ApplicationControl control = ApplicationControl.getInstance();

		RenderedImage fullSizePic = toBeInserted.getPicture();
		RenderedImage preview = Picture.getScaledCopy(fullSizePic,
				control.getPicturePreviewDimension());
		RenderedImage thumbnail = Picture.getScaledCopy(fullSizePic,
				control.getPictureThumbnailDimension());

		try {

			String sql = "INSERT INTO pictures (user_id, description, creation_date, origin, publication, exemplary, bad_example, saving_date, thumbnail, preview, picture) VALUES ("
					+ toBeInserted.getUserId()
					+ ", '"
					+ toBeInserted.getDescription()
					+ "', '"
					+ toBeInserted.getCreationDate()
					+ "', '"
					+ toBeInserted.getOrigin()
					+ "', "
					+ toBeInserted.isPublication()
					+ ", "
					+ toBeInserted.isExemplary()
					+ ", "
					+ toBeInserted.isBadExample() + ", NOW(),?,?,?);";
			PreparedStatement pstmt = getConnection().prepareStatement(sql);

			File thumbnailFile = Picture.createDbUploadFile(thumbnail);
			FileInputStream thumbnailFis = new FileInputStream(thumbnailFile);

			File previewFile = Picture.createDbUploadFile(preview);
			FileInputStream previewFis = new FileInputStream(previewFile);

			File pictureFile = Picture.createDbUploadFile(fullSizePic);
			FileInputStream pictureFis = new FileInputStream(pictureFile);

			pstmt.setBinaryStream(1, thumbnailFis, (int) thumbnailFile.length());
			pstmt.setBinaryStream(2, previewFis, (int) previewFile.length());
			pstmt.setBinaryStream(3, pictureFis, (int) pictureFile.length());

			pstmt.executeUpdate();
			pstmt.close();

			getConnection().commit();

			try {
				thumbnailFis.close();
				previewFis.close();
				pictureFis.close();

				thumbnailFile.delete();
				previewFile.delete();
				pictureFile.delete();
			} catch (RuntimeException e) {
				logInfo("problem occured while closing and deleting files after successfull picture upload: "
						+ e.toString());
			}

			ResultSet result = doSelect("select lastval();");
			if (!result.next()) {
				throw new IllegalStateException(
						"Can not find newly uploaded picture in database!");
			}
			int newId = result.getInt(1);

			toBeInserted.setId(newId);

		} catch (Exception e) {
			logSevere(toBeInserted,
					"failed to insert new picture in database, rolling back changes: "
							+ e.toString());
			getConnection().rollback();
			throw e;
		}
		logInfo(toBeInserted, "successfully inserted new picture in database");
	}

	/**
	 * get all categories from database
	 * 
	 * @return a map of category ID to category objects
	 */
	public HashMap<Integer, Category> getCategories() {
		HashMap<Integer, Category> idToCategory = new HashMap<Integer, Category>();
		String sql = "SELECT * FROM categories;";
		try {
			logFine("attemting to load id/category map from database, SQL: "
					+ sql);
			ResultSet result = doSelect(sql);
			int id, parent;
			while (result.next()) {
				id = result.getInt("id");
				parent = result.getString("parentcategory") != null ? result
						.getInt("parentcategory") : 0;
				// if (ApplicationControl.getInstance().isDebug())
				// logFine("creating category " + id + ": " +
				// result.getString("name"));
				idToCategory.put(id, new Category(id, result.getString("name"),
						parent));
			}
			result.close();
		} catch (Exception e) {
			ApplicationControl.displayErrorToUser(e);
			e.printStackTrace();
			return null;
		}
		logFine("successfully loaded id/category map from database, SQL: "
				+ sql);
		return idToCategory;
	}

	/**
	 * DO NOT USE THIS METHOD FOR USE IN THE APPLICATION! USE
	 * {@link ApplicationControl#login(String, String)} INSTEAD!
	 * 
	 * @param userName
	 *            user name entered by User
	 * @param password
	 *            password entered by User
	 * @return a {@link User} object, in case the given combination of user name
	 *         and password is found in the database. Otherwise NULL is
	 *         returned.
	 * @throws Exception
	 */
	public User attemptLogin(String userName, String password) throws Exception {
		ResultSet user = doSelect("SELECT * FROM users WHERE name ilike '"
				+ userName + "' AND password = '" + password + "';");

		if (!user.next())
			return null;

		User u = new User(user.getInt("id"), user.getString("name"),
				user.getString("fullname"), user.getString("password"),
				user.getInt("privileges"));

		if (!user.next())
			return u;
		else
			throw new IllegalStateException(
					"two users with the same name and password found!");
	}

	/**
	 * for adminstrators, only
	 */
	public User[] getAllUsers() throws Exception {
		// if (!ApplicationControl.getInstance().getCurrentUser().isAdmin()){
		// throw new
		// IllegalStateException("This operation is only permitted for administrators.");
		// }
		logFine("attemting to load user list from database.");

		ArrayList<User> userList = new ArrayList<User>();
		ResultSet user = doSelect("SELECT * FROM users ORDER by name;");

		for (@SuppressWarnings("unused")
		int i = 0; user.next(); i++) {
			userList.add(new User(user.getInt("id"), user.getString("name"),
					user.getString("fullname"), user.getString("password"),
					user.getInt("privileges")));
		}

		user.close();

		logFine("successfully loaded user list from database, users found: "
				+ userList.size());

		return userList.toArray(new User[0]);
	}

	public int getAdminCount() throws Exception {
		User[] allUsers = getAllUsers();
		User user = null;
		int count = 0;
		for (int i = 0; i < allUsers.length; i++) {
			user = allUsers[i];
			if (user.isAdmin())
				count++;
		}
		return count;
	}

	public int getReadOnlyUserCount() throws Exception {
		User[] allUsers = getAllUsers();
		User user = null;
		int count = 0;
		for (int i = 0; i < allUsers.length; i++) {
			user = allUsers[i];
			if (user.isReadonly())
				count++;
		}
		return count;
	}

	public int getRegularUserCount() throws Exception {
		User[] allUsers = getAllUsers();
		User user = null;
		int count = 0;
		for (int i = 0; i < allUsers.length; i++) {
			user = allUsers[i];
			if (!user.isAdmin() && !user.isReadonly())
				count++;
		}
		return count;
	}

	/**
	 * 
	 * @param logLevel
	 *            see {@link Level}
	 * @param picture
	 * @param category
	 * @param message
	 * @throws Exception
	 */
	protected void log(Level logLevel, Picture picture, Category category,
			String message) throws Exception {
		User user = ApplicationControl.getInstance().getCurrentUser();
		if (user == null) {
			throw new IllegalStateException("please login, first!");
		}
		if (logLevel.intValue() >= this.logLevel.intValue()) {
			doInsertUpdateDelete(
					"INSERT INTO log (user_id, picture_id, category_id, message, timestamp, level) VALUES ("
							+ user.getId()
							+ ", "
							+ (picture != null ? picture.getId() : -1)
							+ ", "
							+ (category != null ? category.getId() : -1)
							+ ", '"
							+ message
							+ "', now(), '"
							+ logLevel.toString() + "');", true);
		}
	}

	protected void log(Level logLevel, Picture picture, String message)
			throws Exception {
		this.log(logLevel, picture, null, message);
	}

	protected void log(Level logLevel, String message) throws Exception {
		this.log(logLevel, null, null, message);
	}

	public void logFine(Picture picture, Category category, String message) {
		try {
			this.log(Level.FINE, picture, category, message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void logFine(Picture picture, String message) {
		try {
			this.log(Level.FINE, picture, message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void logFine(String message) {
		try {
			this.log(Level.FINE, message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void logInfo(Picture picture, Category category, String message) {
		try {
			this.log(Level.INFO, picture, category, message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void logInfo(Picture picture, String message) {
		try {
			this.log(Level.INFO, picture, message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void logInfo(String message) {
		try {
			this.log(Level.INFO, message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void logWarn(Picture picture, Category category, String message) {
		try {
			this.log(Level.WARNING, picture, category, message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void logWarn(Picture picture, String message) {
		try {
			this.log(Level.WARNING, picture, message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void logWarn(String message) {
		try {
			this.log(Level.WARNING, message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void logSevere(Picture picture, Category category, String message) {
		try {
			this.log(Level.SEVERE, picture, category, message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void logSevere(Picture picture, String message) {
		try {
			this.log(Level.SEVERE, picture, message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void logSevere(String message) {
		try {
			this.log(Level.SEVERE, message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
