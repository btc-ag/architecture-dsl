package com.btc.commons.emf;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Set;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.query.conditions.eobjects.EObjectCondition;
import org.eclipse.emf.query.conditions.eobjects.EObjectSource;
import org.eclipse.emf.query.conditions.eobjects.EObjectTypeRelationCondition;
import org.eclipse.emf.query.conditions.eobjects.IEObjectSource;
import org.eclipse.emf.query.conditions.eobjects.TypeRelation;
import org.eclipse.emf.query.conditions.eobjects.structuralfeatures.EObjectAttributeValueCondition;
import org.eclipse.emf.query.conditions.strings.StringAdapter;
import org.eclipse.emf.query.conditions.strings.StringCondition;
import org.eclipse.emf.query.statements.FROM;
import org.eclipse.emf.query.statements.IQueryResult;
import org.eclipse.emf.query.statements.SELECT;
import org.eclipse.emf.query.statements.WHERE;

import com.btc.commons.java.StringUtils;
import com.btc.commons.java.functional.IMapFunctor;
import com.btc.commons.java.functional.IterationUtils;

/**
 * This class contains static helper method for performing simple, recurring
 * queries on Ecore models using EMFQuery.
 * 
 * @author SIGIESEC
 * 
 */
public class EMFQueryUtils {
	private final IEObjectSource objectSource;

	public EMFQueryUtils(final Collection<? extends EObject> contents) {
		this(new EObjectSource(contents));
	}

	public EMFQueryUtils(final IEObjectSource eObjectSource) {
		this.objectSource = eObjectSource;
	}

	public Iterable<?> getObjectsByType(final EClass type) {
		return getObjectsByType(this.objectSource, type);
	}

	public static Iterable<?> getObjectsByType(
			final Collection<? extends EObject> contents, final EClass type) {
		return getObjectsByType(new EObjectSource(contents), type);
	}

	public static Iterable<?> getObjectsByType(
			final IEObjectSource selectedEObjects, final EClass type) {
		final EObjectCondition condition = new EObjectTypeRelationCondition(
				type, TypeRelation.SAMETYPE_OR_SUBTYPE_LITERAL);
		final IQueryResult result = new SELECT(new FROM(selectedEObjects),
				new WHERE(condition)).execute();
		return result;
	}

	/**
	 * @param contents
	 * @param name
	 * @param type
	 *            the Ecore type of the objects to return
	 * @param nameAttribute
	 *            the string attribute of type acting as the unique identifier
	 *            (key)
	 * @return the unique element with key <code>name</code>, or
	 *         <code>null</code>
	 * 
	 * @precondition 
	 *               nameAttribute.getEAttributeType()==EcorePackage.Literals.ESTRING
	 */
	public static EObject getObjectByTypeAndUniqueStringAttribute(
			final Collection<? extends EObject> contents, final String name,
			final EClass type, final EAttribute nameAttribute) {
		final IEObjectSource selectedEObjects = new EObjectSource(contents);

		final EObjectCondition condition = new EObjectAttributeValueCondition(
				nameAttribute, new StringCondition(StringAdapter.DEFAULT) {
					@Override
					public boolean isSatisfied(String str) {
						return str.equals(name);
					}
				}).AND(new EObjectTypeRelationCondition(type,
				TypeRelation.SAMETYPE_OR_SUBTYPE_LITERAL));
		final IQueryResult result = new SELECT(new FROM(selectedEObjects),
				new WHERE(condition)).execute();
		switch (result.size()) {
		case 0:
			return null;
		case 1:
			return result.iterator().next();
		default:
			throw new AssertionError(
					MessageFormat
							.format("{0} is not a key for {1}, since the model contains multiple elements with value {2}:\n   {3}",
									nameAttribute, type, name,
									StringUtils.join(IterationUtils.map(result,
											new IMapFunctor<EObject, String>() {

												@Override
												public String mapItem(
														EObject obj) {
													return MessageFormat
															.format("{0} in {1}",
																	obj.toString(),
																	obj.eResource()
																			.toString());
												}
											}), "\n   ")));
		}
	}

	/**
	 * @param contents
	 * @param name
	 * @param type
	 *            the Ecore type of the objects to return
	 * @param nameAttribute
	 * @return the elements of the given <code>type</code> which match the
	 *         <code>regex</code>, or <code>null</code>
	 * 
	 * @precondition 
	 *               nameAttribute.getEAttributeType()==EcorePackage.Literals.ESTRING
	 */
	public static Iterable<? extends EObject> getObjectsByTypeAndRegExStringAttribute(
			final Collection<? extends EObject> contents, final String regex,
			final EClass type, final EAttribute nameAttribute) {
		final IEObjectSource selectedEObjects = new EObjectSource(contents);

		final EObjectCondition condition = new EObjectAttributeValueCondition(
				nameAttribute, new StringCondition(StringAdapter.DEFAULT) {
					@Override
					public boolean isSatisfied(String str) {
						return str.matches(regex);
					}
				}).AND(new EObjectTypeRelationCondition(type,
				TypeRelation.SAMETYPE_OR_SUBTYPE_LITERAL));
		final IQueryResult result = new SELECT(new FROM(selectedEObjects),
				new WHERE(condition)).execute();
		return result.getEObjects();
		/*
		 * switch (result.size()) { case 0: return null; case 1: return
		 * result.iterator().next(); default: throw new AssertionError(
		 * MessageFormat .format(
		 * "{0} is not a key for {1}, since the model contains multiple elements with value {2}:\n   {3}"
		 * , nameAttribute, type, name,
		 * StringUtils.join(IterationUtils.map(result, new IMapFunctor<EObject,
		 * String>() {
		 * 
		 * @Override public String mapItem( EObject obj) { return MessageFormat
		 * .format("{0} in {1}", obj.toString(), obj.eResource() .toString()); }
		 * }), "\n   "))); }
		 */
	}
}
