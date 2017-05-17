package me.steffenjacobs.opcuadisplay.wizard.newProject;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

import me.steffenjacobs.opcuadisplay.shared.util.EventBus;
import me.steffenjacobs.opcuadisplay.wizard.newProject.events.NewProjectWizardCancelEvent;
import me.steffenjacobs.opcuadisplay.wizard.newProject.events.NewProjectWizardFinishEvent;
import me.steffenjacobs.opcuadisplay.wizard.newProject.events.NewProjectWizardOpenEvent;

public class NewProjectWizard extends Wizard implements IWorkbenchWizard {

	private NewProjectSelectAutogenerationPage page;

	public NewProjectWizard() {
		super();
		EventBus.getInstance().fireEvent(new NewProjectWizardOpenEvent());
	}

	@Override
	public String getWindowTitle() {
		return "New OPC UA Model";
	}

	@Override
	public void addPages() {
		page = new NewProjectSelectAutogenerationPage();
		super.addPage(page);
	}

	@Override
	public boolean performFinish() {
		EventBus.getInstance()
				.fireEvent(new NewProjectWizardFinishEvent(page.isGenerateFolders(), page.isGenerateBaseTypes()));
		return true;
	}

	@Override
	public boolean performCancel() {
		EventBus.getInstance().fireEvent(new NewProjectWizardCancelEvent());
		return super.performCancel();
	}

	@Override
	public void init(IWorkbench arg0, IStructuredSelection arg1) {
	}
}
