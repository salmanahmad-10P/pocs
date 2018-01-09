package com.redhat.coolstore.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.redhat.coolstore.cart.rest.CartEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.cxf.common.util.StringUtils;

import com.redhat.coolstore.cart.service.CatalogService;

@SpringBootApplication
public class CartServiceApplication {

    public static void main(String[] args) {

        if(StringUtils.isEmpty(System.getProperty(CatalogService.CATALOG_SERVICE_URL))) {
            if(StringUtils.isEmpty(System.getenv(CatalogService.CATALOG_SERVICE_URL))) {

                // Temp placeholder until this property is correctly set
                System.setProperty(CatalogService.CATALOG_SERVICE_URL, "https://www.redhat.com/en/technologies/linux-platforms");

            }else {
                System.setProperty(CatalogService.CATALOG_SERVICE_URL, System.getenv(CatalogService.CATALOG_SERVICE_URL));
            }
        }

        System.out.println("main() "+CatalogService.CATALOG_SERVICE_URL+" = "+System.getProperty(CatalogService.CATALOG_SERVICE_URL));

        SpringApplication.run(CartServiceApplication.class, args);
    }

}
