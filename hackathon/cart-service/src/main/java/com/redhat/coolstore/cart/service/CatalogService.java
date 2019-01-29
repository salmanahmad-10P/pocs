package com.redhat.coolstore.cart.service;

import com.redhat.coolstore.cart.model.Product;
import com.redhat.coolstore.cart.exceptions.*;

public interface CatalogService {

    public static final String CATALOG_SERVICE_URL="catalog_service_url";
    public static final String ITEM_ID="ITEM_ID";

    Product getProduct(String itemId) throws itemIdException;

}
