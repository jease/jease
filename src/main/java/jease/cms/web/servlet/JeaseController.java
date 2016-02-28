/*
    Copyright (C) 2016 maik.jablonski@jease.org

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
package jease.cms.web.servlet;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import javax.servlet.DispatcherType;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jease.Names;
import jease.Registry;
import jease.cmf.domain.Node;
import jease.cmf.service.Nodes;
import jease.cms.domain.Redirect;
import jfix.db4o.Database;
import jfix.servlet.ResponseRewriter;
import jfix.servlet.Servlets;

import org.apache.commons.lang3.StringUtils;

@WebFilter(urlPatterns = { "/*" }, dispatcherTypes = { DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE, DispatcherType.ERROR })
public class JeaseController implements javax.servlet.Filter {

	private static Supplier<List<Redirect>> redirectSupplier = () -> {
		List<Redirect> redirects = Database.query(Redirect.class);
		redirects.sort(Comparator.comparing(Redirect::getTimestamp));
		return redirects;
	};

	private static Supplier<Function<String, String>> rewriterSupplier = () -> {
		try {
			String jeaseSiteRewriter = Registry.getParameter(Names.JEASE_SITE_REWRITER);
			return (Function<String, String>) Class.forName(jeaseSiteRewriter).newInstance();
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
			return null;
		}
	};

	private static String contextPath;
	private static String dispatcher;
	private static Pattern servlets;
	private static Set<String> locales;

	public static String getContextPath() {
		return contextPath;
	}

	public void init(FilterConfig config) throws ServletException {
		contextPath = config.getServletContext().getContextPath();
		dispatcher = config.getServletContext().getInitParameter(Names.JEASE_SITE_DISPATCHER);
		servlets = Pattern.compile(String.format("/(%s).*", config.getServletContext().getInitParameter(Names.JEASE_SITE_SERVLETS)));
		locales = new HashSet<>();
		for (Locale locale : Locale.getAvailableLocales()) {
			locales.add(locale.getLanguage());
		}
	}

	public void destroy() {
	}

	public void doFilter(ServletRequest servletRequest,
			ServletResponse servletResponse, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		String uri = request.getRequestURI();

		// Strip jsessionid from URI.
		int jsessionidIndex = uri.indexOf(";jsessionid=");
		if (jsessionidIndex != -1) {
			uri = uri.substring(0, jsessionidIndex);
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

		// Save "virtual" root node. Per default it is the absolute root of the
		// instance.
		// If a node with the server name exists, this node is used as virtual
		// root.
		if (request.getAttribute("Root") == null) {
			String server = Servlets.getServerName(request).replaceFirst(
					"www.", "");
			Node root = Nodes.getRoot() != null ? Nodes.getRoot().getChild(
					server) : null;
			if (root == null) {
				root = Nodes.getRoot();
			}
			if (node != null) {
				if (node.getParent() == root && locales.contains(node.getId())) {
					root = node;
				} else {
					for (Node parent : node.getParents()) {
						if (parent.getParent() == root
								&& locales.contains(parent.getId())) {
							root = parent;
							break;
						}
					}
				}
			}
			request.setAttribute("Root", root);
			if (node != null && root.getParent() == node) {
				response.sendRedirect(response.encodeRedirectURL(request
						.getContextPath() + root.getPath()));
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
			response.sendRedirect(response.encodeRedirectURL(buildURI(uri + "/", request.getQueryString())));
		} else {
			// Set node into request scope and forward to dispatcher
			request.setAttribute(Node.class.getSimpleName(), node);
			request.setAttribute(Names.JEASE_SITE_DISPATCHER, dispatcher);
			Function<String, String> rewriter = StringUtils.isNotBlank(Registry
					.getParameter(Names.JEASE_SITE_REWRITER)) ? Database
					.query(rewriterSupplier) : null;
			request.getRequestDispatcher(dispatcher).forward(
					request,
					rewriter != null ? new ResponseRewriter(response, rewriter)
							: response);
		}
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
