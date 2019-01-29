<%@page import="javax.servlet.http.HttpSession, org.jboss.cache.TreeCacheMBean"%>
<% 
	 session.invalidate();
	 response.sendRedirect("index.html");
%>
