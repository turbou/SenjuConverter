package jp.co.tabocom.senjuconverter.model;

public class Search {

    private String column;
    private RegexTypeEnum columnRegType;
    private String value;
    private RegexTypeEnum valueRegType;
    private String orgColumn;
    private String orgValue;
    private String range;

    public Search(String column, String value, String range) {
        this.column = column.replaceAll("\\*", "");
        this.columnRegType = getRegType(column);
        this.value = value.replaceAll("\\*", "");
        this.valueRegType = getRegType(value);
        this.orgColumn = column;
        this.orgValue = value;
        this.range = range;
    }

    public String getColumn() {
        return column;
    }

    public RegexTypeEnum getColumnRegType() {
        return columnRegType;
    }

    public String getValue() {
        return value;
    }

    public String getRange() {
        return range;
    }

    public RegexTypeEnum getValueRegType() {
        return valueRegType;
    }

    public String getSearchStr() {
        return orgColumn + "_" + orgValue;
    }

    private RegexTypeEnum getRegType(String str) {
        if (str == null || str.isEmpty()) {
            return RegexTypeEnum.UNCONDITIONAL;
        } else if (str.equals("*")) {
            return RegexTypeEnum.NOTEMPTY;
        } else if (str.startsWith("*") && str.endsWith("*")) {
            return RegexTypeEnum.CONTAINS;
        } else if (str.endsWith("*")) {
            return RegexTypeEnum.STARTSWITH;
        } else if (str.startsWith("*")) {
            return RegexTypeEnum.ENDSWITH;
        }
        return RegexTypeEnum.NONE;
    }
}
