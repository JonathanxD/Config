/*
 *      Config-Yaml - Yaml backend for Config <https://github.com/JonathanxD/Config/>
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
package com.github.jonathanxd.config.backend.yaml;

import com.github.jonathanxd.config.CommonTypes;
import com.github.jonathanxd.config.backend.AbstractIOBackend;
import com.github.jonathanxd.config.backend.ConfigIO;
import com.github.jonathanxd.iutils.type.TypeInfo;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;

/**
 * Yaml backend that uses {@link ConfigIO} to read and write configuration. Save and load operations
 * may throw {@link IOException}.
 */
public class YamlBackend extends AbstractIOBackend {

    private final Yaml yaml;

    /**
     * Creates yaml backend.
     *
     * @param yaml     Yaml specification.
     * @param configIO IO to read and write yaml.
     */
    public YamlBackend(Yaml yaml, ConfigIO configIO) {
        super(configIO);
        this.yaml = yaml;
    }

    @Override
    public void save(Map<Object, Object> map, Writer writer) {
        this.yaml.dump(map, writer);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<Object, Object> load(Reader reader) {
        return (Map<Object, Object>) this.yaml.load(reader);
    }

    @Override
    public boolean supports(TypeInfo<?> type) {
        return CommonTypes.isValidBasicType(type);
    }
}
