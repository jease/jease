<%@page import="java.util.*,org.apache.commons.io.*,jease.cms.domain.*,jease.site.*"%>
<%!
	final String SCRIPT_DEFAULT_EXTENSION = ".jsp";
	final String SCRIPT_WEBINF_FOLDER = "/WEB-INF/tmp/script/";

	java.io.File workDirectory;
	
	public void jspInit() {
		workDirectory = new java.io.File(getServletContext().getRealPath("/")
				+ SCRIPT_WEBINF_FOLDER.replace("/", java.io.File.separator));
	}
%>
<%
	Script script = (Script) request.getAttribute("Node");

	String scriptParentPath = script.getParent().getPath();
	java.io.File scriptDirectory = new java.io.File(workDirectory, scriptParentPath.replace("/", java.io.File.separator));
	scriptDirectory.mkdirs();
	
	String scriptId = script.getId();
	if (scriptId.lastIndexOf(".") == -1) {
		scriptId += SCRIPT_DEFAULT_EXTENSION;
	}

	java.io.File scriptFile = new java.io.File(scriptDirectory, scriptId);
	if (scriptFile.lastModified() < script.getLastModified().getTime()) {
		FileUtils.writeStringToFile(scriptFile, script.getCode(), "UTF-8");
	}

	try {
		if(scriptFile.getName().endsWith(".java")) {
			HttpServlet servlet = (HttpServlet) Compilers.eval(scriptFile);
			servlet.init(config);
			servlet.service(request, response);
			servlet.destroy();
		} else {
			String resourcePath = SCRIPT_WEBINF_FOLDER + scriptParentPath + "/" + scriptId;
			if(request.getParameter("file") != null) {
				pageContext.forward(resourcePath);
			} else {
				pageContext.include(resourcePath);	
			}
		}
	} catch (Exception e) {
		response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		out.println("<pre>" + jfix.util.Regexps.quoteMarkup(e.getMessage()) + "</pre>");
	}
%>
