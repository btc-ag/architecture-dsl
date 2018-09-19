package com.btc.arch.zest;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.ecore.resource.Resource;

public class ResourceZestGraphInfo extends ZestGraphInfo {
	private final Resource resource;
	private final long lastModifiedEpoch;
	private final ZestGraphProvider provider;

	public ResourceZestGraphInfo(Resource resource, ZestGraphProvider provider)
			throws IOException {
		this(resource, provider, ((IFile) ResourcesPlugin
				.getWorkspace()
				.getRoot()
				.findMember(
						resource.getURI().toString()
								.substring("platform:/resource/".length())))
				.getModificationStamp());
	}

	public ResourceZestGraphInfo(Resource resource, ZestGraphProvider provider,
			long lastModifiedEpoch) {
		this.resource = resource;
		this.lastModifiedEpoch = lastModifiedEpoch;
		this.provider = provider;
	}

	@Override
	public boolean equalSpecification(ZestGraphInfo other) {
		return other instanceof ResourceZestGraphInfo
				&& this.getResource()
						.getURI()
						.equals(((ResourceZestGraphInfo) other).getResource()
								.getURI())
				&& this.provider.getClass().equals(
						((ResourceZestGraphInfo) other).getProvider()
								.getClass());
	}

	@Override
	public int compareVersion(ZestGraphInfo other) {
		if (equalSpecification(other)) {
			return new Long(this.lastModifiedEpoch).compareTo(new Long(
					((ResourceZestGraphInfo) other).getLastModifiedEpoch()));
		} else
			throw new IllegalArgumentException(
					"Cannot compare versions of different files");
	}

	public Resource getResource() {
		return resource;
	}

	public long getLastModifiedEpoch() {
		return lastModifiedEpoch;
	}

	public ZestGraphProvider getProvider() {
		return provider;
	}

}
