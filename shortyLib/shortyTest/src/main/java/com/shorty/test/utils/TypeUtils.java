package com.shorty.test.utils;


import com.shorty.test.enums.TypeKind;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static com.shorty.test.utils.Consts.BOOLEAN;
import static com.shorty.test.utils.Consts.BYTE;
import static com.shorty.test.utils.Consts.CHAR;
import static com.shorty.test.utils.Consts.DOUBEL;
import static com.shorty.test.utils.Consts.FLOAT;
import static com.shorty.test.utils.Consts.INTEGER;
import static com.shorty.test.utils.Consts.LONG;
//import static com.shorty.test.utils.Consts.PARCELABLE;
import static com.shorty.test.utils.Consts.PARCELABLE;
import static com.shorty.test.utils.Consts.SERIALIZABLE;
import static com.shorty.test.utils.Consts.SHORT;
import static com.shorty.test.utils.Consts.STRING;


/**
 * Utils for type exchange
 *
 * @author zhilong <a href="mailto:zhilong.lzl@alibaba-inc.com">Contact me.</a>
 * @version 1.0
 * @since 2017/2/21 下午1:06
 */
public class TypeUtils {

    private Types types;
    private TypeMirror parcelableType;
    private TypeMirror serializableType;

    public TypeUtils(Types types, Elements elements) {
        this.types = types;

        parcelableType = elements.getTypeElement(PARCELABLE).asType();
        serializableType = elements.getTypeElement(SERIALIZABLE).asType();
    }

    /**
     * Diagnostics out the true java type
     *
     * @param typeMirror Raw TypeMirror
     * @return Type class of java
     */
    public int typeExchange(TypeMirror typeMirror) {

        // Primitive
        if (typeMirror.getKind().isPrimitive()) {
            return typeMirror.getKind().ordinal();
        }
        switch (typeMirror.toString()) {
            case BYTE:
                return TypeKind.BYTE.ordinal();
            case SHORT:
                return TypeKind.SHORT.ordinal();
            case INTEGER:
                return TypeKind.INT.ordinal();
            case LONG:
                return TypeKind.LONG.ordinal();
            case FLOAT:
                return TypeKind.FLOAT.ordinal();
            case DOUBEL:
                return TypeKind.DOUBLE.ordinal();
            case BOOLEAN:
                return TypeKind.BOOLEAN.ordinal();
            case CHAR:
                return TypeKind.CHAR.ordinal();
            case STRING:
                return TypeKind.STRING.ordinal();
            default:
                // Other side, maybe the PARCELABLE or SERIALIZABLE or OBJECT.
                if (types.isSubtype(typeMirror, parcelableType)) {
                    // PARCELABLE
                    return TypeKind.PARCELABLE.ordinal();
                } else if (types.isSubtype(typeMirror, serializableType)) {
                    // SERIALIZABLE
                    return TypeKind.SERIALIZABLE.ordinal();
                } else {
                    return TypeKind.OBJECT.ordinal();
                }
        }
    }
}
