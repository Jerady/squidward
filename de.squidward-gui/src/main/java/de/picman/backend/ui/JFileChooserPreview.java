/*
 * Created on 05.07.2008
 *
 * SVN header information:
 *  $Author$
 *  $Rev$
 *  $Date$
 *  $Id$
 */
package de.picman.backend.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.UIManager;


/**
 * TODO: comment class
 * 
 * <br>
 * <br>
 * <b>Last change by $Author$ on $Date$</b>
 * 
 * @author http://www.javalobby.org/forums/thread.jspa?messageID=91844740
 * 
 * @version $Rev$
 * 
 */
public class JFileChooserPreview {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }

        final JFrame frame = new JFrame();
        JButton button = new JButton("Open File Chooser");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                PreviewPane previewPane = new PreviewPane();
                chooser.setAccessory(previewPane);
                chooser.addPropertyChangeListener(previewPane);
                chooser.showDialog(frame, "OK");
            }
        });
        frame.getContentPane().add(button);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

}
