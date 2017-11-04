/*
 *      Config-Yaml - Yaml backend for Config <https://github.com/JonathanxD/Config/>
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
package com.github.jonathanxd.config.backend.yaml;

import com.github.jonathanxd.config.CommonTypes;
import com.github.jonathanxd.config.backend.Backend;
import com.github.jonathanxd.iutils.exception.RethrowException;
import com.github.jonathanxd.iutils.type.TypeInfo;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Yaml backend that uses supplier of reader and writer. Save and load operations may throw {@link
 * IOException}.
 */
public class YamlBackend implements Backend {

    private final Yaml yaml;
    private final Supplier<Writer> writerSupplier;
    private final Supplier<Reader> readerSupplier;

    /**
     * Creates yaml backend.
     *
     * @param yaml           Yaml specification.
     * @param writerSupplier Supplier of the target to write yaml.
     * @param readerSupplier Supplier of the source of the yaml.
     */
    public YamlBackend(Yaml yaml,
                       Supplier<Writer> writerSupplier,
                       Supplier<Reader> readerSupplier) {
        this.yaml = yaml;
        this.writerSupplier = writerSupplier;
        this.readerSupplier = readerSupplier;
    }

    @Override
    public void save(Map<String, Object> map) {
        try (Writer writer = this.writerSupplier.get()) {
            this.yaml.dump(map, writer);
            writer.flush();
        } catch (IOException e) {
            throw RethrowException.rethrow(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> load() {
        try (Reader reader = this.readerSupplier.get()) {
            return (Map<String, Object>) this.yaml.load(reader);
        } catch (IOException e) {
            throw RethrowException.rethrow(e);
        }
    }

    @Override
    public boolean supports(TypeInfo<?> type) {
        return CommonTypes.isValidBasicType(type);
    }
}
