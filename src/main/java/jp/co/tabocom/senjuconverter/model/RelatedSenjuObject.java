package jp.co.tabocom.senjuconverter.model;

import java.util.Set;
import java.util.TreeSet;

public class RelatedSenjuObject {
    private Set<String> systemList;
    private Set<String> netList;
    private Set<String> jobList;
    private Set<String> jobServiceList;
    private Set<String> trgList;

    public RelatedSenjuObject() {
        systemList = new TreeSet<String>();
        netList = new TreeSet<String>();
        jobList = new TreeSet<String>();
        jobServiceList = new TreeSet<String>();
        trgList = new TreeSet<String>();
    }

    public void addSystem(String system) {
        if (!this.systemList.contains(system)) {
            this.systemList.add(system);
        }
    }

    public void addNet(String net) {
        if (!this.netList.contains(net)) {
            this.netList.add(net);
        }
    }

    public void addJob(String job) {
        if (!this.jobList.contains(job)) {
            this.jobList.add(job);
        }
    }

    public void addJobService(String jobService) {
        if (!this.jobServiceList.contains(jobService)) {
            this.jobServiceList.add(jobService);
        }
    }

    public void addTrg(String trg) {
        if (!this.trgList.contains(trg)) {
            this.trgList.add(trg);
        }
    }

    public void addSystemList(Set<String> systemSet) {
        this.systemList.addAll(systemSet);
    }

    public void addNetList(Set<String> netSet) {
        this.netList.addAll(netSet);
    }

    public void addJobList(Set<String> jobSet) {
        this.jobList.addAll(jobSet);
    }

    public void addJobServiceList(Set<String> jobServiceSet) {
        this.jobServiceList.addAll(jobServiceSet);
    }

    public void addTrgList(Set<String> trgSet) {
        this.trgList.addAll(trgSet);
    }

    public Set<String> getSystemList() {
        return systemList;
    }

    public Set<String> getJobServiceList() {
        return jobServiceList;
    }

    public Set<String> getNetList() {
        return netList;
    }

    public Set<String> getJobList() {
        return jobList;
    }

    public Set<String> getTrgList() {
        return trgList;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("system:\r\n");
        for (String str : this.systemList) {
            builder.append(str + "\r\n");
        }
        builder.append("\r\n");
        builder.append("net:\r\n");
        for (String str : this.netList) {
            builder.append(str + "\r\n");
        }
        builder.append("\r\n");
        builder.append("job:\r\n");
        for (String str : this.jobList) {
            builder.append(str + "\r\n");
        }
        builder.append("\r\n");
        builder.append("jobservice:\r\n");
        for (String str : this.jobServiceList) {
            builder.append(str + "\r\n");
        }
        builder.append("\r\n");
        builder.append("trg:\r\n");
        for (String str : this.trgList) {
            builder.append(str + "\r\n");
        }
        builder.append("\r\n");
        return builder.toString();
    }

}
