package jease.cms.web.filter.gzip;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.function.Predicate;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import jfix.zk.Modal;

public class GZIPResponseWrapper extends HttpServletResponseWrapper {
    private final HttpServletResponse origResponse;
    private GZIPResponseStream stream;
    private PrintWriter writer;
    private Predicate<String> stopGZIP;

    public GZIPResponseWrapper(HttpServletResponse response) {
        super(response);
        origResponse = response;
    }

    private GZIPResponseStream createOutputStream() throws IOException {
        GZIPResponseStream st = new GZIPResponseStream(origResponse);
        st.setStopGZIP(stopGZIP);
        return st;
    }

    public void finishResponse() {
        try {
            if (writer != null) {
                writer.close();
            } else {
                if (stream != null) {
                    stream.close();
                }
            }
        } catch (IOException e) {
            Modal.exception(e);
        }
    }

    @Override
    public void flushBuffer() throws IOException {
        stream.flush();
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (writer != null) {
            throw new IllegalStateException("getWriter() has already been called!");
        }

        if (stream == null) stream = createOutputStream();
        return (stream);
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (writer != null) {
            return (writer);
        }

        if (stream != null) {
            throw new IllegalStateException("getOutputStream() has already been called!");
        }

        stream = createOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(stream, getCharacterEncoding()));
        return (writer);
    }

    @Override
    public void setContentLength(int length) {
      // Ignore, since content length of zipped content does not match content length of unzipped content.
    }

    public Predicate<String> getStopGZIP() {
        return stopGZIP;
    }

    public void setStopGZIP(Predicate<String> stopGZIP) {
        this.stopGZIP = stopGZIP;
        if (stream != null) stream.setStopGZIP(stopGZIP);
    }
}
