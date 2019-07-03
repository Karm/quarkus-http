/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2018 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.undertow.server;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.HttpClientUtils;
import io.undertow.testutils.ProxyIgnore;
import io.undertow.testutils.TestHttpClient;
import io.undertow.httpcore.StatusCodes;
import io.undertow.util.UndertowOptionMap;
import io.undertow.util.UndertowOptions;

@RunWith(DefaultServer.class)
public class EncodedEncodedSlashTestCase {

    @BeforeClass
    public static void setup() {
        DefaultServer.setRootHandler(new HttpHandler() {
            @Override
            public void handleRequest(HttpServerExchange exchange) throws Exception {
                exchange.writeAsync(exchange.getRequestPath());
            }
        });
    }

    @Test
    public void testSlashNotDecoded() throws Exception {

        final TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(DefaultServer.getDefaultServerURL() + "/%2f%5c");
            HttpResponse result = client.execute(get);
            Assert.assertEquals(StatusCodes.OK, result.getStatusLine().getStatusCode());
            Assert.assertEquals("/%2f%5c", HttpClientUtils.readResponse(result));

        } finally {
            client.getConnectionManager().shutdown();
        }
    }


    @Test @ProxyIgnore
    @Ignore("UT3 - P1")
    public void testSlashDecoded() throws Exception {

        final TestHttpClient client = new TestHttpClient();
        UndertowOptionMap old = DefaultServer.getUndertowOptions();
        DefaultServer.setUndertowOptions(UndertowOptionMap.create(UndertowOptions.ALLOW_ENCODED_SLASH, true));
        try {
            HttpGet get = new HttpGet(DefaultServer.getDefaultServerURL() + "/%2f%5c");
            HttpResponse result = client.execute(get);
            Assert.assertEquals(StatusCodes.OK, result.getStatusLine().getStatusCode());
            Assert.assertEquals("//\\", HttpClientUtils.readResponse(result));

        } finally {
            DefaultServer.setUndertowOptions(old);
            client.getConnectionManager().shutdown();
        }
    }
}
