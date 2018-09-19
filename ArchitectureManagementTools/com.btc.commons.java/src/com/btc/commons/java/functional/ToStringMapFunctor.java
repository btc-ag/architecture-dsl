package com.btc.commons.java.functional;

public class ToStringMapFunctor implements IMapFunctor<Object, String> {

	@Override
	public String mapItem(Object obj) {
		return obj.toString();
	}

}
