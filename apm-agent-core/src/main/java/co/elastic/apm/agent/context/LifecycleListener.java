/*-
 * #%L
 * Elastic APM Java agent
 * %%
 * Copyright (C) 2018 - 2020 Elastic and contributors
 * %%
 * Licensed to Elasticsearch B.V. under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch B.V. licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * #L%
 */
package co.elastic.apm.agent.context;

import co.elastic.apm.agent.impl.ElasticApmTracer;

import java.io.Closeable;

/**
 * A {@link LifecycleListener} notifies about the start and stop event of the {@link ElasticApmTracer}.
 * <p>
 * Implement this interface and register it as a {@linkplain java.util.ServiceLoader service} under
 * {@code src/main/resources/META-INF/services/co.elastic.apm.agent.context.LifecycleListener}.
 * </p>
 * <p>
 * Implementations may have a constructor with an {@link ElasticApmTracer} argument
 * </p>
 */
public interface LifecycleListener {

    /**
     * Callback for when the {@link ElasticApmTracer} starts.
     *
     * @param tracer The tracer.
     */
    void start(ElasticApmTracer tracer);

    /**
     * Callback for when {@link ElasticApmTracer#stop()} has been called.
     * <p>
     * Typically, this method is used to clean up resources like thread pools
     * so that there are no class loader leaks when a webapp is redeployed in an application server.
     * </p>
     * <p>
     * Exceptions thrown from this method are caught and handled so that they don't prevent further cleanup actions.
     * </p>
     *
     * @throws Exception When something goes wrong performing the cleanup.
     */
    void stop() throws Exception;

    class ClosableAdapter implements LifecycleListener {

        private final Closeable closeable;

        public static LifecycleListener of(Closeable closeable) {
            return new ClosableAdapter(closeable);
        }

        private ClosableAdapter(Closeable closeable) {
            this.closeable = closeable;
        }

        @Override
        public void start(ElasticApmTracer tracer) {

        }

        @Override
        public void stop() throws Exception {
            closeable.close();
        }
    }
}
