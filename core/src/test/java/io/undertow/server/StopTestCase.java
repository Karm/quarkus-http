package io.undertow.server;

import org.junit.Test;

import io.undertow.Undertow;
import io.undertow.httpcore.UndertowOptions;

public class StopTestCase {

    @Test
    public void testStopUndertowNotStarted() {
        Undertow.builder().build().stop();
    }

    @Test
    public void testStopUndertowAfterExceptionDuringStart() {
        // Making the NioXnioWorker constructor throw an exception, resulting in the Undertow.worker field not getting set.
        Undertow undertow = Undertow.builder().build();
        try {
            undertow.start();
        }
        catch (RuntimeException e) {
        }
        undertow.stop();
    }
}
