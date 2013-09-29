package org.github.pister.wsearch.core.doc.field.fields;

import org.apache.lucene.document.NumericField;

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
}
