/*
 *      Config-Jackson-XML - Json backend for Config <https://github.com/JonathanxD/Config/>
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
package com.github.jonathanxd.config.backend.jackson.xml.test;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.github.jonathanxd.config.Config;
import com.github.jonathanxd.config.Key;
import com.github.jonathanxd.config.backend.ConfigIO;
import com.github.jonathanxd.config.backend.jackson.xml.JacksonXmlBackend;
import com.github.jonathanxd.iutils.box.IMutableBox;
import com.github.jonathanxd.iutils.box.MutableBox;
import org.junit.Assert;
import org.junit.Test;

import java.util.Objects;

public class JacksonXmlBackendTest {

    @Test
    public void testToString() {
        IMutableBox<String> box = new MutableBox<>();

        XmlMapper om = new XmlMapper();

        JacksonXmlBackend jsonBackend = new JacksonXmlBackend(om, ConfigIO.stringBox(box));

        Config config = new Config(jsonBackend);
        Key<String> key = config.getRootKey().getKey("backend", String.class);
        Key<Byte> byteKey = config.getRootKey().getKey("byte", Byte.class);
        key.setValue("Jackson backend");

        byteKey.setValue((byte) 9);

        config.save();

        String first = box.get();

        box.set("<root><backend>Jackson backend Uhu</backend><byte>9</byte></root>");

        config.load();

        Byte b = byteKey.getValue();

        if (!Objects.equals("<root><backend>Jackson backend</backend><byte>9</byte></root>", first)
                && !Objects.equals("<root><byte>9</byte><backend>Jackson backend</backend></root>", first)) {
            Assert.fail("Failed! Obj: " + first);
        }

        Assert.assertEquals("Jackson backend Uhu", key.getValue());
        Assert.assertEquals(9, (int) b);
    }

}
