package de.picman.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JDialog;

import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXLoginPane;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.auth.LoginService;
import org.pushingpixels.trident.Timeline;

import de.picman.gui.components.SquidWardLoginService;
import de.picman.gui.main.GUIControl;
import de.picman.gui.panels.DatabaseSettingsPanel;

public class SquidwardLoginDialog {
	
	private JXButton dbConnectionBtn;
	private JXPanel toolsPanel;
	private Timeline rolloverTimeline;
	
	public SquidwardLoginDialog() {
		LoginService loginService = new SquidWardLoginService();
		final JXLoginPane panel = new JXLoginPane(loginService);
		panel.setBannerText("Squidward");
		panel.add(getToolsPanel(), BorderLayout.SOUTH);
		
		JXLoginPane.showLoginDialog(null, panel);
	}
	
	protected JXPanel getToolsPanel() {
		if(toolsPanel == null){
			toolsPanel = new JXPanel(new BorderLayout());
			toolsPanel.add(getDbConnectionBtn(), BorderLayout.WEST);
			toolsPanel.addMouseListener(new MouseAdapter(){
				@Override
				public void mouseEntered(MouseEvent e) {
					fadeIn();
				}
				@Override
				public void mouseExited(MouseEvent e) {
					fadeOut();
				}
			});
			toolsPanel.setAlpha(0.0f);
		}
		return toolsPanel;
	}
	
	private Timeline getTimeline(){
		if(rolloverTimeline == null){
			rolloverTimeline = new Timeline(getToolsPanel());
		}
		return rolloverTimeline;
	}
	
	public void fadeIn(){
		getTimeline().abort();
		getTimeline().addPropertyToInterpolate("alpha", 0.0f, 1.0f);
		getTimeline().setDuration(500);
		getTimeline().play();
	}
	
	public void fadeOut(){
		getTimeline().abort();
		getTimeline().addPropertyToInterpolate("alpha", 0.0f, 1.0f);
		getTimeline().setDuration(500);
		getTimeline().replayReverse();
	}
	
	
	protected JXButton getDbConnectionBtn() {
		if(dbConnectionBtn == null){
			dbConnectionBtn = new JXButton("");
			dbConnectionBtn.setIcon(GUIControl.get().getImageIcon("icon.edit"));
			dbConnectionBtn.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					JDialog dialog = new JDialog();
					dialog.setModal(true);
					dialog.setLocationByPlatform(true);
					dialog.add(new DatabaseSettingsPanel());
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.pack();
					dialog.setVisible(true);				}
			});

		}
		return dbConnectionBtn;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new SquidwardLoginDialog();
	}
	
	

}
