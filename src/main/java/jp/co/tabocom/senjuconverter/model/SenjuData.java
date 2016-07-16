package jp.co.tabocom.senjuconverter.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SenjuData implements Serializable {

    private static final long serialVersionUID = -6916471875458854416L;

    /* 変換対象データとして保持する千手オフライザファイルデータ */
    private Map<String, List<Map<String, String>>> senjuDataMap;
    /* 変換対象データではないが、出力先に複製しておきたいファイルのリスト */
    private List<File> ignoreButCopyFileList;

    public SenjuData() {
        this.senjuDataMap = new HashMap<String, List<Map<String, String>>>();
        this.ignoreButCopyFileList = new ArrayList<File>();
    }

    public static SenjuData copyOf(SenjuData obj) {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(obj);
            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream in = new ObjectInputStream(byteIn);
            return (SenjuData) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void addData(String file, List<Map<String, String>> data) {
        this.senjuDataMap.put(file, data);
    }

    public Map<String, List<Map<String, String>>> getSenjuDataMap() {
        return senjuDataMap;
    }

    public void addIgnoreButCopyFile(File file) {
        this.ignoreButCopyFileList.add(file);
    }

    public List<File> getIgnoreButCopyFileList() {
        return ignoreButCopyFileList;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (String key : senjuDataMap.keySet()) {
            builder.append(String.format("%10d : %s\r\n", senjuDataMap.get(key).size(), key));
        }
        return builder.toString();
    }
}
