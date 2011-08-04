package de.picman.gui.actions;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;

import de.picman.gui.components.MainFrame;
import de.picman.gui.main.GUIControl;

public class DisplayDropTargetAction extends AbstractPicmanAction {

	private static final long serialVersionUID = -3920723993806870020L;

	public DisplayDropTargetAction() {
		super("Display DropTarget");
		putValue(Action.SMALL_ICON, GUIControl.get().getIconProvider().getImageIcon("icon.picture"));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {

		new DropTargetDialog().setVisible(true);
	}


	
	private class DropTargetDialog extends JDialog{
		private static final long serialVersionUID = -8148103404666845928L;

		public DropTargetDialog() {
			super(MainFrame.getInstance());
			setTitle("DropTarget");
			setSize(400,400);
			
			final DefaultListModel listModel = new DefaultListModel();
		    listModel.addElement("TEST1");
		    listModel.addElement("TEST2");
		    listModel.addElement("TEST3");
		    listModel.addElement("TEST4");
		    
		    
			final JList list = new JList();
			list.setDropMode(DropMode.ON_OR_INSERT);
			list.setModel(listModel);
			
			list.setVisibleRowCount(-1);
			list.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

			
			list.setTransferHandler(new TransferHandler() {
				private static final long serialVersionUID = -8344873708232287624L;

				public boolean canImport(TransferHandler.TransferSupport support) {
			          if (!support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			            return false;
			          }
			          JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
			          if (dl.getIndex() == -1) {
			            return false;
			          } else {
			            return true;
			          }
			        }

			        public boolean importData(TransferHandler.TransferSupport support) {
			          if (!canImport(support)) {
			            return false;
			          }

			          Transferable transferable = support.getTransferable();
			          String data;
			          try {
			            data = (String) transferable.getTransferData(DataFlavor.stringFlavor);
			          } catch (Exception e) {
			            return false;
			          }

			          System.out.println(data);
			          
			          JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
			          int index = dl.getIndex();
			          if (dl.isInsert()) {
			            listModel.add(index, data);
			          } else {
			            listModel.set(index, data);
			          }
			          Rectangle r = list.getCellBounds(index, index);
			          list.scrollRectToVisible(r);
			          return true;
			        }
			      });

			setLayout(new BorderLayout());
			add(new JScrollPane(list), BorderLayout.CENTER);
			
		}
		
	}

}
