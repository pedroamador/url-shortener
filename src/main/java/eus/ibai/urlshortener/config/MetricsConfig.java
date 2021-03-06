package eus.ibai.urlshortener.config;

import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "management.enabled", havingValue = "true")
public class MetricsConfig {

    @Value("${spring.application.name}")
    private String appName;

    @Value("${NAMESPACE:default}")
    private String namespace;

    @Value("${HOSTNAME:default}")
    private String hostname;

    @Value("${NODE:default}")
    private String node;

    @Value("${DOMAIN:default}")
    private String domain;

    @Bean
    public MeterRegistryCustomizer<PrometheusMeterRegistry> tags() {
        return registry -> registry.config()
                .commonTags("appName", appName)
                .commonTags("namespace", namespace)
                .commonTags("pod", hostname)
                .commonTags("node", node)
                .commonTags("domain", domain);
    }
}
