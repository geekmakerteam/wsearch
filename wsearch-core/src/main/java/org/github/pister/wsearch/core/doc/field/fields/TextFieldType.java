package org.github.pister.wsearch.core.doc.field.fields;

/**
 * User: longyi
 * Date: 13-9-29
 * Time: 下午3:32
 */
public class TextFieldType extends StringFieldType {

    @Override
    protected boolean needAnalyse() {
        return true;
    }

}
