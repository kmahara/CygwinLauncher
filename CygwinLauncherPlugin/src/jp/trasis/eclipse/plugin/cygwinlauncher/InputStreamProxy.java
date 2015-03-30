package jp.trasis.eclipse.plugin.cygwinlauncher;

import java.io.BufferedReader;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class InputStreamProxy extends FilterInputStream {
	private final Charset outputCharset;

	private final BufferedReader in;

	private byte[] restBytes;
	private int restStart;

	public InputStreamProxy(InputStream is, Charset inputCharset, Charset outputCharset) {
		super(is);

		this.outputCharset = outputCharset;

		in = new BufferedReader(new InputStreamReader(is, inputCharset));
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		byte[] currentBytes;
		int start;
		int size;

		if (restBytes == null) {
			String s = in.readLine();

			if (s == null) {
				return -1;
			}

			s += "\n";

			currentBytes = s.getBytes(outputCharset);
			start = 0;
			size = currentBytes.length;
		} else {
			currentBytes = restBytes;
			start = restStart;
			size = restBytes.length - start;
		}


		if (size < len) {
			System.arraycopy(currentBytes, start, b, off, size);

			restBytes = null;

			return size;
		}

		System.arraycopy(currentBytes, start, b, off, len);

		restBytes = currentBytes;
		restStart = start + len;

		return len;
	}

	private byte[] shortBuf;

	@Override
	public int read() throws IOException {
		if (shortBuf == null) {
			shortBuf = new byte[1];
		}

		return read(shortBuf, 0, 1);
	}

	@Override
	public long skip(long n) throws IOException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public int available() throws IOException {
		return super.available();
	}

	@Override
	public void close() throws IOException {
		in.close();
	}

	@Override
	public synchronized void mark(int readlimit) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public synchronized void reset() throws IOException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public boolean markSupported() {
		return false;
	}
}
