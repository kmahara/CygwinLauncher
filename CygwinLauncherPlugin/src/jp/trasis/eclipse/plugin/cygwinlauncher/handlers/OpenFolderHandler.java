package jp.trasis.eclipse.plugin.cygwinlauncher.handlers;

import java.io.File;

import jp.trasis.eclipse.plugin.cygwinlauncher.CygwinUtils;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * Open the selected folder in Cygwin.
 *
 * @author mahara
 *
 */
public class OpenFolderHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		// Get selected file or folder.
		File file = CygwinUtils.getFile(event);

		if (file == null) {
			return null;
		}

		// Open the folder in Cygwin.
		CygwinUtils.openFolderInCygwin(file);

		return null;
	}
}
