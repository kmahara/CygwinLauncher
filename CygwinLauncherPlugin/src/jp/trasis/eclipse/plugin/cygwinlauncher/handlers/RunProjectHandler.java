package jp.trasis.eclipse.plugin.cygwinlauncher.handlers;

import java.io.File;

import jp.trasis.eclipse.plugin.cygwinlauncher.Activator;
import jp.trasis.eclipse.plugin.cygwinlauncher.CygwinUtils;
import jp.trasis.eclipse.plugin.cygwinlauncher.PreferenceConstants;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Run the project command in Cygwin.
 *
 * @author mahara
 *
 */
public class RunProjectHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		// Get selected file or folder.
		File file = CygwinUtils.getFile(event);

		if (file == null) {
			return null;
		}

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		String command = store.getString(PreferenceConstants.P_PROJECT_COMMAND);

		if (command == null || command.isEmpty()) {
			return null;
		}

		File commandFile = findCommand(file, command);

		if (commandFile != null) {
			// Run the file in Cygwin.
			CygwinUtils.runOnCygwin(commandFile);
		}

		return null;
	}

	private File findCommand(File file, String filename) {
		if (!file.isDirectory()) {
			if (file.getName().equals(filename)) {
				return file;
			}

			file = file.getParentFile();
		}

		while (file != null) {
			File file2 = new File(file, filename);

			if (file2.exists()) {
				return file2;
			}

			file = file.getParentFile();
		}

		return null;
	}
}
