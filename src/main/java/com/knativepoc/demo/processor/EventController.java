package com.knativepoc.demo.processor;

import com.knativepoc.demo.config.ApplicationProperties;
import io.cloudevents.CloudEvent;
import io.cloudevents.jackson.JsonFormat;
import io.cloudevents.core.message.MessageReader;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.http.HttpMessageFactory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;

@RestController
@Slf4j
public class EventController {
    @Autowired
    private ApplicationProperties applicationProperties;

    @PostMapping("/event")
    public String receiveEvent(HttpServletRequest request) throws IOException {
        log.info("Received event with headers: {}", request.getHeaderNames());
        MessageReader messageReader = createMessageReader(request);
        CloudEvent cloudEvent = messageReader.toEvent();
        byte[]serialized = EventFormatProvider
                .getInstance()
                .resolveFormat(JsonFormat.CONTENT_TYPE)
                .serialize(cloudEvent);
        String cloudEvensString = new String(serialized);
        JSONObject cloudEventJson = new JSONObject(cloudEvensString);
        log.info("Received event with data: {}", cloudEventJson.getJSONObject("data").toString());
        sendFileToEndpoint(determineEndpoint(applicationProperties.getMessageType()));
        return cloudEvensString;
    }

    private String determineEndpoint(String messageType) {
        String configuredMessageType = applicationProperties.getMessageType();
        if (!configuredMessageType.equals(messageType)) {
            throw new IllegalArgumentException("Received unknown message type: " + messageType);
        }

        return switch (configuredMessageType) {
            case "A" -> applicationProperties.getMessageTypeAEndpoint();
            case "B" -> applicationProperties.getMessageTypeBEndpoint();
            default -> throw new IllegalArgumentException("Configured unknown message type: " + configuredMessageType);
        };
    }

    void sendFileToEndpoint(String endpoint) {
        log.info("Sending file to endpoint: {}", endpoint);
    }

    private MessageReader createMessageReader(HttpServletRequest request) throws IOException {
        Map<String, List<String>> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, Collections.list(request.getHeaders(headerName)));
        }

        byte[] body = IOUtils.toByteArray(request.getInputStream());

        return HttpMessageFactory.createReaderFromMultimap(headers, body);
    }



}
