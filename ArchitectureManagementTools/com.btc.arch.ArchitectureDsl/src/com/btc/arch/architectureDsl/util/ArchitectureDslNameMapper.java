package com.btc.arch.architectureDsl.util;

import java.text.MessageFormat;

import com.btc.arch.architectureDsl.ComponentInstance;
import com.btc.arch.architectureDsl.Domain;
import com.btc.arch.architectureDsl.ConnectorInstance;
import com.btc.arch.architectureDsl.Model;
import com.btc.arch.architectureDsl.Module;
import com.btc.arch.architectureDsl.ModuleGroup;
import com.btc.commons.java.Pair;
import com.btc.commons.java.functional.IMapFunctor;

public class ArchitectureDslNameMapper implements IMapFunctor<Object, String> {

	@Override
	public String mapItem(Object subject) {
		return ArchitectureDslNameMapper.getName(subject);
	}

	public static String getName(Object subject) {
		if (subject instanceof Pair<?, ?>) {
			Pair<?, ?> pair = (Pair<?, ?>) subject;
			return String.format("%s,%s", getName(pair.getFirst()),
					getName(pair.getSecond()));
		} else if (subject instanceof Module) {
			return ((Module) subject).getName();
		} else if (subject instanceof ModuleGroup) {
			return ((ModuleGroup) subject).getName();
		} else if (subject instanceof Domain) {
			return ((Domain) subject).getName();
		} else if (subject instanceof ComponentInstance) {
			return ((ComponentInstance) subject).getName();
		} else if (subject instanceof ConnectorInstance) {
			return ((ConnectorInstance) subject).getName();
		} else if (subject instanceof Model) {
			return "Model";
		}
		throw new IllegalArgumentException(
				MessageFormat.format("Unknown object of class {0}: {1}",
						subject.getClass(), subject));

	}
}
