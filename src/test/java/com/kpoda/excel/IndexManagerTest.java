package com.kpoda.excel;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


/**
* @author 陈嘉镇
* @version 创建时间：2015-3-14 下午10:23:07
* @email benjaminchen555@gmail.com
*/
public class IndexManagerTest {
	
	private static  IndexManager indexManager = IndexManager.getManager();
	@BeforeClass
	public static void init() {
		indexManager.createIndex(null);
	}
	
	@Test
	public void testSearch() throws Exception {
		
		Result result = indexManager.search("博雅");
		
		System.out.println("result"+result);
		String cell1 = result.getRow().get(0);
		Assert.assertTrue(cell1.indexOf("博雅")>0);
		
	}

}
