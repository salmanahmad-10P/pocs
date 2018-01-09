package com.redhat.coolstore.cart.implement;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.cxf.common.util.StringUtils;

import com.redhat.coolstore.cart.model.Product;
import com.redhat.coolstore.cart.model.ShoppingCart;
import com.redhat.coolstore.cart.model.ShoppingCartItem;
import com.redhat.coolstore.cart.service.CatalogService;
import com.redhat.coolstore.cart.service.PriceCalculationService;
import com.redhat.coolstore.cart.service.ShoppingCartService;
import com.redhat.coolstore.cart.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ShoppingCartServiceImpl implements ShoppingCartService{

      private static Map<String, ShoppingCart> sCartMap = new ConcurrentHashMap<String, ShoppingCart>();

      @Autowired
      CatalogService cService;

      @Autowired
      PriceCalculationService pService;

      public ShoppingCart calculateCartPrice(ShoppingCart sc) {

          pService.priceShoppingCart(sc);
          return sc;
      }

      public ShoppingCart getShoppingCart(String cartId) {
        return sCartMap.get(cartId);
      }

      public ShoppingCart addToCart(String cartId, String itemId, int quantity) throws itemIdException{

        ShoppingCart sc = null;
        if(StringUtils.isEmpty(cartId) || ShoppingCartService.NEW_CART.equals(cartId)) {
            sc = newCart(null);
        } else if (sCartMap.get(cartId) == null) {
            sc = newCart(cartId);
        } else {
            sc = sCartMap.get(cartId);
        }

        ShoppingCartItem sci = new ShoppingCartItem();

        Product prodObj = cService.getProduct(itemId);

        sci.setProduct(prodObj);
        sci.setQuantity(quantity);

        sci.setPrice(prodObj.getPrice() * quantity);
        sc.addShoppingCartItem(sci);

        return sc;
      }

      public ShoppingCart removeFromCart(String cartId, String itemId, int quantity) throws cartIdException, itemIdException{

        ShoppingCart sc = sCartMap.get(cartId);
        List<ShoppingCartItem> scList = sc.getShoppingCartItemList();
        ShoppingCartItem sCartItem = null;

        // Iterate through shopping cart list to locate item
        for(ShoppingCartItem scItem : scList) {
            if(scItem.getProduct().getItemId().equals(itemId)){
                sCartItem = scItem;
                int currentQty = sCartItem.getQuantity();
                int newQty = currentQty - quantity;
                sCartItem.setQuantity(newQty);
                break;
            }
        }

        // Throw a custom exception rather than a vanilla RuntimeException
        if(sCartItem == null)
            throw new itemIdException(itemId);

        // If quantity of item is less than 1, then remove item from shopping cart
        if(sCartItem.getQuantity() < 1) {
            sc.removeShoppingCartItem(sCartItem);
        }

        return sc;

      }

      private ShoppingCart newCart(String cartId) {

          ShoppingCart sc = new ShoppingCart();

          if(cartId == null) {
            Random random = new Random(System.currentTimeMillis());
            long randomLong = random.nextLong();
            cartId = Long.toHexString(randomLong);
          }

          sc.setId(cartId);
          sCartMap.put(cartId, sc);
          return sc;
      }
}
