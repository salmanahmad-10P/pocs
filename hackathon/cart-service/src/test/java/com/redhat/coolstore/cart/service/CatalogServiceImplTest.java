package com.redhat.coolstore.cart.service;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import org.apache.cxf.common.util.StringUtils;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.Ignore;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.tomakehurst.wiremock.WireMockServer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import com.redhat.coolstore.cart.model.Product;
import com.redhat.coolstore.cart.exceptions.itemIdException;

@RunWith(SpringRunner.class)

// Tells SpringBoot to go and look for a main configuration class(ie: one with @SpringBootApplication) & use it to start a Spring application context.
@SpringBootTest 
public class CatalogServiceImplTest extends BaseTest {

  private static final String CATALOG_MOCK_SERVICE_PORT = "CATALOG_MOCK_SERVICE_PORT";

  private static int cart_service_mock_port = 8888;
  private static double product_price = 33.56;
  private static Logger log = Logger.getLogger("CatalogServiceImplTest");
  private static ObjectMapper jsonMapper;
  private static String itemId;
  private static WireMockServer wireMockServer;

  @Autowired
  private CatalogService catalogService;
  
  @BeforeClass
  public static void startup() throws JsonProcessingException {

    // 1) Resolve test variables
    String catPort = System.getProperty(CATALOG_MOCK_SERVICE_PORT);
    if(!StringUtils.isEmpty(catPort)){
        cart_service_mock_port = Integer.parseInt(catPort);
    }
    itemId = System.getProperty(CatalogService.ITEM_ID);
   
    // 2) Instantiate a JSON reporesentation of a Product obj 
    jsonMapper = new ObjectMapper();
    Product testProdObj = getTestProduct(itemId);
    String pResponse = jsonMapper.writeValueAsString(testProdObj);

    // 3) Instatiate WireMock listener and configure with appropriate response body(ie:  prouduct JSON) and status
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
  public void getProductTest() throws JsonProcessingException, itemIdException {
      Product pObj = catalogService.getProduct(itemId);
      log.info("getProductTest() product = "+pObj.toString());
      assert(pObj != null);
  }
  
  @Ignore
  @Test(expected=org.springframework.web.client.HttpClientErrorException.class)
  public void getWrongProductTest() throws JsonProcessingException, itemIdException  {
      Product pObj = catalogService.getProduct(itemId+"sfsdf");
      log.info("getWrongProductTest() product = "+pObj.toString());
  }

}
