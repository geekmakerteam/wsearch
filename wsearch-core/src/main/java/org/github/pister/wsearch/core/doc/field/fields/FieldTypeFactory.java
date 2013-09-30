package org.github.pister.wsearch.core.doc.field.fields;

import java.util.HashMap;
import java.util.Map;

/**
 * User: longyi
 * Date: 13-9-30
 * Time: 上午10:55
 */
public class FieldTypeFactory {

    private static Map<String, FieldType> namedFieldTypes = new HashMap<String, FieldType>();

    static  {
        namedFieldTypes.put("string", new StringFieldType());
        namedFieldTypes.put("text", new TextFieldType());
        namedFieldTypes.put("int", new IntFieldType());
        namedFieldTypes.put("long", new LongFieldType());
        namedFieldTypes.put("float", new FloatFieldType());
        namedFieldTypes.put("double", new DoubleFieldType());
        namedFieldTypes.put("date", new DateFieldType());
    }

    public static FieldType getFieldType(String name) {
         return namedFieldTypes.get(name);
    }

    public static final FieldType DEFAULT_FIELD_TYPE = new StringFieldType();

}
