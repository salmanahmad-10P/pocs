package com.redhat.coolstore.cart.service;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.*;

import org.apache.cxf.common.util.StringUtils;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.Ignore;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import com.github.tomakehurst.wiremock.WireMockServer;

import com.redhat.coolstore.cart.model.Product;
import com.redhat.coolstore.cart.model.ShoppingCart;
import com.redhat.coolstore.cart.model.ShoppingCartItem;

// TO-DO:  Push more test functionality down to this base class.
public class BaseTest {

    protected static double product_price = 33.56;
    protected static String itemId;

    protected static Product getTestProduct(String itemId) {
      Product pObj = new Product();
      pObj.setDesc("test desc");
      pObj.setItemId(itemId);
      pObj.setName("test name");
      pObj.setPrice(product_price);
      return pObj;
    }

}
