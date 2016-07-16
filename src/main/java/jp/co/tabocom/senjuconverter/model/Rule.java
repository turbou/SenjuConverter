package jp.co.tabocom.senjuconverter.model;

import java.util.ArrayList;
import java.util.List;

public class Rule {

    private List<Convert> systemConvertList;
    private List<Convert> netConvertList;
    private List<Convert> jobConvertList;
    private List<Convert> triggerConvertList;

    public Rule() {
        this.systemConvertList = new ArrayList<Convert>();
        this.netConvertList = new ArrayList<Convert>();
        this.jobConvertList = new ArrayList<Convert>();
        this.triggerConvertList = new ArrayList<Convert>();
    }

    public void addSystemConvert(Convert convert) {
        this.systemConvertList.add(convert);
    }

    public void addNetConvert(Convert convert) {
        this.netConvertList.add(convert);
    }

    public void addJobConvert(Convert convert) {
        this.jobConvertList.add(convert);
    }

    public void addTriggerConvert(Convert convert) {
        this.triggerConvertList.add(convert);
    }

    public List<Convert> getSystemConvertList() {
        return systemConvertList;
    }

    public List<Convert> getNetConvertList() {
        return netConvertList;
    }

    public List<Convert> getJobConvertList() {
        return jobConvertList;
    }

    public List<Convert> getTriggerConvertList() {
        return triggerConvertList;
    }

}
