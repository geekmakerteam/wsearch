package org.github.pister.wsearch.core.doc.field.fields;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.github.pister.wsearch.core.doc.field.FieldInfo;

/**
 * User: longyi
 * Date: 13-9-29
 * Time: 下午3:11
 */
public class StringFieldType extends AbstractFieldType {

    @Override
    public Fieldable createField(FieldInfo fieldInfo, String value) {
        Field.Store store = getStore(fieldInfo);
        Field field = new Field(fieldInfo.getName(), value, store, getIndex(fieldInfo), fieldInfo.getTermVector());
        return field;
    }

}