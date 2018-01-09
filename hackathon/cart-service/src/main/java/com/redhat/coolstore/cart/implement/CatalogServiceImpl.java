package com.redhat.coolstore.cart.implement;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import org.apache.log4j.Logger;
import com.redhat.coolstore.cart.exceptions.*;
import com.redhat.coolstore.cart.model.Product;
import com.redhat.coolstore.cart.service.CatalogService;

@Component
public class CatalogServiceImpl implements CatalogService {

    @Value("#{systemProperties.catalog_service_url}")
    private String cServiceUrl;

    private Logger log = Logger.getLogger("CatalogServiceImpl");

    private String getProductContextPath = "/product/";
    private String getProductsContextPath = "/products/";

    @PostConstruct
    public void initializationCheck() {
        if(cServiceUrl == null || cServiceUrl.isEmpty())
            throw new RuntimeException("Must specify a system property of : "+CatalogService.CATALOG_SERVICE_URL);
    }

    public Product getProduct(String itemId) throws itemIdException{
        Product pObj;
        StringBuilder sBuilder = new StringBuilder(cServiceUrl);
        sBuilder.append(getProductContextPath);
        sBuilder.append(itemId);

        log.info("getProduct() cService Url  = "+sBuilder.toString());
        RestTemplate restTemplate = new RestTemplate();

        try {
            pObj = restTemplate.getForObject(sBuilder.toString(), Product.class);
        }
        catch (RestClientException rc){
         throw new itemIdException(itemId);
        }
        return pObj;
    }
}
