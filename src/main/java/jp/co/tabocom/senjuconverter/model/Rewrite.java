package jp.co.tabocom.senjuconverter.model;

public class Rewrite {

    private String column;
    private RegexTypeEnum columnRegType;
    private String replace;
    private RegexTypeEnum replaceRegType;
    private String value;
    private String range;

    public Rewrite(String column, String replace, String value, String range) {
        this.column = column.replaceAll("\\*", "");
        this.columnRegType = getRegType(column);
        if (replace != null) {
            this.replace = replace.replaceAll("\\*", "");
            this.replaceRegType = getRegType(replace);
        }
        this.value = value;
        this.range = range;
    }

    public String getColumn() {
        return column;
    }

    public RegexTypeEnum getColumnRegType() {
        return columnRegType;
    }

    public String getReplace() {
        return replace;
    }

    public RegexTypeEnum getReplaceRegType() {
        return replaceRegType;
    }

    public String getValue() {
        return value;
    }

    public String getRange() {
        return range;
    }

    private RegexTypeEnum getRegType(String str) {
        if (str.startsWith("*") && str.endsWith("*")) {
            return RegexTypeEnum.CONTAINS;
        } else if (str.endsWith("*")) {
            return RegexTypeEnum.STARTSWITH;
        } else if (str.startsWith("*")) {
            return RegexTypeEnum.ENDSWITH;
        }
        return RegexTypeEnum.NONE;
    }
}
