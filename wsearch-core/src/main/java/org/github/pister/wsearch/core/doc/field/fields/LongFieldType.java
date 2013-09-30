package org.github.pister.wsearch.core.doc.field.fields;

import org.apache.lucene.document.NumericField;
import org.apache.lucene.search.SortField;

/**
 * User: longyi
 * Date: 13-9-29
 * Time: 下午4:48
 */
public class LongFieldType extends AbstractNumericFieldType {
    @Override
    protected void setValue(NumericField numericField, String value) {
        numericField.setLongValue(Long.parseLong(value));
    }

    @Override
    protected Object getValue(NumericField numericField) {
        Number number = numericField.getNumericValue();
        if (number != null) {
            return number.longValue();
        }
        return null;
    }

    @Override
    public int getSortType() {
        return SortField.LONG;
    }
}
