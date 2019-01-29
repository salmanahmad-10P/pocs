package com.redhat.coolstore.cart.service;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import org.apache.cxf.common.util.StringUtils;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;

import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import com.github.tomakehurst.wiremock.WireMockServer;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;


import com.redhat.coolstore.cart.model.Product;
import com.redhat.coolstore.cart.model.ShoppingCart;
import com.redhat.coolstore.cart.model.ShoppingCartItem;
import com.redhat.coolstore.cart.exceptions.itemIdException;;

// Enables Spring based dependency injection
@RunWith(SpringRunner.class)

// Tells SpringBoot to go and look for a main configuration class(ie: one with @SpringBootApplication) & use it to start a Spring application context.
// SpringBootTest.WebEnvironment loads an embedded WebApplicationContext and provides a real servlet environment
// A tomcat servlet container will be started in this test to host our JAX-RS based CartEndpoint 
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) 
public class CartEndpointTest extends BaseTest {

  private static final String CATALOG_MOCK_SERVICE_PORT = "CATALOG_MOCK_SERVICE_PORT";

  private static int cart_service_mock_port = 8888;
  private static int itemQty = 2;
  private static Logger log = Logger.getLogger("CartEndpointTest");
  private static ObjectMapper jsonMapper;
  private static String itemId;
  private static WireMockServer wireMockServer;

  @LocalServerPort
  private int servletContainerPort;

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
  
  @Before
  public void before() {
    if(servletContainerPort == 0)
      throw new RuntimeException("uh-oh: something wrong with injection of value of servlet container port");
     
    log.info("before() random servlet container port = "+servletContainerPort);     
    RestAssured.baseURI = String.format("http://localhost:%d/cart", servletContainerPort);
      
  }

  @AfterClass
  public static void shutdown() {
    wireMockServer.stop();
  }

  @Test
  public void getCartTest() throws itemIdException {

      // 1) Invoke ShoppingCartService to instantiate a new cart and add item & qty
      ShoppingCart testSCart = sCartService.addToCart(null, itemId, itemQty);
      String cartId = testSCart.getId();

      RestAssured.given().get("/{cartId}", cartId)
               .then()
               .assertThat()
               .statusCode(200)
               .contentType(ContentType.JSON)
               .body("id", equalTo(cartId));
  }

  //@Ignore
  @Test
  public void sanityCheckTest() {
    RestAssured.given().get("/sanityCheck")
               .then()
               .assertThat()
               .statusCode(200);
  }

}
