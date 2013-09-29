package org.github.pister.wsearch.core.doc.field.fields;

import org.apache.lucene.document.Fieldable;
import org.apache.lucene.document.NumericField;
import org.github.pister.wsearch.core.doc.field.FieldInfo;

/**
 * User: longyi
 * Date: 13-9-29
 * Time: 下午4:23
 */
public abstract class AbstractNumericFieldType extends AbstractFieldType {

    @Override
    public Fieldable createField(FieldInfo fieldInfo, String value) {
        NumericField numericField = new NumericField(fieldInfo.getName(), fieldInfo.getPrecisionStep(), getStore(fieldInfo), fieldInfo.isIndex());
        return numericField;
    }

    protected abstract void setValue(NumericField numericField, String value);


}
