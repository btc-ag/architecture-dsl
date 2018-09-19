package com.btc.commons.emf;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.btc.commons.java.functional.IterationUtils;
import com.btc.commons.java.functional.IMapFunctor;

public class EcoreUtils {

	/**
	 * TODO: Is this domain specific or can it also be used for other model
	 * elements?
	 * 
	 * @param domains
	 * @param structuralFeature
	 * @return
	 */
	public static String[] getStringFeatureArray(
			final Collection<? extends EObject> domains,
			final EAttribute structuralFeature) {
		final String[] domainNames = new String[domains.size()];
		int i = 0;
		for (EObject domain : domains) {
			domainNames[i] = (String) domain.eGet(structuralFeature);
			++i;
		}
		return domainNames;
	}

	public static TreeIterator<Notifier> getResourceSetContents(
			final EObject object) {
		if (object.eIsProxy()) {
			// TODO this should never happen
			throw new IllegalArgumentException("Object " + object
					+ " is an (unresolved?) proxy object.");
		} else {
			return object.eResource().getResourceSet().getAllContents();
		}
	}

	public static boolean areAllElementsResolved(EObject object) {
		return EcoreUtil.UnresolvedProxyCrossReferencer.find(object).isEmpty();
	}

	public static boolean areAllElementsResolved(
			Collection<? extends EObject> contents) {
		return EcoreUtil.UnresolvedProxyCrossReferencer.find(contents)
				.isEmpty();
	}

	public static void validate(EObject obj) {
		Map<EObject, Collection<Setting>> unresolvedProxies = EcoreUtil.UnresolvedProxyCrossReferencer
				.find(obj);
		if (!unresolvedProxies.isEmpty()) {
			// FIXME

			StringBuilder builder = new StringBuilder();
			for (EObject baseObject : unresolvedProxies.keySet()) {
				builder.append(baseObject.toString());
				builder.append(" has unresolved feature references:");
				for (Setting s : unresolvedProxies.get(baseObject)) {
					builder.append(s.getEStructuralFeature().toString());
					builder.append(", ");
				}
				builder.append("\n");
			}

			throw new IllegalArgumentException(
					"Model contains unresolved elements: \n"
							+ builder.toString());
		}
	}

	public static HashSet<Resource> calcUniqueResources(
			Collection<? extends EObject> contents) {
		return IterationUtils.materialize(IterationUtils.map(contents,
				new IMapFunctor<EObject, Resource>() {

					@Override
					public Resource mapItem(EObject obj) {
						return obj.eResource();
					}
				}), new HashSet<Resource>());
	}

}
