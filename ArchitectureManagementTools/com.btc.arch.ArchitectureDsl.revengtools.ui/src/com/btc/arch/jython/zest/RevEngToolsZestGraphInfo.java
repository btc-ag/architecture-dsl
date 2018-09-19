package com.btc.arch.jython.zest;

import com.btc.arch.zest.ZestGraphInfo;

public class RevEngToolsZestGraphInfo extends ZestGraphInfo {

	public static final String FULL_LINK_DEPS_GRAPH = "Full link dependencies graph";
	private String description;

	public RevEngToolsZestGraphInfo(String description) {
		this.description = description;
	}

	@Override
	public boolean equalSpecification(ZestGraphInfo other) {
		return other instanceof RevEngToolsZestGraphInfo
				&& this.description
						.equals(((RevEngToolsZestGraphInfo) other).description);
	}

	@Override
	public int compareVersion(ZestGraphInfo other) {
		if (this.equalSpecification(other)) {
			return 0;
		} else {
			return -1;
		}
	}

}
