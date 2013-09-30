package org.github.pister.wsearch.core.doc.field.fields;

import org.apache.lucene.document.NumericField;
import org.apache.lucene.search.SortField;
import org.github.pister.wsearch.core.util.date.SmartDateConvert;

import java.util.Date;

/**
 * User: longyi
 * Date: 13-9-29
 * Time: 下午4:50
 */
public class DateFieldType extends AbstractNumericFieldType {

    private SmartDateConvert smartDateConvert = new SmartDateConvert();

    private long parseDate(String text) {
        Date date = smartDateConvert.convertTo(text, null);
        if (date == null) {
            return 0L;
        }
        return date.getTime();
    }

    @Override
    protected Object getValue(NumericField numericField) {
        Number number = numericField.getNumericValue();
        if (number != null) {
            return new Date(number.longValue());
        }
        return null;
    }

    @Override
    public int getSortType() {
        return SortField.LONG;
    }


    @Override
    protected void setValue(NumericField numericField, Object value) {
        if (value instanceof Date) {
            numericField.setLongValue(((Date)value).getTime());
        } else if (value instanceof Number) {
            numericField.setLongValue(((Number)value).longValue());
        } else {
            numericField.setLongValue(parseDate(value.toString()));
        }
    }
}
