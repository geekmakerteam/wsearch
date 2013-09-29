package org.github.pister.wsearch.core.doc.field.fields;

import org.apache.lucene.document.NumericField;
import org.apache.lucene.search.SortField;

/**
 * User: longyi
 * Date: 13-9-29
 * Time: 下午4:50
 */
public class FloatFieldType extends AbstractNumericFieldType {
    @Override
    protected void setValue(NumericField numericField, String value) {
        numericField.setFloatValue(Float.parseFloat(value));
    }

    @Override
    public int getSortType() {
        return SortField.FLOAT;
    }
}
