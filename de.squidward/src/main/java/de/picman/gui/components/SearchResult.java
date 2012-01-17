package de.picman.gui.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import de.picman.backend.control.ApplicationControl;
import de.picman.backend.db.Picture;

public class SearchResult extends Observable{

	private static SearchResult me;
	private Map<Integer, Picture> pictureMap;
	private List<Picture> pictureList;
	
	private int startIndex = 0;
	private int endIndex = 50;
	private int indexStep = 50;
	
	private SearchResult() {
	}
	
	public static SearchResult getInstance(){
		if(me == null){
			me = new SearchResult();
		}
		return me;
	}
	

	protected List<Picture> getPicutureList() {
		if(pictureList == null){
			pictureList = new ArrayList<Picture>();
		}
		return pictureList;
	}

	public  Map<Integer, Picture> getPictureMap() {
		if(pictureMap == null){
			pictureMap = new HashMap<Integer, Picture>();
		}
		return pictureMap;
	}
	
	public void removePicture(Picture picture) {
		getPictureMap().remove(picture.getId());
		getPicutureList().remove(picture);
		notifyObservers();
	}
	
	public void addPicture(Picture picture){
		if(picture == null)
			return;
		getPictureMap().put(picture.getId(), picture);
		getPicutureList().add(picture);
	}

	public void removeAll() {
		getPictureMap().clear();
		getPicutureList().clear();
		try {
			ApplicationControl.getInstance().clearPictures();
		} catch (Exception e) {
			e.printStackTrace();
		}
		startIndex = 0;
		endIndex = indexStep;
		notifyObservers();
	}

	public void setPictureMap(Map<Integer, Picture> picMap){
		removeAll();
		pictureMap = picMap;
		notifyObservers();
	}

	public void setPictureList(List<Picture> pictureList){
		removeAll();
		this.pictureList.addAll(pictureList);
		notifyObservers();
	}
	
	
	public Picture[] getPicturesAsArray(){
		return getPictureMap().values().toArray(new Picture[0]);
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

	
	public int getNumberOfResults(){
		return getPicutureList().size();
	}

	public int getIndexStep(){
		return indexStep;
	}

	
	public void next(){
		try {
			ApplicationControl.getInstance().clearPictures();
		} catch (Exception e) {
			e.printStackTrace();
		}

		startIndex=endIndex;
		endIndex+=indexStep;
		debug();
		notifyObservers();
	}
	
	private void debug(){
		System.out.println("Alle:   " + getNumberOfResults());
		System.out.println("Start:  " + startIndex);
		System.out.println("End:    " + endIndex);
	}
	
	public void previous(){
		try {
			ApplicationControl.getInstance().clearPictures();
		} catch (Exception e) {
			e.printStackTrace();
		}

		endIndex = startIndex;
		startIndex-=indexStep;
		debug();
		notifyObservers();
	}
	
	public Picture[] getCurrentResultFrame(){
		
		if(startIndex<0)
			startIndex = 0;
			
		if(endIndex>getNumberOfResults())
			endIndex = getNumberOfResults();
		
		if(endIndex<0)
			endIndex = 0;
		
		return getPicutureList().subList(startIndex, endIndex).toArray(new Picture[0]);
	}
	
	public int getStartIndex(){
		return startIndex;
	}
	public int getEndIndex(){
		return endIndex;
	}
	
}
