package jp.co.tabocom.senjuconverter.ui;

import java.io.IOException;

import jp.co.tabocom.senjuconverter.model.RuleDefinition;
import jp.co.tabocom.senjuconverter.preference.PreferenceConstants;
import jp.co.tabocom.senjuconverter.worker.ImportRuleWorker;
import jp.co.tabocom.senjuconverter.worker.SWPropertyChangeListener;
import jp.co.tabocom.senjuconverter.worker.WorkerRtn;
import jp.co.tabocom.senjuconverter.worker.WorkerRtnEnum;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;

public class ImportRuleDialog extends Dialog {

    private TreeViewer treeViewer;
    private TargetNode rootNode;
    private TargetNode repoNode;
    private RuleDefinition ruleDef;
    private PreferenceStore preferenceStore;

    public ImportRuleDialog(Shell parentShell, PreferenceStore preferenceStore) {
        super(parentShell);
        this.preferenceStore = preferenceStore;
    }

    public RuleDefinition getRuleDef() {
        return ruleDef;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);
        GridLayout baseLayout = new GridLayout(1, false);
        baseLayout.marginWidth = 20;
        composite.setLayout(baseLayout);
        FontRegistry fontRegistry = new FontRegistry(Display.getCurrent());
        fontRegistry.put("MSGothic", new FontData[] { new FontData("ＭＳ Ｐゴシック", 14, SWT.NORMAL) });
        rootNode = new TargetNode();
        repoNode = new TargetNode();
        repoNode.setName("リポジトリ読み込み中...");
        rootNode.addChild(repoNode);
        final ImportRuleWorker worker = new ImportRuleWorker(preferenceStore, repoNode);
        // リスナーを登録
        worker.addPropertyChangeListener(new SWPropertyChangeListener(getShell()) {
            @Override
            public void started() throws Exception {
                // これはworkerがexecuteされた時に呼び出されます。
            }

            @Override
            public void done() throws Exception {
                // これはworkerの処理が完了した時に呼び出されます。
                // if (worker.get()) { //
                // worker.get()で結果を確認することができます。（doInBackgroundの戻り値）
                repoNode.setName(preferenceStore.getString(PreferenceConstants.REPOS_URL) + "/tags/");
                treeViewer.refresh();
                treeViewer.getTree().setSelection(new TreeItem[0]);
                treeViewer.expandAll();
                WorkerRtn rtn = worker.get();
                if (rtn.getCode() == WorkerRtnEnum.SUCCESS) {
                } else {
                    ExceptionDialog dialog = new ExceptionDialog(getShell(), rtn.getMessage(), rtn.getStackTrace());
                    dialog.open();
                }
            }
        });
        // そして実行
        worker.execute();

