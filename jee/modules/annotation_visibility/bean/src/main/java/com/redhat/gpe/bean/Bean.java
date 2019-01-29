package com.redhat.gpe.bean;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.Id;

import com.redhat.gpe.domain.Customer;

@Singleton
@Startup
public class Bean {

    @PostConstruct
    public void init() throws ClassNotFoundException{
        Customer cObj = new Customer();
        //Class varClass = Class.forName("com.redhat.gpe.domain.Customer");
        Class varClass = cObj.getClass();
        System.out.println("init() class = "+varClass);
        do {
            Field[] fields = varClass.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                Id id = field.getAnnotation(Id.class);
                if (id != null) {
                   System.out.println("******* FOUND ANNOTATED FIELD **************");
                }
            }
        } while ((varClass = varClass.getSuperclass()) != null);
    }
}
