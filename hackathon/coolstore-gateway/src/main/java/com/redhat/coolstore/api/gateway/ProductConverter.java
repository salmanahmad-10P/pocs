package com.redhat.coolstore.api.gateway;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.redhat.coolstore.api.gateway.model.Product;

@Component
public class ProductConverter implements Converter<Product, String> {

	public String convert(Product source) {
		return source.toString();
	}

}
