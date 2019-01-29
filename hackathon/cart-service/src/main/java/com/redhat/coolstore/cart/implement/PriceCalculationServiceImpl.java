package com.redhat.coolstore.cart.implement;

import com.redhat.coolstore.cart.model.ShoppingCart;
import com.redhat.coolstore.cart.model.ShoppingCartItem;
import com.redhat.coolstore.cart.service.PriceCalculationService;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.ListIterator;

@Component
public class PriceCalculationServiceImpl implements PriceCalculationService{

    public void priceShoppingCart(ShoppingCart sc) {

      double cartItemTotal = sc.getCartItemTotal();
      List<ShoppingCartItem> shoppingCartItemList;
      double cartTotal = 0;

      //Cart Price Calculation without shipping Cost

      ListIterator<ShoppingCartItem> litr = null;
      shoppingCartItemList = sc.getShoppingCartItemList();
      litr=shoppingCartItemList.listIterator();

      System.out.println("Calculating Total Price of Shopping Cart Items");
      while(litr.hasNext()){
          ShoppingCartItem scItem = litr.next();
          cartTotal = cartTotal + scItem.getPrice();
      }


     //Shipping Cost Calculation
      if (sc.getCartItemTotal() > 0 && sc.getCartItemTotal() < 25) {
        sc.setShippingTotal(2.99);
      } else if (sc.getCartItemTotal() < 50) {
        sc.setShippingTotal(4.99);
      } else if (sc.getCartItemTotal() < 75) {
        sc.setShippingTotal(6.99);
      } else {
        sc.setShippingTotal(0);
      }
    
      // Calculate Cart Total
      cartTotal = cartTotal + sc.getShippingTotal();
      sc.setCartTotal(cartTotal);
  }
}
