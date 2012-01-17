/*
 * Created on 06.07.2008
 *
 * SVN header information:
 *  $Author$
 *  $Rev$
 *  $Date$
 *  $Id$
 */
package de.picman.backend.db;

import java.text.NumberFormat;
import java.util.HashMap;

import de.picman.backend.control.ApplicationControl;

/**
 * <br><br><b>Last change by $Author$ on $Date$</b>
 *
 * @author Ole Rahn
 * 
 * @version $Rev$
 * 
 */
public class Category  extends DatabaseObject implements Comparable<Category>{

    protected int parentCategory;
    protected String name;
    
    protected static NumberFormat numberFormat = null;
    
    public Category(int id, String name, int parentCategory) {
        super(id);
        this.name = name;
        this.parentCategory = parentCategory;
    }
    
    
    
    public NumberFormat getNumberFormat() {
        if (numberFormat==null){
            numberFormat = NumberFormat.getIntegerInstance();
            numberFormat.setMinimumIntegerDigits(5);
        }
        return numberFormat;
    }

    /**
     * 
     * @return the ID of the parent category (>0), if there is one, else 0 is returned.
     */
    public int getParentCategoryId() {
        return parentCategory;
    }
    
    
    /**
     * 
     * @return the {@link Category} object of the parent category, 
     * if there is one, else <code>null</code>
     */
    public Category getParentCategory() {
        if (!isTopLevel()) {
            DbController dbcontrol = ApplicationControl.getInstance().getDbController();
            HashMap<Integer, Category> catMap = dbcontrol.getCategories();
            return getParentCategory(catMap);
        }
        return null;
    }
    
    /**
     * 
     * @return the {@link Category} object of the parent category, 
     * if there is one, else <code>null</code>
     */
    public Category getParentCategory(HashMap<Integer, Category> catMap) {
        if (!isTopLevel()) {
            if (catMap.containsKey(getParentCategoryId()))
                return catMap.get(getParentCategoryId());
        }
        return null;
    }
    
    public String getName() {
        return name;
    }
    
    /**
     * use {@link #isTopLevel()} instead
     * @deprecated
     * @return
     */
    public boolean isFirstLevel(){
        return isTopLevel();
    }
    
    public boolean isTopLevel(){
        return parentCategory == 0;
    }

    /**
     * 
     * @param possibleParent
     * @return true if <code>this</code> {@link Category} is a child of the given Category, else false.
     */
    public boolean isChildOf(Category possibleParent){
        if (isTopLevel())
            return false;
        DbController dbcontrol = ApplicationControl.getInstance().getDbController();
        HashMap<Integer, Category> catMap = dbcontrol.getCategories();
        Category cat = getParentCategory(catMap);
        while (cat!=null){
            if (possibleParent.getId() == cat.getId()){
                return true;
            }
            cat = cat.getParentCategory(catMap);
        }
        return false;
    }
    
    /**
     * 
     * @return the top level category, which this category is a child of or - in case
     * this is a top level category - this.
     */
    public Category getTopLevelCategory(){
        if (isTopLevel())
            return this;
        DbController dbcontrol = ApplicationControl.getInstance().getDbController();
        HashMap<Integer, Category> catMap = dbcontrol.getCategories();
        Category cat = getParentCategory(catMap);
        while (!cat.isTopLevel()){
            cat = cat.getParentCategory(catMap);
        }
        return cat;
    }
    
    /**
     * Get a path made of the names of all the names of the categories that directly or 
     * indirectly contain this category
     * @param pathSeparator the separator that separates the category names
     * @return
     */
    public String getPathAsString(String pathSeparator){
        return getPathAsString(pathSeparator, true);
    }
    
    /**
     * Get a path made of the names of all the names of the categories that directly or 
     * indirectly contain this category
     * @param pathSeparator the separator that separates the category names
     * @param includeTopLevel if true, the top level category containing this node will
     * be part of the path
     * @return
     */
    public String getPathAsString(String pathSeparator, boolean includeTopLevel){
        String path = getName();
        DbController dbcontrol = ApplicationControl.getInstance().getDbController();
        HashMap<Integer, Category> catMap = dbcontrol.getCategories();
        Category cat = getParentCategory(catMap);
        while (cat!=null){
            if (cat.isTopLevel() && !includeTopLevel)
                break;
            path = cat.getName() + pathSeparator + path;
            cat = cat.getParentCategory(catMap);
        }
        return path;
    }
    
    public void setName(String name) {
		this.name = name;
	}



	public void setParentCategory(int parentCategory) {
		this.parentCategory = parentCategory;
	}



	public int compareTo(Category o) {
        return (getNumberFormat().format(getParentCategoryId()) + getName()).compareTo(getNumberFormat().format(o.getParentCategoryId()) + o.getName());
    }
   
    
}
