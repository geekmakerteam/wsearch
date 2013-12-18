package org.github.pister.wsearch.core.config.parser;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.github.pister.wsearch.core.doc.field.FieldInfo;
import org.github.pister.wsearch.core.schema.Schema;
import org.github.pister.wsearch.core.util.ClassUtil;
import org.github.pister.wsearch.core.util.LuceneConfig;
import org.github.pister.wsearch.core.util.StringUtil;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * User: longyi
 * Date: 13-12-18
 * Time: 下午7:43
 */
public class SchemaParser {

    private static final String DEFAULT_ANALYZER = "default";

    private static final String DEFAULT_ID_NAME = "id";
    private static final String DEFAULT_DEFAULT_FIELD = "search_content";

    protected Analyzer createAnalyzer(String analyzer) {
        if (DEFAULT_ANALYZER.equals(analyzer)) {
            return new StandardAnalyzer(LuceneConfig.LUCENE_VERSION);
        }
        return (Analyzer)ClassUtil.newInstance(analyzer);
    }

    protected String getIdName(String idName) {
        if (StringUtil.isBlank(idName)) {
            return DEFAULT_ID_NAME;
        }
        return idName.trim();
    }

    protected String getDefaultField(String defaultField) {
        if (StringUtil.isBlank(defaultField)) {
            return DEFAULT_DEFAULT_FIELD;
        }
        return defaultField.trim();
    }


    public Schema parse(InputStream is) throws Exception {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        XPathExpression idNameExpr = xpath.compile("schema/idName/text()");
        XPathExpression defaultFieldExpr = xpath.compile("schema/defaultField/text()");
        XPathExpression analyzerExpr = xpath.compile("schema/analyzer/text()");
        XPathExpression fieldsListExpr = xpath.compile("schema/fields/field");

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
        Document document = builder.parse(is);

        String idName = (String)idNameExpr.evaluate(document, XPathConstants.STRING);
        String defaultField = (String)defaultFieldExpr.evaluate(document, XPathConstants.STRING);
        String analyzer = (String)analyzerExpr.evaluate(document, XPathConstants.STRING);
        NodeList fieldsList = (NodeList)fieldsListExpr.evaluate(document, XPathConstants.NODESET);

        Schema schema = new Schema();
        schema.setIdName(getIdName(idName));
        schema.setDefaultSearchField(getDefaultField(defaultField));
        schema.setAnalyzer(createAnalyzer(analyzer));

        if (fieldsList == null || fieldsList.getLength() == 0) {
            throw new RuntimeException("fields/field can not be empty!");
        }

        for (int i = 0, len = fieldsList.getLength(); i < len; i++) {
            Node fieldNode = fieldsList.item(i);
            NamedNodeMap namedNodeMap = fieldNode.getAttributes();
            String name = getAttrString(namedNodeMap, "name", null);
            if (StringUtil.isEmpty(name)) {
                throw new RuntimeException("field's name can not be empty!");
            }
            FieldInfo fieldInfo = new FieldInfo(name);
            fieldInfo.setIndex(getAttrBoolean(namedNodeMap, "index", false));
            fieldInfo.setStore(getAttrBoolean(namedNodeMap, "store", false));
            fieldInfo.setType(getAttrString(namedNodeMap, "type", null));
            fieldInfo.setPrecisionStep(getAttrInt(namedNodeMap, "precisionStep", 0));
            fieldInfo.setNorms(getAttrBoolean(namedNodeMap, "norms", false));
            fieldInfo.setBoost(getAttrFloat(namedNodeMap, "boost", 0.0f));

            schema.addFieldInfo(fieldInfo);
        }


        return schema;
    }

    private static String getAttrString(NamedNodeMap namedNodeMap, String name, String defaultValue) {
        Node node = namedNodeMap.getNamedItem(name);
        if (node == null) {
            return defaultValue;
        }
        String value = node.getNodeValue();
        if (StringUtil.isEmpty(value)) {
            return defaultValue;
        }
        return value.trim();
    }

    private static int getAttrInt(NamedNodeMap namedNodeMap, String name, int defaultValue) {
        Node node = namedNodeMap.getNamedItem(name);
        if (node == null) {
            return defaultValue;
        }
        String value = node.getNodeValue();
        if (StringUtil.isEmpty(value)) {
            return defaultValue;
        }
        return Integer.parseInt(value.trim());
    }

    private static boolean getAttrBoolean(NamedNodeMap namedNodeMap, String name, boolean defaultValue) {
        Node node = namedNodeMap.getNamedItem(name);
        if (node == null) {
            return defaultValue;
        }
        String value = node.getNodeValue();
        if (StringUtil.isEmpty(value)) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value.trim());
    }

    private static float getAttrFloat(NamedNodeMap namedNodeMap, String name, float defaultValue) {
        Node node = namedNodeMap.getNamedItem(name);
        if (node == null) {
            return defaultValue;
        }
        String value = node.getNodeValue();
        if (StringUtil.isEmpty(value)) {
            return defaultValue;
        }
        return Float.parseFloat(value.trim());
    }

    public static void main(String[] args) throws Exception {
        FileInputStream is = new FileInputStream("/Users/longyi/work/open_source/wsearch/wsearch-core/src/main/resources/schema-template.xml");
        SchemaParser parser = new SchemaParser();
        parser.parse(is);
        is.close();
    }


}
