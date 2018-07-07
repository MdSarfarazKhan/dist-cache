package com.msrk.tools.cache.service;

import com.msrk.tools.cache.domain.CacheItem;
import com.msrk.tools.cache.domain.Data;

/**
 * @author sarfaraz
 * Date: 07/07/2018
 */
public interface ClusterReplicatorService {
	
	public void replicateAcrossNodes(CacheItem<String,Data> cacheItem);
	public void requestSync(String host);

}
