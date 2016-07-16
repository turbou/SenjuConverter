package jp.co.tabocom.senjuconverter.model;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 作業に必要な情報をまとめて持つオブジェクトです。
 * 
 * @author turbou
 * 
 */
public class WorkData {

    /* 指定年月 */
    private String targetMonth;
    /* 認証サーバのアドレス(本番・UATの場合はAudit G/W、開発の場合はプロキシサーバ) */
    private String authAddress;
    /* 集計対象が格納されているサーバのアドレス */
    private String targetAddress;
    /* 認証ユーザー(AudioG/W ID or Proxy ID) */
    private String authUsr;
    /* 認証パスワード(AudioG/W パスワード or Proxy パスワード) */
    private String authPwd;
    /* サーバのログインユーザー */
    private String loginUsr;
    /* サーバのログインパスワード */
    private String loginPwd;
    /* 集計対象のファイルパス */
    private String targetFile;
    /* 集計結果の一時保存場所(ローカル) */
    private String localSaveDir;
    /* 集計結果の出力場所(ローカル) */
    private String resultDir;
    /* 集計結果のファイル名 */
    private String resultFileName;
    /* 集計時の除外条件マップ(Key:画面名、Value:除外する条件項目[複数]) */
    private Map<String, List<String>> excludeMap;

    public String getTargetMonth() {
        return targetMonth;
    }

    public void setTargetMonth(String targetMonth) {
        this.targetMonth = targetMonth;
    }

    public String getAuthAddress() {
        return authAddress;
    }

    public void setAuthAddress(String authAddress) {
        this.authAddress = authAddress;
    }

    public String getTargetAddress() {
        return targetAddress;
    }

    public void setTargetAddress(String targetAddress) {
        this.targetAddress = targetAddress;
    }

    public String getAuthUsr() {
        return authUsr;
    }

    public void setAuthUsr(String authUsr) {
        this.authUsr = authUsr;
    }

    public String getAuthPwd() {
        return authPwd;
    }

    public void setAuthPwd(String authPwd) {
        this.authPwd = authPwd;
    }

    public String getLoginUsr() {
        return loginUsr;
    }

    public void setLoginUsr(String loginUsr) {
        this.loginUsr = loginUsr;
    }

    public String getLoginPwd() {
        return loginPwd;
    }

    public void setLoginPwd(String loginPwd) {
        this.loginPwd = loginPwd;
    }

    public String getTargetFile() {
        return targetFile;
    }

    public void setTargetFile(String targetFile) {
        this.targetFile = targetFile;
    }

    public String getLocalSaveDir() {
        return localSaveDir;
    }

    public void setLocalSaveDir(String localSaveDir) {
        this.localSaveDir = localSaveDir;
    }

    public String getResultDir() {
        return resultDir;
    }

    public void setResultDir(String resultDir) {
        this.resultDir = resultDir;
    }

    public String getResultFileName() {
        return resultFileName;
    }

    public void setResultFileName(String resultFileName) {
        this.resultFileName = resultFileName;
    }

    public Map<String, List<String>> getExcludeMap() {
        return excludeMap;
    }

    public void setExcludeMap(Map<String, List<String>> excludeMap) {
        this.excludeMap = excludeMap;
    }

    public boolean isError() {
        boolean flg = false;
        if (targetMonth.isEmpty())
            flg |= true;
        if (authAddress.isEmpty())
            flg |= true;
        if (targetAddress.isEmpty())
            flg |= true;
        if (loginUsr.isEmpty())
            flg |= true;
        if (loginPwd.isEmpty())
            flg |= true;
        if (targetFile.isEmpty())
            flg |= true;
        if (localSaveDir.isEmpty())
            flg |= true;
        if (resultDir.isEmpty())
            flg |= true;
        if (resultFileName.isEmpty())
            flg |= true;
        return flg;
    }

    public String getCorrectPath() {
        String repStr = new String(targetFile);
        if (repStr.contains("YYYYMM")) {
            repStr = repStr.replaceAll("YYYYMM", targetMonth);
        }

        if (!repStr.contains("$")) {
            return repStr;
        }
        String men = loginUsr.substring(loginUsr.length() - 2); // ユーザー名から面情報を取得(例：01)
        if (repStr.startsWith("$APL_DIR")) {
            repStr = repStr.replaceAll(Pattern.quote("$APL_DIR"), String.format("/APL/group%s/local", men));
        } else if (repStr.startsWith("$APL_PKG01_DIR")) {
            repStr = repStr.replaceAll(Pattern.quote("$APL_PKG01_DIR"), String.format("/APL/group%s/pkg01", men));
        } else if (repStr.startsWith("$WEBADM")) {
            repStr = repStr.replaceAll(Pattern.quote("$WEBADM"), String.format("/webadm%s", men));
        }
        return repStr;
    }

    public String getCorrectFileName() {
        String fileName = getCorrectPath().substring(getCorrectPath().lastIndexOf("/"), getCorrectPath().length());
        return fileName;
    }

    public String getResultFileFullPath() {
        StringBuilder builder = new StringBuilder(resultDir);
        builder.append("\\");
        if (resultFileName.contains("YYYYMM")) {
            builder.append(resultFileName.replaceAll("YYYYMM", targetMonth));
        } else {
            builder.append(resultFileName);
        }
        builder.append(".xlsx");
        return builder.toString();
    }

}
