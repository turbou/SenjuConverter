package jp.co.tabocom.senjuconverter.model;

import java.util.ArrayList;
import java.util.List;

/**
 * アクセスジャーナルログの１行に相当するオブジェクト
 * 
 * @author turbou
 * 
 */
public class JournalLine {

    /* 画面名(なんちゃらAction.do的な) */
    private String screenName;
    /* アクセス日時 */
    private String timestamp;
    /* 所要時間 */
    private String processTime;
    /* 条件項目文字列(カンマ区切りになっている) */
    private String condition;

    public JournalLine(String screenName, String timestamp, String processTime, String condition) {
        this.screenName = screenName;
        this.timestamp = timestamp;
        this.processTime = processTime;
        this.condition = condition;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getDisplayTimeStamp() {
        return timestamp.replaceAll("\\..*$", "");
    }

    public String getProcessTime() {
        return processTime;
    }

    public String getCondition() {
        return condition;
    }

    /**
     * 条件項目文字列の中から有効な条件項目を洗い出してListに格納して返す。 条件項目の文字列は<br>
     * [key1]=[value],[key2]=[value] の形式になっており、valueが空でない 条件を有効な条件項目として抽出している。
     * 
     * @return 有効な条件項目のList<String>
     */
    public List<String> getValidCondition() {
        List<String> list = new ArrayList<String>();
        for (String condPart : this.condition.split(",")) {
            if (!condPart.contains("=")) {
                continue;
            }
            String key = condPart.split("=")[0].trim();
            String val = condPart.split("=")[1].trim();
            String realVal = val.replaceAll("^\\[|\\]$", "").trim();
            // 値が空、パスワード系、_ALLの場合はその条件は無視する。
            if (realVal.isEmpty() || realVal.equals("*****") || realVal.equals("_ALL")) {
                continue;
            }
            list.add(key);
        }
        return list;
    }
}
