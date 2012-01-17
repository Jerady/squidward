package de.picman.gui.providers;

import java.util.HashMap;

import javax.swing.Action;

import de.picman.backend.control.ApplicationControl;
import de.picman.backend.db.Category;
import de.picman.gui.actions.ClearPictureClipboardAction;
import de.picman.gui.actions.DeletePictureAction;
import de.picman.gui.actions.DisplayAboutDialogAction;
import de.picman.gui.actions.DisplayAllPicturesAction;
import de.picman.gui.actions.DisplayAssignCategoriesDialogAction;
import de.picman.gui.actions.DisplayCategoryEditorDialogAction;
import de.picman.gui.actions.DisplayDatabaseSettingsAction;
import de.picman.gui.actions.DisplayDropTargetAction;
import de.picman.gui.actions.DisplayExportPicturesDialogAction;
import de.picman.gui.actions.DisplayLogViewerAction;
import de.picman.gui.actions.DisplaySendMailDialogAction;
import de.picman.gui.actions.EditUserAction;
import de.picman.gui.actions.ExitAction;
import de.picman.gui.actions.ExportPictureClipboardAction;
import de.picman.gui.actions.RestorePictureAction;
import de.picman.gui.actions.SearchDeletedPicturesAction;
import de.picman.gui.actions.SearchPictureByCategoryAction;
import de.picman.gui.actions.SendClipboardMailAction;
import de.picman.gui.actions.StartGarbageCollectorAction;
import de.picman.gui.actions.UploadPictureAction;

public class ActionProvider {
	
	private static Action exitAction;
	private static Action uploadPictureAction;
	private static Action displayCategoriesAction;
	private static Action editUserAction;
	private static Action editCategoriesAction;
	private static Action displayAllPicturesAction;
	private static Action displayDropTargetAction;
	private static Action displayExportPicturesDialogAction;
	private static Action clearPictureClipboardAction;
	private static SearchPictureByCategoryAction searchPictureByCategoryAction;
	private static Action sendClipboardMailAction;
	private static Action displayDatabaseSettingsAction;
	private static Action displayLogViewerAction;
	private static Action displayAboutDialogAction;
	private static Action deletePictureAction;
	private static Action searchDeletedPicturesAction;
	private static Action restorePictureAction;
	private static Action exportPictureClipboardAction;
	private static Action garbageCollectorAction;
	private static Action displaySendMailDialog;
	
	public static Action getDisplaySendMailDialogAction() {
		if(displaySendMailDialog == null)
			displaySendMailDialog = new DisplaySendMailDialogAction();
		return displaySendMailDialog;
	}
	
	public static Action getGarbageCollectorAction() {
		if(garbageCollectorAction == null)
			garbageCollectorAction = new StartGarbageCollectorAction();
		return garbageCollectorAction;
	}

	public static Action getExportPictureClipboardAction() {
		if(exportPictureClipboardAction == null)
			exportPictureClipboardAction = new ExportPictureClipboardAction();
		return exportPictureClipboardAction;
	}
	
	public static Action getDisplayAboutDialogAction() {
		if(displayAboutDialogAction == null)
			displayAboutDialogAction = new DisplayAboutDialogAction();
		return displayAboutDialogAction;
	}
	
	public static Action getSearchDeletedPictureAction() {
		if(searchDeletedPicturesAction == null){
			searchDeletedPicturesAction = new SearchDeletedPicturesAction();
		}
		return searchDeletedPicturesAction;
	}
	
	
	public static Action getDeletePictureAction() {
		if(deletePictureAction == null){
			deletePictureAction = new DeletePictureAction();
		}
		return deletePictureAction;
	}
	public static Action getRestorePictureAction() {
		if(restorePictureAction == null){
			restorePictureAction = new RestorePictureAction();
		}
		return restorePictureAction;
	}


	public static Action getBeendenAction(){
		if(exitAction == null)
			exitAction = new ExitAction();
		return exitAction;
	}
	public static Action getDisplayLogViewerAction(){
		if(displayLogViewerAction == null)
			displayLogViewerAction = new DisplayLogViewerAction();
		return displayLogViewerAction;
	}

	
	public static Action getDisplayDatabaseSettingsDialogAction(){
		if(displayDatabaseSettingsAction == null)
			displayDatabaseSettingsAction = new DisplayDatabaseSettingsAction();
		return displayDatabaseSettingsAction;
	}

	
	public static SearchPictureByCategoryAction getSearchPictureByCategoryAction(){
		if(searchPictureByCategoryAction == null)
			searchPictureByCategoryAction = new SearchPictureByCategoryAction();
		return searchPictureByCategoryAction;
	}

	
	public static Action getClearPictureClipboardAction(){
		if(clearPictureClipboardAction == null)
			clearPictureClipboardAction = new ClearPictureClipboardAction();
		return clearPictureClipboardAction;
	}

	public static Action getSendClipboardMailAction(){
		if(sendClipboardMailAction == null)
			sendClipboardMailAction = new SendClipboardMailAction();
		return sendClipboardMailAction;
	}

	
	public static Action getUploadPictureAction(){
		if(uploadPictureAction == null){
			uploadPictureAction = new UploadPictureAction();
			uploadPictureAction.setEnabled(!ApplicationControl.getInstance().getCurrentUser().isReadonly());
		}
		return uploadPictureAction;
	}
	
	public static Action getEditUserAction(){
		if(editUserAction == null)
			editUserAction = new EditUserAction();
		return editUserAction;
	}
	
	public static Action getEditCategoriesAction(){
		if(editCategoriesAction == null)
			editCategoriesAction = new DisplayCategoryEditorDialogAction();
		return editCategoriesAction;
	}

	public static Action getDisplayExportPicturesDialogAction(){
		if(displayExportPicturesDialogAction == null)
			displayExportPicturesDialogAction = new DisplayExportPicturesDialogAction();
		return displayExportPicturesDialogAction;
	}
	
	public static Action getAssignCategoriesDialogAction(HashMap<Integer, Category> assignedCategories){
		if(displayCategoriesAction == null)
			displayCategoriesAction = new DisplayAssignCategoriesDialogAction(assignedCategories);
		return displayCategoriesAction;
	}
	
	public static Action getDisplayAllPicturesAction(){
		if(displayAllPicturesAction == null)
			displayAllPicturesAction = new DisplayAllPicturesAction();
		return displayAllPicturesAction;
	}

	public static Action getDisplayDropTargetAction(){
		if(displayDropTargetAction == null)
			displayDropTargetAction = new DisplayDropTargetAction();
		return displayDropTargetAction;
	}
	
}
