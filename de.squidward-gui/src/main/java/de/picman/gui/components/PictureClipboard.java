package de.picman.gui.components;

import java.util.HashMap;
import java.util.Observable;

import de.rahn.bilderdb.control.ApplicationControl;
import de.rahn.bilderdb.db.Picture;

public class PictureClipboard extends Observable{

	private static PictureClipboard me;
	private HashMap<Integer, Picture> pictureMap;
	
	private PictureClipboard() {
	}
	
	public static PictureClipboard getInstance(){
		if(me == null){
			me = new PictureClipboard();
		}
		return me;
	}
	

	public  HashMap<Integer, Picture> getPictureMap() {
		if(pictureMap == null){
			pictureMap = new HashMap<Integer, Picture>();
		}
		return pictureMap;
	}
	
	public void removePicture(Picture picture) {
		getPictureMap().remove(picture.getId());
		notifyObservers();
	}
	
	public void addPicture(Picture picture){
		getPictureMap().put(picture.getId(), picture);
		notifyObservers();
	}

	public void addPicture(String pictureID){
		Integer id = new Integer(pictureID);
		try {
			getPictureMap().put(id, ApplicationControl.getInstance().getMasterMap().get(id));
		} catch (Exception e) {
			e.printStackTrace();
		}
		notifyObservers();
	}

	
	
	@Override
	public void notifyObservers() {
		setChanged();
		super.notifyObservers();
	}
	
	@Override
	public String toString() {
		return getPictureMap().toString();
	}

	public void removeAll() {
		getPictureMap().clear();
		notifyObservers();
		
	}
	
}
