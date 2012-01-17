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

public class User extends DatabaseObject{
    
    protected String login = null, fullname = null, password = null;
    protected int privileges = 0;

    /**
     * @param id
     * @param login
     */
    public User(int id, String login) {
        super(id);
        this.login = login;
    }
    
    

    /**
     * @param id
     * @param login
     * @param fullname
     * @param privileges numeric representation of user rights
     */
    public User(int id, String login, String fullname, String password, int privileges) {
        super(id);
        this.login = login;
        this.fullname = fullname;
        this.privileges = privileges;
        this.password = password;
    }


    public String getPassword() {
        return password;
    }



    public void setPassword(String password) {
        this.password = password;
    }



    public boolean canSeeHiddenPictures(){
        return isAdmin();
    }

    public String getLogin() {
        return login;
    }

    public String getFullname() {
        return fullname;
    }

    public boolean isAdmin() {
        return privileges%2 == 1;
    }

    public boolean isReadonly() {
        return (privileges/10)%2 == 1;
    }
    
    public void setLogin(String login) {
        this.login = login;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
    
    public int getPrivileges() {
		return privileges;
	}

    public void setAdmin(boolean admin) {
    	if (isAdmin()==admin)
    		return;
        if (!isAdmin() && admin){
        	this.privileges += 1;
        } else if (isAdmin() && !admin){
        	this.privileges -= 1;
        }
    }
    
    public void setReadonly(boolean readOnly) {
        if (isReadonly()==readOnly)
            return;
        if (!isReadonly() && readOnly){
            this.privileges += 10;
        } else if (isReadonly() && !readOnly){
            this.privileges -= 10;
        }
    }

}
