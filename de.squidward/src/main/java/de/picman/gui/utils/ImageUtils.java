package de.picman.gui.utils;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ImageUtils {

	public static BufferedImage createImage(JComponent component) {
		BufferedImage img = new BufferedImage(component.getWidth(),
				component.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		component.paintAll(g);
		g.dispose();
		return img;
	}

	public static BufferedImage createImageNotVisibleComponent(
			Component component, Container container, Rectangle rectangle) {
		BufferedImage img = new BufferedImage(rectangle.width,
				rectangle.height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		SwingUtilities.paintComponent(g, component, container, rectangle);
		g.dispose();
		return img;
	}

	public static BufferedImage createReflection(BufferedImage image) {

		int height = image.getHeight();
		int reflectionOffset = 2;
		int reflectionX = 0;
		int reflectionY = -height - height - reflectionOffset;
		int reflectionHeight = 20;
		float reflectionAlphaBegin = 0.5f;
		float reflectionAlphaEnd = 0.0f;

		BufferedImage result = new BufferedImage(image.getWidth(), height
				+ reflectionOffset + reflectionHeight,
				BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2 = result.createGraphics();

		// Paint original image
		g2.drawImage(image, 0, 0, null);

		// Paints reflection
		g2.scale(1.0, -1.0); // horizontal flip

		g2.drawImage(image, reflectionX, reflectionY, null);
		g2.scale(1.0, -1.0); // again horizontal flip: back to original

		// Move the origin of the clone
		g2.translate(0, height);

		// Creates the alpha mask
		GradientPaint mask;
		mask = new GradientPaint(0, 0, new Color(1.0f, 1.0f, 1.0f,
				reflectionAlphaBegin), 0, reflectionHeight, new Color(1.0f,
				1.0f, 1.0f, reflectionAlphaEnd));
		g2.setPaint(mask);

		// Sets the alpha composite
		g2.setComposite(AlphaComposite.DstIn);

		// Paints the mask
		g2.fillRect(0, 0, image.getWidth(), height);
		g2.dispose();

		return result;

	}

	public static AbstractButton addReflection(AbstractButton button) {
		BufferedImage imageComponent = createImageNotVisibleComponent(button,
				new Container(), new Rectangle(button.getPreferredSize()));
		BufferedImage image = createReflection(imageComponent);
		button.setIcon(new ImageIcon(image));
		button.setText("");
		button.setBorderPainted(false);
		return button;
	}

	private static void createAndShowTestGUI() {

		JPanel container = new JPanel();
		container.setLayout(new BorderLayout());

		AbstractButton button1 = addReflection(new JButton("Push1"));
		AbstractButton button2 = addReflection(new JButton("Push2"));
		AbstractButton button3 = addReflection(new JButton("Push3"));

		Box buttonBox = Box.createHorizontalBox();
		buttonBox.add(button1);
		buttonBox.add(button2);
		buttonBox.add(button3);

		container.add(buttonBox, BorderLayout.SOUTH);

		TestFrame frame = new TestFrame(container);
		frame.setSize(300, 400);
		frame.setVisible(true);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Runnable doCreateAndShowGUI = new Runnable() {
			public void run() {
				createAndShowTestGUI();
			}
		};
		SwingUtilities.invokeLater(doCreateAndShowGUI);

	}

}
