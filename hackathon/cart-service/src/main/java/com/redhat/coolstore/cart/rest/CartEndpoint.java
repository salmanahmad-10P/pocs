package com.redhat.coolstore.cart.rest;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import com.redhat.coolstore.cart.implement.ShoppingCartServiceImpl;
import com.redhat.coolstore.cart.model.ShoppingCart;
import com.redhat.coolstore.cart.exceptions.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

@Path("/cart")
@Component
public class CartEndpoint {

    private static Logger log = Logger.getLogger("CartEndpoint");
    private ShoppingCartServiceImpl scs;

@Autowired
public void setShoppingCartSvcImpl(ShoppingCartServiceImpl scs) {
      this.scs = scs;
  }

@GET
@Path("/{cartId}")
@Produces(MediaType.APPLICATION_JSON)
public ShoppingCart getCart(@PathParam("cartId") String cartId) throws cartIdException, itemIdException {

return scs.getShoppingCart(cartId);
  }

@POST
@Path("/cart/{cartId}/{itemId}/{quantity}")
@Produces(MediaType.APPLICATION_JSON)
public ShoppingCart addItem(@PathParam("cartId") String cartId,@PathParam("itemId") String itemId,@PathParam("quantity") String quantity) throws cartIdException, itemIdException {

    ShoppingCart sCart = scs.addToCart(cartId, itemId, Integer.parseInt(quantity));
    log.info("addItem() sCart = \n"+sCart);
    return sCart;
  }

@DELETE
@Path("/cart/{cartId}/{itemId}/{quantity}")
@Produces(MediaType.APPLICATION_JSON)
public ShoppingCart removeItem(@PathParam("cartId") String cartId,@PathParam("itemId") String itemId,@PathParam("quantity") String quantity) throws cartIdException, itemIdException {

return scs.removeFromCart(cartId, itemId, Integer.parseInt(quantity));
    }

@POST
@Path("/cart/checkout/{cartId}")
@Produces(MediaType.APPLICATION_JSON)
public ShoppingCart checkoutCart(@PathParam("cartId") String cartId) throws cartIdException, itemIdException {

// TO_DO:  this appears to be incomplete.  Need to call shopping cart service rather than instantiate a new cart.
ShoppingCart sc = new ShoppingCart();
sc = scs.getShoppingCart(cartId);
sc.resetShoppingCartItemList();
return sc;
    }

    @GET
    @Path("/sanityCheck")
    @Produces("application/text")
    public String sanityCheck() {
        return "good to go!"; 
    }

}
