<%@page import="java.util.*,java.util.concurrent.*,org.apache.commons.io.*,jease.cms.domain.*"%>
<%!
	final String SCRIPT_DEFAULT_EXTENSION = ".jsp";
	final String SCRIPT_WEBINF_FOLDER = "/WEB-INF/tmp/script/";

	String scriptDirectory;

	public void jspInit() {
		scriptDirectory = getServletContext().getRealPath("/")
				+ SCRIPT_WEBINF_FOLDER.replace("/", java.io.File.separator);
		new java.io.File(scriptDirectory).mkdirs();
	}
%>
<%
	Script script = (Script) request.getAttribute("Node");

	String scriptId = script.getPath().replace("/", "_");
	if (scriptId.lastIndexOf(".") == -1) {
		scriptId += SCRIPT_DEFAULT_EXTENSION;
	}

	java.io.File scriptFile = new java.io.File(scriptDirectory + scriptId);
	if (scriptFile.lastModified() < script.getLastModified().getTime()) {
		FileUtils.writeStringToFile(scriptFile, script.getCode());
	}

	try {
		pageContext.include(SCRIPT_WEBINF_FOLDER + scriptId);
	} catch (Exception e) {
		response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		out.println("<pre>" + jfix.util.Regexps.quoteMarkup(e.getMessage()) + "</pre>");
	}
%>
