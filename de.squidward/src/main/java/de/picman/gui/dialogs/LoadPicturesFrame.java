package de.picman.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.border.EmptyBorder;

import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.border.DropShadowBorder;
import org.jdesktop.swingx.icon.EmptyIcon;
import org.jdesktop.swingx.painter.BusyPainter;

import de.picman.gui.main.GUIControl;



public class LoadPicturesFrame extends JXFrame {

	private static final long serialVersionUID = -6548221686404683804L;
	private JXPanel splashPanel;
	private JXBusyLabel busyLabel;
	private JXPanel busyPanel;
	
	public LoadPicturesFrame() {
		add( getSplashPanel(),BorderLayout.CENTER);
		setWaitPane(getBusyPanel());
		setStartPosition(StartPosition.CenterInScreen);
		setUndecorated(true);
		setAlwaysOnTop(true);
		pack();
	}
	
	@Override
	public void setWaiting(boolean waiting) {
		super.setWaiting(waiting);
		busyLabel.setBusy(waiting);
	}
	
	
	protected JXPanel getSplashPanel() {
		if(splashPanel == null){
			splashPanel = new JXPanel();
			JXLabel splashLabel = new JXLabel();
		 	splashLabel.setIcon(GUIControl.get().getImageIcon("splashscreen"));
		 	splashPanel.add(splashLabel);
		 	splashPanel.setBorder(new DropShadowBorder());
		 	
		}
		return splashPanel;
	}

	protected JXPanel getBusyPanel() {
		if(busyPanel == null){
			busyPanel = new JXPanel();
			busyPanel.setLayout(new BorderLayout());
			busyPanel.setOpaque(false);
			
			int top = 150;
			int right = 150;
			int left = 150;
			int bottom = 100;
			
			busyPanel.setBorder(new EmptyBorder(top, left, bottom, right));
			
			busyPanel.add(getBusyLabel(),BorderLayout.CENTER);
			JXLabel loadLabel = new JXLabel("Bilder laden ...");
			loadLabel.setFont(new Font("Arial",Font.BOLD, 14));
			loadLabel.setHorizontalTextPosition(JXLabel.CENTER);
			busyPanel.add(loadLabel, BorderLayout.SOUTH);

		}
		return busyPanel;
	}

	protected JXBusyLabel getBusyLabel() {
		if(busyLabel == null){
			busyLabel = new JXBusyLabel(new Dimension(100,100));
			BusyPainter painter = new BusyPainter(
				new RoundRectangle2D.Float(0, 0,33.5f,6.1f,10.0f,10.0f),
				new Ellipse2D.Float(15.0f,15.0f,70.0f,70.0f));
			painter.setTrailLength(3);
			painter.setPoints(14);
			painter.setFrame(5);
			busyLabel.setPreferredSize(new Dimension(100,100));
			busyLabel.setIcon( new EmptyIcon(100,100));
			busyLabel.setBusyPainter(painter);
		}
		return busyLabel;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LoadPicturesFrame loadDialog = new LoadPicturesFrame();
		loadDialog.setVisible(true);
		loadDialog.setWaiting(true);
	}

}
