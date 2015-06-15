import java
import javax
from javax.swing import (
    ImageIcon, JButton, JComboBox, JFileChooser, JFrame, JLabel, JPanel,
    JSlider, JSpinner, SpinnerNumberModel)
from javax.swing.event import ChangeListener
from java.awt import BorderLayout, Color, RenderingHints
from java.awt.event import ActionListener

from ca.nengo.model import SimulationMode
import timeview.data
import os

# for save_pdf
import sys
if 'lib/itextpdf-5.3.4.jar' not in sys.path:
    sys.path.append('lib/itextpdf-5.3.4.jar')

class Icon:
    pass

class ShadedIcon:
    pass

parent = os.path.dirname(__file__)
parent = parent[:parent.index("python")]
for name in 'pause play configure end start backward forward restart arrowup arrowdown save restore refresh data pdf'.split():
    setattr(Icon, name, ImageIcon(parent + ('python/images/%s.png' % name)))
    setattr(ShadedIcon, name, ImageIcon(parent + ('python/images/%s-pressed.png' % name)))

class TimeControl(JPanel, ChangeListener, ActionListener):
    def __init__(self, view):
        JPanel.__init__(self)
        self.view = view
        self.background = Color.white
        self.config_panel_height = 60

        mainPanel = JPanel(background=self.background, layout=BorderLayout())
        mainPanel.border = self.RoundedBorder()
        configPanel = JPanel(background=self.background, visible=False)

        self.layout = BorderLayout()
        self.add(mainPanel, BorderLayout.NORTH)
        self.add(configPanel, BorderLayout.SOUTH)

        self.config_button = JButton(Icon.arrowdown, rolloverIcon=ShadedIcon.arrowdown, toolTipText='configure', actionPerformed=self.configure, borderPainted=False, focusPainted=False, contentAreaFilled=False)
        self.add(self.config_button)

        self.configPanel = configPanel

        self.slider = JSlider(0, 1, 0, background=self.background)
        self.slider.snapToTicks = True
        mainPanel.add(self.slider)
        self.slider.addChangeListener(self)

        self.min_time = JLabel(' 0.0000 ', opaque=True, background=self.background)
        self.max_time = JLabel(' 0.0000 ', opaque=True, background=self.background)

        self.left_panel = JPanel(background=self.background)
        self.left_panel.add(JButton(Icon.restart, rolloverIcon=ShadedIcon.restart, toolTipText='restart', actionPerformed=self.start, borderPainted=False, focusPainted=False, contentAreaFilled=False))
        self.left_panel.add(self.min_time)
        self.left_panel.add(JButton(icon=Icon.start, rolloverIcon=ShadedIcon.start, toolTipText='jump to beginning', actionPerformed=lambda x: self.slider.setValue(self.slider.minimum), borderPainted=False, focusPainted=False, contentAreaFilled=False))

        self.right_panel = JPanel(background=self.background)
        self.right_panel.add(JButton(icon=Icon.end, rolloverIcon=ShadedIcon.end, toolTipText='jump to end', actionPerformed=lambda x: self.slider.setValue(self.slider.maximum), borderPainted=False, focusPainted=False, contentAreaFilled=False))
        self.right_panel.add(self.max_time)
        self.playpause_button = JButton(Icon.play, actionPerformed=self.pause, rolloverIcon=ShadedIcon.play, toolTipText='continue', borderPainted=False, focusPainted=False, contentAreaFilled=False)
        self.right_panel.add(self.playpause_button)

        mainPanel.add(self.left_panel, BorderLayout.WEST)
        mainPanel.add(self.right_panel, BorderLayout.EAST)

        pdf = JPanel(layout=BorderLayout(), opaque=False)
        pdf.add(JButton(Icon.pdf, rolloverIcon=ShadedIcon.pdf, toolTipText='save pdf', actionPerformed=self.save_pdf, borderPainted=False, focusPainted=False, contentAreaFilled=False))
        pdf.add(JLabel('pdf', horizontalAlignment=javax.swing.SwingConstants.CENTER), BorderLayout.NORTH)
        pdf.maximumSize = pdf.preferredSize
        configPanel.add(pdf)

        self.data = JPanel(layout=BorderLayout(), opaque=False)
        self.data.add(JButton(Icon.data, rolloverIcon=ShadedIcon.data, toolTipText='examine data', actionPerformed=self.show_data, borderPainted=False, focusPainted=False, contentAreaFilled=False))
        self.data.add(JLabel('data', horizontalAlignment=javax.swing.SwingConstants.CENTER), BorderLayout.NORTH)
        self.data.maximumSize = self.data.preferredSize
        configPanel.add(self.data)

        mode = JPanel(layout=BorderLayout(), opaque=False)
        cb = JComboBox(['default', 'rate', 'direct'])
        if self.view.network.mode in [SimulationMode.DEFAULT, SimulationMode.PRECISE]:
            cb.setSelectedIndex(0)
        elif self.view.network.mode in [SimulationMode.RATE]:
            cb.setSelectedIndex(1)
        elif self.view.network.mode in [SimulationMode.DIRECT, SimulationMode.APPROXIMATE]:
            cb.setSelectedIndex(2)
        cb.addActionListener(self)
        self.mode_combobox = cb
        mode.add(cb)
        mode.add(JLabel('mode'), BorderLayout.NORTH)
        mode.maximumSize = mode.preferredSize
        configPanel.add(mode)

        dt = JPanel(layout=BorderLayout(), opaque=False)
        cb = JComboBox(['0.001', '0.0005', '0.0002', '0.0001'])
        cb.setSelectedIndex(0)
        self.view.dt = float(cb.getSelectedItem())
        cb.addActionListener(self)
        self.dt_combobox = cb
        dt.add(cb)
        dt.add(JLabel('time step'), BorderLayout.NORTH)
        dt.maximumSize = dt.preferredSize
        configPanel.add(dt)

        rate = JPanel(layout=BorderLayout(), opaque=False)
        self.rate_combobox = JComboBox(['fastest', '1x', '0.5x', '0.2x', '0.1x', '0.05x', '0.02x', '0.01x', '0.005x', '0.002x', '0.001x'])
        self.rate_combobox.setSelectedIndex(4)
        self.view.set_target_rate(self.rate_combobox.getSelectedItem())
        self.rate_combobox.addActionListener(self)
        rate.add(self.rate_combobox)
        rate.add(JLabel('speed'), BorderLayout.NORTH)
        rate.maximumSize = rate.preferredSize
        configPanel.add(rate)

        spin1 = JPanel(layout=BorderLayout(), opaque=False)
        self.record_time_spinner = JSpinner(SpinnerNumberModel((self.view.timelog.tick_limit - 1) * self.view.dt, 0.1, 100, 1), stateChanged=self.tick_limit)
        spin1.add(self.record_time_spinner)
        spin1.add(JLabel('recording time'), BorderLayout.NORTH)
        spin1.maximumSize = spin1.preferredSize
        configPanel.add(spin1)

        spin2 = JPanel(layout=BorderLayout(), opaque=False)
        self.filter_spinner = JSpinner(SpinnerNumberModel(self.view.tau_filter, 0, 0.5, 0.01), stateChanged=self.tau_filter)
        spin2.add(self.filter_spinner)
        spin2.add(JLabel('filter'), BorderLayout.NORTH)
        spin2.maximumSize = spin2.preferredSize
        configPanel.add(spin2)

        spin3 = JPanel(layout=BorderLayout(), opaque=False)
        self.time_shown_spinner = JSpinner(SpinnerNumberModel(self.view.time_shown, 0.01, 50, 0.1), stateChanged=self.time_shown)
        spin3.add(self.time_shown_spinner)
        spin3.add(JLabel('time shown'), BorderLayout.NORTH)
        spin3.maximumSize = spin3.preferredSize
        configPanel.add(spin3)

        spin4 = JPanel(layout=BorderLayout(), opaque=False)
        self.freq_spinner = JSpinner(SpinnerNumberModel(1000.0/self.view.data_update_period, 1, 50, 1), stateChanged=self.update_frequency)
        spin4.add(self.freq_spinner)
        spin4.add(JLabel('freq (Hz)'), BorderLayout.NORTH)
        spin4.maximumSize = spin4.preferredSize
        configPanel.add(spin4)

        layout = JPanel(layout=BorderLayout(), opaque=False)
        layout.add(JButton(icon=Icon.save, rolloverIcon=ShadedIcon.save, actionPerformed=self.save, borderPainted=False, focusPainted=False, contentAreaFilled=False, margin=java.awt.Insets(0, 0, 0, 0), toolTipText='save layout'), BorderLayout.WEST)
        layout.add(JButton(icon=Icon.restore, rolloverIcon=ShadedIcon.restore, actionPerformed=self.restore, borderPainted=False, focusPainted=False, contentAreaFilled=False, margin=java.awt.Insets(0, 0, 0, 0), toolTipText='restore layout'), BorderLayout.EAST)

        layout.add(JLabel('layout', horizontalAlignment=javax.swing.SwingConstants.CENTER), BorderLayout.NORTH)
        layout.maximumSize = layout.preferredSize
        configPanel.add(layout)

        configPanel.setPreferredSize(java.awt.Dimension(20, self.config_panel_height))
        configPanel.visible = False

        for c in [dt, rate, spin1, spin2, spin3]:
            c.border = javax.swing.border.EmptyBorder(0, 10, 0, 10)

    def show_data(self, event):
        frame = JFrame('%s Data' % self.view.network.name)
        frame.visible = True
        frame.add(timeview.data.DataPanel(self.view))
        frame.size = (500, 600)

    def forward_one_frame(self, event):
        self.slider.setValue(self.slider.value + 1)

    def backward_one_frame(self, event):
        self.slider.setValue(self.slider.value - 1)

    def set_max_time(self, maximum):
        self.slider.maximum = maximum
        self.max_time.text = ' %1.4f ' % (self.view.dt * maximum)

    def set_min_time(self, minimum):
        self.slider.minimum = minimum
        self.min_time.text = ' %1.4f ' % (self.view.dt * minimum)

    def stateChanged(self, event):
        self.view.current_tick = self.slider.value
        self.view.area.repaint()

    def start(self, event):
        self.view.restart = True

    def configure(self, event):
        view_state = self.view.frame.getExtendedState()
        if self.configPanel.visible:
            self.view.frame.setSize(self.view.frame.width, self.view.frame.height - self.config_panel_height)
            self.configPanel.visible = False
            self.config_button.icon = Icon.arrowdown
            self.config_button.rolloverIcon = ShadedIcon.arrowdown
            self.config_button.toolTipText = 'configure'
        else:
            if(view_state & self.view.frame.MAXIMIZED_BOTH == self.view.frame.MAXIMIZED_BOTH):
                self.view.frame.setSize(self.view.frame.width, self.view.frame.height)
            else:
                self.view.frame.setSize(self.view.frame.width, self.view.frame.height + self.config_panel_height)
            self.configPanel.visible = True
            self.config_button.icon = Icon.arrowup
            self.config_button.rolloverIcon = ShadedIcon.arrowup
            self.config_button.toolTipText = 'hide configuration'
        self.view.frame.setExtendedState(view_state)
        self.view.frame.layout.layoutContainer(self.view.frame)
        self.layout.layoutContainer(self)
        self.view.frame.layout.layoutContainer(self.view.frame)
        self.layout.layoutContainer(self)
        self.view.frame.layout.layoutContainer(self.view.frame)
        self.view.frame.repaint()

    def pause(self, event):
        self.view.paused = not self.view.paused
        if self.view.paused:
            self.playpause_button.icon = Icon.play
            self.playpause_button.rolloverIcon = ShadedIcon.play
            self.playpause_button.toolTipText = 'continue'
        else:
            self.playpause_button.icon = Icon.pause
            self.playpause_button.rolloverIcon = ShadedIcon.pause
            self.playpause_button.toolTipText = 'pause'

    def tau_filter(self, event):
        self.view.tau_filter = float(event.source.value)
        self.view.area.repaint()

    def time_shown(self, event):
        self.view.time_shown = float(event.source.value)
        self.view.area.repaint()

    def actionPerformed(self, event):
        dt = float(self.dt_combobox.getSelectedItem())
        if dt != self.view.dt:
            self.view.dt = dt
            self.record_time_spinner.value = (self.view.timelog.tick_limit - 1) * self.view.dt
            self.dt_combobox.repaint()
            self.view.restart = True
        self.view.set_target_rate(self.rate_combobox.getSelectedItem())

        if self.mode_combobox is not None:
            mode = self.mode_combobox.getSelectedItem()
            if mode == 'default':
                requested = SimulationMode.DEFAULT
            elif mode == 'rate':
                requested = SimulationMode.RATE
            elif mode == 'direct':
                requested = SimulationMode.DIRECT
            if requested != self.view.network.mode:
                self.view.requested_mode = requested

    def tick_limit(self, event):
        self.view.timelog.tick_limit = int(event.source.value / self.view.dt) + 1

    def update_frequency(self, event):
        self.view.data_update_period = 1000.0 / event.source.value

    def save(self, event):
        self.view.save()

    def restore(self, event):
        self.view.restore()

    def save_pdf(self, event):
        from com.itextpdf.text.pdf import PdfWriter
        from com.itextpdf.text import Document

        fileChooser = JFileChooser()
        fileChooser.setSelectedFile(java.io.File('%s.pdf' % self.view.network.name))
        if fileChooser.showSaveDialog(self) == JFileChooser.APPROVE_OPTION:
            f = fileChooser.getSelectedFile()

            doc = Document()
            writer = PdfWriter.getInstance(doc, java.io.FileOutputStream(f))
            doc.open()
            cb = writer.getDirectContent()
            w = self.view.area.size.width
            h = self.view.area.size.height
            pw = 550
            ph = 800
            tp = cb.createTemplate(pw, ph)
            g2 = tp.createGraphicsShapes(pw, ph)
            at = java.awt.geom.AffineTransform()
            s = min(float(pw) / w, float(ph) / h)
            at.scale(s, s)
            g2.transform(at)
            self.view.area.pdftemplate = tp, s
            self.view.area.paint(g2)
            self.view.area.pdftemplate = None
            g2.dispose()

            cb.addTemplate(tp, 20, 0)
            doc.close()

    class RoundedBorder(javax.swing.border.AbstractBorder):
        def __init__(self):
            self.color = Color(0.7, 0.7, 0.7)

        def getBorderInsets(self, component):
            return java.awt.Insets(5, 5, 5, 5)

        def paintBorder(self, c, g, x, y, width, height):
            g.color = self.color
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            g.drawRoundRect(x, y, width - 1, height - 1, 10, 10)
