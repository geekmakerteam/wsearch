package org.github.pister.wsearch.core.doc.field;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.util.NumericUtils;
import org.github.pister.wsearch.core.doc.field.fields.FieldType;

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

    private FieldType type;

    private int precisionStep = NumericUtils.PRECISION_STEP_DEFAULT;

    public Fieldable createField(String value) {
         return type.createField(this, value);
    }

    public Float getBoost() {
        return boost;
    }

    public void setBoost(Float boost) {
        this.boost = boost;
    }

    public Field.TermVector getTermVector() {
        return termVector;
    }

    public void setTermVector(Field.TermVector termVector) {
        this.termVector = termVector;
    }

    public boolean isNorms() {
        return norms;
    }

    public void setNorms(boolean norms) {
        this.norms = norms;
    }

    public boolean isIndex() {
        return index;
    }

    public void setIndex(boolean index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isStore() {
        return store;
    }

    public void setStore(boolean store) {
        this.store = store;
    }

    public FieldType getType() {
        return type;
    }

    public void setType(FieldType type) {
        this.type = type;
    }

    public int getPrecisionStep() {
        return precisionStep;
    }

    public void setPrecisionStep(int precisionStep) {
        this.precisionStep = precisionStep;
    }
}
