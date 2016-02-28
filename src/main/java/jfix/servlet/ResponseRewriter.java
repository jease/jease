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
package jfix.servlet;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.function.Function;

public class ResponseRewriter extends HttpServletResponseWrapper {

	private PrintWriter printWriter;

	public ResponseRewriter(ServletResponse response, Function<String, String> rewriter) throws IOException {
		super((HttpServletResponse) response);
		this.printWriter = new Rewriter(response.getOutputStream(), rewriter);
	}

	public PrintWriter getWriter() throws IOException {
		return printWriter;
	}

	private static class Rewriter extends PrintWriter {
		private static final int BUFFER_SIZE = 8 * 1024;
		private StringBuilder buffer = new StringBuilder(BUFFER_SIZE);
		private Function<String, String> rewriter;

		public Rewriter(OutputStream stream, Function<String, String> rewriter) throws IOException {
			super(new OutputStreamWriter(stream, "UTF-8"), true);
			this.rewriter = rewriter;
		}

		public void write(int c) {
			buffer.append((char) c);
		}

		public void write(char[] chars, int offset, int length) {
			buffer.append(chars, offset, length);
			flush();
		}

		public void write(String string, int offset, int length) {
			buffer.append(string, offset, length);
			flush();
		}

		public void flush() {
			try {
				synchronized (lock) {
					if (out != null) {
						out.write(rewriter.apply(buffer.toString()));
						buffer = new StringBuilder(BUFFER_SIZE);
					}
					super.flush();
				}
			} catch (IOException e) {
				setError();
			}
		}
	}

}