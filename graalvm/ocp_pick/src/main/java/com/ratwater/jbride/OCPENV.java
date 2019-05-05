package com.ratwater.jbride;

public class OCPENV {

    private String type = "AWS";
    private String guid = null;
    private boolean loginAsAdmin = false;
    private String adminUser = "opentlc-mgr";
    private String nonAdminUser = "user1";
    private String subdomainBase = "openshift.opentlc.com";

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getGuid() {
        return guid;
    }
    public void setGuid(String guid) {
        this.guid = guid;
    }
    public boolean isLoginAsAdmin() {
        return loginAsAdmin;
    }
    public void setLoginAsAdmin(boolean loginAsAdmin) {
        this.loginAsAdmin = loginAsAdmin;
    }
    public String getAdminUser() {
        return adminUser;
    }
    public void setAdminUser(String adminUser) {
        this.adminUser = adminUser;
    }
    public String getNonAdminUser() {
        return nonAdminUser;
    }
    public void setNonAdminUser(String nonAdminUser) {
        this.nonAdminUser = nonAdminUser;
    }
    public String getSubdomainBase() {
        return subdomainBase;
    }
    public void setSubdomainBase(String sb) {
        this.subdomainBase = sb;
    }

    public String toString() {
        return "OCPEnv [guid=" + guid + ", adminUser=" + adminUser + ", loginAsAdmin=" + loginAsAdmin
                + ", nonAdminUser=" + nonAdminUser + ", type=" + type + ", subdomainBase=" +subdomainBase + "]";
    }
    
}