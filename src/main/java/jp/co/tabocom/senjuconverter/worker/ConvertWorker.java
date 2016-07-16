package jp.co.tabocom.senjuconverter.worker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.channels.FileChannel;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.swing.SwingWorker;

import jp.co.tabocom.senjuconverter.model.Convert;
import jp.co.tabocom.senjuconverter.model.Rewrite;
import jp.co.tabocom.senjuconverter.model.RuleDefinition;
import jp.co.tabocom.senjuconverter.model.Search;
import jp.co.tabocom.senjuconverter.model.SenjuData;
import jp.co.tabocom.senjuconverter.model.Site;

public class ConvertWorker extends SwingWorker<WorkerRtn, String> {

    private SenjuData senjuData;
    private RuleDefinition ruleDef;
    private String dirPath;
    private boolean mergeFlg;
    private boolean srcOutFlg;

    // 変換対象ファイル(今のところこの4種類)
    private static final String SYSTEM_TXT = "システム.txt";
    private static final String NET_TXT = "ネット.定義有効日.txt";
    private static final String JOB_TXT = "ジョブ.txt";
    private static final String TRIGGER_TXT = "トリガ.txt";

    public ConvertWorker(SenjuData senjuData, RuleDefinition ruleDef, String dirPath, boolean mergeFlg, boolean srcOutFlg) {
        this.senjuData = senjuData;
        this.ruleDef = ruleDef;
        this.dirPath = dirPath;
        this.mergeFlg = mergeFlg;
        this.srcOutFlg = srcOutFlg;
    }

