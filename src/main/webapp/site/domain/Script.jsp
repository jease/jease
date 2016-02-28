<%@page import="org.apache.commons.lang3.StringEscapeUtils"%>
<%@page import="org.apache.commons.io.FileUtils"%>
<%@page import="jease.cms.domain.Script"%>
<%!
	final String SCRIPT_DEFAULT_EXTENSION = ".jsp";
	final String SCRIPT_WEBINF_FOLDER = "/WEB-INF/tmp/";

	java.io.File workDirectory;
	
	public void jspInit() {
		workDirectory = new java.io.File(getServletContext().getRealPath("/")
				+ SCRIPT_WEBINF_FOLDER.replace("/", java.io.File.separator));
		workDirectory.deleteOnExit();
	}
%>
<%
	Script script = (Script) request.getAttribute("Node");
	if (session.getAttribute(script.getPath()) != null) {
		script = (Script) session.getAttribute(script.getPath());
	}
	
	java.io.File scriptDirectory = new java.io.File(workDirectory, script.getUUID());
	scriptDirectory.mkdirs();
	scriptDirectory.deleteOnExit();
	
	String scriptId = script.getId();
	if (scriptId.lastIndexOf(".") == -1) {
		scriptId += SCRIPT_DEFAULT_EXTENSION;
	}

	java.io.File scriptFile = new java.io.File(scriptDirectory, scriptId);
	scriptFile.deleteOnExit();
	if (scriptFile.lastModified() < script.getLastModified().getTime()) {
		scriptFile.delete();
		FileUtils.writeStringToFile(scriptFile, script.getCode(), "UTF-8");
	}

	try {
		String resourcePath = SCRIPT_WEBINF_FOLDER + script.getUUID() + "/" + scriptId;
		if (script.isForward() || request.getParameter("file") != null) {
			pageContext.forward(resourcePath);
		} else {
			pageContext.include(resourcePath);
		}
	} catch (Exception e) {
		response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		out.println("<pre>" + StringEscapeUtils.escapeHtml4(e.getMessage()) + "</pre>");
	}
%>