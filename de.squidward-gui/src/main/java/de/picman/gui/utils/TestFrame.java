package de.picman.gui.utils;

import java.awt.BorderLayout;

import javax.swing.JComponent;

import org.jdesktop.swingx.JXFrame;

/**
 * @author Jens Deters, Keynote SIGOS GmbH, Sept, 2010
 */
public class TestFrame extends JXFrame {

  private static final long serialVersionUID = 1L;

  public TestFrame(JComponent component) {
    setLayout(new BorderLayout());
    add(component, BorderLayout.CENTER);
    setTitle(component.getClass().getSimpleName());
    setLocationByPlatform(true);
    pack();
    setDefaultCloseOperation(JXFrame.EXIT_ON_CLOSE);
    setVisible(true);
  }

}
