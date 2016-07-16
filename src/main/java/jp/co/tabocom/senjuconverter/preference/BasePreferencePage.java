package jp.co.tabocom.senjuconverter.preference;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class BasePreferencePage extends PreferencePage {

    private Text proxyAdrTxt;
    private Text proxyUsrTxt;
    private Text proxyPwdTxt;
    private Text localRuleXmlTxt;
    private Text reposUrlTxt;
    private Text defaultSnjDirTxt;
    private Text defaultOutDirTxt;
    private Text ignoreSenjuFilesTxt;
    private Button ignoreButCopyCheck;
    private Button outputSiteMergeCheck;
    private Button convertSrcOutputWith;
    private Text defaultNetValidDateTxt;

    private static final String REPOS_URL = "http://192.177.238.199/svn/SENJU";
    private static final String PROXY_ADR = "192.177.237.12:80";

    @Override
    protected Control createContents(Composite parent) {
        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(3, false));
        IPreferenceStore preferenceStore = getPreferenceStore();

        // ==================== プロキシ認証グループ ==================== //
        Group proxyGrp = new Group(composite, SWT.NONE);
        proxyGrp.setLayout(new GridLayout(4, false));
        GridData inputGrpGrDt = new GridData(GridData.FILL_HORIZONTAL);
        inputGrpGrDt.horizontalSpan = 3;
        proxyGrp.setLayoutData(inputGrpGrDt);
        proxyGrp.setText("プロキシ認証");

        // ---------- プロキシサーバURL ---------- //
        new Label(proxyGrp, SWT.LEFT).setText("プロキシサーバIPアドレス：");
        proxyAdrTxt = new Text(proxyGrp, SWT.BORDER);
        GridData proxyUrlTxtGrDt = new GridData(GridData.FILL_HORIZONTAL);
        proxyUrlTxtGrDt.horizontalSpan = 3;
        proxyAdrTxt.setLayoutData(proxyUrlTxtGrDt);
        String proxyAdr = preferenceStore.getString(PreferenceConstants.PROXY_ADR);
        if (proxyAdr == null || proxyAdr.isEmpty()) {
            proxyAdr = PROXY_ADR;
        }
        proxyAdrTxt.setText(proxyAdr);
        proxyAdrTxt.setMessage(PROXY_ADR);
        proxyAdrTxt.setEditable(false);

        // ---------- プロキシ情報 ---------- //
        new Label(proxyGrp, SWT.LEFT).setText("プロキシ認証：");
        proxyUsrTxt = new Text(proxyGrp, SWT.BORDER);
        proxyUsrTxt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        proxyUsrTxt.setText(preferenceStore.getString(PreferenceConstants.PROXY_USR));
        new Label(proxyGrp, SWT.LEFT).setText("/");
        proxyPwdTxt = new Text(proxyGrp, SWT.BORDER);
        proxyPwdTxt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        proxyPwdTxt.setEchoChar('*');
        proxyPwdTxt.setText(preferenceStore.getString(PreferenceConstants.PROXY_PWD));

        // ==================== 変換ルールグループ ==================== //
        Group ruleGrp = new Group(composite, SWT.NONE);
        ruleGrp.setLayout(new GridLayout(2, false));
        ruleGrp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        ruleGrp.setText("変換ルール定義");

        // ---------- ローカルルールXMLパス ---------- //
        new Label(ruleGrp, SWT.LEFT).setText("ローカルのルール定義を使う：");
        localRuleXmlTxt = new Text(ruleGrp, SWT.BORDER);
        localRuleXmlTxt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        localRuleXmlTxt.setText(preferenceStore.getString(PreferenceConstants.LOCAL_RULEXML));
        localRuleXmlTxt.setMessage("C:\\Users\\turbou\\Desktop\\rule_common.xml");

        // ---------- SENJUリポジトリURL ---------- //
        new Label(ruleGrp, SWT.LEFT).setText("SENJUリポジトリURL：");
        reposUrlTxt = new Text(ruleGrp, SWT.BORDER);
        reposUrlTxt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        String reposUrl = preferenceStore.getString(PreferenceConstants.REPOS_URL);
        if (reposUrl == null || reposUrl.isEmpty()) {
            reposUrl = REPOS_URL;
        }
        reposUrlTxt.setText(reposUrl);
        reposUrlTxt.setMessage(REPOS_URL);
        reposUrlTxt.setEditable(false);

        // ==================== その他グループ ==================== //
        Group otherGrp = new Group(composite, SWT.NONE);
        otherGrp.setLayout(new GridLayout(3, false));
        GridData otherGrpGrDt = new GridData(GridData.FILL_HORIZONTAL);
        otherGrpGrDt.horizontalSpan = 3;
        otherGrp.setLayoutData(otherGrpGrDt);
        otherGrp.setText("その他");

        // ---------- デフォルト千手ディレクトリ ---------- //
        new Label(otherGrp, SWT.LEFT).setText("デフォルト千手ディレクトリ：");
        defaultSnjDirTxt = new Text(otherGrp, SWT.BORDER);
        defaultSnjDirTxt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        defaultSnjDirTxt.setText(preferenceStore.getString(PreferenceConstants.DEFAULT_SNJ_DIR));
        defaultSnjDirTxt.setMessage("C:\\Users\\oyoyo\\Desktop");

        Button snjDirBtn = new Button(otherGrp, SWT.NULL);
        snjDirBtn.setText("参照");
        snjDirBtn.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
            }

            public void widgetSelected(SelectionEvent e) {
                String dir = dirDialogOpen("オフライザを選択するデフォルトディレクトリを指定してください。", defaultSnjDirTxt.getText());
                if (dir != null) {
                    defaultSnjDirTxt.setText(dir);
                }
            }
        });

        // ---------- デフォルト出力ディレクトリ ---------- //
        new Label(otherGrp, SWT.LEFT).setText("デフォルト出力ディレクトリ：");
        defaultOutDirTxt = new Text(otherGrp, SWT.BORDER);
        defaultOutDirTxt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        defaultOutDirTxt.setText(preferenceStore.getString(PreferenceConstants.DEFAULT_OUT_DIR));
        defaultOutDirTxt.setMessage("C:\\Users\\oyoyo\\Desktop");

        Button outDirBtn = new Button(otherGrp, SWT.NULL);
        outDirBtn.setText("参照");
        outDirBtn.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
            }

            public void widgetSelected(SelectionEvent e) {
                String dir = dirDialogOpen("出力するデフォルトディレクトリを指定してください。", defaultOutDirTxt.getText());
                if (dir != null) {
                    defaultOutDirTxt.setText(dir);
                }
            }
        });

        // ---------- 無視する千手オフライザファイル ---------- //
        new Label(otherGrp, SWT.LEFT).setText("無視する千手ファイル：");
        ignoreSenjuFilesTxt = new Text(otherGrp, SWT.BORDER);
        GridData ignoreSenGrDt = new GridData(GridData.FILL_HORIZONTAL);
        ignoreSenGrDt.horizontalSpan = 2;
        ignoreSenjuFilesTxt.setLayoutData(ignoreSenGrDt);
        ignoreSenjuFilesTxt.setText(preferenceStore.getString(PreferenceConstants.IGNORE_SENJU_FILES));
        ignoreSenjuFilesTxt.setMessage("ジョブサービス.txt, リソース.txt, リリースメモ.txt, 営業日カレンダー.txt, 稼働日カレンダー.txt, 動作環境プール.txt, 動作環境.txt");
        ignoreSenjuFilesTxt.setEnabled(false);

        // ---------- 変換対象外ファイルもコピーするかしないか ---------- //
        new Label(otherGrp, SWT.LEFT).setText("");
        ignoreButCopyCheck = new Button(otherGrp, SWT.CHECK);
        GridData ibcChkGrDt = new GridData();
        ibcChkGrDt.horizontalSpan = 2;
        ignoreButCopyCheck.setLayoutData(ibcChkGrDt);
        ignoreButCopyCheck.setText("上記ファイルの変換はしないけど出力先に複製はしておいてもらいたい。");
        ignoreButCopyCheck.setSelection(preferenceStore.getBoolean(PreferenceConstants.IGNORE_BUT_COPY));

        // ---------- 複数サイト分の変換ルールがある場合にマージ出力するかしないか ---------- //
        new Label(otherGrp, SWT.LEFT).setText("サイトごと出力マージ：");
        outputSiteMergeCheck = new Button(otherGrp, SWT.CHECK);
        GridData osmChkGrDt = new GridData();
        osmChkGrDt.horizontalSpan = 2;
        outputSiteMergeCheck.setLayoutData(osmChkGrDt);
        outputSiteMergeCheck.setText("サイトごとの出力を１つにマージする。(千手オフライザへの読み込みが１回でできます)");
        outputSiteMergeCheck.setSelection(preferenceStore.getBoolean(PreferenceConstants.OUTPUT_SITE_MERGE));

        // ---------- マージ出力する場合に変換元も一緒にマージするかしないか ---------- //
        new Label(otherGrp, SWT.LEFT).setText("マージする場合に変換元も出力：");
        convertSrcOutputWith = new Button(otherGrp, SWT.CHECK);
        GridData csoChkGrDt = new GridData();
        csoChkGrDt.horizontalSpan = 2;
        convertSrcOutputWith.setLayoutData(csoChkGrDt);
        convertSrcOutputWith.setText("マージ出力する場合に変換元(例えばZ1フレーム)も合わせて出力することができます。");
        convertSrcOutputWith.setSelection(preferenceStore.getBoolean(PreferenceConstants.CONVERT_SRC_OUTWITH));

        // ---------- デフォルトネット定義有効日 ---------- //
        new Label(otherGrp, SWT.LEFT).setText("デフォルトネット定義有効日：");
        defaultNetValidDateTxt = new Text(otherGrp, SWT.BORDER);
        GridData defaultNetValidDateTxtGrDt = new GridData(GridData.FILL_HORIZONTAL);
        defaultNetValidDateTxtGrDt.horizontalSpan = 2;
        defaultNetValidDateTxt.setLayoutData(defaultNetValidDateTxtGrDt);
        defaultNetValidDateTxt.setText(preferenceStore.getString(PreferenceConstants.DEFAULT_NET_VALID_DATE));
        defaultNetValidDateTxt.setMessage("2008/01/01");

        noDefaultAndApplyButton();
        return composite;
    }

    @Override
    public boolean performOk() {
        IPreferenceStore preferenceStore = getPreferenceStore();
        if (preferenceStore == null) {
            return true;
        }
        if (this.proxyAdrTxt != null) {
            preferenceStore.setValue(PreferenceConstants.PROXY_ADR, this.proxyAdrTxt.getText());
        }
        if (this.proxyUsrTxt != null) {
            preferenceStore.setValue(PreferenceConstants.PROXY_USR, this.proxyUsrTxt.getText());
        }
        if (this.proxyPwdTxt != null) {
            preferenceStore.setValue(PreferenceConstants.PROXY_PWD, this.proxyPwdTxt.getText());
        }
        if (this.localRuleXmlTxt != null) {
            preferenceStore.setValue(PreferenceConstants.LOCAL_RULEXML, this.localRuleXmlTxt.getText());
        }
        if (this.reposUrlTxt != null) {
            preferenceStore.setValue(PreferenceConstants.REPOS_URL, this.reposUrlTxt.getText());
        }
        if (this.defaultSnjDirTxt != null) {
            preferenceStore.setValue(PreferenceConstants.DEFAULT_SNJ_DIR, this.defaultSnjDirTxt.getText());
        }
        if (this.defaultOutDirTxt != null) {
            preferenceStore.setValue(PreferenceConstants.DEFAULT_OUT_DIR, this.defaultOutDirTxt.getText());
        }
        if (this.ignoreSenjuFilesTxt != null) {
            preferenceStore.setValue(PreferenceConstants.IGNORE_SENJU_FILES, this.ignoreSenjuFilesTxt.getText());
        }
        if (this.defaultNetValidDateTxt != null) {
            preferenceStore.setValue(PreferenceConstants.DEFAULT_NET_VALID_DATE, this.defaultNetValidDateTxt.getText());
        }
        preferenceStore.setValue(PreferenceConstants.IGNORE_BUT_COPY, this.ignoreButCopyCheck.getSelection());
        preferenceStore.setValue(PreferenceConstants.OUTPUT_SITE_MERGE, this.outputSiteMergeCheck.getSelection());
        preferenceStore.setValue(PreferenceConstants.CONVERT_SRC_OUTWITH, this.convertSrcOutputWith.getSelection());
        return true;
    }

    private String dirDialogOpen(String msg, String currentPath) {
        DirectoryDialog dialog = new DirectoryDialog(getShell());
        dialog.setText(msg);
        dialog.setFilterPath(currentPath.isEmpty() ? "C:\\" : currentPath);
        String dir = dialog.open();
        return dir;
    }
}
