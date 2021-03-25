package com.mytest.sso;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class ComponentResponseWrapper extends HttpServletResponseWrapper {

    private final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    private final HttpServletResponse response;
    private PrintWriter printWriter;

    public ComponentResponseWrapper(HttpServletResponse response) {
        super(response);
        this.response = response;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return new MyServletOutputStream(this.bytes);
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        this.printWriter = new PrintWriter(new OutputStreamWriter(this.bytes, StandardCharsets.UTF_8));
        return this.printWriter;
    }

    public byte[] getBytes() {
        if (null != this.printWriter) {
            this.printWriter.close();
        } else {
            try {
                this.bytes.flush();
            } catch (IOException var2) {
                var2.printStackTrace();
            }

        }
        return this.bytes.toByteArray();
    }

    static class MyServletOutputStream extends ServletOutputStream {
        private final ByteArrayOutputStream byteArrayOutputStream;

        public MyServletOutputStream(ByteArrayOutputStream ostream) {
            this.byteArrayOutputStream = ostream;
        }

        public void write(int b) {
            this.byteArrayOutputStream.write(b);
        }
    }
}
