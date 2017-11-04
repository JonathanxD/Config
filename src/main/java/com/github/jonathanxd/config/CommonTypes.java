/*
 *      Config - Configuration library <https://github.com/JonathanxD/Config>
 *
 *         The MIT License (MIT)
 *
 *      Copyright (c) 2017 TheRealBuggy/JonathanxD (https://github.com/JonathanxD/) <jonathan.scripter@programmer.net>
 *      Copyright (c) contributors
 *
 *
 *      Permission is hereby granted, free of charge, to any person obtaining a copy
 *      of this software and associated documentation files (the "Software"), to deal
 *      in the Software without restriction, including without limitation the rights
 *      to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *      copies of the Software, and to permit persons to whom the Software is
 *      furnished to do so, subject to the following conditions:
 *
 *      The above copyright notice and this permission notice shall be included in
 *      all copies or substantial portions of the Software.
 *
 *      THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *      IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *      FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *      AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *      LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *      OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *      THE SOFTWARE.
 */
package com.github.jonathanxd.config;

import com.github.jonathanxd.iutils.collection.Collections3;
import com.github.jonathanxd.iutils.type.TypeInfo;
import com.github.jonathanxd.iutils.type.TypeParameterProvider;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Common basic types
 */
public class CommonTypes {

    public static final TypeInfo<Boolean> BOXED_BOOLEAN = TypeInfo.of(Boolean.class);
    public static final TypeInfo<Byte> BOXED_BYTE = TypeInfo.of(Byte.class);
    public static final TypeInfo<Short> BOXED_SHORT = TypeInfo.of(Short.class);
    public static final TypeInfo<Character> BOXED_CHAR = TypeInfo.of(Character.class);
    public static final TypeInfo<Integer> BOXED_INTEGER = TypeInfo.of(Integer.class);
    public static final TypeInfo<Float> BOXED_FLOAT = TypeInfo.of(Float.class);
    public static final TypeInfo<Long> BOXED_LONG = TypeInfo.of(Long.class);
    public static final TypeInfo<Double> BOXED_DOUBLE = TypeInfo.of(Double.class);
    public static final TypeInfo<Void> BOXED_VOID = TypeInfo.of(Void.class);

    public static final TypeInfo<Boolean> BOOLEAN = TypeInfo.of(Boolean.TYPE);
    public static final TypeInfo<Byte> BYTE = TypeInfo.of(Byte.TYPE);
    public static final TypeInfo<Short> SHORT = TypeInfo.of(Short.TYPE);
    public static final TypeInfo<Character> CHAR = TypeInfo.of(Character.TYPE);
    public static final TypeInfo<Integer> INTEGER = TypeInfo.of(Integer.TYPE);
    public static final TypeInfo<Float> FLOAT = TypeInfo.of(Float.TYPE);
    public static final TypeInfo<Long> LONG = TypeInfo.of(Long.TYPE);
    public static final TypeInfo<Double> DOUBLE = TypeInfo.of(Double.TYPE);
    public static final TypeInfo<Void> VOID = TypeInfo.of(Void.TYPE);

    public static final TypeInfo<Object> OBJECT = TypeInfo.of(Object.class);
    public static final TypeInfo<String> STRING = TypeInfo.of(String.class);

    public static final TypeInfo<Map> MAP = TypeInfo.of(Map.class);
    public static final TypeInfo<List> LIST = TypeInfo.of(List.class);

    public static final TypeInfo<TypeInfo<?>> TYPE_INFO = new TypeParameterProvider<TypeInfo<?>>() {
    }.createTypeInfo();

    public static final TypeInfo<Class<?>> CLASS = new TypeParameterProvider<Class<?>>() {
    }.createTypeInfo();

    public static final List<TypeInfo<?>> ALL = Collections.unmodifiableList(Collections3.listOf(
            BOXED_BOOLEAN, BOOLEAN,
            BYTE, BOXED_BYTE,
            SHORT, BOXED_SHORT,
            CHAR, BOXED_CHAR,
            INTEGER, BOXED_INTEGER,
            FLOAT, BOXED_FLOAT,
            LONG, BOXED_LONG,
            DOUBLE, BOXED_DOUBLE,
            /*OBJECT, */STRING
    ));

    public static boolean isValidBasicType(TypeInfo<?> type) {
        return type.isAssignableFrom(BOXED_BOOLEAN)
                || type.isAssignableFrom(BOXED_BYTE)
                || type.isAssignableFrom(BOXED_SHORT)
                || type.isAssignableFrom(BOXED_CHAR)
                || type.isAssignableFrom(BOXED_INTEGER)
                || type.isAssignableFrom(BOXED_FLOAT)
                || type.isAssignableFrom(BOXED_LONG)
                || type.isAssignableFrom(BOXED_DOUBLE)
                //|| type.isAssignableFrom(BOXED_VOID)
                || type.isAssignableFrom(BOOLEAN)
                || type.isAssignableFrom(BYTE)
                || type.isAssignableFrom(SHORT)
                || type.isAssignableFrom(CHAR)
                || type.isAssignableFrom(INTEGER)
                || type.isAssignableFrom(FLOAT)
                || type.isAssignableFrom(LONG)
                || type.isAssignableFrom(DOUBLE)
                //|| type.isAssignableFrom(VOID)
                || type.isAssignableFrom(OBJECT)
                || type.isAssignableFrom(STRING)
                || CommonTypes.isValidMap(type)
                || CommonTypes.isValidList(type);
    }

    public static boolean isValidMap(TypeInfo<?> map) {
        return map.getTypeClass() == MAP.getTypeClass()
                && map.getTypeParameters().stream().allMatch(CommonTypes::isValidBasicType);
    }

    public static boolean isValidList(TypeInfo<?> list) {
        return list.getTypeClass() == LIST.getTypeClass()
                && list.getTypeParameters().stream().allMatch(CommonTypes::isValidBasicType);
    }
}
