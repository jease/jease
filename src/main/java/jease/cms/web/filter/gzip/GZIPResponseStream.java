package jease.cms.web.filter.gzip;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.Predicate;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;

public class GZIPResponseStream extends ServletOutputStream {
    private final ByteArrayOutputStream baos;
    private boolean closed;
    private final HttpServletResponse response;
    private final ServletOutputStream output;
    private Predicate<String> stopGZIP;

    public GZIPResponseStream(HttpServletResponse response) throws IOException {
        super();
        closed = false;
        this.response = response;
        this.output = response.getOutputStream();
        baos = new ByteArrayOutputStream(response.getBufferSize());
    }

    @Override
    public void close() throws IOException {
        if (closed) {
            throw new IOException("This output stream has already been closed");
        }

        byte[] bytes = baos.toByteArray();
        final String contentType = response.getContentType();
        if (stopGZIP == null || !stopGZIP.test(contentType)) {
            ByteArrayOutputStream newBaos = new ByteArrayOutputStream();
            GZIPOutputStream gzipstream = new GZIPOutputStream(newBaos);
            gzipstream.write(bytes);
            gzipstream.finish();
            bytes = newBaos.toByteArray();
            response.addHeader("Content-Length", Integer.toString(bytes.length));
            response.addHeader("Content-Encoding", "gzip");
        }

        output.write(bytes);
        output.flush();
        output.close();
        closed = true;
    }

    @Override
    public void flush() throws IOException {
        if (closed) {
            throw new IOException("Cannot flush a closed output stream");
        }
        baos.flush();
    }

    @Override
    public void write(int b) throws IOException {
        if (closed) {
            throw new IOException("Cannot write to a closed output stream");
        }
        baos.write((byte) b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (closed) {
            throw new IOException("Cannot write to a closed output stream");
        }
        baos.write(b, off, len);
    }

    public boolean closed() {
        return this.closed;
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {
    }

    public Predicate<String> getStopGZIP() {
        return stopGZIP;
    }

    public void setStopGZIP(Predicate<String> stopGZIP) {
        this.stopGZIP = stopGZIP;
    }
}
