package org.github.pister.wsearch.core.schema;

import org.apache.lucene.analysis.Analyzer;
import org.github.pister.wsearch.core.doc.field.FieldInfo;

import java.io.Serializable;
import java.util.Map;

/**
 * User: longyi
 * Date: 13-9-28
 * Time: 下午10:22
 */
public class Schema implements Serializable {

    private static final long serialVersionUID = -903262651256505953L;

    private MetaInfo metaInfo;

    private String idName;

    private String defaultSearchField;

    private Map<String, FieldInfo> fieldInfos;

    private Analyzer analyzer;

    public void setIdName(String idName) {
        this.idName = idName;
    }

    public String getIdName() {
        return idName;
    }

    public Map<String, FieldInfo> getFieldInfos() {
        return fieldInfos;
    }

    public void setFieldInfos(Map<String, FieldInfo> fieldInfos) {
        this.fieldInfos = fieldInfos;
    }

    public MetaInfo getMetaInfo() {
        return metaInfo;
    }

    public void setMetaInfo(MetaInfo metaInfo) {
        this.metaInfo = metaInfo;
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
