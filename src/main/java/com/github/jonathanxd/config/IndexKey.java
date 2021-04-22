/*
 *      Config - Configuration library <https://github.com/JonathanxD/Config>
 *
 *         The MIT License (MIT)
 *
 *      Copyright (c) 2021 TheRealBuggy/JonathanxD (https://github.com/JonathanxD/) <jonathan.scripter@programmer.net>
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

import com.github.jonathanxd.iutils.type.TypeInfo;

import java.util.List;

/**
 * An Key which points to an Index in a {@code Key<List<T>>}, this is very useful for reading and changing values inside List.
 * However, this does not provide a way to insert new values in the List, just access and modify the ones that are already present
 * in the list.
 * <p>
 * Example usage:
 * {@code
 * <pre>
 *     Key<List<String>> names = config.getRootKey().getKey("names", CommonTypes.LIST_OF_STRING);
 *     Key<String> nameAtIndex0 = IndexKey.forKeyAndIndex(names, 0);
 * </pre>
 * }
 * <p>
 * Note that, even that you could create keys pointing to indexes which there is no data (eg index equal or greater than list size),
 * reading and writing value operations ensures that there is a value in those indexes.
 *
 * @param <T> Type of value.
 */
public class IndexKey<T> extends Key<T> {
    private final int index;

    IndexKey(Config config, Key<?> parent, int index, TypeInfo<T> typeInfo, Storage storage, Key<?> original) {
        super(config, parent, String.valueOf(index), typeInfo, storage, original);
        this.index = index;
    }

    /**
     * Creates a IndexKey which points to an element in the provided {@code index} in the value referenced
     * by {@code key}.
     *
     * @param key   The key which points to the list in the storage.
     * @param index The index of element being referenced. Could be an index which does not exists in the
     *              target list, but the index must be valid when reading or writing the value.
     * @param <V>   Type of the element.
     * @return A IndexKey pointing to the {@code index} in the value pointed by {@code key}.
     */
    public static <V> IndexKey<V> forKeyAndIndex(Key<List<V>> key, int index) {
        return new IndexKey<V>(
                key.getConfig(),
                key,
                index,
                key.getTypeInfo().getTypeParameters().get(0).cast(),
                Storage.createInnerIndexStorage(key),
                key.getOriginalKey()
        );
    }

    public int getIndex() {
        return index;
    }
}
