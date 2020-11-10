<%@page import="java.lang.reflect.*,jfix.db4o.*,jease.cms.domain.*" contentType="text/plain"%>
<%
	Field revisionsField = Content.class.getDeclaredField("revisions");
	revisionsField.setAccessible(true);
	for (Content content : Database.query(Content.class)) {
		Blob[] revisions = (Blob[]) revisionsField.get(content);
		if (revisions != null) {
			Version[] versions = new Version[revisions.length];
			for (int i = 0; i < revisions.length; i++) {
				versions[i] = new Version(null, revisions[i]);
			}
			revisionsField.set(content, null);
			content.setRevisions(versions);
			Database.save(content);
			out.println("Revisions updated: " + content.getPath());
		}
	}
%>