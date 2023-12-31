/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.logging.log4j.io.internal;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.io.ByteStreamLogger;
import org.apache.logging.log4j.spi.ExtendedLogger;

/**
 * Internal class that exists primarily to allow location calculation to work.
 *
 * @since 2.12
 */
public class InternalInputStream extends FilterInputStream {

    private final String fqcn;
    private final ByteStreamLogger logger;

    public InternalInputStream(
            final InputStream in,
            final Charset charset,
            final ExtendedLogger logger,
            final String fqcn,
            final Level level,
            final Marker marker) {
        super(in);
        this.logger = new ByteStreamLogger(logger, level, marker, charset);
        this.fqcn = fqcn;
    }

    @Override
    public void close() throws IOException {
        this.logger.close(this.fqcn);
        super.close();
    }

    @Override
    public int read() throws IOException {
        final int b = super.read();
        this.logger.put(this.fqcn, b);
        return b;
    }

    @Override
    public int read(final byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        final int bytesRead = super.read(b, off, len);
        this.logger.put(this.fqcn, b, off, bytesRead);
        return bytesRead;
    }

    @Override
    public String toString() {
        return "{stream=" + this.in + '}';
    }
}
