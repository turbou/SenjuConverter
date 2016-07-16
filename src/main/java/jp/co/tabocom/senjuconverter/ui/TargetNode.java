package jp.co.tabocom.senjuconverter.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TargetNode implements Comparable<TargetNode>, PropertyChangeListener, Serializable {

    private static final long serialVersionUID = 8798516404046738828L;

    private String name;

    private List<TargetNode> i_children = new ArrayList<TargetNode>();

    private TargetNode i_parent;

    private boolean i_isEnabled;

    private boolean i_isGrayed;

    // ========== 実データここから ========== //
    // ----- XMLに定義されているデータ ----- //
    private String url;

    // ========== 実データここまで ========== //

    public void propertyChange(final PropertyChangeEvent event) {
    }

    public TargetNode() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TargetNode getParent() {
        return i_parent;
    }

    public TargetNode addChild(TargetNode child) {
        i_children.add(child);
        child.i_parent = this;
        return this;
    }

    public TargetNode addChildren(List<TargetNode> children) {
        if (children != null) {
            for (TargetNode child : children) {
                i_children.add(child);
                Collections.sort(i_children);
                child.i_parent = this;
            }
        }
        return this;
    }

    public List<TargetNode> getChildren() {
        return i_children;
    }

    public boolean isEnabled() {
        return i_isEnabled;
    }

    public void setIsEnabled(boolean enabled) {
        i_isEnabled = enabled;
    }

    public boolean isGrayed() {
        return i_isGrayed;
    }

    public void setIsGrayed(boolean grayed) {
        i_isGrayed = grayed;
    }

    public String toString() {
        return name;
    }

    public int compareTo(TargetNode o) {
        return name.compareTo(o.toString());
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
