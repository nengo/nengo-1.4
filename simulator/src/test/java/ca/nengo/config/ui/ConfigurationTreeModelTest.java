package ca.nengo.config.ui;

import ca.nengo.config.ConfigUtil;
import ca.nengo.config.JavaSourceParser;
import ca.nengo.math.impl.IndicatorPDF;
import ca.nengo.model.impl.NoiseFactory;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFrame;
import org.junit.Test;

public class ConfigurationTreeModelTest {
	@Test
	public void testNothing() {
	}

	public static void main(String[] args) {
        try {
            JavaSourceParser.addSource(new File("src/java/main"));

            final Object configurable = NoiseFactory.makeRandomNoise(1, new IndicatorPDF(0, 1));
            final JFrame frame = new JFrame("Tree Test");

            JButton button = new JButton("configure");
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    ConfigUtil.configure(frame, configurable);
                }
            });
            frame.getContentPane().add(button);

            frame.pack();
            frame.setVisible(true);

            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent arg0) {
                    System.exit(0);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
