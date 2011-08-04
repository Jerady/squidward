package de.picman.gui.components;



import java.awt.Color;

import java.awt.Container;

import java.awt.Cursor;

import java.awt.Graphics;

import java.awt.Graphics2D;

import java.awt.Toolkit;

import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;

import java.awt.event.MouseEvent;



import javax.swing.JButton;

import javax.swing.JComponent;

import javax.swing.JFrame;

import javax.swing.JLabel;

import javax.swing.JScrollPane;

import javax.swing.JTextArea;

import javax.swing.event.MouseInputListener;



public class WindowBlocker extends JComponent implements MouseInputListener {


	private static final long serialVersionUID = 1L;

	private Cursor old_cursor;

	

	/** The animation thread is responsible for fade in/out and rotation. */

    protected Thread  animation  = null;

     /** Alpha level of the veil, used for fade in/out. */

    protected int     alphaLevel = 0;

    /** Duration of the veil's fade in/out. */

    protected int     rampDelay  = 200;

    /** Alpha level of the veil. */

    protected float   shield     = 0.70f;

    /** Amount of frames per second. Lowers this to save CPU. */

    protected float   fps        = 25.0f;

    /** Notifies whether the animation is running or not. */

    protected boolean started    = false;



    protected boolean locked;

	

	public WindowBlocker() {

		addMouseListener(this);

		addMouseMotionListener(this);

	}



	public void mouseMoved(MouseEvent e) {

	}



	public void mouseDragged(MouseEvent e) {

	}



	public void mouseClicked(MouseEvent e) {

		Toolkit.getDefaultToolkit().beep();

	}



	public void mouseEntered(MouseEvent e) {

	}



	public void mouseExited(MouseEvent e) {

	}



	public void mousePressed(MouseEvent e) {

	}



	public void mouseReleased(MouseEvent e) {

	}



	public boolean isLocked() {

		return locked;

	}



	public void setLocked(boolean locked) {

		this.locked = locked;

	}



	public void block() {

		old_cursor = getCursor();

		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		alphaLevel = 0;

		setVisible(true);

		setLocked(true);

	}



	public void blockLight() {

		old_cursor = getCursor();

		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		setVisible(true);

        animation = new Thread(new Animator(true));

        animation.start();

		setLocked(true);

	}

	public void unBlock() {

		setCursor(old_cursor);

		setVisible(false);

		setLocked(false);

	}



	public void unBlockLight() {

		setCursor(old_cursor);

		if (animation != null) {

	        animation.interrupt();

	        animation = null;

	        animation = new Thread(new Animator(false));

	        animation.start();

        }

		setLocked(false);



	}





	   /**

     * Animation thread.

     */

    private class Animator implements Runnable

    {

    	

        private boolean rampUp = true;



        protected Animator(boolean rampUp)

        {

            this.rampUp = rampUp;

        }





        public void run()

        {

            long start = System.currentTimeMillis();

            if (rampDelay == 0)

                alphaLevel = rampUp ? 255 : 0;



            started = true;

            boolean inRamp = rampUp;



            while (!Thread.interrupted())

            {

                repaint();



                if (rampUp)

                {

                    if (alphaLevel < 255)

                    {

                        alphaLevel = (int) (255 * (System.currentTimeMillis() - start) / rampDelay);

                        if (alphaLevel >= 255)

                        {

                            alphaLevel = 255;

                            inRamp = false;

                        }

                    }

                } else if (alphaLevel > 0) {

                    alphaLevel = (int) (255 - (255 * (System.currentTimeMillis() - start) / rampDelay));

                    if (alphaLevel <= 0)

                    {

                        alphaLevel = 0;

                        break;

                    }

                }

                

                try

                {

                    Thread.sleep(inRamp ? 10 : (int) (1000 / fps));

                } catch (InterruptedException ie) {

                    break;

                }

                Thread.yield();

            }



            if (!rampUp)

            {

                started = false;

                repaint();



                setVisible(false);

            }

        }

    }



	





	 public void paintComponent(Graphics g){

        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(new Color(100, 100, 100, (int) (alphaLevel * shield)));

        g2.fillRect(0, 0, getWidth(), getHeight());

	 }



	public static void main(String[] args) {

		JFrame frame = new JFrame("Blocking Window");

		JTextArea jta = new JTextArea(10, 40);

		JScrollPane scroll = new JScrollPane(jta);

		JButton start = new JButton("Start Processing");

		JLabel status = new JLabel("status");



		final WindowBlocker blocker = new WindowBlocker();

		frame.setGlassPane(blocker);

		

		start.addActionListener(new ActionListener(){

			@Override

			public void actionPerformed(ActionEvent arg0) {

				blocker.blockLight();

			}

		});

		



		Container comp = frame.getContentPane();

		comp.add("North", start);

		comp.add("Center", scroll);

		comp.add("South", status);



		frame.pack();

		frame.setVisible(true);

	}



}