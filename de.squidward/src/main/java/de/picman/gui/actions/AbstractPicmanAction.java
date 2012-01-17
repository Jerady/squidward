package de.picman.gui.actions;

import javax.swing.AbstractAction;
import javax.swing.Action;

public abstract class AbstractPicmanAction extends AbstractAction {

	private static final long serialVersionUID = -2667101981438823668L;


	public AbstractPicmanAction() {
		putValue(Action.NAME, "no ActionName set");
	}
	
	public AbstractPicmanAction(String name){
		putValue(Action.NAME, name);
		putValue(Action.SHORT_DESCRIPTION, name);
	}
	
	
	@Override
	public String toString() {
		return (String)getValue(Action.NAME);
	}
	
}
