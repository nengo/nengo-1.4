package ca.nengo.ui.lib;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import ca.nengo.ui.lib.Style.NengoStyle;

/**
 * Customized split pane implementation which holds main and an auxillary
 * Container which can be hidden /shown.
 * 
 * @author Shu Wu
 */
public class AuxillarySplitPane extends JSplitPane {

    private static final long serialVersionUID = 1L;

    /**
     * TODO
     */
    public static final int MINIMUM_WIDTH = 300;

    /**
     * TODO
     */
    public static final int MINIMUM_HEIGHT = 200;

    private static int getJSplitPaneOrientation(Orientation orientation) {
        if (orientation == Orientation.Left || orientation == Orientation.Right) {
            return JSplitPane.HORIZONTAL_SPLIT;
        } else {
            return JSplitPane.VERTICAL_SPLIT;
        }
    }

    private int auxPanelSize;
    private final String auxTitle;
    private JPanel auxPanelWr;

    private Container mainPanel;

    private final Orientation orientation;

    /**
     * @param mainPanel TODO
     * @param auxPanel TODO
     * @param auxTitle TODO
     * @param orientation TODO
     */
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

    private JPanel createAuxPanelWrapper(final Container auxPanel, String title) {
        /*
         * Initialize auxillary panel
         */
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());
        leftPanel.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (auxPanel!=null) {
                    auxPanel.requestFocusInWindow();
                }
            }
        });

        NengoStyle.applyStyle(leftPanel);

        /*
         * Create auxillary panel's title bar
         */
        JPanel titleBar = new JPanel();
        titleBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        NengoStyle.applyStyle(titleBar);
        titleBar.setBackground(NengoStyle.COLOR_BACKGROUND2);
        titleBar.setOpaque(true);
        titleBar.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel(title);

        titleLabel.setFont(NengoStyle.FONT_BIG);
        NengoStyle.applyStyle(titleLabel);
        titleLabel.setBackground(NengoStyle.COLOR_BACKGROUND2);
        titleLabel.setOpaque(true);

        String hideButtonTxt = " << ";
        if (orientation == Orientation.Right) {
            hideButtonTxt = " >> ";
        }
        JLabel hideButton = new JLabel(hideButtonTxt);
        NengoStyle.applyStyle(hideButton);
        hideButton.setBackground(NengoStyle.COLOR_BACKGROUND2);
        hideButton.setOpaque(true);

        /*
         * Keep in this order, Swing puts items added first on top. We want the
         * button to be on top
         */
        titleBar.add(hideButton, BorderLayout.EAST);
        titleBar.add(titleLabel, BorderLayout.WEST);

        hideButton.addMouseListener(new HideButtonListener(hideButton));

        leftPanel.add(titleBar, BorderLayout.NORTH);

        /*
         * Create data viewer
         */
        leftPanel.setMinimumSize(new Dimension(MINIMUM_WIDTH, MINIMUM_HEIGHT));

        if (auxPanel != null) {
            NengoStyle.applyStyle(auxPanel);
            leftPanel.add(auxPanel, BorderLayout.CENTER);
        }

        return leftPanel;
    }

    private void init(Container auxillaryPanel) {
        NengoStyle.applyStyle(this);
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

    /**
     * @return TODO
     */
    public String getAuxTitle() {
        return auxTitle;
    }

    /**
     * @return TODO
     */
    public JPanel getAuxPaneWrapper() {
        return auxPanelWr;
    }

    /**
     * @return TODO
     */
    public boolean isAuxVisible() {
        return auxPanelWr.isVisible();
    }

    /**
     * @param auxPane TODO
     * @param title TODO
     */
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

    /**
     * @param isVisible TODO
     */
    public void setAuxVisible(boolean isVisible) {
        setAuxVisible(isVisible, false);

    }

    /**
     * @param isVisible TODO
     * @param resetDividerLocation TODO
     */
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
                auxPanelWr.requestFocus();
                auxPanelWr.setVisible(true);
            }
            auxPanelWr.requestFocusInWindow();
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

    /**
     * TODO
     */
    public enum Orientation {
        /**
         * TODO
         */
        Bottom,

        /**
         * TODO
         */
        Left,

        /**
         * TODO
         */
        Right
    }

    class HideButtonListener implements MouseListener {
        private Container hideButton;

        public HideButtonListener(Container hideButton) {
            super();
            this.hideButton = hideButton;
        }

        public void mouseClicked(MouseEvent e) {
            setAuxVisible(false);
        }

        public void mouseEntered(MouseEvent e) {
            hideButton.setBackground(NengoStyle.COLOR_FOREGROUND2);
        }

        public void mouseExited(MouseEvent e) {
            hideButton.setBackground(null);
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }

    }

}