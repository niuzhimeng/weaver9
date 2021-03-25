package com.mytest.sso;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class MyHttpServletResponseWrapper1 extends HttpServletResponseWrapper {

    private static class ResponsePrintWriter extends PrintWriter {
        ByteArrayOutputStream output;

        public ResponsePrintWriter(ByteArrayOutputStream output) {
            super(output);
            this.output = output;
        }

        public ByteArrayOutputStream getByteArrayOutputStream() {
            return output;
        }
    }

    private ResponsePrintWriter writer;
    private ByteArrayOutputStream output;

    public MyHttpServletResponseWrapper1(HttpServletResponse httpServletResponse) {
        super(httpServletResponse);
        output = new ByteArrayOutputStream();
        writer = new ResponsePrintWriter(output);
    }

    public void finalize() throws Throwable {
        super.finalize();
        output.close();
        writer.close();
    }

    public String getContent() {
        try {
            writer.flush();
            return writer.getByteArrayOutputStream().toString("GBK");
        } catch (UnsupportedEncodingException e) {
            return "UnsupportedEncoding";
        }
    }

    public void close() throws IOException {
        writer.close();
    }

    public PrintWriter getWriter() throws IOException {
        return writer;
    }
}
