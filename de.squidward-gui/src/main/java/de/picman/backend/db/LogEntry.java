/*
 * Created on 14.03.2009
 *
 * SVN header information:
 *  $Author$
 *  $Rev$
 *  $Date$
 *  $Id$
 */
package de.picman.backend.db;

import java.sql.Date;

/**
 * TODO: comment class
 *
 * <br><br><b>Last change by $Author$ on $Date$</b>
 *
 * @author Ole Rahn
 * 
 * @version $Rev$
 * 
 */
public class LogEntry extends DatabaseObject {
    protected int userId, pictureId, categoryId;
    protected String userName, message, logLevel;
    protected Date timestamp;
    
    /**
     * @param id
     * @param userId
     * @param pictureId
     * @param categoryId
     * @param userName
     * @param message
     * @param logLevel
     * @param timestamp
     */
    public LogEntry(int id, int userId, int pictureId, int categoryId, String userName, String message, String logLevel, Date timestamp) {
        super(id);
        this.userId = userId;
        this.pictureId = pictureId;
        this.categoryId = categoryId;
        this.userName = userName;
        this.message = message;
        this.logLevel = logLevel;
        this.timestamp = timestamp;
    }

    
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public int getCategoryId() {
        return categoryId;
    }


    public String getLogLevel() {
        return logLevel;
    }


    public String getMessage() {
        return message;
    }


    public int getPictureId() {
        return pictureId;
    }
    
    
}
