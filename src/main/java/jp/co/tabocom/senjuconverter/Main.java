package jp.co.tabocom.senjuconverter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import jp.co.tabocom.senjuconverter.model.Convert;
import jp.co.tabocom.senjuconverter.model.Rule;
import jp.co.tabocom.senjuconverter.model.RuleDefinition;
import jp.co.tabocom.senjuconverter.model.SenjuData;
import jp.co.tabocom.senjuconverter.model.Site;
import jp.co.tabocom.senjuconverter.preference.BasePreferencePage;
import jp.co.tabocom.senjuconverter.preference.PreferenceConstants;
import jp.co.tabocom.senjuconverter.ui.ExceptionDialog;
import jp.co.tabocom.senjuconverter.ui.ImportRuleDialog;
import jp.co.tabocom.senjuconverter.ui.NetValidDateDialog;
import jp.co.tabocom.senjuconverter.ui.RuleShowDialog;
import jp.co.tabocom.senjuconverter.worker.ConvertWorker;
import jp.co.tabocom.senjuconverter.worker.ImportSenjuWorker;
import jp.co.tabocom.senjuconverter.worker.SWPropertyChangeListener;
import jp.co.tabocom.senjuconverter.worker.WorkerRtn;
import jp.co.tabocom.senjuconverter.worker.WorkerRtnEnum;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class Main implements PropertyChangeListener {

    private Shell shell;

    private SenjuData senjuData;
    private RuleDefinition ruleDef;

    private Button impSjTxtButton;
    private Button impRuleButton;
    private Button showRuleButton;
    private Button duplicateChkButton;
    private Button convertButton;
    private Button settingButton;
    private StatusTitleLabel statusTitleLabel;
    private StatusCountLabel statusCountLabel;
    private StatusTotalLabel statusTotalLabel;
    private Label slashLabel;
    private Map<String, String> netValidDateMap;
    static private Main main;

    private PreferenceStore preferenceStore;

    public static final String TOOL_PROPERTIES_FILE = "senjutool.properties";

    /**
     * @param args
     */
    public static void main(String[] args) {
        main = new Main();
        main.createPart();
    }

    private void createPart() {
        final Display display = new Display();
        shell = new Shell(display, SWT.TITLE | SWT.MIN | SWT.CLOSE);
        shell.setData("main", this);
        shell.setText("千手変換ツール (v1.0.0)");
        shell.setSize(400, 300);
        // アイコンセットアップ
        Image[] imageArray = new Image[3];
        imageArray[0] = new Image(display, Main.class.getClassLoader().getResourceAsStream("icon16.png"));
        imageArray[1] = new Image(display, Main.class.getClassLoader().getResourceAsStream("icon32.png"));
        imageArray[2] = new Image(display, Main.class.getClassLoader().getResourceAsStream("icon48.png"));
        shell.setImages(imageArray);

        shell.addShellListener(new ShellListener() {
            @Override
            public void shellIconified(ShellEvent event) {
            }

            @Override
            public void shellDeiconified(ShellEvent event) {
            }

            @Override
            public void shellDeactivated(ShellEvent event) {
            }

            @Override
            public void shellClosed(ShellEvent event) {
            }

            @Override
            public void shellActivated(ShellEvent event) {
            }
        });
        try {
            String homeDir = System.getProperty("user.home");
            this.preferenceStore = new PreferenceStore(homeDir + "\\" + TOOL_PROPERTIES_FILE);
            this.preferenceStore.load();
        } catch (FileNotFoundException fnfe) {
            // senjutool.propertiesがない場合(初回など)は初期値用のプロパティファイルを読み込む.
            InputStream in = null;
            try {
                in = Main.class.getClassLoader().getResourceAsStream("initial.properties");
                this.preferenceStore.load(in);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            MessageDialog.openInformation(shell, "ご利用ありがとうございます。", "まず最初に基本設定を行なってください。\r\nプロキシ認証の情報は必須です。\r\nデフォルトディレクトリについては任意です。");
        } catch (IOException e) {
            e.printStackTrace();
        }

        GridLayout baseLayout = new GridLayout(1, false);
        baseLayout.marginWidth = 10;
        shell.setLayout(baseLayout);

        FontRegistry fontRegistry = new FontRegistry(Display.getCurrent());
        fontRegistry.put("IMPSENJU", new FontData[] { new FontData("ＭＳ Ｐゴシック", 12, SWT.NORMAL) });
        fontRegistry.put("IMPRULE", new FontData[] { new FontData("ＭＳ Ｐゴシック", 10, SWT.NORMAL) });
        fontRegistry.put("CHKRULE", new FontData[] { new FontData("ＭＳ Ｐゴシック", 9, SWT.NORMAL) });
        fontRegistry.put("CONVERTEXEC", new FontData[] { new FontData("ＭＳ Ｐゴシック", 16, SWT.BOLD) });
        fontRegistry.put("STATUS", new FontData[] { new FontData("ＭＳ Ｐゴシック", 9, SWT.NORMAL) });

        // ============================== グループ(なくてもよいけど) ================
        Composite composite = new Composite(shell, SWT.NULL);
        composite.setLayout(new GridLayout(1, false));
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        Composite impSenjuGroup = new Composite(composite, SWT.NULL);
        impSenjuGroup.setLayout(new GridLayout(1, false));
        impSenjuGroup.setLayoutData(new GridData(GridData.FILL_BOTH));

        impSjTxtButton = new Button(impSenjuGroup, SWT.PUSH);
        impSjTxtButton.setLayoutData(new GridData(GridData.FILL_BOTH));
        impSjTxtButton.setText("千手データ取り込み（増幅・変換対象）");
        impSjTxtButton.setEnabled(true);
        impSjTxtButton.setFont(fontRegistry.get("IMPSENJU"));
        impSjTxtButton.addSelectionListener(new SelectionListener() {
            ImportSenjuWorker worker = null;

            @Override
            public void widgetSelected(SelectionEvent arg0) {
                FileDialog dialog = new FileDialog(shell, SWT.MULTI);
                dialog.setText("千手オフライザファイルを選択してください。");
                String defaultDir = preferenceStore.getString(PreferenceConstants.DEFAULT_SNJ_DIR);
                if (defaultDir == null || defaultDir.isEmpty()) {
                    dialog.setFilterPath("C:\\");
                } else {
                    dialog.setFilterPath(defaultDir);
                }
                dialog.setFilterExtensions(new String[] { "*.txt" });
                String selectedFile = dialog.open();
                if (selectedFile == null) {
                    // ファイルが選択されなければそのまま終わり
                    return;
                }

                // 指定されても無視する千手オフライザファイルのリストを取得する。
                List<String> ignoreSenjuFileList = new ArrayList<String>();
                for (String ignoreFile : preferenceStore.getString(PreferenceConstants.IGNORE_SENJU_FILES).split(",")) {
                    ignoreSenjuFileList.add(ignoreFile.trim());
                }
                // 読み込む千手オフライザファイルを指定させる。
                String[] files = dialog.getFileNames();
                List<String> senjuFileList = new ArrayList<String>();
                List<String> copyFileList = new ArrayList<String>();
                for (String fileName : files) {
                    if (ignoreSenjuFileList.contains(fileName)) {
                        if (preferenceStore.getBoolean(PreferenceConstants.IGNORE_BUT_COPY)) {
                            copyFileList.add(dialog.getFilterPath() + "\\" + fileName);
                        }
                    } else {
                        senjuFileList.add(dialog.getFilterPath() + "\\" + fileName);
                    }
                }
                // -------------------- ここから本処理 --------------------
                if (senjuData != null) {
                    senjuData = null;
                }
                worker = new ImportSenjuWorker(senjuFileList, copyFileList);
                // リスナーを登録
                worker.addPropertyChangeListener(statusTitleLabel);
                worker.addPropertyChangeListener(statusCountLabel);
                worker.addPropertyChangeListener(statusTotalLabel);
                worker.addPropertyChangeListener(new SWPropertyChangeListener(shell) {
                    @Override
                    public void started() throws Exception {
                        // これはworkerがexecuteされた時に呼び出されます。
                        setButtonEnable(false);
                        showCountStatus(true);
                    }

                    @Override
                    public void done() throws Exception {
                        // これはworkerの処理が完了した時に呼び出されます。
                        // if (worker.get()) { //
                        // worker.get()で結果を確認することができます。（doInBackgroundの戻り値）
                        senjuData = worker.getSenjuData();
                        setButtonEnable(true);
                        showCountStatus(false);
                        WorkerRtn rtn = worker.get();
                        if (rtn.getCode() == WorkerRtnEnum.SUCCESS) {
                            MessageDialog.openInformation(shell, "読み込み完了", "千手データの読み込みが完了しました。");
                            statusTitleLabel.setText("千手データ読込み完了");
                        } else {
                            statusTitleLabel.setText("");
                            ExceptionDialog dialog = new ExceptionDialog(shell, rtn.getMessage(), rtn.getStackTrace());
                            dialog.open();
                        }
                        worker = null;
                    }
                });
                // そして実行
                worker.execute();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });

        Composite impRuleGroup = new Composite(composite, SWT.NULL);
        GridLayout impGroupLt = new GridLayout(2, false);
        impGroupLt.verticalSpacing = 5;
        impRuleGroup.setLayout(impGroupLt);
        impRuleGroup.setLayoutData(new GridData(GridData.FILL_BOTH));

        impRuleButton = new Button(impRuleGroup, SWT.PUSH);
        GridData impRuleButtonGrDt = new GridData(GridData.FILL_BOTH);
        impRuleButtonGrDt.verticalSpan = 2;
        impRuleButton.setLayoutData(impRuleButtonGrDt);
        impRuleButton.setText("変換ルールファイル取り込み");
        impRuleButton.setEnabled(true);
        impRuleButton.setFont(fontRegistry.get("IMPRULE"));
        impRuleButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                String localRuleXmlFile = preferenceStore.getString(PreferenceConstants.LOCAL_RULEXML);
                if (localRuleXmlFile == null || localRuleXmlFile.isEmpty()) {
                    // ローカルのファイルが指定されていない場合はリポジトリから取得。
                    if (!checkImportRule()) {
                        MessageDialog.openError(shell, "エラー", "基本設定に不備があります。");
                        return;
                    }
                    ImportRuleDialog dialog = new ImportRuleDialog(shell, preferenceStore);
                    int result = dialog.open();
                    if (IDialogConstants.OK_ID != result) {
                        return;
                    }
                    ruleDef = dialog.getRuleDef();
                } else {
                    // ローカルからルールファイルを読み込む。
                    Path path = Paths.get(localRuleXmlFile);
                    try {
                        byte[] data = Files.readAllBytes(path);
                        ruleDef = new RuleDefinition(data);
                        ruleDef.initialize();
                    } catch (Exception e) {
                        e.printStackTrace();
                        MessageDialog.openError(shell, "エラー", "ローカルのルールファイルの読み込みに失敗しました。");
                        return;
                    }
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });

        showRuleButton = new Button(impRuleGroup, SWT.PUSH);
        showRuleButton.setLayoutData(new GridData(GridData.FILL_BOTH));
        showRuleButton.setText("定義確認");
        showRuleButton.setFont(fontRegistry.get("CHKRULE"));
        showRuleButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                if (ruleDef == null || ruleDef.getVersion() == null) {
                    MessageDialog.openError(shell, "実行エラー", "ルール定義ファイルが読み込まれていません。");
                    return;
                }
                RuleShowDialog dialog = new RuleShowDialog(shell, "変換ルール", ruleDef.getXmlStr());
                int result = dialog.open();
                if (IDialogConstants.OK_ID != result) {
                    return;
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });

        duplicateChkButton = new Button(impRuleGroup, SWT.PUSH);
        duplicateChkButton.setLayoutData(new GridData(GridData.FILL_BOTH));
        duplicateChkButton.setText("重複チェック");
        duplicateChkButton.setFont(fontRegistry.get("CHKRULE"));
        duplicateChkButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                if (ruleDef == null || ruleDef.getVersion() == null) {
                    MessageDialog.openError(shell, "実行エラー", "ルール定義ファイルが読み込まれていません。");
                    return;
                }
                StringBuilder builder = new StringBuilder();
                for (Site site : ruleDef.getSiteMap().values()) {
                    builder.append(site.duplicateChk());
                }
                if (builder.length() > 0) {
                    RuleShowDialog dialog = new RuleShowDialog(shell, "重複している変換ルール", builder.toString());
                    int result = dialog.open();
                    if (IDialogConstants.OK_ID != result) {
                        return;
                    }
                } else {
                    MessageDialog.openInformation(shell, "重複チェック", "重複している変換ルールはないようです。");
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });

        // ============================== 実行ボタン ==============================
        // final NetValidDateDialog dialog = new NetValidDateDialog(shell);
        // dialog.addPropertyChangeListener(this);

        Composite convertGroup = new Composite(composite, SWT.NULL);
        convertGroup.setLayout(new GridLayout(1, false));
        convertGroup.setLayoutData(new GridData(GridData.FILL_BOTH));

        convertButton = new Button(convertGroup, SWT.PUSH);
        convertButton.setLayoutData(new GridData(GridData.FILL_BOTH));
        convertButton.setText("変換実行");
        convertButton.setEnabled(true);
        convertButton.setFont(fontRegistry.get("CONVERTEXEC"));
        convertButton.addSelectionListener(new SelectionListener() {
            ConvertWorker worker = null;

            @Override
            public void widgetSelected(SelectionEvent event) {
                if (senjuData == null) {
                    MessageDialog.openError(shell, "実行エラー", "千手オフライザが読み込まれていません。");
                    return;
                }
                if (ruleDef == null || ruleDef.getVersion() == null) {
                    MessageDialog.openError(shell, "実行エラー", "ルール定義ファイルが読み込まれていません。");
                    return;
                }

                // ========== ネット定義有効日の書き換え処理 ここから ========== //
                // とりあえず最初にダイアログを表示してネット定義有効日を指定させる
                netValidDateMap = new HashMap<String, String>();
                NetValidDateDialog dialog = new NetValidDateDialog(shell, ruleDef, preferenceStore);
                dialog.addPropertyChangeListener(main);
                dialog.setRuleDef(ruleDef);
                int result = dialog.open();
                if (IDialogConstants.OK_ID != result) {
                    // cancelを押されてたら処理せず終了
                    return;
                }
                // すでにルール定義XMLからロードしてある変換ルールに定義有効開始日の一括置換ルールを追加する。
                for (String key : netValidDateMap.keySet()) {
                    for (Site site : ruleDef.getSiteMap().values()) {
                        if (site.getName().equals(key)) {
                            Rule rule = site.getRule();
                            Convert convert = new Convert();
                            convert.addSearch("定義有効開始日", "2*", null); // カラムは定義有効開始日を指定、ここの検索文字列の2は20XX年の先頭の2です。
                            convert.addRewrite("定義有効開始日", null, netValidDateMap.get(key), null);
                            rule.addNetConvert(convert); // ネット定義有効日.txtに対する置換処理なので
                            break;
                        }
                    }
                }
                // ========== ネット定義有効日の書き換え処理 ここまで ========== //

                String dirStr = dirDialogOpen("出力先ディレクトリを指定してください。", preferenceStore.getString(PreferenceConstants.DEFAULT_OUT_DIR));
                if (dirStr == null) {
                    return;
                }

                // -------------------- ここから本処理 --------------------
                boolean mergeFlg = preferenceStore.getBoolean(PreferenceConstants.OUTPUT_SITE_MERGE);
                boolean srcOutFlg = preferenceStore.getBoolean(PreferenceConstants.CONVERT_SRC_OUTWITH);
                worker = new ConvertWorker(senjuData, ruleDef, dirStr, mergeFlg, srcOutFlg);
                // リスナーを登録
                worker.addPropertyChangeListener(statusTitleLabel);
                worker.addPropertyChangeListener(new SWPropertyChangeListener(shell) {
                    @Override
                    public void started() throws Exception {
                        // これはworkerがexecuteされた時に呼び出されます。
                        setButtonEnable(false);
                    }

                    @Override
                    public void done() throws Exception {
                        // これはworkerの処理が完了した時に呼び出されます。
                        // if (worker.get()) { //
                        // worker.get()で結果を確認することができます。（doInBackgroundの戻り値）
                        setButtonEnable(true);
                        WorkerRtn rtn = null;
                        try {
                            rtn = worker.get();
                        } catch (ExecutionException ee) {
                            ee.printStackTrace();
                            MessageDialog.openError(shell, "OutOfMemoryError", "ヒープサイズが足りません。");
                            return;
                        }
                        if (rtn.getCode() == WorkerRtnEnum.SUCCESS) {
                            MessageDialog.openInformation(shell, "変換完了", "千手データの変換出力が完了しました。");
                            String warningMsg = ruleDef.getMessage().getEndWarning();
                            if (warningMsg != null && !warningMsg.isEmpty()) {
                                MessageDialog.openWarning(shell, "変換完了", warningMsg);
                            }
                            statusTitleLabel.setText("千手データ変換出力完了");
                        } else {
                            statusTitleLabel.setText("");
                            ExceptionDialog dialog = new ExceptionDialog(shell, rtn.getMessage(), rtn.getStackTrace());
                            dialog.open();
                        }
                        worker = null;
                    }
                });
                // そして実行
                worker.execute();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
            }
        });

        // ============================== ステータスバー罫線1 ==========================
        Label statusbarLbl1 = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
        GridData statusbarLblGrDt1 = new GridData(GridData.FILL_HORIZONTAL);
        statusbarLbl1.setLayoutData(statusbarLblGrDt1);

        // ============================== 状況表示ラベル ==============================
        Composite statusLabelGroup = new Composite(shell, SWT.NULL);
        GridLayout statusGrpLt = new GridLayout(4, false);
        statusLabelGroup.setLayout(statusGrpLt);
        statusLabelGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        statusTitleLabel = new StatusTitleLabel(statusLabelGroup, SWT.NONE);
        statusTitleLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        statusTitleLabel.setFont(fontRegistry.get("STATUS"));

        statusCountLabel = new StatusCountLabel(statusLabelGroup, SWT.RIGHT);
        statusCountLabel.setFont(fontRegistry.get("STATUS"));
        statusCountLabel.setVisible(false);

        slashLabel = new Label(statusLabelGroup, SWT.CENTER);
        slashLabel.setText("/");
        slashLabel.setVisible(false);

        statusTotalLabel = new StatusTotalLabel(statusLabelGroup, SWT.RIGHT);
        statusTotalLabel.setFont(fontRegistry.get("STATUS"));
        statusTotalLabel.setVisible(false);

        // ============================== ステータスバー罫線2 ==========================
        Label statusbarLbl2 = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
        GridData statusbarLblGrDt2 = new GridData(GridData.FILL_HORIZONTAL);
        statusbarLbl2.setLayoutData(statusbarLblGrDt2);

        // ============================== 設定ボタン ================================
        Composite settingsGroup = new Composite(shell, SWT.NULL);
        GridLayout settingsGrpLt = new GridLayout(2, false);
        settingsGroup.setLayout(settingsGrpLt);
        settingsGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

        settingButton = new Button(settingsGroup, SWT.PUSH);
        GridData settingBtnGrDt = new GridData(GridData.FILL_HORIZONTAL);
        settingBtnGrDt.horizontalSpan = 2;
        settingButton.setLayoutData(settingBtnGrDt);
        settingButton.setText("基本設定");
        settingButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                PreferenceManager mgr = new PreferenceManager();
                PreferenceNode baseNode = new PreferenceNode("local", "基本設定", null, BasePreferencePage.class.getName());
                mgr.addToRoot(baseNode);
                PreferenceDialog dialog = new PreferenceDialog(shell, mgr);
                dialog.setPreferenceStore(preferenceStore);
                if (IDialogConstants.OK_ID == dialog.open()) {
                    try {
                        preferenceStore.save();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
            }
        });

        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
    }

    private boolean checkImportRule() {
        boolean flg = true;
        String proxyUrl = preferenceStore.getString(PreferenceConstants.PROXY_ADR);
        String proxyUsr = preferenceStore.getString(PreferenceConstants.PROXY_USR);
        String proxyPwd = preferenceStore.getString(PreferenceConstants.PROXY_PWD);
        String reposUrl = preferenceStore.getString(PreferenceConstants.REPOS_URL);
        if (proxyUrl == null || proxyUrl.isEmpty()) {
            flg &= false;
        }
        if (proxyUsr == null || proxyUsr.isEmpty()) {
            flg &= false;
        }
        if (proxyPwd == null || proxyPwd.isEmpty()) {
            flg &= false;
        }
        if (reposUrl == null || reposUrl.isEmpty()) {
            flg &= false;
        }
        return flg;
    }

    private void setButtonEnable(boolean flg) {
        this.impSjTxtButton.setEnabled(flg);
        this.impRuleButton.setEnabled(flg);
        this.showRuleButton.setEnabled(flg);
        this.convertButton.setEnabled(flg);
        this.settingButton.setEnabled(flg);
    }

    private void showCountStatus(boolean flg) {
        this.statusCountLabel.setVisible(flg);
        this.slashLabel.setVisible(flg);
        this.statusTotalLabel.setVisible(flg);
    }

    private String dirDialogOpen(String msg, String currentPath) {
        DirectoryDialog dialog = new DirectoryDialog(shell);
        dialog.setText(msg);
        dialog.setFilterPath(currentPath == null || currentPath.isEmpty() ? "C:\\" : currentPath);
        String dir = dialog.open();
        return dir;
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if ("netValidOn".equals(event.getPropertyName())) {
            // 定義有効開始日のチェックがONにされた時、またはその日付が変更された時に通知されます。
            String validDateKey = (String) event.getOldValue();
            for (Site site : ruleDef.getSiteMap().values()) {
                if (site.getValidDateKey() != null && site.getValidDateKey().equals(validDateKey)) {
                    netValidDateMap.put(site.getName(), (String) event.getNewValue());
                }
            }
        } else if ("netValidOff".equals(event.getPropertyName())) {
            // 定義有効開始日のチェックがOFFにされた時に通知されます。
            String validDateKey = (String) event.getOldValue();
            for (Site site : ruleDef.getSiteMap().values()) {
                if (site.getValidDateKey() != null && site.getValidDateKey().equals(validDateKey)) {
                    netValidDateMap.remove(site.getName());
                }
            }
        }
    }

    /**
     * 拡張したUIを内部クラスとして定義
     * 
     * @author turbou
     * 
     */
    class StatusTitleLabel extends Label implements PropertyChangeListener {

        public StatusTitleLabel(Composite parent, int style) {
            super(parent, style);
        }

        @Override
        public void propertyChange(final PropertyChangeEvent event) {
            if ("status_title".equals(event.getPropertyName())) {
                shell.getDisplay().asyncExec(new Runnable() {
                    public void run() {
                        setText((String) event.getNewValue());
                    }
                });
            }
        }

        @Override
        protected void checkSubclass() {
            // UIを継承した場合、これをやらないとコンパイラに怒られる
            // super.checkSubclass();
        }
    }

    /**
     * 拡張したUIを内部クラスとして定義
     * 
     * @author turbou
     * 
     */
    class StatusCountLabel extends Label implements PropertyChangeListener {

        public StatusCountLabel(Composite parent, int style) {
            super(parent, style);
            GridData gridData = new GridData();
            gridData.widthHint = 45;
            this.setLayoutData(gridData);
        }

        @Override
        public void propertyChange(final PropertyChangeEvent event) {
            if ("status_count".equals(event.getPropertyName())) {
                shell.getDisplay().asyncExec(new Runnable() {
                    public void run() {
                        setText(String.format("%9s", (String) event.getNewValue()));
                    }
                });
            }
        }

        @Override
        protected void checkSubclass() {
            // UIを継承した場合、これをやらないとコンパイラに怒られる
            // super.checkSubclass();
        }
    }

    /**
     * 拡張したUIを内部クラスとして定義
     * 
     * @author turbou
     * 
     */
    class StatusTotalLabel extends Label implements PropertyChangeListener {

        public StatusTotalLabel(Composite parent, int style) {
            super(parent, style);
            GridData gridData = new GridData();
            gridData.widthHint = 45;
            this.setLayoutData(gridData);
        }

        @Override
        public void propertyChange(final PropertyChangeEvent event) {
            if ("status_total".equals(event.getPropertyName())) {
                shell.getDisplay().asyncExec(new Runnable() {
                    public void run() {
                        // setText((String) event.getNewValue());
                        setText(String.format("%9s", (String) event.getNewValue()));
                    }
                });
            }
        }

        @Override
        protected void checkSubclass() {
            // UIを継承した場合、これをやらないとコンパイラに怒られる
            // super.checkSubclass();
        }
    }

}
