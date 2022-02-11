/*
 *      Config-Toml - Json backend for Config <https://github.com/JonathanxD/Config/>
 *
 *         The MIT License (MIT)
 *
 *      Copyright (c) 2022 TheRealBuggy/JonathanxD (https://github.com/JonathanxD/) <jonathan.scripter@programmer.net>
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
package com.github.jonathanxd.config.backend.toml;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

/**
 * Experimental Toml renderer.
 *
 * This renderer does no guarantee to keep the original structure of TOML, as Config framework only works with Maps.
 */
public class TomlRenderer {

    public static void render(Map<Object, Object> obj, Writer w) throws IOException {
        Visitor visitor = new Visitor(w);
        visitor.visitRoot(obj);
    }

    public static class Visitor {
        private final Writer w;

        public Visitor(Writer w) {
            this.w = w;
        }

        @SuppressWarnings("unchecked")
        public void visitRoot(Map<Object, Object> obj) throws IOException {
            for (Map.Entry<Object, Object> kv : obj.entrySet()) {
                Object key = kv.getKey();
                Object value = kv.getValue();

                if (value instanceof Map<?, ?>) {
                    this.w.write("[");
                    this.visitKey(key);
                    this.w.write("]\n");
                    visitMap((Map<Object, Object>) value);
                } else {
                    this.visitKey(key);
                    this.w.write(" = ");
                    this.visitValue(value);
                }

                this.w.write("\n");
            }
        }

        public void visitMap(Map<Object, Object> obj) throws IOException {
            int x = 0;
            for (Map.Entry<Object, Object> kv : obj.entrySet()) {
                if (x > 0) {
                    this.w.write(", ");
                }

                Object key = kv.getKey();
                Object value = kv.getValue();

                this.visitKey(key);
                this.w.write(" = ");
                this.visitValue(value);
                ++x;
            }
        }

        public void visitList(List<Object> list) throws IOException {
            int x = 0;
            for (Object o : list) {
                if (x > 0) {
                    w.write(", ");
                }
                this.visitValue(o);
                ++x;
            }
        }

        public void visitKey(Object key) throws IOException {
            if (key instanceof String) {
                w.write((String) key);
            } else {
                throw new IOException("Invalid key type: " + key.getClass().getCanonicalName());
            }
        }

        @SuppressWarnings("unchecked")
        public void visitValue(Object value) throws IOException {
            if (value instanceof Map<?, ?>) {
                w.write("{ ");
                this.visitMap((Map<Object, Object>) value);
                w.write(" }");
            } else if (value instanceof List<?>) {
                w.write("[");
                this.visitList((List<Object>) value);
                w.write("]");
            } else if (value instanceof String) {
                w.write("\"");
                w.write(((String) value).replace("\"", "\\\""));
                w.write("\"");
            } else {
                w.write(value.toString());
            }
        }
    }



}
