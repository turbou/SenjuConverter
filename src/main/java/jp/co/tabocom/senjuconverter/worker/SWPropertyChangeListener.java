package jp.co.tabocom.senjuconverter.worker;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.SwingWorker;

import org.eclipse.swt.widgets.Shell;

public abstract class SWPropertyChangeListener implements PropertyChangeListener {

    private Shell shell;

    public SWPropertyChangeListener(Shell shell) {
        this.shell = shell;
    }

    public abstract void started() throws Exception;

    public abstract void done() throws Exception;

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if ("state".equals(evt.getPropertyName())) {
            shell.getDisplay().asyncExec(new Runnable() {
                public void run() {
                    SwingWorker.StateValue state = (SwingWorker.StateValue) evt.getNewValue();
                    try {
                        if (SwingWorker.StateValue.STARTED == state) {
                            started();
                        } else if (SwingWorker.StateValue.DONE == state) {
                            done();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
