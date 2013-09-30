package org.github.pister.wsearch.core.doc;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;
import org.github.pister.wsearch.core.doc.field.FieldInfo;
import org.github.pister.wsearch.core.schema.Schema;
import org.github.pister.wsearch.core.searcher.query.OutputDocument;
import org.github.pister.wsearch.core.util.CollectionUtil;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * User: longyi
 * Date: 13-9-29
 * Time: 下午1:52
 */
public class DocumentTransformUtil {

    public static OutputDocument toOutputDocument(Document document, Schema schema) {
        if (document == null) {
            return null;
        }
        Map<String, FieldInfo> fieldInfoMap = schema.getFieldInfos();
        OutputDocument ret = new OutputDocument();
        List<Fieldable> fields = document.getFields();
        for (Fieldable fieldable : fields) {
            String name = fieldable.name();
            FieldInfo fieldInfo = fieldInfoMap.get(name);
            Object value;
            if (fieldInfo != null) {
               value = fieldInfo.getFieldType().getValue(fieldable);
            } else {
                value = fieldable.stringValue();
            }
            ret.addField(name, value);
        }
        return ret;
    }

    public static Document toLuceneDocument(InputDocument inputDocument, Schema schema) {
        Map<String, FieldInfo> fieldInfoMap = schema.getFieldInfos();
        Document document = new Document();
        for (Map.Entry<String, String> entry : inputDocument.getFields().entrySet()) {
            document.add(createField(entry.getKey(), entry.getValue(), fieldInfoMap));
        }
        return document;
    }

    private static Fieldable createField(String name, String value, Map<String, FieldInfo> fieldInfoMap) {
        FieldInfo fieldInfo = fieldInfoMap.get(name);
        if (fieldInfo == null) {
            throw new RuntimeException("field " + name + " not config!");
        }
        return fieldInfo.createField(value);
    }

    public static List<Document> toLuceneDocuments(Collection<InputDocument> inputDocuments, Schema schema) {
        if (CollectionUtil.isEmpty(inputDocuments)) {
            return CollectionUtil.newArrayList(0);
        }
        List<Document> ret = CollectionUtil.newArrayList(inputDocuments.size());
        for (InputDocument inputDocument : inputDocuments) {
            ret.add(toLuceneDocument(inputDocument, schema));
        }
        return ret;
    }
}
