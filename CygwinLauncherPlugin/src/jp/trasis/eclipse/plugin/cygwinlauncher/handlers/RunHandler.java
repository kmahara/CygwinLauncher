package jp.trasis.eclipse.plugin.cygwinlauncher.handlers;

import java.io.File;

import jp.trasis.eclipse.plugin.cygwinlauncher.CygwinUtils;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * Run the selected file in Cygwin.
 *
 * @author mahara
 *
 */
public class RunHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		// Get selected file or folder.
		File file = CygwinUtils.getFile(event);

		if (file == null) {
			return null;
		}

		if (file.isDirectory()) {
			// Open the folder in Cygwin.
			CygwinUtils.openFolderInCygwin(file);
		} else {
			// Run the file in Cygwin.
			CygwinUtils.runOnCygwin(file);
		}

		return null;
	}
}
