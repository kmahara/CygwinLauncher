package jp.trasis.eclipse.plugin.cygwinlauncher;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.internal.ui.views.console.ProcessConsole;
import org.eclipse.debug.ui.console.ConsoleColorProvider;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.FileEditorInput;

public final class CygwinUtils {

	private static final Color BLUE;

	static {
		Display display = Display.getCurrent();

		BLUE = new Color(display, 0, 0, 255);
	}

	private CygwinUtils() {
	}

	/**
	 * Open the selected folder in Cygwin.<br />
	 *
	 * If the file has been selected, open the parent folder.
	 *
	 * @param file
	 */
	public static void openFolderInCygwin(File file) {
		if (!file.isDirectory()) {
			file = file.getParentFile();
		}

		try {
			List<String> params = new ArrayList<>();

			IPreferenceStore store = Activator.getDefault().getPreferenceStore();

			String home = store.getString(PreferenceConstants.P_SYGWIN_HOME);

			params.add(normalizePath(home) + "/bin/mintty.exe");
			params.add("-");

			ProcessBuilder processBuilder = new ProcessBuilder(params);

			processBuilder.environment().put("CHERE_INVOKING", "yes");
			processBuilder.directory(file);

			processBuilder.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Run the file in Cygwin.<br />
	 *
	 * @param file
	 */
	@SuppressWarnings("restriction")
	public static void runOnCygwin(File file) {
		// Create process.
		Process process;

		try {
			process = createProcess(file);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		String processOutputEncodingString = store.getString(PreferenceConstants.P_PROCESS_OUTPUT_ENCODING);
		String consoleOutputEncodingString = store.getString(PreferenceConstants.P_CONSOLE_OUTPUT_ENCODING);

		Charset processOutputEncoding = Charset.forName(processOutputEncodingString);
		Charset consoleOutputEncoding = Charset.forName(consoleOutputEncodingString);

		process = new ProcessProxy(process, processOutputEncoding, consoleOutputEncoding);

		IProcess iProcess = DebugPlugin.newProcess(new Launch(null, ILaunchManager.RUN_MODE, null), process, "Cygwin console");

		// Remove finished console.

		IConsoleManager consoleManager = ConsolePlugin.getDefault().getConsoleManager();

		IConsole[] consoles = consoleManager.getConsoles();

		for (IConsole console : consoles) {
			if (console instanceof ProcessConsole) {
				ProcessConsole console2 = (ProcessConsole) console;

				if (console2.getProcess().isTerminated()) {
					consoleManager.removeConsoles(new IConsole[] { console });
				}
			}
		}

		// Create console.
		ProcessConsole console = new ProcessConsole(iProcess, new ConsoleColorProvider(), consoleOutputEncodingString);

		// Add startup message.
		try {
			IOConsoleOutputStream os = console.newOutputStream();

			os.setColor(BLUE);

			os.write("Run " + file.getAbsolutePath() + "\n");
			os.flush();
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		consoleManager.addConsoles(new IConsole[] { console });

		// Show console.
		consoleManager.showConsoleView(console);
	}

	private static Process createProcess(File file) throws IOException {
		File dir = file.getParentFile();

		Runtime rt = Runtime.getRuntime();

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		String home = store.getString(PreferenceConstants.P_SYGWIN_HOME);

		StringBuilder sb = new StringBuilder(normalizePath(home) + "/bin/bash.exe -l");

		String dirPath = normalizePath(dir.getAbsolutePath());
		String filePath = normalizePath(file.getAbsolutePath());

		sb.append(" -c \"cd " + dirPath + " && ").append(filePath).append("\"");

		String command = sb.toString();

		ILog log = Activator.getDefault().getLog();
		log.log(new Status(IStatus.INFO, Activator.PLUGIN_ID, "run command: " + command));

		Process process = rt.exec(command, null, dir);
		return process;
	}

	private static String normalizePath(String path) {
		return path.replace('\\', '/');
	}

	/**
	 * Get current selected file or folder.
	 *
	 * @return
	 * @throws ExecutionException
	 */
	public static File getFile(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelectionChecked(event);

		if (selection == null || selection.isEmpty()) {
			return null;
		}

		if (selection instanceof IStructuredSelection) {
			IStructuredSelection selection2 = (IStructuredSelection) selection;

			Object object =  selection2.getFirstElement();

			if (object instanceof IResource) {
				return ((IResource) object).getLocation().toFile();
			}

			try {
				if (object instanceof IJavaElement) {
					return ((IJavaElement) object).getResource().getLocation().toFile();
				}
			} catch (NoClassDefFoundError e) {
				// ignore
			}

			return null;
		}

		if (selection instanceof TextSelection) {
			IEditorPart editorPart = HandlerUtil.getActiveEditor(event);

			FileEditorInput input = (FileEditorInput) editorPart.getEditorInput();

			return input.getFile().getLocation().toFile();
		}

		return null;
	}
}
