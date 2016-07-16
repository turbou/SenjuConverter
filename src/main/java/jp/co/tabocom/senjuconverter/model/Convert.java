package jp.co.tabocom.senjuconverter.model;

import java.util.ArrayList;
import java.util.List;

public class Convert {
    private List<Search> searchList;
    private List<Rewrite> rewriteList;
    boolean deleteFlg;

    public Convert() {
        this.searchList = new ArrayList<Search>();
        this.rewriteList = new ArrayList<Rewrite>();
    }

    public void addSearch(String column, String value, String range) {
        this.searchList.add(new Search(column, value, range));
    }

    public void addRewrite(String column, String replace, String value, String range) {
        this.rewriteList.add(new Rewrite(column, replace, value, range));
    }

    public List<Search> getSearchList() {
        return searchList;
    }

    public List<Rewrite> getRewriteList() {
        return rewriteList;
    }

    public void setDeleteOn() {
        this.deleteFlg = true;
    }

    public boolean isDeleteFlg() {
        return deleteFlg;
    }

    public String getSearchString() {
        StringBuilder builder = new StringBuilder();
        for (Search search : this.searchList) {
            builder.append(search.getSearchStr());
        }
        return builder.toString();
    }
}
