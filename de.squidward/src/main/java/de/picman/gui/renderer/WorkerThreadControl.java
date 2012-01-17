package de.picman.gui.renderer;

import javax.swing.SwingWorker;

public class WorkerThreadControl {

	private static WorkerThreadControl me;
	private SwingWorker<Object, Void> worker; 
	
	private WorkerThreadControl() {
	}

	public static WorkerThreadControl getInstance(){
		if(me == null){
			me = new WorkerThreadControl();
		}
		return me;
	}
	
	public SwingWorker<Object, Void> getWorker(){
		return worker;
	}

	public void setWorker(SwingWorker<Object, Void> worker) {
		this.worker = worker;
	}
	
	
	
	
}
