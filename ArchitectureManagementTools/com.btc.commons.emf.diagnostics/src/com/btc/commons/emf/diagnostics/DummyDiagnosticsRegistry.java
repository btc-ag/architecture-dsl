package com.btc.commons.emf.diagnostics;

import java.util.HashMap;
import java.util.Map;

import com.btc.commons.emf.diagnostics.IDiagnosticResultBase.Severity;
import com.btc.commons.emf.diagnostics.internal.DiagnosticResultFactory;

/**
 * A dummy diagnostics registry which does not actually register diagnostics,
 * but returns a dummy descriptor for any diagnostics identifier.
 * 
 * @author SIGIESEC
 * 
 */
public class DummyDiagnosticsRegistry implements IDiagnosticsRegistry {
	private int count = 1;
	private final Map<String, IDiagnosticDescriptor> descriptors = new HashMap<String, IDiagnosticDescriptor>();

	@Override
	public IDiagnosticDescriptor getDiagnosticDescriptor(String identifier) {
		if (descriptors.containsKey(identifier)) {
			return descriptors.get(identifier);
		} else {
			IDiagnosticDescriptor descriptor = createDiagnosticDescriptor(identifier);
			descriptors.put(identifier, descriptor);
			return descriptor;
		}
	}

	private IDiagnosticDescriptor createDiagnosticDescriptor(String identifier) {
		return new DiagnosticDescriptor(identifier, "R" + count++,
				Severity.ERROR, "Dummy description", "Dummy", "Dummy");
	}

	@Override
	public IDiagnosticResultFactory createDiagnosticResultFactory(
			IDiagnosticSubjectDescriber describer) {
		return new DiagnosticResultFactory(this, describer);

	}

}
