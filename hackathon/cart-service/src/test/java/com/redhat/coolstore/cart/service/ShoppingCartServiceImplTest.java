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
import com.redhat.coolstore.cart.exceptions.itemIdException;
import com.redhat.coolstore.cart.exceptions.cartIdException;

@RunWith(SpringRunner.class)

// Tells SpringBoot to go and look for a main configuration class(ie: one with @SpringBootApplication) & use it to start a Spring application context.
@SpringBootTest 
public class ShoppingCartServiceImplTest extends BaseTest {

  private static final String CATALOG_MOCK_SERVICE_PORT = "CATALOG_MOCK_SERVICE_PORT";

  private static int cart_service_mock_port = 8888;
  private static double product_price = 33.56;
  private static int itemQty = 2;
  private static Logger log = Logger.getLogger("ShoppingCartServiceImplTest");
  private static ObjectMapper jsonMapper;
  private static String itemId;
  private static WireMockServer wireMockServer;

  @Autowired
  private ShoppingCartService sCartService;

  @BeforeClass
  public static void setupServer() throws JsonProcessingException {

    // 1) Resolve test variables
    String catPort = System.getProperty(CATALOG_MOCK_SERVICE_PORT);
    if(!StringUtils.isEmpty(catPort)){
        cart_service_mock_port = Integer.parseInt(catPort);
    }
    itemId = System.getProperty(CatalogService.ITEM_ID);

    // 2) Instantiate a JSON representation of a Product obj 
    jsonMapper = new ObjectMapper();
    Product testProdObj = getTestProduct(itemId);
    String pResponse = jsonMapper.writeValueAsString(testProdObj);

    // 3) Instantiate WireMock listener and configure with appropriate response body(ie: product JSON) and status
    wireMockServer = new WireMockServer(cart_service_mock_port);
    wireMockServer.stubFor(get(urlEqualTo("/"+itemId))
          .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(pResponse)));

    // 4) Start WireMock listener
    wireMockServer.start();

  }

  @AfterClass
  public static void shutdown() {
    wireMockServer.stop();
  }

  //@Ignore
  @Test
  public void calculateCartPriceTest() throws itemIdException {
     
      // 1) Invoke ShoppingCartService to instantiate a new cart and add item & qty
      ShoppingCart testSCart = sCartService.addToCart(null, itemId, itemQty);

      // 2) Determine total price of shopping cart 
      testSCart = sCartService.calculateCartPrice(testSCart);

      // 3) Assert result from shopping cart service
      log.info("calculateCartPriceTest() testSCart = \n"+testSCart);
      assertTrue(testSCart.getCartTotal() == 72.11);
  }

  //@Ignore
  @Test
  public void removeAllFromCartTest() throws itemIdException, cartIdException {
      // 1) Invoke ShoppingCartService to instantiate a new cart and add item & qty
      ShoppingCart testSCart = sCartService.addToCart(null, itemId, itemQty);
      String cartId = testSCart.getId();

      // 2) remove item cart
      ShoppingCart sCart = sCartService.removeFromCart(cartId, itemId, itemQty);

      // 3) ensure sCartService removes entire shopping cart item
      assertTrue(sCart.getShoppingCartItemList().size() == 0);
  }
  
  //@Ignore
  @Test
  public void removeSomeFromCartTest() throws itemIdException, cartIdException {
      
      int extraQty = itemQty+1;
      
      // 1) Invoke ShoppingCartService to instantiate a new cart and add item & extra qty
      ShoppingCart testSCart = sCartService.addToCart(null, itemId, extraQty);
      String cartId = testSCart.getId();

      // 2) remove item qty from cart
      ShoppingCart sCart = sCartService.removeFromCart(cartId, itemId, itemQty);

      // 3) ensure sCartService removes qty but retains cartItem
      assertTrue(sCart.getShoppingCartItemList().size() == 1);
      ShoppingCartItem sCartItem = sCart.getShoppingCartItemList().get(0);
      assertTrue(sCartItem.getQuantity() == 1);
  }

}
