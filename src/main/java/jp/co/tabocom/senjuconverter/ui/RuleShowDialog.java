package jp.co.tabocom.senjuconverter.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class RuleShowDialog extends Dialog {

    private StyledText widget;
    private String title;
    private String value;

    public RuleShowDialog(Shell parentShell, String title, String value) {
        super(parentShell);
        this.title = title;
        this.value = value;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);
        composite.setLayout(new GridLayout(1, false));
        FontRegistry fontRegistry = new FontRegistry(Display.getCurrent());
        fontRegistry.put("FONT", new FontData[] { new FontData("ＭＳ ゴシック", 10, SWT.NORMAL) });
        this.widget = new StyledText(composite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        this.widget.setLayoutData(new GridData(GridData.FILL_BOTH));
        this.widget.setMargins(5, 5, 10, 5);
        this.widget.setEditable(false);
        this.widget.setText(this.value);
        this.widget.setFont(fontRegistry.get("FONT"));
        return composite;
    }

    @Override
    protected Point getInitialSize() {
        return new Point(720, 640);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(this.title);
    }

    @Override
    protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
        if (id == IDialogConstants.CANCEL_ID) {
            return null;
        }
        return super.createButton(parent, id, label, defaultButton);
    }
}
