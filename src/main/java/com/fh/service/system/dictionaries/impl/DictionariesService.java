package com.fh.service.system.dictionaries.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fh.dao.DaoSupport;
import com.fh.entity.Page;
import com.fh.entity.system.Dictionaries;
import com.fh.util.PageData;
import com.fh.service.system.dictionaries.DictionariesManager;

/** 
 * 说明： 数据字典
 * 创建人：FH Q313596790
 * 创建时间：2015-12-16
 * @version
 */
@Service("dictionariesService")
public class DictionariesService implements DictionariesManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		dao.save("DictionariesMapper.save", pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		dao.delete("DictionariesMapper.delete", pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		dao.update("DictionariesMapper.edit", pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(Dictionaries dictionaries)throws Exception{
		PageData pd = new PageData();
		pd.put("BIANMA", dictionaries.getBIANMA());
		pd.put("DICTIONARIES_ID", dictionaries.getDICTIONARIES_ID());
		pd.put("NAME", dictionaries.getNAME());
		pd.put("NAME_EN", dictionaries.getNAME_EN());
		pd.put("ORDER_BY", dictionaries.getORDER_BY());
		pd.put("PARENT_ID", dictionaries.getPARENT_ID());
		dao.update("DictionariesMapper.edit", pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> list(Page page)throws Exception{
		return (List<PageData>)dao.findForList("DictionariesMapper.datalistPage", page);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return (PageData)dao.findForObject("DictionariesMapper.findById", pd);
	}
	
	/**通过编码获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findByBianma(PageData pd)throws Exception{
		return (PageData)dao.findForObject("DictionariesMapper.findByBianma", pd);
	}
	
	/**
	 * 通过ID获取其子级列表
	 * @param parentId
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<Dictionaries> listSubDictByParentId(String parentId) throws Exception {
		return (List<Dictionaries>) dao.findForList("DictionariesMapper.listSubDictByParentId", parentId);
	}
	
	
	/**
	 * 通过编码获取其子级列表
	 * @param bianma
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<Dictionaries> listSubDictByBianma(String bianma) throws Exception {
		return (List<Dictionaries>) dao.findForList("DictionariesMapper.listSubDictByBianma", bianma);
	}
	
	/**
	 * 通过编码获取其孙子级列表
	 * @param bianma
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<Dictionaries> listGrandsonDictByBianma(String bianma) throws Exception {
		return (List<Dictionaries>) dao.findForList("DictionariesMapper.listGrandsonDictByBianma", bianma);
	}
	
	/**
	 * 获取所有数据并填充每条数据的子级列表(递归处理)
	 * @param MENU_ID
	 * @return
	 * @throws Exception
	 */
	public List<Dictionaries> listAllDict(String parentId) throws Exception {
		List<Dictionaries> dictList = this.listSubDictByParentId(parentId);
		for(Dictionaries dict : dictList){
			dict.setTreeurl("dictionaries/list.do?DICTIONARIES_ID="+dict.getDICTIONARIES_ID());
			dict.setSubDict(this.listAllDict(dict.getDICTIONARIES_ID()));
			dict.setTarget("treeFrame");
		}
		return dictList;
	}
	
	/**排查表检查是否被占用
	 * @param pd
	 * @throws Exception
	 */
	public PageData findFromTbs(PageData pd)throws Exception{
		return (PageData)dao.findForObject("DictionariesMapper.findFromTbs", pd);
	}
	
}

