package com.redhat.gpe.auditReview;

import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.acme.insurance.Policy;

@WebService(targetNamespace="urn:com.redhat.gpe.auditReview:1.0",
            serviceName="AuditReview", 
            portName="AuditReviewPort")
public class AuditReview implements IAuditReview {
    
    Logger log = LoggerFactory.getLogger("AuditReview");

    public boolean reviewQuote(Policy policyObj) {
        log.info("reviewQuote() policy = "+policyObj);
        return true;
    }
}
