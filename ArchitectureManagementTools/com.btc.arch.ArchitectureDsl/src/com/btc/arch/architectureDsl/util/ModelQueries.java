package com.btc.arch.architectureDsl.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import com.btc.arch.architectureDsl.ArchitectureDslPackage;
import com.btc.arch.architectureDsl.Domain;
import com.btc.arch.architectureDsl.Model;
import com.btc.arch.architectureDsl.Module;
import com.btc.arch.architectureDsl.ModuleGroup;
import com.btc.arch.architectureDsl.ReleaseUnit;
import com.btc.commons.emf.EMFQueryUtils;
import com.btc.commons.java.Pair;
import com.btc.commons.java.functional.IMapFunctor;
import com.btc.commons.java.functional.IterationUtils;

/**
 * TODO the methods do not operate on a Model object, but on an arbitrary
 * collection of EObjects...
 * 
 * @author SIGIESEC
 * 
 */
public class ModelQueries {
	public static List<? extends EObject> modelAsList(final Model model) {
		return Collections.singletonList(model);
	}

	public static Module getModuleByName(
			final Collection<? extends EObject> contents, final String name) {
		final EClass type = ArchitectureDslPackage.Literals.MODULE;
		// ArchitectureDslPackage.eINSTANCE.getModule();
		final EAttribute nameAttribute = ArchitectureDslPackage.eINSTANCE
				.getBuildingBlock_Name();
		return (Module) EMFQueryUtils.getObjectByTypeAndUniqueStringAttribute(
				contents, name, type, nameAttribute);
	}

	public static Iterable<Module> getModulesByRegEx(
			final Collection<? extends EObject> contents, final String regex) {
		final EClass type = ArchitectureDslPackage.Literals.MODULE;
		// ArchitectureDslPackage.eINSTANCE.getModule();
		final EAttribute nameAttribute = ArchitectureDslPackage.eINSTANCE
				.getBuildingBlock_Name();
		Iterable<Module> modules = null;
		modules = (Iterable<Module>) EMFQueryUtils
				.getObjectsByTypeAndRegExStringAttribute(contents, regex, type,
						nameAttribute);
		return modules;
	}

	public static ModuleGroup getModuleGroupByName(
			final Collection<? extends EObject> contents, final String name) {
		final EClass type = ArchitectureDslPackage.Literals.MODULE_GROUP;
		final EAttribute nameAttribute = ArchitectureDslPackage.eINSTANCE
				.getModuleGroup_Name();
		return (ModuleGroup) EMFQueryUtils
				.getObjectByTypeAndUniqueStringAttribute(contents, name, type,
						nameAttribute);
	}

	public static Domain getDomainByName(
			final Collection<? extends EObject> contents, final String name) {
		final EClass type = ArchitectureDslPackage.Literals.DOMAIN;
		final EAttribute nameAttribute = ArchitectureDslPackage.eINSTANCE
				.getDomain_Name();
		return (Domain) EMFQueryUtils.getObjectByTypeAndUniqueStringAttribute(
				contents, name, type, nameAttribute);
	}

	@SuppressWarnings("unchecked")
	public static Iterable<Module> getAllModules(
			final Collection<? extends EObject> contents) {
		return (Iterable<Module>) EMFQueryUtils.getObjectsByType(contents,
				ArchitectureDslPackage.Literals.MODULE);
	}

	@SuppressWarnings("unchecked")
	public static Iterable<EObject> getAllModelContents(
			final Collection<? extends EObject> contents) {
		Iterable<Model> models = (Iterable<Model>) EMFQueryUtils
				.getObjectsByType(contents,
						ArchitectureDslPackage.Literals.MODEL);
		Collection<EObject> eObjects = new ArrayList<EObject>();
		for (Model model : models) {
			eObjects.addAll(model.eContents());
		}
		return eObjects;
	}

	public static Iterable<Pair<Module, Module>> getAllDependenciesAsPairs(
			final Collection<? extends EObject> contents) {
		return IterationUtils.mapToIterablesAndChain(getAllModules(contents),
				new IMapFunctor<Module, Iterable<Pair<Module, Module>>>() {
					@Override
					public Iterable<Pair<Module, Module>> mapItem(
							final Module module) {
						return ModuleQueries.getAllDependenciesAsPairs(module);
					}

				});
	}

	@SuppressWarnings("unchecked")
	public static Iterable<ModuleGroup> getAllModuleGroups(
			final Collection<? extends EObject> contents) {
		return (Iterable<ModuleGroup>) EMFQueryUtils.getObjectsByType(contents,
				ArchitectureDslPackage.Literals.MODULE_GROUP);
	}
	
	@SuppressWarnings("unchecked")
	public static Iterable<ReleaseUnit> getAllReleaseUnits(
			final Collection<? extends EObject> contents) {
		return (Iterable<ReleaseUnit>) EMFQueryUtils.getObjectsByType(contents,
				ArchitectureDslPackage.Literals.RELEASE_UNIT);
	}

	public static Model findUniqueModel(final Iterable<? extends EObject> base) {
		Model foundModel = null;
		for (final EObject obj : base) {
			if (obj instanceof Model) {
				if (foundModel == null) {
					foundModel = (Model) obj;
				} else {
					if (foundModel != obj) {
						throw new IllegalArgumentException(
								MessageFormat.format(
										"Multiple models in input data {0}",
										base));
					}
				}
			}
		}
		return foundModel;
	}

	@SuppressWarnings("unchecked")
	public static List<Model> findModels(final Iterable<? extends EObject> base) {
		return IterationUtils.materialize(IterationUtils.filterByClass(
				(Iterable<EObject>) base, Model.class), new ArrayList<Model>());
	}

	// public static List<BuildingBlock> findBuildingBlocks(
	// final Iterable<? extends EObject> base) {
	// return IterationUtils.materialize(IterationUtils.filterByClass(
	// (Iterable<EObject>) base, BuildingBlock.class),
	// new ArrayList<BuildingBlock>());
	// }
}
