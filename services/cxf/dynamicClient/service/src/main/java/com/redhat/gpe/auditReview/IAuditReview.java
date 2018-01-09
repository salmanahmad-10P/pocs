package com.redhat.gpe.auditReview;

import org.acme.insurance.Policy;

public interface IAuditReview {

    // implementation returns boolean indicating either approval or rejection of quote
    boolean reviewQuote(Policy policyObj);
}
