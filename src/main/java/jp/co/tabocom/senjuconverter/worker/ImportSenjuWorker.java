package jp.co.tabocom.senjuconverter.worker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingWorker;

import jp.co.tabocom.senjuconverter.model.SenjuData;

public class ImportSenjuWorker extends SwingWorker<WorkerRtn, String> {

    private SenjuData senjuData;
    private List<String> senjuFileList;
    private List<String> copyFileList;

    public ImportSenjuWorker(List<String> senjuFileList, List<String> copyFileList) {
        this.senjuFileList = senjuFileList;
        this.copyFileList = copyFileList;
        this.senjuData = new SenjuData();
    }

    public SenjuData getSenjuData() {
        return senjuData;
    }

    @Override
    protected WorkerRtn doInBackground() {
        WorkerRtn rtn = new WorkerRtn();
        try {
            importSenjuTxt();
            loadCopyFileList();
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
        // for (String str : chunks) {
        // firePropertyChange("status", null, str);
        // }
    }

    private void loadCopyFileList() throws Exception {
        for (String filePath : this.copyFileList) {
            this.senjuData.addIgnoreButCopyFile(new File(filePath));
        }
    }

    private void importSenjuTxt() throws Exception {
        BufferedReader br = null;
        InputStream in = null;
        for (String filePath : this.senjuFileList) {
            int totalCount = 0;
            File file = new File(filePath);
            in = new FileInputStream(file);
            Reader reader = new InputStreamReader(in, "Windows-31J");
            br = new BufferedReader(reader);
            List<String> lineList = new ArrayList<String>();
            String line;
            while ((line = br.readLine()) != null) {
                lineList.add(line);
            }
            totalCount = lineList.size();

            String[] columnArray = lineList.get(0).split("\\t");

            firePropertyChange("status_title", null, file.getName());
            firePropertyChange("status_total", null, String.valueOf(totalCount));
            firePropertyChange("status_count", null, String.valueOf(0));
            List<Map<String, String>> list = new ArrayList<Map<String, String>>();
            for (int i = 1; i < lineList.size(); i++) {
                firePropertyChange("status_count", null, String.valueOf(i + 1));
                String[] valueArray = lineList.get(i).split("\\t");
                Map<String, String> valueMap = new LinkedHashMap<String, String>();
                for (int j = 0; j < columnArray.length; j++) {
                    try {
                        valueMap.put(columnArray[j], valueArray[j]);
                    } catch (ArrayIndexOutOfBoundsException aioobe) {
                        valueMap.put(columnArray[j], "");
                    }
                }
                list.add(valueMap);
            }
            this.senjuData.addData(file.getName(), list);
        }
    }
}
