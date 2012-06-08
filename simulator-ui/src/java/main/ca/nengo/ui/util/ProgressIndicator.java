package ca.nengo.ui.util;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JButton;

import org.python.core.PyException;
import org.python.core.PyFrame;
import org.python.core.PyObject;
import org.python.core.TraceFunction;
import org.python.core.ThreadState;
import org.python.core.Py;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;

import ca.nengo.sim.SimulatorEvent;
import ca.nengo.sim.SimulatorListener;


public class ProgressIndicator extends JPanel implements ActionListener, SimulatorListener {
	public static final long serialVersionUID=1;
	
	JProgressBar bar;
	JButton stop;

	ThreadState pythonThread=null;
	Thread javaThread=null;
	boolean interruptFlag=false;
	boolean isRunning=false;
	

	public ProgressIndicator() {
		setLayout(new BorderLayout());
		
		bar=new JProgressBar(0,100);
		bar.setStringPainted(true);
		bar.setString("");
		add(bar);
		
		stop=new JButton("stop");
		add(stop,BorderLayout.EAST);
		stop.addActionListener(this);

		setEnabled(false);
	}
	
	public void setText(String text) {
		bar.setString(text);
	}
	
	public void setEnabled(boolean enabled) {
		setVisible(enabled);
		isRunning=enabled;

		if (enabled) {
			pythonThread=null;	
			javaThread=null;
			interruptFlag=false;
			bar.setIndeterminate(true);
		}
	}
	
	public void setThread() {
		pythonThread=Py.getThreadState();
		javaThread=Thread.currentThread();
	}
	
	protected void interruptViaPython() {

		ThreadState ts=pythonThread;
		TraceFunction breaker=new BreakTraceFunction();
        TraceFunction oldTrace = ts.tracefunc;
        ts.tracefunc = breaker;
        if (ts.frame != null)
            ts.frame.tracefunc = breaker;
        ts.tracefunc = oldTrace;
	}
	
	public void interrupt() {
		bar.setString("Attempting to quit (will force quit in 3 seconds)");
		
		interruptFlag=true;
		if (pythonThread!=null) interruptViaPython();
		
		new java.util.Timer().schedule(
		        new java.util.TimerTask() {
		            @Override
		            public void run() {
		            	if (isRunning)
		            		javaThread.stop();
		            }
		        }, 
		        3000 
		);
	}
	
	public void actionPerformed(ActionEvent e) {
		interrupt();
	}

	public void processEvent(SimulatorEvent event) {
		int value=(int)(100*event.getProgress());
		if (value!=bar.getValue()) {
			bar.setString("Simulation progress: "+value+"%");
			bar.setIndeterminate(false);
			bar.setMaximum(100);
			bar.setValue(value);
		}
		if (interruptFlag) event.setInterrupt(true);
	}
	

}


class ScriptInterruptException extends RuntimeException {
    private static final long serialVersionUID = 1L;
}

class BreakTraceFunction extends TraceFunction {
    private void doBreak(PyFrame frame) {
        throw new ScriptInterruptException();
    }
    public TraceFunction traceCall(PyFrame frame) {
        doBreak(frame);
        return null;
    }
    public TraceFunction traceReturn(PyFrame frame, PyObject ret) {
        doBreak(frame);
        return null;
    }
    public TraceFunction traceLine(PyFrame frame, int line) {
        doBreak(frame);
        return null;
    }
    public TraceFunction traceException(PyFrame frame, PyException exc) {
        doBreak(frame);
        return null;
    }
}