        treeViewer = new TreeViewer(composite, SWT.BORDER | SWT.VIRTUAL);
        treeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
        treeViewer.setContentProvider(new TreeContentProvider());
        ColumnViewerToolTipSupport.enableFor(treeViewer, ToolTip.NO_RECREATE);
        treeViewer.setLabelProvider(new TreeLabelProvider());
        treeViewer.setInput(this.rootNode);
        treeViewer.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                // 何をしているかというと、要は親ノードがダブルクリックされたら、ツリーを展開するとか、逆に閉じるとかの処理をしている。
                IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                if (!selection.isEmpty()) {
                    TreeViewer treeViewer = (TreeViewer) event.getSource();
                    Object selectedObject = selection.getFirstElement();
                    if (treeViewer.getExpandedState(selectedObject)) {
                        treeViewer.collapseToLevel(selectedObject, 1);
                    } else {
                        treeViewer.expandToLevel(selectedObject, TreeViewer.ALL_LEVELS);
                    }
                    treeViewer.reveal(selectedObject);
                }
            }
        });

        return composite;
    }

    private byte[] getResponse(String url) {
        String proxyHost = preferenceStore.getString(PreferenceConstants.PROXY_ADR).split(":")[0];
        int proxyPort = Integer.parseInt(preferenceStore.getString(PreferenceConstants.PROXY_ADR).split(":")[1]);
        String proxyUsr = preferenceStore.getString(PreferenceConstants.PROXY_USR);
        String proxyPwd = preferenceStore.getString(PreferenceConstants.PROXY_PWD);

        // HttpClient の生成
        HttpClient client = new HttpClient();
        client.getHostConfiguration().setProxy(proxyHost, proxyPort);
        client.getState().setProxyCredentials(new AuthScope(proxyHost, proxyPort), new UsernamePasswordCredentials(proxyUsr, proxyPwd));

        // GetMethod の生成
        GetMethod method = new GetMethod(url);
        method.setDoAuthentication(true);

        try {
            // GetMethod の実行
            int status = client.executeMethod(method);
            if (status != HttpStatus.SC_OK) {
                throw new HttpException("Connection failed.");
            }
            // Response を取得して表示
            byte[] responseBody = method.getResponseBody();
            return responseBody;
        } catch (HttpException e) {
            System.err.println("Http error: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            method.releaseConnection();
            method = null;
        }
        return null;
    }

    @Override
    protected Point getInitialSize() {
        return new Point(400, 300);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("使用する変換定義ルールを指定してください。");
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.CANCEL_ID, "キャンセル", false);
        createButton(parent, IDialogConstants.OK_ID, "OK", true);
    }

    @Override
    protected void buttonPressed(int buttonId) {
        if (IDialogConstants.OK_ID == buttonId) {
            IStructuredSelection selection = (IStructuredSelection) this.treeViewer.getSelection();
            TargetNode node = (TargetNode) selection.getFirstElement();
            if (node == null || !node.getUrl().endsWith(".xml")) {
                MessageDialog.openError(getParentShell(), "エラー", "対象のルールファイル(*.xml)を選択してください。");
                return;
            }
            byte[] data = getResponse(node.getUrl());
            this.ruleDef = new RuleDefinition(data);
            try {
                this.ruleDef.initialize();
            } catch (Exception e) {
                MessageDialog.openError(getParentShell(), "エラー", "ルールファイル(*.xml)の読み込みに失敗しました。");
            }
        }
        super.buttonPressed(buttonId);
    }

    /**
     * TreeContentProvider<br>
     * <p>
     * このクラスはTreeViewerで必要です。 形式ばったものなので、あまり中身を知らなくて良いです。
     * どこかでTreeViewerを使いたい場合はこの辺も必要なのでコピーして使ってください。
     * </p>
     * 
     * @author turbou
     * 
     */
    class TreeContentProvider implements ITreeContentProvider {

        public Object[] getElements(Object inputElement) {
            return getChildren(inputElement);
        }

        public Object[] getChildren(Object parentElement) {
            return ((TargetNode) parentElement).getChildren().toArray();
        }

        public Object getParent(Object element) {
            return ((TargetNode) element).getParent();
        }

        public boolean hasChildren(Object element) {
            if (((TargetNode) element).getChildren().size() > 0) {
                return true;
            } else {
                return false;
            }
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

    }

    /**
     * TreeLabelProvider<br>
     * <p>
     * これもTreeViewerに必要なクラスです。 でも上のやつよりは重要で<br>
     * 要はTreeViewerのノードのタイトルとかをどのように表示するか を定義するクラスです。
     * getTextメソッドは表示されるタイトルを返します。<br>
     * getToolTipTextはマウスをノードに重ねた時にでるヒント文字列です。
     * </p>
     * 
     * @author turbou
     * 
     */
    class TreeLabelProvider extends ColumnLabelProvider {
        public String getText(Object element) {
            return ((TargetNode) element).getName().replaceAll("/$", "");
        }

        @Override
        public String getToolTipText(Object element) {
            TargetNode node = (TargetNode) element;
            return node.getUrl();
        }

        @Override
        public Font getToolTipFont(Object object) {
            FontRegistry fontRegistry = new FontRegistry(Display.getCurrent());
            fontRegistry.put("MSGothic", new FontData[] { new FontData("ＭＳ ゴシック", 9, SWT.NORMAL) });
            return fontRegistry.get("MSGothic");
        }

        @Override
        public Point getToolTipShift(Object object) {
            return new Point(15, 5);
        }

        @Override
        public int getToolTipDisplayDelayTime(Object object) {
            return 0;
        }

        @Override
        public int getToolTipTimeDisplayed(Object object) {
            return 15000;
        }

    }

}