    @Override
    protected WorkerRtn doInBackground() {
        WorkerRtn rtn = new WorkerRtn();
        try {
            convert();
            ignoreFileTransfer();
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

    /**
     * 変換対象外のファイルも出力先にそのままコピーする処理です。
     * 
     * @throws Exception
     */
    private void ignoreFileTransfer() throws Exception {
        StringBuilder mergeCaseDir = new StringBuilder();
        if (this.mergeFlg) {
            for (Site site : this.ruleDef.getSiteMap().values()) {
                mergeCaseDir.append(site.getName());
            }
        }
        for (File file : this.senjuData.getIgnoreButCopyFileList()) {
            FileChannel srcChannel = new FileInputStream(file).getChannel();
            String outFilePath = null;
            if (this.mergeFlg) {
                outFilePath = this.dirPath + "\\" + mergeCaseDir + "\\" + file.getName();
                FileChannel destChannel = new FileOutputStream(outFilePath).getChannel();
                srcChannel.transferTo(0, srcChannel.size(), destChannel);
                destChannel.close();
            } else {
                for (Site site : this.ruleDef.getSiteMap().values()) {
                    outFilePath = this.dirPath + "\\" + site.getName() + "\\" + file.getName();
                    FileChannel destChannel = new FileOutputStream(outFilePath).getChannel();
                    srcChannel.transferTo(0, srcChannel.size(), destChannel);
                    destChannel.close();
                }
            }
            srcChannel.close();
        }
    }

    /**
     * 千手データファイルを変換して出力します。
     * 
     * @throws Exception
     */
    private void convert() throws Exception {
        // マージ出力する場合のディレクトリ名
        StringBuilder mergeCaseDir = new StringBuilder();

        // ========================= 出力先ディレクトリの作成 =========================
        if (this.mergeFlg) {
            // マージ出力の場合
            Set<String> set = new HashSet<String>();
            for (Site site : this.ruleDef.getSiteMap().values()) {
                set.add(site.getMergeGroup());
            }
            for (String dirStr : set) {
                File dir = new File(this.dirPath + "\\" + dirStr);
                dir.mkdirs();
            }
        } else {
            // 出力をマージしない場合
            for (Site site : this.ruleDef.getSiteMap().values()) {
                // サイトごとのディレクトリを作ります。
                File dir = new File(this.dirPath + "\\" + site.getName());
                dir.mkdirs();
            }
        }

        // ========================= 既存ファイルの削除 =========================
        for (Site site : this.ruleDef.getSiteMap().values()) {
            SenjuData convertedSenjuData = convertSenjuData(site);
            for (String fileName : convertedSenjuData.getSenjuDataMap().keySet()) {
                String outFilePath = null;
                if (this.mergeFlg) {
                    outFilePath = this.dirPath + "\\" + site.getMergeGroup() + "\\" + fileName;
                } else {
                    outFilePath = this.dirPath + "\\" + site.getName() + "\\" + fileName;
                }
                File file = new File(outFilePath);
                file.delete();
            }
        }

        if (this.mergeFlg && this.srcOutFlg) {
            outputSenjuData(this.senjuData, this.dirPath + "\\" + mergeCaseDir, "変換元");
        }

        // ========================= 変換＆出力処理 =========================
        for (Site site : this.ruleDef.getSiteMap().values()) {
            firePropertyChange("status_title", null, String.format("%s : 変換処理中...", site.getName()));
            String outputDir = null;
            if (this.mergeFlg) {
                outputDir = this.dirPath + "\\" + site.getMergeGroup();
            } else {
                outputDir = this.dirPath + "\\" + site.getName();
            }

            // ********** 実変換処理 **********
            SenjuData convertedSenjuData = convertSenjuData(site);
            outputSenjuData(convertedSenjuData, outputDir, site.getName());
            Thread.sleep(500);
        }
    }

    private void outputSenjuData(SenjuData senjuData, String outputDir, String siteName) throws Exception {
        for (String fileName : senjuData.getSenjuDataMap().keySet()) {
            String outFilePath = outputDir + "\\" + fileName;
            firePropertyChange("status_title", null, String.format("%s : %s", siteName, fileName));

            BufferedWriter writer = null;
            File file = new File(outFilePath);
            try {
                // BufferedWriterを開くんだけど、マージ出力の場合は追加書き込みモードを指定しています。
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, this.mergeFlg), "Windows-31J"));

                List<Map<String, String>> list = senjuData.getSenjuDataMap().get(fileName);
                if (list.isEmpty()) {
                    continue;
                }

                Iterator<Map.Entry<String, String>> it;

                if (file.length() == 0) { // ファイルサイズが０つまり最初だけカラム行を出力します。
                    // ============================== カラム行出力
                    // ==============================
                    Map<String, String> columnMap = senjuData.getSenjuDataMap().get(fileName).get(0);
                    it = columnMap.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<String, String> entry = (Map.Entry<String, String>) it.next();
                        writer.write(entry.getKey());
                        if (it.hasNext()) {
                            writer.write("\t");
                        } else {
                            break;
                        }
                    }
                    writer.newLine();
                }

                // ============================== データ行出力
                // ==============================
                for (Map<String, String> map : senjuData.getSenjuDataMap().get(fileName)) {
                    if (map.containsKey("DEL_FLG")) {
                        continue;
                    }
                    it = map.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<String, String> entry = (Map.Entry<String, String>) it.next();
                        writer.write(entry.getValue());
                        if (it.hasNext()) {
                            writer.write("\t");
                        } else {
                            break;
                        }
                    }
                    writer.newLine();
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            } finally {
                try {
                    if (writer != null) {
                        writer.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Thread.sleep(500);
        }
    }

    private SenjuData convertSenjuData(Site site) throws Exception {
        // 元の千手データから複製
        SenjuData convertSenjuData = SenjuData.copyOf(this.senjuData);

        // ---------- 一括変換 ----------
        // システム
        multiConvert(this.senjuData.getSenjuDataMap().get(SYSTEM_TXT), convertSenjuData.getSenjuDataMap().get(SYSTEM_TXT), site.getRule()
                .getSystemConvertList());
        // ネット
        multiConvert(this.senjuData.getSenjuDataMap().get(NET_TXT), convertSenjuData.getSenjuDataMap().get(NET_TXT), site.getRule()
                .getNetConvertList());
        // ジョブ
        multiConvert(this.senjuData.getSenjuDataMap().get(JOB_TXT), convertSenjuData.getSenjuDataMap().get(JOB_TXT), site.getRule()
                .getJobConvertList());
        // トリガ
        multiConvert(this.senjuData.getSenjuDataMap().get(TRIGGER_TXT), convertSenjuData.getSenjuDataMap().get(TRIGGER_TXT), site.getRule()
                .getTriggerConvertList());

        return convertSenjuData;
    }

    /**
     * マルチ変換を行います。
     * 
     * @param refLineMapList
     *            参照用の行マップリスト
     * @param lineMapList
     *            実際に内容が変換される行マップリスト
     * @param convertList
     *            変換ルールリスト
     * @throws Exception
     */
    private void multiConvert(final List<Map<String, String>> refLineMapList, List<Map<String, String>> lineMapList, List<Convert> convertList)
            throws Exception {
        if (lineMapList == null || convertList == null) {
            // ファイルの内容、または該当する変換定義が無い場合は何もしない。
            return;
        }

        for (int i = 0; i < refLineMapList.size(); i++) {
            Map<String, String> refLineMap = refLineMapList.get(i);
            Map<String, String> lineMap = lineMapList.get(i);
            for (Convert convert : convertList) {
                // =============== まずは列名が条件にマッチするかをチェック ===============
                boolean convertFlg = true;
                for (Search sPlus : convert.getSearchList()) {
                    for (String column : refLineMap.keySet()) {
                        boolean columnMatchFlg = false;
                        switch (sPlus.getColumnRegType()) {
                            case STARTSWITH:
                                if (column.startsWith(sPlus.getColumn())) {
                                    columnMatchFlg = true;
                                }
                                break;
                            case ENDSWITH:
                                if (column.endsWith(sPlus.getColumn())) {
                                    columnMatchFlg = true;
                                }
                                break;
                            case CONTAINS:
                                if (column.contains(sPlus.getColumn())) {
                                    columnMatchFlg = true;
                                }
                                break;
                            default:
                                if (column.equals(sPlus.getColumn())) {
                                    columnMatchFlg = true;
                                }
                        }
                        // もしも列名が適合していた場合はその列のもつ値をチェック
                        if (columnMatchFlg) {
                            boolean valueMatchFlg = false;
                            String value = refLineMap.get(column);
                            if (!value.isEmpty() && sPlus.getRange() != null && !sPlus.getRange().isEmpty()) {
                                String[] fromto = sPlus.getRange().split(",");
                                int from = Integer.valueOf(fromto[0].trim()) - 1;
                                int to = Integer.valueOf(fromto[1].trim());
                                try {
                                    value = value.substring(from, to);
                                } catch (IndexOutOfBoundsException e) {
                                }
                            }
                            switch (sPlus.getValueRegType()) {
                                case UNCONDITIONAL:
                                    valueMatchFlg = true;
                                    break;
                                case NOTEMPTY:
                                    if (!value.isEmpty()) {
                                        valueMatchFlg = true;
                                    }
                                    break;
                                case STARTSWITH:
                                    if (value.startsWith(sPlus.getValue())) {
                                        valueMatchFlg = true;
                                    }
                                    break;
                                case ENDSWITH:
                                    if (value.endsWith(sPlus.getValue())) {
                                        valueMatchFlg = true;
                                    }
                                    break;
                                case CONTAINS:
                                    if (value.contains(sPlus.getValue())) {
                                        valueMatchFlg = true;
                                    }
                                    break;
                                default:
                                    if (value.equals(sPlus.getValue())) {
                                        valueMatchFlg = true;
                                    }
                            }
                            convertFlg &= valueMatchFlg;
                        }
                    }
                }
                if (convertFlg) {
                    // =============== 変換対象ですとなったらここに入る ===============
                    if (convert.isDeleteFlg()) {
                        // 行削除となる場合は、マップから消すのではなく削除フラグを付けておく。
                        lineMap.put("DEL_FLG", "TRUE");
                    } else {
                        for (Rewrite rewrite : convert.getRewriteList()) {
                            for (String column : refLineMap.keySet()) {
                                boolean columnMatchFlg = false;
                                switch (rewrite.getColumnRegType()) {
                                    case STARTSWITH:
                                        if (column.startsWith(rewrite.getColumn())) {
                                            columnMatchFlg = true;
                                        }
                                        break;
                                    case ENDSWITH:
                                        if (column.endsWith(rewrite.getColumn())) {
                                            columnMatchFlg = true;
                                        }
                                        break;
                                    case CONTAINS:
                                        if (column.contains(rewrite.getColumn())) {
                                            columnMatchFlg = true;
                                        }
                                        break;
                                    default:
                                        if (column.equals(rewrite.getColumn())) {
                                            columnMatchFlg = true;
                                        }
                                }
                                // もしも列名が適合していた場合はその列のもつ値を置換する
                                if (columnMatchFlg) {
                                    String replace = rewrite.getReplace();
                                    String value = rewrite.getValue();
                                    String range = rewrite.getRange();
                                    String refValue = refLineMap.get(column); // まずは対象の項目の値を取得します。
                                    String convertedValue = lineMap.get(column);
                                    String targetValue = null;
                                    if (refValue.equals(convertedValue)) {
                                        targetValue = refValue;
                                    } else {
                                        // すでに他の変換ルールで値が変換済みの場合はそちらの値を変換対象とする。
                                        targetValue = convertedValue;
                                    }
                                    String targetPartValue = null;
                                    if (!targetValue.isEmpty() && range != null && !range.isEmpty()) {
                                        String[] fromto = range.split(",");
                                        int from = Integer.valueOf(fromto[0].trim()) - 1;
                                        int to = Integer.valueOf(fromto[1].trim());
                                        try {
                                            targetPartValue = value.substring(from, to);
                                        } catch (IndexOutOfBoundsException e) {
                                        }
                                    }
                                    if (replace != null) {
                                        String regex = Pattern.quote(replace);
                                        if (targetPartValue != null) {
                                            // 要するに位置が指定されている場合
                                            StringBuilder builder = new StringBuilder(targetValue);
                                            String[] fromto = range.split(",");
                                            int from = Integer.valueOf(fromto[0].trim()) - 1;
                                            int to = Integer.valueOf(fromto[1].trim());
                                            switch (rewrite.getReplaceRegType()) {
                                                case STARTSWITH:
                                                    targetPartValue = targetPartValue.replaceAll("^" + regex, value);
                                                    break;
                                                case ENDSWITH:
                                                    targetPartValue = targetPartValue.replaceAll(regex + "$", value);
                                                    break;
                                                default:
                                                    targetPartValue = targetPartValue.replaceAll(regex, value);
                                            }
                                            lineMap.put(column, builder.replace(from, to, targetPartValue).toString());
                                        } else {
                                            switch (rewrite.getReplaceRegType()) {
                                                case STARTSWITH:
                                                    lineMap.put(column, targetValue.replaceAll("^" + regex, value));
                                                    break;
                                                case ENDSWITH:
                                                    lineMap.put(column, targetValue.replaceAll(regex + "$", value));
                                                    break;
                                                default:
                                                    lineMap.put(column, targetValue.replaceAll(regex, value));
                                            }
                                        }
                                    } else {
                                        lineMap.put(column, value);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
