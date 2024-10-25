package jease.cms.web.filter.etag;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dynatrace.hash4j.hashing.Hashing;

public class ETagServletFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ETagServletFilter.class);

    private static final String ETAG_HEADER = "ETag";
    private static final String IF_NONE_MATCH_HEADER = "If-None-Match";
    private static final String IF_MODIFIED_SINCE_HEADER = "If-Modified-Since";
    private static final String LAST_MODIFIED_HEADER = "Last-Modified";
    private static final String CACHE_CONTROL_HEADER = "Cache-Control";

    private String contentTypeFilter;
    private Pattern contentTypePattern;

    private String subPathFilter = ".*";
    private Pattern subPathPattern;

    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        contentTypeFilter = filterConfig.getInitParameter("contentTypeFilter");
        if (!isEmpty(contentTypeFilter)) {
            LOGGER.info("ETagServletFilter contentTypeFilter = " + contentTypeFilter);
            contentTypePattern = Pattern.compile(contentTypeFilter);
        } else {
            contentTypePattern = null;
        }

        String subPathFilter = filterConfig.getInitParameter("subPathFilter");
        if (!isEmpty(subPathFilter)) this.subPathFilter = subPathFilter;
        LOGGER.info("ETagServletFilter subPathFilter = " + this.subPathFilter);
        subPathPattern = Pattern.compile(this.subPathFilter);
    }

    // See http://stackoverflow.com/a/23014248
    private static String getSubPathCheckURL(HttpServletRequest servletRequest) {
        // Implement this if you want to match query parameters, otherwise
        // servletRequest.getRequestURI() or servletRequest.getRequestURL
        // should be good enough. Also you may want to handle URL decoding here.
        return servletRequest.getRequestURI();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;

        Matcher m = subPathPattern.matcher(getSubPathCheckURL(req));
        if (!m.matches()) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletResponse resp = (HttpServletResponse) response;
        ETagResponseWrapper wrapResp = new ETagResponseWrapper(resp);

        chain.doFilter(request, wrapResp);

        byte[] bytes = wrapResp.getCaptureAsBytes();
        LOGGER.info("Response bytes length = " + bytes.length);

        if (contentTypePattern != null) {
            String contentType = resp.getContentType();
            if (contentType == null || !contentTypePattern.matcher(contentType).matches()) {
                LOGGER.info("Non-supported content: " + contentType);
                writeResp(bytes, resp);
                return;
            }
        }
        //String resourcePath = getResourcePathFromRequest(req);
        //String token = ETagHashUtils.getMd5Digest(bytes, resourcePath);
        long bytesHash = Hashing.xxh3_64().hashBytesToLong(bytes);
        String token = Long.toUnsignedString(bytesHash, 16);
        if (isEmpty(token)) {
            writeResp(bytes, resp);
            return;
        }
        resp.setHeader(ETAG_HEADER, token);
        String s = req.getServletContext().getInitParameter("jease.etag.max.age");
        if (s == null || s.isEmpty()) s = "900";
        resp.setHeader(CACHE_CONTROL_HEADER, "max-age=" + s);

        String previousToken = req.getHeader(IF_NONE_MATCH_HEADER);
        if (previousToken != null && previousToken.equals(token)) { // compare previous token with current one
            LOGGER.info(ETAG_HEADER + " match: returning '304 Not Modified'");
            resp.sendError(HttpServletResponse.SC_NOT_MODIFIED);
            resp.setHeader(LAST_MODIFIED_HEADER, resp.getHeader(IF_MODIFIED_SINCE_HEADER));
        } else { // first time through - set last modified time to now
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.MILLISECOND, 0);
            Date lastModified = cal.getTime();
            resp.setDateHeader(LAST_MODIFIED_HEADER, lastModified.getTime());
            writeResp(bytes, resp);
        }
    }

    private static void writeResp(byte[] bytes, HttpServletResponse resp) throws IOException {
        LOGGER.info("Sending response content, length = " + bytes.length);
        resp.setContentLength(bytes.length);
        ServletOutputStream outStream = resp.getOutputStream();
        outStream.write(bytes);
        outStream.flush();
        //outStream.close();
    }

    private static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    private static String getResourcePathFromRequest(HttpServletRequest req) {
        String requestPath = req.getRequestURI();
        return requestPath;
    }

}
