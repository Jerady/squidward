/*
 * Created on 08.01.2009
 *
 * SVN header information:
 *  $Author$
 *  $Rev$
 *  $Date$
 *  $Id$
 */
package de.picman.backend.db;

public enum PictureColumn {
	ID("id", Integer.class),
	DESCRIPTION("description", String.class),
	ORIGIN("origin", String.class),
	CREATIONDATE("creation_date", String.class),
	USERID("user_id", Integer.class),
	ISDELETED("deleted", Boolean.class),
	ISEXEMPLARY("exemplary", Boolean.class),
	ISPUBLICATION("publication", Boolean.class),
	ISBADEXAMPLE("bad_example", Boolean.class),
	SAVINGDATE("saving_date", Boolean.class),
    CATEGORY("category", (new Integer[0]).getClass());
	
	String columnName;
	Class<? extends Object> dataType;
	
	private PictureColumn(String columnName, Class<? extends Object> dataType) {
		this.columnName = columnName;
		this.dataType = dataType;
	}

	public String getColumnName() {
		return columnName;
	}

	public Class<? extends Object> getDataType() {
		return dataType;
	}
}