package org.acme.insurance;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class Rejection {

    private String reason;

    public Rejection() {
    }

    public Rejection(String reason) {
        this.reason = reason;
    }
    public String getReason(){
        return reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }
}
