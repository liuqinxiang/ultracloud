package com.cmp.service.resourcemgt;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cmp.sid.CmpDict;
import com.fh.dao.DaoSupport;
import com.fh.entity.Page;
import com.fh.util.PageData;

/**
 * 数据中心业务层实现类
 * 
 * @author liuweixing
 *
 */
@Service("datacenterService")
public class DatacenterServiceImpl implements DatacenterService {

	@Resource(name = "daoSupport")
	private DaoSupport dao;

	/**
	 * 新增
	 * 
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd) throws Exception {
		dao.save("DatacenterMapper.save", pd);
	}

	/**
	 * 删除
	 * 
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd) throws Exception {
		dao.delete("DatacenterMapper.delete", pd);
	}

	/**
	 * 修改
	 * 
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd) throws Exception {
		dao.update("DatacenterMapper.edit", pd);
	}

	/**
	 * 列表
	 * 
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> list(Page page) throws Exception {
		return (List<PageData>) dao.findForList("DatacenterMapper.datalistPage", page);
	}

	/**
	 * 列表(全部)
	 * 
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> listAll(PageData pd) throws Exception {
		return (List<PageData>) dao.findForList("DatacenterMapper.listAll", pd);
	}

	/**
	 * 通过id获取数据
	 * 
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd) throws Exception {
		return (PageData) dao.findForObject("DatacenterMapper.findById", pd);
	}
	
	/**
	 * 通过云平台id获取数据
	 * 
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> findBycpfId(PageData pd) throws Exception {
		return (List<PageData>) dao.findForList("DatacenterMapper.findByCpfId", pd);
	}

	/**
	 * 批量删除
	 * 
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS) throws Exception {
		dao.delete("DatacenterMapper.deleteAll", ArrayDATA_IDS);
	}

	//数据中心列表查询
	@SuppressWarnings("unchecked")
	public List<CmpDict> getDataCenterList() throws Exception {
		return (List<CmpDict>) dao.findForList("DatacenterMapper.getDataCenterList", null);
	}

	
}
