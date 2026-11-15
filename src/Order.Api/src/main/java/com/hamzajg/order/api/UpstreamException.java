package com.hamzajg.order.api;

import java.net.SocketTimeoutException;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClientException;

public class UpstreamException extends RuntimeException {

    private final int status;

    public UpstreamException(String message, int status, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public static UpstreamException from(RestClientException e) {
        if (hasCause(e, SocketTimeoutException.class)) {
            return new UpstreamException("upstream timed out", HttpStatus.GATEWAY_TIMEOUT.value(), e);
        }
        return new UpstreamException("upstream unavailable", HttpStatus.BAD_GATEWAY.value(), e);
    }

    public static UpstreamException badGateway(String message, Throwable cause) {
        return new UpstreamException(message, HttpStatus.BAD_GATEWAY.value(), cause);
    }

    private static boolean hasCause(Throwable t, Class<? extends Throwable> type) {
        while (t != null) {
            if (type.isInstance(t)) {
                return true;
            }
            t = t.getCause();
        }
        return false;
    }
}
