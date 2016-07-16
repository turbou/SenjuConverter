package jp.co.tabocom.senjuconverter.model;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.digester.Digester;

public class RuleDefinition {
    private byte[] xmlData;
    private String version;
    private Message message;
    private Map<String, Site> siteMap;

    public RuleDefinition() {
        this.siteMap = new LinkedHashMap<String, Site>();
    }

    public RuleDefinition(byte[] xmlData) {
        this.xmlData = xmlData;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public void addSite(Site site) {
        this.siteMap.put(site.getId(), site);
    }

    public Map<String, Site> getSiteMap() {
        return siteMap;
    }

    public void setSiteMap(Map<String, Site> siteMap) {
        this.siteMap = siteMap;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getXmlStr() {
        return new String(this.xmlData);
    }

    public void initialize() throws Exception {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            Digester digester = new Digester(parser);
            RuleDefinition definition;

            // ========== RuleDefinition ========== //
            digester.addObjectCreate("rule", RuleDefinition.class);
            digester.addSetProperties("rule");

            digester.addObjectCreate("rule/message", Message.class);
            digester.addBeanPropertySetter("rule/message/endWarning");
            digester.addSetRoot("rule/message", "setMessage");

            // ========== 各サイトごとのルール定義 ========== //
            digester.addObjectCreate("rule/sites", HashMap.class);
            digester.addObjectCreate("rule/sites/site", Site.class);
            digester.addSetProperties("rule/sites/site");
            digester.addObjectCreate("rule/sites/site/rule", Rule.class);

            // System
            digester.addObjectCreate("rule/sites/site/rule/system/convert", Convert.class);
            digester.addCallMethod("rule/sites/site/rule/system/convert/search", "addSearch", 3);
            digester.addCallParam("rule/sites/site/rule/system/convert/search", 0, "column");
            digester.addCallParam("rule/sites/site/rule/system/convert/search", 1, "value");
            digester.addCallParam("rule/sites/site/rule/system/convert/search", 2, "range");
            digester.addCallMethod("rule/sites/site/rule/system/convert/rewrite", "addRewrite", 4);
            digester.addCallParam("rule/sites/site/rule/system/convert/rewrite", 0, "column");
            digester.addCallParam("rule/sites/site/rule/system/convert/rewrite", 1, "replace");
            digester.addCallParam("rule/sites/site/rule/system/convert/rewrite", 2, "value");
            digester.addCallParam("rule/sites/site/rule/system/convert/rewrite", 3, "range");
            digester.addCallMethod("rule/sites/site/rule/system/convert/delete", "setDeleteOn");
            digester.addSetNext("rule/sites/site/rule/system/convert", "addSystemConvert");

            // Net
            digester.addObjectCreate("rule/sites/site/rule/net/convert", Convert.class);
            digester.addCallMethod("rule/sites/site/rule/net/convert/search", "addSearch", 3);
            digester.addCallParam("rule/sites/site/rule/net/convert/search", 0, "column");
            digester.addCallParam("rule/sites/site/rule/net/convert/search", 1, "value");
            digester.addCallParam("rule/sites/site/rule/net/convert/search", 2, "range");
            digester.addCallMethod("rule/sites/site/rule/net/convert/rewrite", "addRewrite", 4);
            digester.addCallParam("rule/sites/site/rule/net/convert/rewrite", 0, "column");
            digester.addCallParam("rule/sites/site/rule/net/convert/rewrite", 1, "replace");
            digester.addCallParam("rule/sites/site/rule/net/convert/rewrite", 2, "value");
            digester.addCallParam("rule/sites/site/rule/net/convert/rewrite", 3, "range");
            digester.addCallMethod("rule/sites/site/rule/net/convert/delete", "setDeleteOn");
            digester.addSetNext("rule/sites/site/rule/net/convert", "addNetConvert");

            // Job
            digester.addObjectCreate("rule/sites/site/rule/job/convert", Convert.class);
            digester.addCallMethod("rule/sites/site/rule/job/convert/search", "addSearch", 3);
            digester.addCallParam("rule/sites/site/rule/job/convert/search", 0, "column");
            digester.addCallParam("rule/sites/site/rule/job/convert/search", 1, "value");
            digester.addCallParam("rule/sites/site/rule/job/convert/search", 2, "range");
            digester.addCallMethod("rule/sites/site/rule/job/convert/rewrite", "addRewrite", 4);
            digester.addCallParam("rule/sites/site/rule/job/convert/rewrite", 0, "column");
            digester.addCallParam("rule/sites/site/rule/job/convert/rewrite", 1, "replace");
            digester.addCallParam("rule/sites/site/rule/job/convert/rewrite", 2, "value");
            digester.addCallParam("rule/sites/site/rule/job/convert/rewrite", 3, "range");
            digester.addCallMethod("rule/sites/site/rule/job/convert/delete", "setDeleteOn");
            digester.addSetNext("rule/sites/site/rule/job/convert", "addJobConvert");

            // Trigger
            digester.addObjectCreate("rule/sites/site/rule/trigger/convert", Convert.class);
            digester.addCallMethod("rule/sites/site/rule/trigger/convert/search", "addSearch", 3);
            digester.addCallParam("rule/sites/site/rule/trigger/convert/search", 0, "column");
            digester.addCallParam("rule/sites/site/rule/trigger/convert/search", 1, "value");
            digester.addCallParam("rule/sites/site/rule/trigger/convert/search", 2, "range");
            digester.addCallMethod("rule/sites/site/rule/trigger/convert/rewrite", "addRewrite", 4);
            digester.addCallParam("rule/sites/site/rule/trigger/convert/rewrite", 0, "column");
            digester.addCallParam("rule/sites/site/rule/trigger/convert/rewrite", 1, "replace");
            digester.addCallParam("rule/sites/site/rule/trigger/convert/rewrite", 2, "value");
            digester.addCallParam("rule/sites/site/rule/trigger/convert/rewrite", 3, "range");
            digester.addCallMethod("rule/sites/site/rule/trigger/convert/delete", "setDeleteOn");
            digester.addSetNext("rule/sites/site/rule/trigger/convert", "addTriggerConvert");

            digester.addSetNext("rule/sites/site/rule", "setRule");

            // Site追加繰返し
            digester.addSetRoot("rule/sites/site", "addSite");

            try {
                definition = (RuleDefinition) digester.parse(new ByteArrayInputStream(this.xmlData));
                setVersion(definition.getVersion());
                setMessage(definition.getMessage());
                setSiteMap(definition.getSiteMap());
            } catch (Exception e) {
                setVersion(null);
                throw e;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
