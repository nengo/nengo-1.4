package ca.shu.ui.lib;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import ca.shu.ui.lib.Style.Style;

/**
 * Customized split pane implementation which holds main and an auxillary
 * Container which can be hidden /shown.
 * 
 * @author Shu Wu
 */
public class AuxillarySplitPane extends JSplitPane {

	private static final long serialVersionUID = 1L;

	private static int getJSplitPaneOrientation(Orientation orientation) {
		if (orientation == Orientation.Left || orientation == Orientation.Right) {
			return JSplitPane.HORIZONTAL_SPLIT;
		} else {
			return JSplitPane.VERTICAL_SPLIT;
		}
	}

	private int auxPanelSize;
	private Container auxPanelWr;
	private final String auxTitle;

	private Container mainPanel;

	private final Orientation orientation;

	public AuxillarySplitPane(Container mainPanel, Container auxPanel, String auxTitle,
			Orientation orientation) {
		super(getJSplitPaneOrientation(orientation));
		this.mainPanel = mainPanel;
		this.auxTitle = auxTitle;
		this.orientation = orientation;

		this.addComponentListener(new ComponentListener() {
			public void componentHidden(ComponentEvent e) {
			}

			public void componentMoved(ComponentEvent e) {
			}

			public void componentResized(ComponentEvent e) {
				updateAuxSize();
			}

			public void componentShown(ComponentEvent e) {
			}

		});

		init(auxPanel);
	}

	private JPanel createAuxPanelWrapper(Container auxPanel, String title) {
		/*
		 * Initialize auxillary panel
		 */
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BorderLayout());

		Style.applyStyle(leftPanel);

		/*
		 * Create auxillary panel's title bar
		 */
		JPanel titleBar = new JPanel();
		titleBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		Style.applyStyle(titleBar);
		titleBar.setBackground(Style.COLOR_BACKGROUND2);
		titleBar.setOpaque(true);
		titleBar.setLayout(new BorderLayout());

		JLabel titleLabel = new JLabel(title);

		titleLabel.setFont(Style.FONT_BIG);
		Style.applyStyle(titleLabel);
		titleLabel.setBackground(Style.COLOR_BACKGROUND2);
		titleLabel.setOpaque(true);

		String hideButtonTxt = " << ";
		if (orientation == Orientation.Right) {
			hideButtonTxt = " >> ";
		}
		JLabel hideButton = new JLabel(hideButtonTxt);
		Style.applyStyle(hideButton);
		hideButton.setBackground(Style.COLOR_BACKGROUND2);
		hideButton.setOpaque(true);

		/*
		 * Keep in this order, Swing puts items added first on top. We want the
		 * button to be on top
		 */
		titleBar.add(hideButton, BorderLayout.EAST);
		titleBar.add(titleLabel, BorderLayout.WEST);

		hideButton.addMouseListener(new HideDataViewerListener());

		leftPanel.add(titleBar, BorderLayout.NORTH);

		/*
		 * Create data viewer
		 */
		leftPanel.setMinimumSize(new Dimension(200, 200));

		if (auxPanel != null) {
			Style.applyStyle(auxPanel);
			leftPanel.add(auxPanel, BorderLayout.CENTER);
		}

		return leftPanel;
	}

	private void init(Container auxillaryPanel) {
		Style.applyStyle(this);
		setOneTouchExpandable(true);
		setBorder(null);

		setAuxPane(auxillaryPanel, auxTitle);

		setAuxVisible(false);
	}

	private void updateAuxSize() {
		if (orientation == Orientation.Bottom) {
			setDividerLocation(getHeight() - auxPanelSize);
		} else if (orientation == Orientation.Right) {
			setDividerLocation(getWidth() - auxPanelSize);
		}
	}

	public String getAuxTitle() {
		return auxTitle;
	}

	public boolean isAuxVisible() {
		if (getDividerLocation() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public void setAuxPane(Container auxPane, String title) {
		this.auxPanelWr = createAuxPanelWrapper(auxPane, title);

		if (auxPane != null) {
			setAuxVisible(true, true);
		} else {
			setAuxVisible(false);
		}

		if (orientation == Orientation.Left) {
			setRightComponent(mainPanel);
			setLeftComponent(auxPanelWr);
		} else if (orientation == Orientation.Right) {
			setRightComponent(auxPanelWr);
			setLeftComponent(mainPanel);
		} else if (orientation == Orientation.Bottom) {
			setTopComponent(mainPanel);
			setBottomComponent(auxPanelWr);
		}
	}

	public void setAuxVisible(boolean isVisible) {
		setAuxVisible(isVisible, false);

	}

	public void setAuxVisible(boolean isVisible, boolean resetDividerLocation) {
		if (isVisible) {
			int auxSize = this.getDividerLocation();

			double minAuxSize;
			if (orientation == Orientation.Bottom) {
				auxSize = getHeight() - auxSize;
				minAuxSize = auxPanelWr.getMinimumSize().getHeight();
			} else if (orientation == Orientation.Right) {
				auxSize = getWidth() - auxSize;
				minAuxSize = auxPanelWr.getMinimumSize().getWidth();
			} else {
				minAuxSize = auxPanelWr.getMinimumSize().getWidth();
			}

			if (auxSize < minAuxSize || resetDividerLocation) {
				if (orientation == Orientation.Bottom) {
					setDividerLocation((int) (getHeight() - minAuxSize));

				} else if (orientation == Orientation.Right) {
					setDividerLocation((int) (getWidth() - minAuxSize));
				} else {
					setDividerLocation((int) minAuxSize);
				}

			}
			setDividerSize(2);
			if (!auxPanelWr.isVisible()) {
				auxPanelWr.setVisible(true);
			}
		} else {
			auxPanelWr.setVisible(false);

			// setDividerLocation(0);
			setDividerSize(0);
		}
	}

	@Override
	public void setDividerLocation(int location) {
		super.setDividerLocation(location);
		if (orientation == Orientation.Bottom) {
			auxPanelSize = getHeight() - location;
		} else if (orientation == Orientation.Right) {
			auxPanelSize = getWidth() - location;
		}
	}

	public enum Orientation {
		Bottom, Left, Right
	}

	class HideDataViewerListener implements MouseListener {

		public void mouseClicked(MouseEvent e) {
			setAuxVisible(false);
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}

	}

}