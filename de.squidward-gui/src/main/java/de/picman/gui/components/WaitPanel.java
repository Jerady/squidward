package de.picman.gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.painter.PainterGlasspane;
import org.jdesktop.swingx.painter.RectanglePainter;
import org.pushingpixels.trident.Timeline;

public class WaitPanel extends JXPanel implements MouseListener, MouseMotionListener, FocusListener{
	
	private static final long serialVersionUID = -5346090783456286855L;
	private Timeline faderTimeline;
	private JXLabel content;
	
	public WaitPanel() {
		initComponent();
	}
	
	@SuppressWarnings("rawtypes")
	private void initComponent() {
		PainterGlasspane glasspane = new PainterGlasspane();
		setBackgroundPainter(glasspane.getPainter());

		 Color blue = new Color(0x417DDD);
		  Color translucent = new Color(blue.getRed(), blue.getGreen(), blue.getBlue(), 0);
		  setBackground(Color.DARK_GRAY);
		  setForeground(Color.LIGHT_GRAY);
		  GradientPaint blueToTranslucent = new GradientPaint(
		    new Point2D.Double(.4, 0),
		    blue,
		    new Point2D.Double(1, 0),
		    translucent);
		  MattePainter veil = new MattePainter(blueToTranslucent);
		  veil.setPaintStretched(true);
		  Painter backgroundPainter = new RectanglePainter(this.getBackground(), null);
		  Painter p = new CompoundPainter(backgroundPainter, veil);
		  setBackgroundPainter(p);
		
		
		setLayout(new BorderLayout());
		setBackground(Color.BLACK);
		setOpaque(false);
		add(getContent(), BorderLayout.CENTER);
		setAlpha(0.0f);
		
		addMouseListener(this);
	    addMouseMotionListener(this);
	    addFocusListener(this);
	}

	private Timeline getFaderTimeline(){
		if(faderTimeline == null){
			faderTimeline = new Timeline(this);
		}
		return faderTimeline;
	}
	
	
	
	protected JXLabel getContent() {
		if(content == null){
			content = new JXLabel("TEST");
			content.setBackground(Color.BLACK);
			content.setOpaque(false);
		}
		return content;
	}

	public void fadeIn(){
		getFaderTimeline().abort();
		getFaderTimeline().addPropertyToInterpolate("alpha", 0.0f, 0.5f);
		getFaderTimeline().setDuration(2500);
		getFaderTimeline().play();
	}
	
	public void fadeOut(){
		getFaderTimeline().abort();
		getFaderTimeline().addPropertyToInterpolate("alpha", 0.0f, 0.5f);
		getFaderTimeline().setDuration(2500);
		getFaderTimeline().replayReverse();
	}

	
	private void redispatchEvents(MouseEvent e){
		System.out.println("MouseEvent: Nix da!");
	}

	private void redispatchEvents(FocusEvent e){
		System.out.println("FocusEvent: Nix da!");
	}
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final JXFrame frame = new JXFrame("Waitpanel Test", true);
		JTextArea jta = new JTextArea(10, 40);
		JScrollPane scroll = new JScrollPane(jta);
		JButton start = new JButton("Start Processing");
		JLabel status = new JLabel("status");
		Container comp = frame.getContentPane();
		comp.add("North", start);
		comp.add("South", status);
		comp.add("Center", scroll);

		
		JMenu testMenu = new JMenu("Main");
		JMenuItem testItem = new JMenuItem("Exit");
		testItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		testMenu.add(testItem);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(testMenu);
		
		frame.setJMenuBar(menuBar);
		
		
		final WaitPanel suspendPanel = new WaitPanel();
		start.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				suspendPanel.setVisible(true);
				suspendPanel.fadeIn();
			}
		});
		
		frame.setGlassPane(suspendPanel);


		frame.pack();
		frame.setVisible(true);

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		redispatchEvents(e);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		redispatchEvents(e);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		redispatchEvents(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		redispatchEvents(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		redispatchEvents(e);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		redispatchEvents(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		redispatchEvents(e);
	}

	@Override
	public void focusGained(FocusEvent e) {
		redispatchEvents(e);
	}

	@Override
	public void focusLost(FocusEvent e) {
		redispatchEvents(e);
	}

}
