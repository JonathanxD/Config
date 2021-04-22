/*
 *      Config-Toml - Json backend for Config <https://github.com/JonathanxD/Config/>
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
package com.github.jonathanxd.config.backend.toml.test;

import com.github.jonathanxd.config.CommonTypes;
import com.github.jonathanxd.config.Config;
import com.github.jonathanxd.config.Key;
import com.github.jonathanxd.config.KeySpec;
import com.github.jonathanxd.config.backend.ConfigIO;
import com.github.jonathanxd.config.backend.toml.TomlBackend;
import com.github.jonathanxd.iutils.box.IMutableBox;
import com.github.jonathanxd.iutils.box.MutableBox;
import org.junit.Test;
import org.tomlj.Toml;
import org.tomlj.TomlParseResult;
import org.tomlj.TomlTable;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class TomlBackendTest {

    @Test
    public void testToString() {
        IMutableBox<String> box = new MutableBox<>();

        box.set("[project]\n" +
                "name = 'TOML Backend Loader'\n" +
                "[server]\n" +
                "cfg = { port = 8080, addr = \"0.0.0.0\" }");

        TomlBackend jsonBackend = new TomlBackend(ConfigIO.stringBox(box));

        Config config = new Config(jsonBackend);
        config.load();

        Key<Map<Object, Object>> rootKey = config.getRootKey();

        Key<Long> port = rootKey
                .getKeySection("server")
                .getKeySection("cfg")
                .getKey("port", CommonTypes.LONG);

        long loadedPort = port.getValue();

        port.setValue(9090L);

        config.save();

        assertEquals(8080, loadedPort);
        assertEquals(9090, (long) port.getValue());

        TomlParseResult parse = Toml.parse(box.get());
        assertFalse(parse.hasErrors());
        TomlTable server = parse.getTable("server");
        TomlTable cfg = server.getTable("cfg");
        long tomlPort = cfg.getLong("port");
        assertEquals(9090L, tomlPort);
    }

}
