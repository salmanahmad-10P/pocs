package com.redhat.coolstore.cart.service;

import com.redhat.coolstore.cart.model.ShoppingCart;
import com.redhat.coolstore.cart.exceptions.*;

public interface ShoppingCartService {

    public static final String NEW_CART="newcart";

    public ShoppingCart calculateCartPrice(ShoppingCart sc);

    public ShoppingCart getShoppingCart(String cartId);

    public ShoppingCart addToCart(String cartId, String itemId, int quantity) throws itemIdException;

    public ShoppingCart removeFromCart(String cartId, String itemId, int quantity) throws cartIdException, itemIdException;

}
