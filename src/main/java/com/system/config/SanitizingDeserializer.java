package com.system.config;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

import java.io.IOException;

public class SanitizingDeserializer extends JsonDeserializer<String> {
    private static final PolicyFactory policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS);
    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        return policy.sanitize(p.getText());
    }
}
