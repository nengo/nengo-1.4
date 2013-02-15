package ca.nengo.ui.util;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JButton;

import java.util.Timer;
import java.util.TimerTask;

import org.python.core.PyException;
import org.python.core.PyFrame;
import org.python.core.PyObject;
import org.python.core.TraceFunction;
import org.python.core.ThreadState;
import org.python.core.Py;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.awt.Insets;

import ca.nengo.sim.SimulatorEvent;
import ca.nengo.sim.SimulatorListener;
import ca.nengo.ui.NengoGraphics;


public class ProgressIndicator extends JPanel implements ActionListener, SimulatorListener {
	public static final long serialVersionUID=1;
	
	JProgressBar bar;
	JButton stop;
	
	String text;

	ThreadState pythonThread=null;
	Thread javaThread=null;
	
	Timer timer=null;
	long timerStart;

	boolean isRunning=false;
	boolean interruptFlag;
	
	int percentage=-1;
	

	public ProgressIndicator() {
		setLayout(new BorderLayout());
		
		bar=new JProgressBar(0,100);
		bar.setStringPainted(true);
		bar.setString("");
		bar.setIndeterminate(true);				
		add(bar);
		
		stop=new JButton();
		stop.setMargin(new Insets(0,0,0,0));
		stop.setIcon(new ImageIcon("python/images/stop.png"));
		add(stop,BorderLayout.EAST);
		stop.addActionListener(this);

		setVisible(false);
		
		timer = new Timer();
	}
	
	void updateBarString() {
		String bar=this.text;
		if (isRunning) {
			long delta=(System.currentTimeMillis()-timerStart)/1000;
			bar+=" ";
			if (delta>60*60) {
				
				bar+=String.format("%02d:", delta/(60*60));
			}
			bar+=String.format("%02d:%02d", (delta%(60*60))/60,delta%60);
		}		
		if (percentage>=0) {
			bar+=String.format(" (%02d)%%", percentage);
		}
		if (interruptFlag) {
			bar="[Attempting to stop] "+bar;
		}
		this.bar.setString(bar);
	}
	
	public void start(String text) {
		timerStart=System.currentTimeMillis();
		this.text=text;
		this.isRunning=true;
		timer.schedule(
		        new TimerTask() {
					@Override
		            public void run() {
						if (!isRunning) {
							cancel();
							return;
						}
						if (!isVisible())
							setVisible(true);
						
						updateBarString();
		            }
		        }, 
		        1000,1000);		
	}
	
	public void stop() {
		if (!isRunning) return;
		
		this.setVisible(false);
		isRunning=false;
		pythonThread=null;	
		javaThread=null;
		bar.setIndeterminate(true);		
		percentage=-1;
		
		// TODO: figure out why this needs a 100ms delay before scrolling to the bottom
		//        (without this delay, the console ends up a few lines above the bottom)
		timer.schedule(
		        new TimerTask() {
					@Override
		            public void run() {
		        		NengoGraphics.getInstance().getScriptConsole().scrollToBottom();
		            }
		        }, 
		        100 
		);
		
	}
	
	
	
	public void setText(String text) {
		this.text=text;
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
		
		
		interruptFlag=true;
		updateBarString();
		if (pythonThread!=null) interruptViaPython();
		timer.schedule(
		        new TimerTask() {
		            @SuppressWarnings("deprecation")
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
		if (!isRunning) return;
		
		percentage=(int)(100*event.getProgress());
		
		if (percentage!=bar.getValue()) {
			bar.setIndeterminate(false);
			bar.setMaximum(100);
			bar.setValue(percentage);
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

