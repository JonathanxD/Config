/*
 *      Config - Configuration API. <https://github.com/JonathanxD/Config>
 *
 *         The MIT License (MIT)
 *
 *      Copyright (c) 2021 TheRealBuggy/JonathanxD (https://github.com/JonathanxD/ & https://github.com/TheRealBuggy/) <jonathan.scripter@programmer.net>
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
package com.github.jonathanxd.config.key;

import com.github.jonathanxd.config.converter.Converter;

/**
 * Created by jonathan on 25/06/16.
 */
public class ConvertKey<U, T> extends Key<T> {

    private final Converter<T, U> converter;

    ConvertKey(Key<T> key, Converter<T, U> converter) {
        super(key.getTypeRepresentation(), key.getPath(), key.getConfig(), key.getTransformers());
        this.converter = converter;
    }

    public U getConvertedValue(Object[] params) {
        return converter.convert(this.getValue(), params);
    }

    public void setConvertedValue(U convertedValue, Object[] params) {
        setValue(converter.revertConversion(convertedValue, params));
    }

    public U getConvertedValue(U defaultValue, Object[] params) {
        return converter.convert(this.getValue(converter.revertConversion(defaultValue, params)), params);
    }

    public U getConvertedValueT(T defaultValue, Object[] params) {
        return converter.convert(this.getValue(defaultValue), params);
    }

    public void setDefaultConvertedValue(U convertedValue, Object[] params) {
        setDefaultValue(converter.revertConversion(convertedValue, params));
    }
}
