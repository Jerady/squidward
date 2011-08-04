package de.picman.gui.components;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

import de.rahn.bilderdb.db.Picture;


public class PictureTransferHandler extends TransferHandler {

	private static final long serialVersionUID = 1615536956826455661L;
	private DataFlavor pictureFlavor;
	private String pictureFlavorType = DataFlavor.javaJVMLocalObjectMimeType+";class=de.rahn.bilderdb.db.Picture";
	private JList source;
	
	public PictureTransferHandler() {
		try {
			pictureFlavor = new DataFlavor(pictureFlavorType);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private boolean hasPictureFlavor(DataFlavor[] flavors) {
        if (pictureFlavor == null) {
            return false;
        }

        for (int i = 0; i < flavors.length; i++) {
            if (flavors[i].equals(pictureFlavor)) {
                return true;
            }
        }
        return false;
    }

	@Override
	public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
		if(hasPictureFlavor(transferFlavors)){
			return true;
		}
		return false;
	}

	@Override
	public boolean canImport(TransferSupport support) {
		if(hasPictureFlavor(support.getDataFlavors())){
			return true;
		}
		return false;
	}

	
	@Override
	public boolean importData(JComponent comp, Transferable t) {
		
		if(!canImport(comp, t.getTransferDataFlavors())){
			return false;
		}
		
		Picture pic = null;
		try {
			if(hasPictureFlavor(t.getTransferDataFlavors())){
				pic = (Picture) t.getTransferData(pictureFlavor);
			}
			else{
				return false;
			}
		} catch (UnsupportedFlavorException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		PictureClipboard.getInstance().addPicture(pic);

        return true;
	}
	
	@Override
	protected Transferable createTransferable(JComponent c) {
		if(c instanceof JList){
			source = (JList)c;
			Object value = source.getSelectedValue();
			if( value == null || !(value instanceof Picture) ){
				return null;
			}
			Picture pic = (Picture)value;
			
			return new PictureTransferable(pic);
		}
		
		
		return super.createTransferable(c);
	}
	
	@Override
	public int getSourceActions(JComponent c) {
		return COPY_OR_MOVE;
	}
	
	
	public class PictureTransferable implements Transferable{
		private Picture picture;
		public PictureTransferable(Picture picture) {
			this.picture = picture;
		}

		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			if(pictureFlavor.equals(flavor)){
				return true;
			}
			return false;
		}

		@Override
		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[] { 
					pictureFlavor 
			};
		}
		
		
		@Override
		public Object getTransferData(DataFlavor flavor)
				throws UnsupportedFlavorException, IOException {
			
			if(!isDataFlavorSupported(flavor)){
				throw new UnsupportedFlavorException(flavor);
			}
			return picture;
		}
		
		
	}
}
