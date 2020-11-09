<%@page import="java.io.FileInputStream,org.apache.commons.io.*,jease.cms.domain.*"%>
<%
	File file = (File) request.getAttribute("Node");
	response.setContentType(file.getContentType());
	response.setContentLength((int) file.getFile().length());
	IOUtils.copy(new FileInputStream(file.getFile()), response.getOutputStream());
%>