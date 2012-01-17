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

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Set;

import de.picman.backend.control.ApplicationControl;

/**
 * A class to define one or more criteria to search the database for pictures.
 * 
 * For each data member of the class {@link Picture} zero, one or more criteria 
 * - depending on the data type - can be defined.
 *
 * <br><br><b>Last change by $Author$ on $Date$</b>
 *
 * @author Ole Rahn
 * 
 * @version $Rev$
 * 
 */
public class PictureSearchCriteria {
    
    protected SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    protected HashMap<PictureColumn, Object> columnToCriterion = new HashMap<PictureColumn, Object>();
    
    public void clear() {
		columnToCriterion.clear();
	}

	public Object get(PictureColumn column) {
		return columnToCriterion.get(column);
	}

	public boolean containsColumn(PictureColumn column) {
		return columnToCriterion.containsKey(column);
	}

	public boolean isEmpty() {
		return columnToCriterion.isEmpty();
	}

	public Set<PictureColumn> columnSet() {
		return columnToCriterion.keySet();
	}
	
	public PictureColumn[] columnArray() {
		return columnToCriterion.keySet().toArray(new PictureColumn[0]);
	}

	public Object put(PictureColumn column, Object arg1) {
		return columnToCriterion.put(column, arg1);
	}

	public Object remove(PictureColumn column) {
		return columnToCriterion.remove(column);
	}

	public int size() {
		return columnToCriterion.size();
	}

	protected String generateCondition(PictureColumn column, Object criterion){
    	String sql = "";

    	if (criterion==null){
    		return sql;
    	}
    	
    	if (!column.getDataType().equals(criterion.getClass())){
    		throw new IllegalArgumentException("given search criterion for column \"" + column + "\" is of a wrong type, should be \"" + column.getDataType().getName() + "\" but is \"" + criterion.getClass().getName() + "\".");
    	}
    	
        if (column.getColumnName().equals(PictureColumn.CATEGORY.getColumnName())){
            Integer[] categoryIds = (Integer[])criterion;
            if (categoryIds==null || categoryIds.length==0){
                //return sql;
                // support queries for pictures without assigned categories
                sql = "(SELECT count(*) FROM pictures_to_categories WHERE picture_id = pictures.id)=0";
                return sql;
            }
            sql = " (ARRAY(SELECT category_id FROM pictures_to_categories WHERE picture_id = pictures.id) @> ARRAY[";
            for (int i=0; i<categoryIds.length; i++){
                if (i>0)
                    sql += ",";
                sql += categoryIds[i];
            }
            sql += "])";
        } else if (String.class.isInstance(criterion)){
    		sql = " " + column.getColumnName() + " ilike '%" + criterion.toString().replace('*', '%') + "%' ";
    	} else if (Boolean.class.isInstance(criterion) || Integer.class.isInstance(criterion)){
    		sql = " " + column.getColumnName() + " = " + criterion.toString() + " ";
    	} else if (Date.class.isInstance(criterion)){
    		sql = " " + column.getColumnName() + " = '" + dateFormat.format((Date)criterion) + "' ";
    	}
    	
    	return sql;
    }
    
    public String generateQuery(PictureColumn orderBy){
    	String sql = "SELECT id FROM pictures WHERE " + (ApplicationControl.getInstance().getCurrentUser().canSeeHiddenPictures()?"":"deleted = FALSE ");
    	
    	PictureColumn[] columns = columnArray();
        
        if (columns.length>0 && !ApplicationControl.getInstance().getCurrentUser().canSeeHiddenPictures())
            sql += " AND ";
        
    	for (int i = 0; i < columns.length; i++) {
    		if (i>0)
    			sql += " AND ";
			sql += generateCondition(columns[i], get(columns[i]));
		}
    	
    	sql += " ORDER BY " + orderBy.getColumnName() + ";";
    	
    	return sql;
    }
}
