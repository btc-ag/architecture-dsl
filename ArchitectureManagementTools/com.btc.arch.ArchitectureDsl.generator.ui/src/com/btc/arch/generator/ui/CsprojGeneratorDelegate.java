package com.btc.arch.generator.ui;

import java.util.Collections;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionDelegate;

import com.btc.arch.architectureDsl.Model;
import com.btc.arch.generator.ArchDslGeneratorException;
import com.btc.arch.generator.IArchitectureDSLGenerator;
import com.btc.arch.visualstudio.generators.CppProjectGenerator;
import com.btc.arch.visualstudio.generators.CsprojGenerator;
import com.btc.commons.emf.EcoreUtils;

public class CsprojGeneratorDelegate extends ArchDslGeneratorDelegate {

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// TODO Auto-generated method stub

	}

	private static final String TARGET_DIR_PARAMETER = "targetDir";

	/**
	 * @see ActionDelegate#run(IAction)
	 */
	@Override
	public void run(IAction action) {
		try {
			IArchitectureDSLGenerator generator = new CsprojGenerator();
			final Model model = this.getModel();
			generator.generate(
					Collections.singletonList(model),
					Collections.singletonList(model),
					Path.fromOSString(this.getContext().getParameter(
							TARGET_DIR_PARAMETER)), null, this.getContext(),
					EcoreUtils.areAllElementsResolved(model));
			showMessage(generator.getOutputMessage(), false);
		} catch (ArchDslGeneratorException e) {
			showMessage(e.getMessage(), true);
		}
	}
}
