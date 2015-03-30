package jp.trasis.eclipse.plugin.cygwinlauncher;

import java.util.Properties;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		Properties properties = System.getProperties();
		System.out.println(properties);

		String consoleOutputEncoding = System.getProperty("sun.jnu.encoding");

		if (consoleOutputEncoding == null) {
			consoleOutputEncoding = "UTF-8";
		}

		store.setDefault(PreferenceConstants.P_SYGWIN_HOME, "C:/cygwin64");
		store.setDefault(PreferenceConstants.P_PROCESS_OUTPUT_ENCODING, "UTF-8");
		store.setDefault(PreferenceConstants.P_CONSOLE_OUTPUT_ENCODING, consoleOutputEncoding);
		store.setDefault(PreferenceConstants.P_PROJECT_COMMAND, "TODEV");
	}

}
