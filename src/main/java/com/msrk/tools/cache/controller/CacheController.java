package com.msrk.tools.cache.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.msrk.tools.cache.domain.CacheItem;
import com.msrk.tools.cache.domain.Data;
import com.msrk.tools.cache.service.CacheService;
import com.msrk.tools.cache.service.ClusterReplicatorService;
import com.msrk.tools.cache.util.Constant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author sarfaraz
 * Date: 07/07/2018
 * 
 */
@RestController
public class CacheController {
	
	private final Logger LOG = LoggerFactory.getLogger(this.getClass());
	 
	
	@Autowired
	private CacheService<String,Data> cacheService;
	
	@Autowired
	private ClusterReplicatorService clusterReplicatorService;
	
	@PostMapping(path="/set/{key}",consumes=MediaType.APPLICATION_JSON_VALUE)
	public void set(@PathVariable("key")String key,@RequestBody String value){
		Data d = new Data(value, new Date().getTime());
		cacheService.add(key, d);
		clusterReplicatorService.replicateAcrossNodes(new CacheItem<>(key,d));
	}
	
	@GetMapping(path="/get/{key}",produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> get(@PathVariable("key")String key){
		Data value =  cacheService.get(key);
		if(value!=null)
			return new ResponseEntity<String>(value.getValue(),HttpStatus.OK);
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
	
	@PostMapping(path=Constant.CLUSTER_ITEM_REPLICATION_URL,consumes=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> replicateItem(@RequestBody CacheItem<String,Data> cacheItem){
		cacheService.add(cacheItem);
		return new ResponseEntity<String>("/get/"+cacheItem.getKey(),HttpStatus.OK);
	}
	
	@PostMapping(path=Constant.CLUSTER_DATA_REPLICATION_URL,consumes=MediaType.APPLICATION_JSON_VALUE)
	public void replicateItemList(@RequestBody List<CacheItem<String,Data>> cacheItem){
		cacheService.add(cacheItem);
	}
	
	@GetMapping(path=Constant.CLUSTER_DATA_REPLICATION_REQUEST_URL)
	public ResponseEntity<Integer> replicateItemList(@RequestParam("host") String host){
		clusterReplicatorService.requestSync(host);
		return new ResponseEntity<Integer>(cacheService.getCacheDataSize(),HttpStatus.OK);
	}
}
