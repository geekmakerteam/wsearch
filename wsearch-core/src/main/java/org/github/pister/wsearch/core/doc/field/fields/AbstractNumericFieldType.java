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
    public Fieldable createField(FieldInfo fieldInfo, Object value) {
        NumericField numericField = new NumericField(fieldInfo.getName(), fieldInfo.getPrecisionStep(), getStore(fieldInfo), fieldInfo.isIndex());
        if (value != null) {
            setValue(numericField, value);
        }
        return numericField;
    }


    public Object getValue(Fieldable fieldable) {
        return getValue(((NumericField) fieldable));
    }

    protected abstract void setValue(NumericField numericField, Object value);

    protected abstract Object getValue(NumericField numericField);


}
