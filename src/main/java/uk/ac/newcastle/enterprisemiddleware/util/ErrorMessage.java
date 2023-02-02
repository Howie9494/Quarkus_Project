package uk.ac.newcastle.enterprisemiddleware.util;


import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>A simple POJO to hold the details of an actual error that will be marshaled into JSON by jackson.</p>
 *
 * @author hugofirth
 */
@RegisterForReflection
public class ErrorMessage {
    private final String error;
    private final Map<String, String> reasons;

    public ErrorMessage(String error) {
        this.error = error;
        this.reasons = new HashMap<>();
    }

    public ErrorMessage(String error, Map<String, String> reasons) {
        this.error = error;
        this.reasons = reasons;
    }

    public Map<String, String> getReasons() {
        return reasons;
    }

    public String getError() {
        return error;
    }
}