package jp.co.tabocom.senjuconverter.preference;

public class Settings {
    private String localRuleXml;
    private String defaultSnjDir;
    private String defaultOutDir;
    private String ignoreSenjuFiles;
    private Boolean ignoreButCopy;
    private Boolean outputSiteMerge;
    private Boolean convertSrcOutputWith;
    private String defaultNetValidDate;

    public String getLocalRuleXml() {
        return localRuleXml;
    }

    public void setLocalRuleXml(String localRuleXml) {
        this.localRuleXml = localRuleXml;
    }

    public String getDefaultSnjDir() {
        return defaultSnjDir;
    }

    public void setDefaultSnjDir(String defaultSnjDir) {
        this.defaultSnjDir = defaultSnjDir;
    }

    public String getDefaultOutDir() {
        return defaultOutDir;
    }

    public void setDefaultOutDir(String defaultOutDir) {
        this.defaultOutDir = defaultOutDir;
    }

    public String getIgnoreSenjuFiles() {
        return ignoreSenjuFiles;
    }

    public void setIgnoreSenjuFiles(String ignoreSenjuFiles) {
        this.ignoreSenjuFiles = ignoreSenjuFiles;
    }

    public Boolean getIgnoreButCopy() {
        return ignoreButCopy;
    }

    public void setIgnoreButCopy(Boolean ignoreButCopy) {
        this.ignoreButCopy = ignoreButCopy;
    }

    public Boolean getOutputSiteMerge() {
        return outputSiteMerge;
    }

    public void setOutputSiteMerge(Boolean outputSiteMerge) {
        this.outputSiteMerge = outputSiteMerge;
    }

    public Boolean getConvertSrcOutputWith() {
        return convertSrcOutputWith;
    }

    public void setConvertSrcOutputWith(Boolean convertSrcOutputWith) {
        this.convertSrcOutputWith = convertSrcOutputWith;
    }

    public String getDefaultNetValidDate() {
        return defaultNetValidDate;
    }

    public void setDefaultNetValidDate(String defaultNetValidDate) {
        this.defaultNetValidDate = defaultNetValidDate;
    }

}
