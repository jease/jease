package jease.cmf.web.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jease.cmf.domain.Node;
import jease.cmf.service.Nodes;

public class JeaseServletFilter implements Filter {

	private String JEASE_SITE_CONTROLLER;

	public void init(FilterConfig config) throws ServletException {
		JEASE_SITE_CONTROLLER = config.getServletContext().getInitParameter(
				"JEASE_SITE_CONTROLLER");
	}

	public void doFilter(ServletRequest servletRequest,
			ServletResponse servletResponse, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		String uri = request.getRequestURI();
		String path = uri.replaceFirst(request.getContextPath(), "");
		Node node = Nodes.getByPath(path);
		if (node == null) {
			chain.doFilter(request, response);
			return;
		}
		// Redirect if trailing slash is missing for containers.
		if (node.isContainer() && !uri.endsWith("/")) {
			if (request.getQueryString() != null) {
				response.sendRedirect(uri + "/?" + request.getQueryString());
			} else {
				response.sendRedirect(uri + "/");
			}
		} else {
			request.setAttribute(Node.class.getSimpleName(), node);
			request.getRequestDispatcher(JEASE_SITE_CONTROLLER).forward(
					request, response);
		}
	}

	public void destroy() {
	}

}
