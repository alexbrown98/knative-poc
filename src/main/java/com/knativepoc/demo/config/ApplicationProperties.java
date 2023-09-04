package com.knativepoc.demo.config;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties
public class ApplicationProperties {

    @NotEmpty
    @Value("${knative.messageType}")
    private String messageType;

    @NotEmpty
    @Value("${knative.regulator.endpoint.messageTypeA}")
    private String messageTypeAEndpoint;

    @NotEmpty
    @Value("${knative.regulator.endpoint.messageTypeB}")
    private String messageTypeBEndpoint;

}