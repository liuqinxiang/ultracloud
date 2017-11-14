package com.cmp.service;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.cmp.ehcache.AbstractDao;
import com.cmp.sid.CmpDict;

@Service
public class CmpDictService extends AbstractDao<CmpDict, Long> {
	private static Logger logger = Logger.getLogger(CmpDictService.class);

	//数据字典列表查询
	public List<CmpDict> getCmpDictList(String dictType) {
		try {
			List<CmpDict> cmpDictList=getCacheQuery().eq("dictType", dictType).getList();
			Collections.sort(cmpDictList);//排序
			return cmpDictList;
		} catch (Exception e) {
			logger.error("数据字典列表查询时错误："+e);
			return null;
		}
	}
	
	//获取数据字典
	public CmpDict getCmpDict(String dictType, String dictCode) {
		try {
			return getCacheQuery().eq("dictType", dictType).eq("dictCode", dictCode).getUniqueResult();
		} catch (Exception e) {
			logger.error("获取数据字典时错误："+e);
			return null;
		}
	}

	//重新加载缓存
	public void reloadCache() {
		try {
			super.reloadCache();//重新加载缓存
		} catch (Exception e) {
			logger.error("重新加载缓存时错误："+e);
		}
	}
	
	@Override
	public String getMybatisQryFunc() {
		return "CmpDictMapper.getCmpDictList";
	}
}