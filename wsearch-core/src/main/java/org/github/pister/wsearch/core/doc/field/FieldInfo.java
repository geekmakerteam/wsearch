package org.github.pister.wsearch.core.doc.field;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.util.NumericUtils;
import org.github.pister.wsearch.core.doc.field.fields.FieldType;
import org.github.pister.wsearch.core.doc.field.fields.FieldTypeFactory;
import org.github.pister.wsearch.core.util.ClassUtil;

import java.io.Serializable;

/**
 * User: longyi
 * Date: 13-9-28
 * Time: 下午9:51
 */
public class FieldInfo implements Serializable {

    private static final long serialVersionUID = 4859698004917928019L;
    private String name;
    private boolean index;
    private boolean store;
    private Float boost;
    private boolean norms = false;
    private Field.TermVector termVector = Field.TermVector.NO;
    private FieldType type = FieldTypeFactory.DEFAULT_FIELD_TYPE;
    private int precisionStep = NumericUtils.PRECISION_STEP_DEFAULT;

    public FieldInfo(String name) {
        this.name = name;
    }

    public Fieldable createField(Object value) {
        return type.createField(this, value);
    }

    public Float getBoost() {
        return boost;
    }

    public FieldInfo setBoost(Float boost) {
        this.boost = boost;
        return this;
    }

    public Field.TermVector getTermVector() {
        return termVector;
    }

    public FieldInfo setTermVector(Field.TermVector termVector) {
        this.termVector = termVector;
        return this;
    }

    public boolean isNorms() {
        return norms;
    }

    public FieldInfo setNorms(boolean norms) {
        this.norms = norms;
        return this;
    }

    public boolean isIndex() {
        return index;
    }

    public FieldInfo setIndex(boolean index) {
        this.index = index;
        return this;
    }

    public String getName() {
        return name;
    }

    public boolean isStore() {
        return store;
    }

    public FieldInfo setStore(boolean store) {
        this.store = store;
        return this;
    }

    public FieldType getFieldType() {
        return type;
    }

    public FieldInfo setType(String typeName) {
        FieldType type = FieldTypeFactory.getFieldType(typeName);
        if (type != null) {
           this.type = type;
        } else {
            FieldType fieldType = (FieldType) ClassUtil.newInstance(typeName);
            this.type = fieldType;
        }
        return this;
    }

    public int getPrecisionStep() {
        return precisionStep;
    }

    public FieldInfo setPrecisionStep(int precisionStep) {
        this.precisionStep = precisionStep;
        return this;
    }
}
