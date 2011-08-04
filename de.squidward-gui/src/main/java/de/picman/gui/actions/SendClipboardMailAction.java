package de.picman.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import de.picman.gui.main.GUIControl;

public class SendClipboardMailAction extends AbstractPicmanAction {

	
	private static final long serialVersionUID = -3521545909459605238L;

	public SendClipboardMailAction() {
		super("Mail");
		putValue(Action.SMALL_ICON, GUIControl.get().getIconProvider().getImageIcon("icon.mail"));
		putValue(Action.SHORT_DESCRIPTION, "Bilder in der Ablage per Mail versenden");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
//		try {
//			List<String> attachList = new ArrayList<String>();
//			attachList.add("C:/EclipseWorkspace/PicMan/src/resources/splashscreen.png");
//			Message message = new Message();
//			message.setAttachments(attachList);
//			message.setBody("");
//			java.awt.Desktop.mail(message);
//		} catch (IOException e1) {
//			ApplicationControl.displayErrorToUser(e1);
//		}
//			Map<Integer, Picture> picMap = PictureClipboard.getInstance().getPictureMap();
//			Collection<Picture> pictureCol = picMap.values();
//			Iterator<Picture> picIter = pictureCol.iterator();
//			while (picIter.hasNext()) {
//				Picture picture = (Picture) picIter.next();
//			}
	}
}
