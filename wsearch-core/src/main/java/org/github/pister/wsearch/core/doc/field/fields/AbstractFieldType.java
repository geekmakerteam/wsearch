package org.github.pister.wsearch.core.doc.field.fields;

import org.apache.lucene.document.Field;
import org.github.pister.wsearch.core.doc.field.FieldInfo;

/**
 * User: longyi
 * Date: 13-9-29
 * Time: 下午3:07
 */
public abstract class AbstractFieldType implements FieldType {

    protected Field.Store getStore(FieldInfo fieldInfo) {
        if (fieldInfo.isStore()) {
            return Field.Store.YES;
        } else {
            return Field.Store.NO;
        }
    }

    protected Field.Index getIndex(FieldInfo fieldInfo) {
        if (!fieldInfo.isIndex()) {
            return Field.Index.NO;
        }
        if (needAnalyse()) {
            if (fieldInfo.isNorms()) {
                return Field.Index.ANALYZED;
            } else {
                return Field.Index.ANALYZED_NO_NORMS;
            }
        } else {
            if (fieldInfo.isNorms()) {
                return Field.Index.NOT_ANALYZED;
            } else {
                return Field.Index.NOT_ANALYZED_NO_NORMS;
            }
        }
    }

    protected boolean needAnalyse() {
        return false;
    }

}
