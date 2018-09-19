package com.btc.arch.base;

public interface IServiceRegistry<T> {

	Iterable<T> getAllServices();

	T getServiceByID(final String id);

}