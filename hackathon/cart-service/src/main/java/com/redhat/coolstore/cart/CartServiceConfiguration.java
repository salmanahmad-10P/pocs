package com.redhat.coolstore.cart;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

/* To enable JSON binding for the REST endpoints, the Jackson provider needs to be initialized and registered into the Spring application context.
 * Otherwise, expect a runtime exception similar to the following:
 *   org.apache.cxf.jaxrs.utils.JAXRSUtils    : No message body writer has been found for class com.redhat.coolstore.cart.model.ShoppingCart, ContentType: application/json
 */
@Configuration
public class CartServiceConfiguration {

    @Bean
    public JacksonJsonProvider jsonProvider(ObjectMapper objectMapper) {
        JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
        provider.setMapper(objectMapper);
        return provider;
    }

}
