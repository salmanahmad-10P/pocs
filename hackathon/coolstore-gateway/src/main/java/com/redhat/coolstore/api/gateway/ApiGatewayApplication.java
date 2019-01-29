package com.redhat.coolstore.api.gateway;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.apache.camel.spi.RestConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@Configuration
@ComponentScan
@EnableSwagger2
@EnableAutoConfiguration
@PropertySource("classpath:swagger.properties")
public class ApiGatewayApplication extends SpringBootServletInitializer {
	private static final String CAMEL_URL_MAPPING = "/api/*";
	private static final String CAMEL_SERVLET_NAME = "CamelServlet";

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(
			SpringApplicationBuilder application) {
		return application.sources(applicationClass);
	}

	@Bean
	public ServletRegistrationBean servletRegistrationBean() {
		ServletRegistrationBean registration = new ServletRegistrationBean(
				new CORSServlet(), CAMEL_URL_MAPPING);
		registration.setName(CAMEL_SERVLET_NAME);

		return registration;
	}

	private static Class<ApiGatewayApplication> applicationClass = ApiGatewayApplication.class;

	private class CORSServlet extends CamelHttpTransportServlet {
		private static final long serialVersionUID = 1L;

		@Override
		protected void doService(HttpServletRequest request,
				HttpServletResponse response) throws ServletException,
				IOException {

			String authHeader = request.getHeader("Authorization");
			String origin = request.getHeader("Origin");
			if (origin == null || origin.isEmpty()) {
				origin = "*";
			}

			if (authHeader == null || authHeader.isEmpty()) {
				response.setHeader("Access-Control-Allow-Origin", origin);
				response.setHeader("Access-Control-Allow-Methods",
						RestConfiguration.CORS_ACCESS_CONTROL_ALLOW_METHODS);
				response.setHeader(
						"Access-Control-Allow-Headers",
						"Authorization, "
								+ RestConfiguration.CORS_ACCESS_CONTROL_ALLOW_HEADERS);
				response.setHeader("Access-Control-Max-Age",
						RestConfiguration.CORS_ACCESS_CONTROL_MAX_AGE);
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}

			super.doService(request, response);
		}
	}
}