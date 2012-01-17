package de.picman.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXTitledPanel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.picman.backend.control.ApplicationControl;
import de.picman.backend.db.Picture;
import de.picman.gui.components.MainFrame;
import de.picman.gui.components.PictureClipboard;
import de.picman.gui.components.SearchResult;
import de.picman.gui.main.GUIControl;
import de.picman.gui.providers.ActionProvider;
import de.picman.gui.renderer.PictureDisplayCellRenderer;
import de.picman.gui.renderer.WorkerThreadControl;

public class PictureDisplayPanel extends JXTitledPanel {

	private static final long serialVersionUID = 7935754925470038801L;
	private JXList pictureList;
	private JLabel resultsCounterLbL;
	private JLabel startIndexLbL;
	private JLabel endIndexLbL;
	private JButton nextResultSetBtn;
	private JButton previousResultSetBtn;
	private JButton addSelectedPictureToClipboardButton;

	public PictureDisplayPanel() {

		initComponent();

	}

	private void initComponent() {
		setLayout(new BorderLayout());
		FormLayout layout = new FormLayout(
		// 1 2 3 4 5
				"fill:default:grow", // columns
				"pref, fill:default:grow, pref "); // rows

		CellConstraints cc = new CellConstraints();

		PanelBuilder builder = new PanelBuilder(layout);
		builder.add(new JScrollPane(getPictureList()), cc.xy(1, 2));
		builder.add(getControlPanel(), cc.xy(1, 3));

		add(builder.getPanel(), BorderLayout.CENTER);

		/*
		 * Actionlistener zum selektieretem Bild:
		 */
		getPictureList().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					addSelectedPictureToClipBoardAction();
				} else if (e.getClickCount() == 1) {
					updateSelectedPictureAction();
				}
			}
		});

		getPictureList().addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				updateSelectedPictureAction();
			}
		});

		setTitle("Suchergebnisse");

		setBorder(BorderFactory.createEmptyBorder());
	}

	protected JButton getAddSelectedPictureToClipboardButton() {
		if (addSelectedPictureToClipboardButton == null) {
			addSelectedPictureToClipboardButton = new JButton();
			addSelectedPictureToClipboardButton
					.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							addSelectedPictureToClipBoardAction();
						}
					});
			addSelectedPictureToClipboardButton.setIcon(GUIControl.get()
					.getImageIcon("icon.arrow.down"));
			addSelectedPictureToClipboardButton
					.setToolTipText("Ausgewähltes Bild zur Ablage hinzufügen");
			addSelectedPictureToClipboardButton.setEnabled(false);
		}
		return addSelectedPictureToClipboardButton;
	}

	protected JLabel getStartIndexLbL() {
		if (startIndexLbL == null) {
			startIndexLbL = new JLabel("0-0");
			startIndexLbL.setForeground(new Color(240, 240, 240));
			startIndexLbL.setFont(getDefaultFont());
			startIndexLbL.setHorizontalTextPosition(JLabel.CENTER);
		}
		return startIndexLbL;
	}

	protected JLabel getEndIndexLbL() {
		if (endIndexLbL == null) {
			endIndexLbL = new JLabel("0");
			endIndexLbL.setForeground(new Color(240, 240, 240));
			endIndexLbL.setFont(getDefaultFont());
			endIndexLbL.setHorizontalTextPosition(JLabel.CENTER);

		}
		return endIndexLbL;
	}

	protected JLabel getResultsCounterLbL() {
		if (resultsCounterLbL == null) {
			resultsCounterLbL = new JLabel("0");
			resultsCounterLbL.setForeground(new Color(240, 240, 240));
			resultsCounterLbL.setFont(getDefaultFont());
			resultsCounterLbL.setHorizontalTextPosition(JLabel.CENTER);

		}
		return resultsCounterLbL;
	}

	protected JButton getNextResultSetBtn() {
		if (nextResultSetBtn == null) {
			nextResultSetBtn = new JButton(GUIControl.get().getImageIcon(
					"icon.forward"));
			nextResultSetBtn.setHorizontalTextPosition(JButton.LEFT);
			nextResultSetBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					SearchResult.getInstance().next();
				}
			});
		}
		nextResultSetBtn.setEnabled(false);
		return nextResultSetBtn;
	}

	protected JButton getPreviousResultSetBtn() {
		if (previousResultSetBtn == null) {
			previousResultSetBtn = new JButton(GUIControl.get().getImageIcon(
					"icon.back"));
			previousResultSetBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					SearchResult.getInstance().previous();
				}
			});
		}
		previousResultSetBtn.setEnabled(false);
		return previousResultSetBtn;
	}

	private JPanel getControlPanel() {
		FormLayout layout = new FormLayout(
		// 1 2 4 5 6 7
				"pref,pref, 10 dlu, 40dlu, 120dlu, pref, fill:default:grow", // columns
				"pref"); // rows

		CellConstraints cc = new CellConstraints();

		PanelBuilder builder = new PanelBuilder(layout);
		builder.add(getPreviousResultSetBtn(), cc.xy(1, 1));
		builder.add(getNextResultSetBtn(), cc.xy(2, 1));
		builder.add(getStartIndexLbL(), cc.xy(4, 1));
		builder.add(getResultsCounterLbL(), cc.xy(5, 1));
		builder.add(getAddSelectedPictureToClipboardButton(), cc.xy(6, 1));
		return builder.getPanel();
	}

	private void updateSelectedPictureAction() {

		SwingWorker<Object, Void> worker = new SwingWorker<Object, Void>() {
			public Object doInBackground() {

				MainFrame.getInstance().lock();

				boolean enableAddBtn = getPictureList().getSelectedIndex() > 0;
				getAddSelectedPictureToClipboardButton().setEnabled(
						enableAddBtn);

				Picture pic = (Picture) getPictureList().getSelectedValue();

				if (pic == null) {
					return null;
				}

				MainFrame.getInstance().getPicturePropertiesPanel()
						.setPicture(pic);

				ActionProvider.getDeletePictureAction().setEnabled(
						!pic.isDeleted());
				ActionProvider.getRestorePictureAction().setEnabled(
						pic.isDeleted());

				ActionProvider.getDeletePictureAction().setEnabled(
						!ApplicationControl.getInstance().getCurrentUser()
								.isReadonly());

				return null;
			}

			public void done() {
				MainFrame.getInstance().unlock();
			}
		};

		SwingWorker<Object, Void> storedWorker = WorkerThreadControl
				.getInstance().getWorker();
		if (storedWorker != null) {
			storedWorker.cancel(true);
		}

		WorkerThreadControl.getInstance().setWorker(worker);

		worker.execute();

	}

	public Font getDefaultFont() {
		return new Font("Monospace", Font.BOLD, 14);
	}

	private void addSelectedPictureToClipBoardAction() {
		Picture pic = (Picture) pictureList.getSelectedValue();
		PictureClipboard.getInstance().addPicture(pic);
		ActionProvider.getDeletePictureAction().setEnabled(!pic.isDeleted());
		ActionProvider.getRestorePictureAction().setEnabled(pic.isDeleted());
	}

	public JList getPictureList() {
		if (pictureList == null) {
			pictureList = new JXList();
			DefaultListModel listModel = new DefaultListModel();
			pictureList.setModel(listModel);
			pictureList.setCellRenderer(new PictureDisplayCellRenderer());
			pictureList.setAutoscrolls(true);
			pictureList.setVisibleRowCount(5);
			pictureList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
			pictureList.setVisibleRowCount(-1);
			pictureList.setBackground(Color.DARK_GRAY);
			pictureList.setDragEnabled(true);
		}

		return pictureList;
	}

	public Picture getSelectedPicture() {
		return (Picture) getPictureList().getSelectedValue();
	}

	public DefaultListModel getListModel() {
		return (DefaultListModel) getPictureList().getModel();
	}

	public void refresh() {
		Picture[] pictures = SearchResult.getInstance().getCurrentResultFrame();
		getListModel().removeAllElements();
		if (getListModel() != null) {
			for (int i = 0; i < pictures.length; i++) {
				getListModel().addElement(pictures[i]);
			}
		}

		int startIndex = SearchResult.getInstance().getStartIndex();
		int endIndex = SearchResult.getInstance().getEndIndex();
		int indexStep = SearchResult.getInstance().getIndexStep();
		int searchResults = SearchResult.getInstance().getNumberOfResults() - 1;

		boolean enablePreviousBtn = pictures.length > 0 && startIndex != 0
				&& pictures.length <= indexStep;
		boolean enableNextBtn = pictures.length > 0 && !(endIndex < indexStep);
		boolean enableAddBtn = getPictureList().getSelectedIndex() > 0;
		getAddSelectedPictureToClipboardButton().setEnabled(enableAddBtn);

		getNextResultSetBtn().setEnabled(enableNextBtn);
		getPreviousResultSetBtn().setEnabled(enablePreviousBtn);
		getResultsCounterLbL().setText(" von " + searchResults);
		getStartIndexLbL().setText((startIndex + 1) + "-" + endIndex);

		if (pictures.length > 0) {
			getPictureList().setSelectedIndex(0);
			updateSelectedPictureAction();
		} else {
			MainFrame.getInstance().getPicturePropertiesPanel().reset();
		}
	}

}
