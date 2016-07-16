package jp.co.tabocom.senjuconverter.ui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jp.co.tabocom.senjuconverter.Main;
import jp.co.tabocom.senjuconverter.model.RuleDefinition;
import jp.co.tabocom.senjuconverter.model.Site;
import jp.co.tabocom.senjuconverter.preference.PreferenceConstants;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * 差分取得状態を一覧表示するダイアログです。
 * 
 * @author turbou
 * 
 */
public class NetValidDateDialog extends Dialog {
    private Table table;
    private RuleDefinition ruleDef;
    private PreferenceStore preferenceStore;
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    /**
     * デフォルトコンストラクタ
     * 
     * @param parentShell
     *            親シェル
     * @param ruleDef
     *            ルール定義データ
     */
    public NetValidDateDialog(Shell parentShell, RuleDefinition ruleDef, PreferenceStore preferenceStore) {
        super(parentShell);
        this.ruleDef = ruleDef;
        this.preferenceStore = preferenceStore;
        setShellStyle(getShellStyle() | SWT.RESIZE);
    }

    public NetValidDateDialog(Shell parentShell) {
        super(parentShell);
        // 閉じるボタンを無効にする場合は下のとおり(とりあえず無効にはしないのでコメント)
        // setShellStyle(getShellStyle() & ~SWT.CLOSE);
        setShellStyle(getShellStyle() | SWT.RESIZE);
    }

    public void setRuleDef(RuleDefinition ruleDef) {
        this.ruleDef = ruleDef;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);
        composite.setLayout(new GridLayout(1, false));
        table = new Table(composite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
        table.setLayoutData(new GridData(GridData.FILL_BOTH));
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        table.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                table.deselectAll();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {
                table.deselectAll();
            }
        });

        // ========== タイトル行 ========== //
        // 1列目 サイト名
        TableColumn siteColumn = new TableColumn(table, SWT.LEFT);
        siteColumn.setText("サイト");
        siteColumn.setWidth(140);

        // 2列目 変更可否
        TableColumn chkBoxColumn = new TableColumn(table, SWT.LEFT);
        chkBoxColumn.setText("変更する");
        chkBoxColumn.setWidth(80);

        // 3列目 定義有効開始日
        TableColumn validDateColumn = new TableColumn(table, SWT.LEFT);
        validDateColumn.setText("定義有効開始日");
        validDateColumn.setWidth(160);

        // ========== データ行 ========== //
        List<String> tempList = new ArrayList<String>();
        for (final Site site : this.ruleDef.getSiteMap().values()) {
            if (site.getValidDateKey() == null || site.getValidDateKey().isEmpty()) {
                continue;
            }
            if (tempList.contains(site.getValidDateKey())) {
                continue;
            }

            tempList.add(site.getValidDateKey());

            TableEditor editor = new TableEditor(table);
            TableItem item = new TableItem(table, SWT.NULL); // これは要するに行データ
            item.setText(0, site.getValidDateKey()); // 1列目はサイト名(正確にはxmlのvalidDateKeyの値)をセット

            final DateTime dateTime = new DateTime(table, SWT.DATE | SWT.DROP_DOWN);
            Calendar defaultCal = Calendar.getInstance();
            try {
                Date date = DateFormat.getDateInstance().parse(preferenceStore.getString(PreferenceConstants.DEFAULT_NET_VALID_DATE));
                defaultCal.setTime(date);
            } catch (ParseException e) {
                // 別に何もしない。
            }
            dateTime.setDate(defaultCal.get(Calendar.YEAR), defaultCal.get(Calendar.MONTH), defaultCal.get(Calendar.DATE));
            dateTime.addSelectionListener(new SelectionListener() {
                @Override
                public void widgetSelected(SelectionEvent arg0) {
                    Calendar cal = Calendar.getInstance();
                    cal.set(dateTime.getYear(), dateTime.getMonth(), dateTime.getDay());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                    propertyChangeSupport.firePropertyChange("netValidOn", site.getValidDateKey(), sdf.format(cal.getTime()));
                }

                @Override
                public void widgetDefaultSelected(SelectionEvent arg0) {
                }
            });

            dateTime.setEnabled(false);
            dateTime.pack();
            editor.minimumWidth = dateTime.getSize().x;
            editor.horizontalAlignment = SWT.CENTER;
            editor.setEditor(dateTime, item, 2); // 先に3列目にカレンダー選択をセット

            editor = new TableEditor(table);
            Button checkButton = new Button(table, SWT.CHECK);
            checkButton.pack();
            editor.minimumWidth = checkButton.getSize().x;
            editor.horizontalAlignment = SWT.CENTER;
            editor.setEditor(checkButton, item, 1); // 2列目にチェックボックスをセット
            checkButton.addSelectionListener(new SelectionListener() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    Button bChk = (Button) event.widget;
                    if (bChk.getSelection()) {
                        dateTime.setEnabled(true);
                        Calendar cal = Calendar.getInstance();
                        cal.set(dateTime.getYear(), dateTime.getMonth(), dateTime.getDay());
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                        propertyChangeSupport.firePropertyChange("netValidOn", site.getValidDateKey(), sdf.format(cal.getTime()));
                    } else {
                        dateTime.setEnabled(false);
                        propertyChangeSupport.firePropertyChange("netValidOff", site.getValidDateKey(), null);
                    }
                }

                @Override
                public void widgetDefaultSelected(SelectionEvent event) {
                }
            });
        }

        return composite;
    }

    @Override
    protected Point getInitialSize() {
        return new Point(400, 240);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("定義有効開始日指定");
        // アイコンセットアップ
        Image[] imageArray = new Image[3];
        imageArray[0] = new Image(newShell.getDisplay(), Main.class.getClassLoader().getResourceAsStream("icon16.png"));
        imageArray[1] = new Image(newShell.getDisplay(), Main.class.getClassLoader().getResourceAsStream("icon32.png"));
        imageArray[2] = new Image(newShell.getDisplay(), Main.class.getClassLoader().getResourceAsStream("icon48.png"));
        newShell.setImages(imageArray);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.propertyChangeSupport.removePropertyChangeListener(listener);
    }
}
