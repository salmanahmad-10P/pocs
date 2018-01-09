package com.redhat.coolstore.cart.service;

import org.junit.Test;
import org.junit.Rule;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

import org.mockito.junit.MockitoRule;
import org.mockito.junit.MockitoJUnit;
import org.mockito.Mock;

import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.beans.factory.annotation.Autowired;

import com.redhat.coolstore.cart.model.ShoppingCart;

@RunWith(SpringRunner.class)

// Tells SpringBoot to go and look for a main configuration class(ie: one with @SpringBootApplication) & use it to start a Spring application context.
@SpringBootTest 
public class PriceCalculationServiceImplTest {

  @Autowired
  private PriceCalculationService priceCalculationService;

  @Test
  public void sanityTest() {
    System.out.println("sanityTest() ");
  }

  @Test
  public void priceCalculationServiceTest() {
    System.out.println("priceCalculationServiceTest() ");

    ShoppingCart sCart = getTestCart(0.01);
    ReflectionTestUtils.invokeMethod(priceCalculationService, "priceShoppingCart", sCart );
    assertTrue(sCart.getShippingTotal() == 2.99);

    sCart = getTestCart(29.01);
    ReflectionTestUtils.invokeMethod(priceCalculationService, "priceShoppingCart", sCart );
    assertTrue(sCart.getShippingTotal() == 4.99);

    sCart = getTestCart(59.01);
    ReflectionTestUtils.invokeMethod(priceCalculationService, "priceShoppingCart", sCart );
    assertTrue(sCart.getShippingTotal() == 6.99);

    sCart = getTestCart(99.01);
    ReflectionTestUtils.invokeMethod(priceCalculationService, "priceShoppingCart", sCart );
    assertTrue(sCart.getShippingTotal() == 0.0);

  }

  private static ShoppingCart getTestCart(double cartValue){
    ShoppingCart sCart = new ShoppingCart();
    sCart.setId("GPTE Shopping Cart");
    sCart.setCartItemTotal(cartValue);
    return sCart;
  }

}
