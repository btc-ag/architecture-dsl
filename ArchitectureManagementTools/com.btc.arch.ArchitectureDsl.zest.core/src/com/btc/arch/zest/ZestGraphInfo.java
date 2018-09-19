package com.btc.arch.zest;

public abstract class ZestGraphInfo {
	public abstract boolean equalSpecification(ZestGraphInfo other);

	public abstract int compareVersion(ZestGraphInfo other);

	public boolean equivalent(ZestGraphInfo other) {
		return equalSpecification(other) && compareVersion(other) == 0;
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof ZestGraphInfo
				&& equivalent((ZestGraphInfo) other);
	}

	@Override
	public int hashCode() {
		// FIXME this is bad, should take into account the information
		// used for equalSpecification and compareVersion, but this probably
		// requires a redesign
		return super.hashCode();
	}
}
