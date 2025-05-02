package jease.cms.web.filter.gzip;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class GZIPFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(GZIPFilter.class);

    private final List<String> excludedFolders = new ArrayList<String>();
    private final List<String> excludedContentTypes = new ArrayList<String>();

    private int gzipMaxCache;
    private int gzipMinCacheFileSize;
    private int gzipMaxCacheFileSize;

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
        s = filterConfig.getInitParameter("gzipMaxCache");
        if (!isEmpty(s)) {
            gzipMaxCache = Integer.parseUnsignedInt(s);
            s = filterConfig.getInitParameter("gzipMinCacheFileSize");
            gzipMinCacheFileSize = !isEmpty(s) ? Integer.parseUnsignedInt(s) : 400_000;
            s = filterConfig.getInitParameter("gzipMaxCacheFileSize");
            gzipMaxCacheFileSize = !isEmpty(s) ? Integer.parseUnsignedInt(s) : 100_000_000;
        } else {
            gzipMaxCache = 0;
            gzipMinCacheFileSize = 0;
            gzipMaxCacheFileSize = 0;
        }
        LOGGER.info("Initialized with excludedContentTypes = {} and excludedFolders = {}",
                excludedContentTypes, excludedFolders);
        if (gzipMaxCache <= 0) {
            LOGGER.info("gzipped files cache is OFF");
        } else {
            LOGGER.info("gzipped files cache: {} | {} | {}", gzipMaxCache, gzipMinCacheFileSize, gzipMaxCacheFileSize);
        }
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
        final String qs = request.getQueryString();
        final StringBuffer sb = request.getRequestURL();
        final String cacheKey;
        if (qs != null) {
            cacheKey = sb.append('?').append(qs).toString();
        } else {
            cacheKey = sb.toString();
        }
        if (qs != null && qs.contains("dir")) {
            execGZIP(request, response, filterChain, cacheKey,
                    gzipMaxCache, gzipMinCacheFileSize, gzipMaxCacheFileSize);
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
            GZIPResponseWrapper wrappedResponse = new GZIPResponseWrapper(response, cacheKey,
                    gzipMaxCache, gzipMinCacheFileSize, gzipMaxCacheFileSize);
            LOGGER.info("Check excludedContentTypes for: " + subPathCheckURL);
            wrappedResponse.setStopGZIP(this::stopGZIP);
            filterChain.doFilter(request, wrappedResponse);
            wrappedResponse.finishResponse();
            return;
        }
        execGZIP(request, response, filterChain, cacheKey, gzipMaxCache, gzipMinCacheFileSize, gzipMaxCacheFileSize);
    }

    private static void execGZIP(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain, String cacheKey,
            int gzipMaxCache, int gzipMinCacheFileSize, int gzipMaxCacheFileSize
            ) throws IOException, ServletException {
        // Wrap the response to enable GZIP compression
        GZIPResponseWrapper wrappedResponse = new GZIPResponseWrapper(response, cacheKey,
                gzipMaxCache, gzipMinCacheFileSize, gzipMaxCacheFileSize);
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
