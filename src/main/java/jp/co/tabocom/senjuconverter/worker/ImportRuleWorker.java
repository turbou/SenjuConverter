package jp.co.tabocom.senjuconverter.worker;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import javax.swing.SwingWorker;

import jp.co.tabocom.senjuconverter.preference.PreferenceConstants;
import jp.co.tabocom.senjuconverter.ui.TargetNode;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.eclipse.jface.preference.PreferenceStore;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ImportRuleWorker extends SwingWorker<WorkerRtn, String> {

    private PreferenceStore preferenceStore;
    private TargetNode repoNode;

    public ImportRuleWorker(PreferenceStore preferenceStore, TargetNode node) {
        this.preferenceStore = preferenceStore;
        this.repoNode = node;
    }

    @Override
    protected WorkerRtn doInBackground() {
        WorkerRtn rtn = new WorkerRtn();
        try {
            loadSenjuRepository(this.preferenceStore.getString(PreferenceConstants.REPOS_URL) + "/tags/", this.repoNode);
        } catch (Exception e) {
            e.printStackTrace();
            rtn.setCode(WorkerRtnEnum.FAILURE);
            rtn.setMessage(e.getMessage());
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            String trace = stringWriter.toString();
            rtn.setStackTrace(trace);
        }
        return rtn;
    }

    @Override
    protected void process(List<String> chunks) {
    }

    private void loadSenjuRepository(String url, TargetNode node) {
        byte[] response = getResponse(url);
        Document doc = Jsoup.parse(new String(response));
        Elements elements = doc.select("ul");
        Elements links = elements.select("a[href]");
        for (Element link : links) {
            String value = link.text();
            if (value.equals("..") || value.startsWith(".")) {
                continue;
            }
            TargetNode subNode = new TargetNode();
            subNode.setName(value);
            subNode.setUrl(url + value);
            node.addChild(subNode);
            this.loadSenjuRepository(url + value, subNode);
        }
    }

    private byte[] getResponse(String url) {
        String proxyHost = this.preferenceStore.getString(PreferenceConstants.PROXY_ADR).split(":")[0];
        int proxyPort = Integer.parseInt(this.preferenceStore.getString(PreferenceConstants.PROXY_ADR).split(":")[1]);
        String proxyUsr = this.preferenceStore.getString(PreferenceConstants.PROXY_USR);
        String proxyPwd = this.preferenceStore.getString(PreferenceConstants.PROXY_PWD);

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
}
