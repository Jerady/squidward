package de.picman.gui.actions;

import java.awt.event.ActionEvent;
import java.lang.reflect.Method;

public class DisplayDebugDialogAction extends AbstractPicmanAction {

	private static final long serialVersionUID = -2536129365904775471L;

	public DisplayDebugDialogAction() {
		super("Debugging Dialog");
	//	putValue(Action.SMALL_ICON, GUIControl.getInstance().getIconProvider().getImageIcon("icon.edit"));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
	  String[] argv = {""};
	  Method method;
		try {
			method = Class.forName( "de.jensd.bilderdb.gui.components.DebugDialog" ). 
			getMethod( "main", argv.getClass() );
			method.invoke( null, new Object[]{ argv } );
		} catch (Exception e1) {
			e1.printStackTrace();
		} 
	}
	
	

}
