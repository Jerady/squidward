/*
 * Created on 27.12.2008
 *
 * SVN header information:
 *  $Author$
 *  $Rev$
 *  $Date$
 *  $Id$
 */
package de.picman.backend.db;

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
public abstract class DatabaseObject {
    protected int id;

    public int getId() {
        return id;
    }

    protected void setId(int id) {
        this.id = id;
    }

    /**
     * @param id
     */
    protected DatabaseObject(int id) {
        super();
        this.id = id;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " (ID:" + getId() + ")";
    }

    public int compareTo(DatabaseObject o) {
        return new Integer(getId()).compareTo(o.getId());
    }
    
    
}
