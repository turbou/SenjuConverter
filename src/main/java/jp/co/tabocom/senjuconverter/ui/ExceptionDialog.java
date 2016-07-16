package jp.co.tabocom.senjuconverter.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class ExceptionDialog extends Dialog {

    private String message;
    private String stackTrace;

    public ExceptionDialog(Shell parentShell, String message, String stackTrace) {
        super(parentShell);
        this.message = message;
        this.stackTrace = stackTrace;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);
        composite.setLayout(new GridLayout(1, false));
        Label messageLbl = new Label(composite, SWT.HORIZONTAL);
        messageLbl.setText(this.message);
        StyledText widget = new StyledText(composite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        widget.setLayoutData(new GridData(GridData.FILL_BOTH));
        widget.setMargins(5, 5, 10, 5);
        widget.setEditable(false);
        widget.setText(this.stackTrace);
        return composite;
    }

    @Override
    protected Point getInitialSize() {
        return new Point(720, 420);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("問題が発生しました！");
    }

    @Override
    protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
        if (id == IDialogConstants.CANCEL_ID) {
            return null;
        }
        return super.createButton(parent, id, label, defaultButton);
    }
}
