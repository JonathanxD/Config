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

import com.github.jonathanxd.iutils.type.TypeInfo;

import java.util.function.UnaryOperator;

/**
 * A Key that support a pre and post value processing. The pre value processing is called before the
 * value setting ({@link #setValue(Object)}) and post processing is called after the value getting
 * ({@link #getValue()}).
 */
public class ValueProcessKey<T> extends AbstractKey<T> {

    private final UnaryOperator<T> preProcessor;
    private final UnaryOperator<T> postProcessor;

    /**
     * Creates a Key that supports pre and post value processing.
     *
     * @param config        Configuration.
     * @param parent        Parent key.
     * @param name          Name of this key.
     * @param typeInfo      Type information of the {@link T value type}.
     * @param storage       Storage to push and fetch values.
     * @param original      Original key that this emulated key was based, or {@code null} if this
     *                      is not an emulated key.
     * @param preProcessor  Pre value processor. (Called before value setting) (Nullable).
     * @param postProcessor Post value processor. (Called after value getting) (Nullable).
     */
    public ValueProcessKey(Config config,
                           Key<?> parent,
                           String name,
                           TypeInfo<T> typeInfo,
                           Storage storage,
                           Key<?> original,
                           UnaryOperator<T> preProcessor,
                           UnaryOperator<T> postProcessor) {
        super(config, parent, name, typeInfo, storage, original);
        this.preProcessor = preProcessor;
        this.postProcessor = postProcessor;
    }

    /**
     * Creates a {@link ValueProcessKey} that delegates operations to {@code key}.
     *
     * @param key           Key to delegate operations.
     * @param preProcessor  Pre Processor (called before value setting).
     * @param postProcessor Post processor (called before value getting).
     * @param <T>           Type of value.
     * @return {@link ValueProcessKey} that delegates operations to {@code key}.
     */
    public static <T> ValueProcessKey<T> from(Key<T> key, UnaryOperator<T> preProcessor, UnaryOperator<T> postProcessor) {
        return new Delegate<>(key, preProcessor, postProcessor);
    }

    @Override
    public T getValue() {

        T value = super.getValue();

        UnaryOperator<T> postProcessor = this.getPostProcessor();

        if (postProcessor != null)
            value = postProcessor.apply(value);

        return value;
    }

    @Override
    public void setValue(T value) {

        UnaryOperator<T> preProcessor = this.getPreProcessor();

        if (preProcessor != null)
            value = preProcessor.apply(value);

        super.setValue(value);
    }

    /**
     * Gets the value pre processor.
     *
     * @return Value pre processor.
     */
    public UnaryOperator<T> getPreProcessor() {
        return this.preProcessor;
    }

    /**
     * Gets the value post processor.
     *
     * @return Value post processor.
     */
    public UnaryOperator<T> getPostProcessor() {
        return this.postProcessor;
    }

    private static class Delegate<T> extends ValueProcessKey<T> {

        private final Key<T> delegate;

        /**
         * Creates a Key that supports pre and post value processing.
         *
         * @param delegate      key to delegate operations.
         * @param preProcessor  Pre value processor. (Called before value setting) (Nullable).
         * @param postProcessor Post value processor. (Called after value getting) (Nullable).
         */
        public Delegate(Key<T> delegate, UnaryOperator<T> preProcessor, UnaryOperator<T> postProcessor) {
            super(delegate.getConfig(),
                    delegate.getParent(),
                    delegate.getName(),
                    delegate.getTypeInfo(),
                    delegate.getStorage(),
                    delegate.getOriginalKey(),
                    preProcessor, postProcessor);
            this.delegate = delegate;
        }

        @Override
        public <V> Key<V> getKey(String name, TypeInfo<V> typeInfo) {
            return this.getDelegate().getKey(name, typeInfo);
        }

        @Override
        public T getValue() {
            T value = this.getDelegate().getValue();

            UnaryOperator<T> postProcessor = this.getPostProcessor();

            if (postProcessor != null)
                value = postProcessor.apply(value);

            return value;
        }

        @Override
        public void setValue(T value) {
            UnaryOperator<T> preProcessor = this.getPreProcessor();

            if (preProcessor != null)
                value = preProcessor.apply(value);

            this.getDelegate().setValue(value);
        }

        @Override
        public Config getConfig() {
            return this.getDelegate().getConfig();
        }

        @Override
        public Key<?> getParent() {
            return this.getDelegate().getParent();
        }

        @Override
        public String getName() {
            return this.getDelegate().getName();
        }

        @Override
        public TypeInfo<T> getTypeInfo() {
            return this.getDelegate().getTypeInfo();
        }

        @Override
        public Storage getStorage() {
            return this.getDelegate().getStorage();
        }

        public Key<T> getDelegate() {
            return this.delegate;
        }
    }
}
