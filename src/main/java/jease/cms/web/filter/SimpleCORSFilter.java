package jease.cms.web.filter;

import java.io.IOException;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebFilter(urlPatterns = { "/*" }, dispatcherTypes = { DispatcherType.REQUEST,
        DispatcherType.FORWARD, DispatcherType.INCLUDE, DispatcherType.ERROR })
public class SimpleCORSFilter implements Filter {

    @Override
    public void init(FilterConfig config) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        String s = req.getServletContext().getInitParameter("jease.cors.enabled");
        if (s == null || s.isEmpty() || "true".equals(s)) {
            HttpServletResponse response = (HttpServletResponse) resp;
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "OPTIONS, GET, POST, DELETE, PUT, PATCH");
            response.setHeader("Access-Control-Max-Age", "3600");
            HttpServletRequest request = (HttpServletRequest) req;
            s = request.getHeader("Access-Control-Request-Headers");
            if (s != null && !s.isEmpty()) response.setHeader("Access-Control-Allow-Headers", s);
            s = request.getHeader("Origin"); // see https://stackoverflow.com/a/45723981
            if (s != null && !s.isEmpty()) response.setHeader("Access-Control-Allow-Origin", s);
        }
        chain.doFilter(req, resp);
    }

    @Override
    public void destroy() {
    }

}
