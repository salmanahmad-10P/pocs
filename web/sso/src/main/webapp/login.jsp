<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<%String path = request.getContextPath();
			String basePath = request.getScheme() + "://"
					+ request.getServerName() + ":" + request.getServerPort()
					+ path + "/";
%>


<f:view>
    <html>
        <head>
            <title><h:outputText value="Login" /></title>

    </script>
        </head>

        <body>
            <f:verbatim>
                <form method="POST" action="<%= request.getContextPath() %>/j_security_check">
            </f:verbatim>

            <h:panelGrid columns="2" border="0" cellpadding="3" cellspacing="5" styleClass="login-table-background">


                <h:panelGrid columns="3" border="0" cellpadding="2" cellspacing="3" headerClass="login-heading">
                    <f:facet name="header">
                        <h:outputText value="Welcome" styleClass="login-text" />
                    </f:facet>

                    <h:panelGroup />
                    <h:messages globalOnly="true" styleClass="errors" />
                    <h:panelGroup />

                    <h:outputLabel for="j_username">
                        <h:outputText value="Username" styleClass="login-text" />
                    </h:outputLabel>
                    <h:inputText id="j_username" size="20" maxlength="30" required="true" value="">
                        <f:validateLength minimum="5" maximum="30" />
                    </h:inputText>
                    <h:message for="j_username" styleClass="errors" />

                    <h:outputLabel for="j_password">
                        <h:outputText value="Password" styleClass="login-text" />
                    </h:outputLabel>
                    <h:inputSecret id="j_password" size="20" maxlength="20"  required="true" value="">
                        <f:validateLength minimum="5" maximum="15" />
                    </h:inputSecret>
                    <h:message for="j_password" styleClass="errors" />

                    <f:verbatim>&nbsp;</f:verbatim>
                    <h:panelGrid columns="3" border="0" cellpadding="0" cellspacing="5" styleClass="login">
                        <f:verbatim>&nbsp;</f:verbatim>
                        <f:verbatim>&nbsp;</f:verbatim>
			<h:form>
                        	<h:commandButton image="/images/submit.png" styleClass="logon" action="j_security_check" title="Logon"  />
			</h:form>
                    </h:panelGrid>
                    <f:verbatim>&nbsp;</f:verbatim>
                </h:panelGrid>
            </h:panelGrid>
            <f:verbatim>
                </form>
            </f:verbatim>

        </body>
    </html>
</f:view>

