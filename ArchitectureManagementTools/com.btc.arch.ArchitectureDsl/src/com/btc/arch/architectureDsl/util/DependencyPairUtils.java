package com.btc.arch.architectureDsl.util;

import java.util.Collection;

import com.btc.arch.architectureDsl.Module;
import com.btc.commons.java.Pair;
import com.btc.commons.java.functional.IterationUtils;
import com.btc.commons.java.functional.IMapFunctor;

public class DependencyPairUtils {

	public static Iterable<Pair<String, String>> mapToNames(
			Collection<Pair<Module, Module>> dependencyPairs) {
		return IterationUtils.map(dependencyPairs,
				new IMapFunctor<Pair<Module, Module>, Pair<String, String>>() {

					@Override
					public Pair<String, String> mapItem(
							Pair<Module, Module> modulePair) {
						return new Pair<String, String>(modulePair.getFirst()
								.getName(), modulePair.getSecond().getName());
					}

				});
	}

}
