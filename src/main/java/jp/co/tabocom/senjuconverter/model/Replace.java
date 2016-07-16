package jp.co.tabocom.senjuconverter.model;

public class Replace {

    private String value;
    private String type;

    public Replace(String value, String type) {
        this.value = value;
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public String getType() {
        return type;
    }
}
