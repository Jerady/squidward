package de.picman.gui.actions;

import java.awt.event.ActionEvent;

import de.picman.backend.db.Picture;
import de.picman.gui.components.MainFrame;
import de.picman.gui.dialogs.PictureViewerDialog;

public class ShowOriginalPictureAction extends AbstractPicmanAction {	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Picture picture;
	
	public ShowOriginalPictureAction(Picture picture) {
		super("Zeige Original");
		this.picture = picture;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		PictureViewerDialog pictureViewerDialog = MainFrame.getInstance().getPictureViewerDialog();

		pictureViewerDialog.showPicture(picture);
		pictureViewerDialog.setModal(true);
		pictureViewerDialog.setVisible(true);
	}
	
}
