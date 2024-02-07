package jease.cms.web.filter.gzip;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GZIPFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(GZIPFilter.class);

    private final List<String> excludedFolders = new ArrayList<String>();
    private final List<String> excludedContentTypes = new ArrayList<String>();

    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String s = filterConfig.getInitParameter("excludedContentTypes");
        if (!isEmpty(s)) {
            String[] arr = s.split(",");
            for (int i = 0; i < arr.length; i++) excludedContentTypes.add(arr[i].trim());
        } else {
            excludedContentTypes.addAll(Arrays.asList("/png", "/jpg", "/jpeg"));
        }
        s = filterConfig.getInitParameter("excludedFolders");
        if (!isEmpty(s)) {
            String[] arr = s.split(",");
            for (int i = 0; i < arr.length; i++) excludedFolders.add(arr[i].trim());
        } else {
            excludedFolders.add("/images/");
        }
        LOGGER.info("GZIPFilter initialized with excludedContentTypes = {} and excludedFolders = {}",
                excludedContentTypes, excludedFolders);
    }

    // See http://stackoverflow.com/a/23014248
    private static String getSubPathCheckURL(HttpServletRequest servletRequest) {
        return servletRequest.getRequestURI();
    }

    private static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
            FilterChain filterChain) throws IOException, ServletException {

        if (!(servletRequest instanceof HttpServletRequest)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String ae = request.getHeader("Accept-Encoding");
        if (ae == null || !ae.contains("gzip")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        final String subPathCheckURL = getSubPathCheckURL(request);
        if (!excludedFolders.isEmpty()) {
            // Check if the request URI starts with an excluded folder
            boolean isExcludedFolder = excludedFolders.stream()
                    .anyMatch(folder -> subPathCheckURL.contains(folder));

            if (isExcludedFolder) {
                LOGGER.info("Excluded folder: " + subPathCheckURL);
                // If the request matches an excluded folder, allow it to continue without gzipping
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }
        }
        if (!excludedContentTypes.isEmpty()) {
            GZIPResponseWrapper wrappedResponse = new GZIPResponseWrapper(response);
            LOGGER.info("Check excludedContentTypes for: " + subPathCheckURL);
            wrappedResponse.setStopGZIP(this::stopGZIP);
            filterChain.doFilter(request, wrappedResponse);
            wrappedResponse.finishResponse();
            return;
        }
        // Wrap the response to enable GZIP compression
        GZIPResponseWrapper wrappedResponse = new GZIPResponseWrapper(response);
        filterChain.doFilter(request, wrappedResponse);
        wrappedResponse.finishResponse();
    }

    private boolean stopGZIP(String contentType) {
        LOGGER.info("Content type: " + contentType);
        if (!isEmpty(contentType)) {
            // Check if the request URI ends with an excluded extension
            boolean isExcludedContentType = excludedContentTypes.stream()
                .anyMatch(t -> contentType.contains(t));

            if (isExcludedContentType) {
                LOGGER.info("No gzip, excluded content type: " + contentType);
                return true;
            }
        }
        return false;
    }

}
