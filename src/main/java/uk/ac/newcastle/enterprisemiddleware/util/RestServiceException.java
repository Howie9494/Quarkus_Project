package uk.ac.newcastle.enterprisemiddleware.util;

import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class RestServiceException extends RuntimeException implements
        Serializable {

    private static final long serialVersionUID = 1264443812161L;
    private static final String defaultMsg = "An unexpected error occurred whilst processing the request";

    private final Map<String, String> reasons;
    private final Response.Status status;

    public RestServiceException() {
        super(defaultMsg);
        this.reasons = new HashMap<>();
        this.status = Response.Status.INTERNAL_SERVER_ERROR;
    }

    public RestServiceException(String msg) {
        super(msg);
        this.reasons = new HashMap<>();
        this.status = Response.Status.INTERNAL_SERVER_ERROR;
    }

    public RestServiceException(String msg, Response.Status status) {
        super(msg);
        this.reasons = new HashMap<>();
        this.status = status;
    }

    public RestServiceException(String msg, Map<String, String> reasons, Response.Status status) {
        super(msg);
        this.reasons = reasons;
        this.status = status;
    }

    public RestServiceException(Exception e) {
        super(defaultMsg, e);
        this.reasons = new HashMap<>();
        this.status = Response.Status.INTERNAL_SERVER_ERROR;
    }

    public RestServiceException(String msg, Exception e) {
        super(msg, e);
        this.reasons = new HashMap<>();
        this.status = Response.Status.INTERNAL_SERVER_ERROR;
    }

    public RestServiceException(String msg, Response.Status status, Exception e) {
        super(msg, e);
        this.reasons = new HashMap<>();
        this.status = status;
    }

    public RestServiceException(String msg, Map<String, String> reasons, Response.Status status, Exception e) {
        super(msg, e);
        this.reasons = reasons;
        this.status = status;
    }

    public Map<String, String> getReasons() {
        return reasons;
    }

    public Response.Status getStatus() {
        return status;
    }
}
