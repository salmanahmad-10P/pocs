package com.redhat.coolstore.cart.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="Shopping Cart ID not found.")
   public class cartIdException extends Exception {

     public cartIdException() {
         super();
     }
     public cartIdException(String cartId) {
         super(cartId);
         System.out.println("Shopping Cart ID "+cartId+" not found.");
     }
     public cartIdException(Throwable cause) {
         super(cause);
         System.out.println(cause.toString());
     }
   }
