package com.msrk.tools.cache;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;


/**
 * @author sarfaraz
 * Date: 07/07/2018
 */
@SpringBootApplication(scanBasePackages = "com.msrk.tools.cache")
@PropertySources({
    @PropertySource("classpath:application.properties"),
    @PropertySource(value = "${cache.properties}", ignoreResourceNotFound = true)

})
public class CacheMain {
	public static void main(String []args){
		 SpringApplication.run(CacheMain.class,args);
	}
}
