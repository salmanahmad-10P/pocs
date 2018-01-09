package com.redhat.coolstore.cart.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="Item ID not found.")
    public class itemIdException extends Exception {

      public itemIdException() {
          super();
      }
      public itemIdException(String itemId) {
          super(itemId);
          System.out.println("Item ID "+itemId+" not found.");
      }
      public itemIdException(Throwable cause) {
          super(cause);
          System.out.println(cause.toString());
      }
   }
