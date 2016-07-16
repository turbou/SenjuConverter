package jp.co.tabocom.senjuconverter.preference;

public class PreferenceConstants {

    // プロキシアドレス
    public static final String PROXY_ADR = new String("jp.co.tabocom.senjuconverter.proxyAdr");
    // プロキシ認証ユーザー
    public static final String PROXY_USR = new String("jp.co.tabocom.senjuconverter.proxyUsr");
    // プロキシ認証パスワード
    public static final String PROXY_PWD = new String("jp.co.tabocom.senjuconverter.proxyPwd");
    // 千手変換ルールのローカルパス
    public static final String LOCAL_RULEXML = new String("jp.co.tabocom.senjuconverter.localRuleXml");
    // 千手変換ルールの保管リポジトリ
    public static final String REPOS_URL = new String("jp.co.tabocom.senjuconverter.reposUrl");
    // 変換元千手データのデフォルトの置き場所
    public static final String DEFAULT_SNJ_DIR = new String("jp.co.tabocom.senjuconverter.defaultSnjDir");
    // 変換後千手データのデフォルト出力先
    public static final String DEFAULT_OUT_DIR = new String("jp.co.tabocom.senjuconverter.defaultOutDir");
    // 変換対象外千手データファイル
    public static final String IGNORE_SENJU_FILES = new String("jp.co.tabocom.senjuconverter.ignoreSenjuFiles");
    // 変換しないファイルも出力先にそのままコピーするか否かのフラグ
    public static final String IGNORE_BUT_COPY = new String("jp.co.tabocom.senjuconverter.ignoreButCopy");
    // 変換ルールが複数サイト分ある場合それらをマージ出力するか否かのフラグ
    public static final String OUTPUT_SITE_MERGE = new String("jp.co.tabocom.senjuconverter.outputSiteMerge");
    // マージ出力する場合に変換元も一緒にマージするか否かのフラグ
    public static final String CONVERT_SRC_OUTWITH = new String("jp.co.tabocom.senjuconverter.convertSrcOutwith");
    // ネット定義有効日変更でのデフォルト日
    public static final String DEFAULT_NET_VALID_DATE = new String("jp.co.tabocom.senjuconverter.defaultNetValidDate");
}
