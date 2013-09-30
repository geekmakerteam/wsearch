package org.github.pister.wsearch.core.schema;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.github.pister.wsearch.core.doc.field.FieldInfo;
import org.github.pister.wsearch.core.util.LuceneConfig;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * User: longyi
 * Date: 13-9-28
 * Time: 下午10:22
 */
public class Schema implements Serializable {

    private static final long serialVersionUID = -903262651256505953L;

    private SchemaMeta schemaMeta;

    private String idName = "id";

    private String defaultSearchField;

    private Map<String, FieldInfo> fieldInfos = new HashMap<String, FieldInfo>();

    private Analyzer analyzer = new StandardAnalyzer(LuceneConfig.LUCENE_VERSION);

    public Schema(SchemaMeta schemaMeta) {
        this.schemaMeta = schemaMeta;
    }

    public void setIdName(String idName) {
        this.idName = idName;
    }

    public String getIdName() {
        return idName;
    }

    public Map<String, FieldInfo> getFieldInfos() {
        return fieldInfos;
    }

    public void addFieldInfo(FieldInfo fieldInfo) {
        fieldInfos.put(fieldInfo.getName(), fieldInfo);
    }

    public SchemaMeta getSchemaMeta() {
        return schemaMeta;
    }

    public void setSchemaMeta(SchemaMeta schemaMeta) {
        this.schemaMeta = schemaMeta;
    }

    public Analyzer getAnalyzer() {
        return analyzer;
    }

    public void setAnalyzer(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    public String getDefaultSearchField() {
        return defaultSearchField;
    }

    public void setDefaultSearchField(String defaultSearchField) {
        this.defaultSearchField = defaultSearchField;
    }
}
