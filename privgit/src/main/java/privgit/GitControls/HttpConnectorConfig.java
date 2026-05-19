package privgit.GitControls;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.tomcat.reactive.TomcatReactiveWebServerFactory;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpConnectorConfig {

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> servletContainer() {

        return factory -> {
            // Create connector to accept http traffic.
            Connector httpConnector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);

            httpConnector.setScheme("http");
            httpConnector.setSecure(false);
            httpConnector.setPort(80);

            factory.addAdditionalConnectors(httpConnector);
        };
    }
}