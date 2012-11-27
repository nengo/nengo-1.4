package ca.nengo.ui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.UIManager;

import ca.nengo.ui.configurable.matrixEditor.CouplingMatrix;
import ca.nengo.ui.configurable.matrixEditor.CouplingMatrixImpl;
import ca.nengo.ui.configurable.matrixEditor.MatrixEditor;

public class WidgetTest {
    public static void main(String args[]) {
        try {
            //Tell the UIManager to use the platform look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception e) { /*Do nothing*/ }

        CouplingMatrix matrix = new CouplingMatrixImpl(5, 3);
        MatrixEditor editor = new MatrixEditor(matrix);

        try {
            JFrame frame = new JFrame("test");
            frame.getContentPane().add(editor);

            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });

            frame.pack();
            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
