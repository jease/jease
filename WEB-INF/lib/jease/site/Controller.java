/*
    Copyright (C) 2011 maik.jablonski@jease.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jease.site;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jease.Names;
import jease.Registry;
import jease.cmf.domain.Node;
import jease.cmf.service.Nodes;
import jease.cms.domain.Redirect;
import jfix.db4o.Database;
import jfix.functor.Function;
import jfix.functor.Supplier;
import jfix.servlet.ResponseRewriter;
import jfix.util.Compiler;

public class Controller implements javax.servlet.Filter {

	private static Supplier<List<Redirect>> redirectSupplier = new Supplier<List<Redirect>>() {
		public List<Redirect> get() {
			List<Redirect> redirects = Database.query(Redirect.class);
			Collections.sort(redirects, new Comparator<Redirect>() {
				public int compare(Redirect r1, Redirect r2) {
					return r2.getTimestamp().compareTo(r1.getTimestamp());
				}
			});
			return redirects;
		}
	};

	private static Supplier<Function<String, String>> rewriterSupplier = new Supplier<Function<String, String>>() {
		public Function<String, String> get() {
			return (Function<String, String>) new Compiler().eval(Registry
					.getParameter(Names.JEASE_SITE_REWRITER));
		}
	};

	private static String contextPath;
	private static String dispatcher;
	private static Pattern servlets;

	public static String getContextPath() {
		return contextPath;
	}

	public void init(FilterConfig config) throws ServletException {
		contextPath = config.getServletContext().getContextPath();
		dispatcher = config.getInitParameter(Names.JEASE_SITE_DISPATCHER);
		servlets = Pattern.compile(String.format("/(%s).*",
				config.getInitParameter(Names.JEASE_SITE_SERVLETS)));
	}

	public void doFilter(ServletRequest servletRequest,
			ServletResponse servletResponse, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		String uri = request.getRequestURI();
		// Redirect URI-embedded parameters
		// Example: test;file -> test?file
		if (!uri.contains(";jsessionid")) {
			int semicolon = uri.indexOf(";");
			if (semicolon != -1) {
				String path = uri.substring(0, semicolon) + "?"
						+ uri.substring(semicolon + 1);
				response.sendRedirect(response.encodeRedirectURL(buildURI(path,
						request.getQueryString())));
				return;
			}
		}

		// Process internal context prefix
		int tilde = uri.indexOf("~");
		if (tilde != -1) {
			String path = uri.substring(tilde + 1);
			response.sendRedirect(response.encodeRedirectURL(buildURI(
					request.getContextPath() + path, request.getQueryString())));
			return;
		}

		// Try to resolve node from URI (without context path).
		String nodePath = uri.substring(request.getContextPath().length());
		Node node = Nodes.getByPath(nodePath);

		// Process redirect rules
		if (node == null && !servlets.matcher(nodePath).matches()) {
			String sourceURI = buildURI(nodePath, request.getQueryString());
			String targetURI = rewriteURI(sourceURI);
			if (!targetURI.equals(sourceURI)) {
				if (targetURI.contains("://")) {
					response.sendRedirect(response.encodeRedirectURL(targetURI));
				} else {
					response.sendRedirect(response.encodeRedirectURL(request
							.getContextPath() + targetURI));
				}
				return;
			}
		}

		// If no node is found, process filter chain.
		if (node == null) {
			chain.doFilter(request, response);
			return;
		}

		// Redirect if trailing slash is missing for containers.
		if (node.isContainer() && !uri.endsWith("/")) {
			response.sendRedirect(response.encodeRedirectURL(buildURI(
					uri + "/", request.getQueryString())));
		} else {
			// Set node into request scope and forward to dispatcher
			request.setAttribute(Node.class.getSimpleName(), node);
			request.setAttribute(Names.JEASE_SITE_DISPATCHER, dispatcher);
			Function<String, String> rewriter = Registry
					.getParameter(Names.JEASE_SITE_REWRITER) != null ? Database
					.query(rewriterSupplier) : null;
			request.getRequestDispatcher(dispatcher).forward(
					request,
					rewriter != null ? new ResponseRewriter(response, rewriter)
							: response);
		}
	}

	public void destroy() {
	}

	private String buildURI(String uri, String query) {
		if (query != null) {
			return String.format("%s%s%s", uri, uri.contains("?") ? "&" : "?",
					query);
		} else {
			return uri;
		}
	}

	private String rewriteURI(String uri) {
		for (Redirect redirect : Database.query(redirectSupplier)) {
			String output = redirect.transform(uri);
			if (!output.equals(uri)) {
				return output;
			}
		}
		return uri;
	}

}
