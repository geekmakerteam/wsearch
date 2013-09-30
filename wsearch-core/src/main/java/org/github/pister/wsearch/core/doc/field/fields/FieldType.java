package org.github.pister.wsearch.core.doc.field.fields;

import org.apache.lucene.document.Fieldable;
import org.github.pister.wsearch.core.doc.field.FieldInfo;

/**
 * User: longyi
 * Date: 13-9-29
 * Time: 下午3:06
 */
public interface FieldType {

    Fieldable createField(FieldInfo fieldInfo, String value);

    Object getValue(Fieldable fieldable);

    int getSortType();


}
