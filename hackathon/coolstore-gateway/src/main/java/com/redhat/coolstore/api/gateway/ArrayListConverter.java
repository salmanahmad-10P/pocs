package com.redhat.coolstore.api.gateway;

import java.io.InputStream;
import java.util.ArrayList;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ArrayListConverter implements Converter<ArrayList<?>, InputStream> {

	public InputStream convert(ArrayList<?> source) {
		return null;
	}

}
