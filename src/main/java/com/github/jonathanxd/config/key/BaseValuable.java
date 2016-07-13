/*
 *      Config - Configuration API. <https://github.com/JonathanxD/Config>
 *
 *         The MIT License (MIT)
 *
 *      Copyright (c) 2016 TheRealBuggy/JonathanxD (https://github.com/JonathanxD/ & https://github.com/TheRealBuggy/) <jonathan.scripter@programmer.net>
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

/**
 * Created by jonathan on 13/07/16.
 */
public interface BaseValuable {


    Object getGenericValue();

    default String getValueAsString() {
        return String.valueOf(this.getGenericValue());
    }

    default short getValueAsShort() {
        return Short.parseShort(getValueAsString());
    }

    default int getValueAsInt() {
        return Integer.parseInt(getValueAsString());
    }

    default long getValueAsLong() {
        return Long.parseLong(getValueAsString());
    }

    default double getValueAsDouble() {
        return Double.parseDouble(getValueAsString());
    }

    default float getValueAsFloat() {
        return Float.parseFloat(getValueAsString());
    }

    default boolean getValueAsBoolean() {
        return Boolean.parseBoolean(getValueAsString());
    }

    default byte getValueAsByte() {
        return Byte.parseByte(getValueAsString());
    }

    default char getValueAsChar() {
        return getValueAsString().charAt(0);
    }

    //

    void setGenericValue(Object o);

    default void setValueAsString(String value) {
        this.setGenericValue(value);
    }

    default void setValueAsShort(short value) {
        this.setGenericValue(value);
    }

    default void setValueAsInt(int value) {
        this.setGenericValue(value);
    }

    default void setValueAsLong(long value) {
        this.setGenericValue(value);
    }

    default void setValueAsDouble(double value) {
        this.setGenericValue(value);
    }

    default void setValueAsFloat(float value) {
        this.setGenericValue(value);
    }

    default void setValueAsBoolean(boolean value) {
        this.setGenericValue(value);
    }

    default void setValueAsByte(byte value) {
        this.setGenericValue(value);
    }

    default void setValueAsChar(char value) {
        this.setGenericValue(value);
    }
}
