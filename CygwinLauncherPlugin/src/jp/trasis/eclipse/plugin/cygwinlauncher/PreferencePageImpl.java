package jp.trasis.eclipse.plugin.cygwinlauncher;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePageImpl
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public PreferencePageImpl() {
		super(GRID);

		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("CyginLauncher " + Activator.getDefault().getBundle().getVersion() + " settings.");
	}

	public void createFieldEditors() {
		Composite parent = getFieldEditorParent();

		addField(new DirectoryFieldEditor(PreferenceConstants.P_SYGWIN_HOME, "Cygwin directory", parent));
		addField(new StringFieldEditor(PreferenceConstants.P_PROCESS_OUTPUT_ENCODING, "Process output encoding", parent));
		addField(new StringFieldEditor(PreferenceConstants.P_CONSOLE_OUTPUT_ENCODING, "Console output encoding", parent));
		addField(new StringFieldEditor(PreferenceConstants.P_PROJECT_COMMAND, "Project command", parent));
	}

	public void init(IWorkbench workbench) {
	}
}