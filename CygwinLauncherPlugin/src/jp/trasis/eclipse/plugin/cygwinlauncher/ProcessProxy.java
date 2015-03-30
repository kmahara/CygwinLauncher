package jp.trasis.eclipse.plugin.cygwinlauncher;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;


public class ProcessProxy extends Process {

	private final Process process;
	private final Charset processOutputCharset;
	private final Charset consoleOutputCharset;

	public ProcessProxy(Process process, Charset processOutputCharset, Charset consoleOutputCharset) {
		this.process = process;
		this.processOutputCharset = processOutputCharset;
		this.consoleOutputCharset = consoleOutputCharset;
	}

	@Override
	public OutputStream getOutputStream() {
		return process.getOutputStream();
	}

	@Override
	public InputStream getInputStream() {
		return new InputStreamProxy(process.getInputStream(), processOutputCharset, consoleOutputCharset);
	}

	@Override
	public InputStream getErrorStream() {
		return new InputStreamProxy(process.getErrorStream(), processOutputCharset, consoleOutputCharset);
	}

	@Override
	public int waitFor() throws InterruptedException {
		return process.waitFor();
	}

	@Override
	public int exitValue() {
		return process.exitValue();
	}

	@Override
	public void destroy() {
		process.destroy();
	}
}
