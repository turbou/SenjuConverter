package jp.co.tabocom.senjuconverter.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Site implements Serializable {

    private static final long serialVersionUID = -5271962284368731165L;

    private String id;
    private String name;
    private String validDateKey;
    private String mergeGroup;
    private Rule rule;

    public Site() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValidDateKey() {
        return validDateKey;
    }

    public void setValidDateKey(String validDateKey) {
        this.validDateKey = validDateKey;
    }

    public String getMergeGroup() {
        return mergeGroup;
    }

    public void setMergeGroup(String mergeGroup) {
        this.mergeGroup = mergeGroup;
    }

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    public String duplicateChk() {
        StringBuilder builder = new StringBuilder();
        Set<String> chkSet = new HashSet<String>();
        // System
        for (Convert convert : this.rule.getSystemConvertList()) {
            if (!chkSet.add(convert.getSearchString())) {
                builder.append(String.format("%-5s - SYSTEM : %s\r\n", this.id, convert.getSearchString()));
            }
        }
        // Net
        chkSet.clear();
        for (Convert convert : this.rule.getNetConvertList()) {
            if (!chkSet.add(convert.getSearchString())) {
                builder.append(String.format("%-5s - NET    : %s\r\n", this.id, convert.getSearchString()));
            }
        }
        // Job
        chkSet.clear();
        for (Convert convert : this.rule.getJobConvertList()) {
            if (!chkSet.add(convert.getSearchString())) {
                builder.append(String.format("%-5s - JOB    : %s\r\n", this.id, convert.getSearchString()));
            }
        }
        // Trigger
        chkSet.clear();
        for (Convert convert : this.rule.getTriggerConvertList()) {
            if (!chkSet.add(convert.getSearchString())) {
                builder.append(String.format("%-5s - TRIGGER: %s\r\n", this.id, convert.getSearchString()));
            }
        }
        return builder.toString();
    }
}
