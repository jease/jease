<%@page import="java.io.FileInputStream,org.apache.commons.io.*,jease.cms.domain.*"%>
<%
	File file = (File) request.getAttribute("Node");		
	response.setContentLength((int) file.getFile().length());
	response.setContentType(file.getContentType());
	if(file.getContentType().startsWith("text")) {
		IOUtils.copy(new FileInputStream(file.getFile()), response.getWriter());
	} else {
		IOUtils.copy(new FileInputStream(file.getFile()), response.getOutputStream());
	}
%>