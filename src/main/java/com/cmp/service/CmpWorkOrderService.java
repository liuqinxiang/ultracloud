package com.cmp.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cmp.sid.CmpWorkOrder;
import com.fh.dao.DaoSupport;
import com.fh.util.PageData;

@Service
public class CmpWorkOrderService {
	@Resource(name = "daoSupport")
	private DaoSupport dao;

	//提交申请
	public void addWorkOrder(String orderId, String applyUserId) throws Exception {
		PageData pd=new PageData();
		pd.put("orderId", orderId);//清单ID
		pd.put("status", "0");//状态：0-待提交；1-审批中；2-审批通过；3-审批不通过
		pd.put("applyUserId", applyUserId);//申请者
		dao.save("CmpWorkOrderMapper.addWorkOrder", pd);
	}
	
	//保存工单
	public void addWordOrder(CmpWorkOrder cmpWorkorder) throws Exception {
		dao.save("CmpWorkOrderMapper.saveWorkOrder", cmpWorkorder);
	}
	
	//查询个人工单
	public List<CmpWorkOrder> selectUserWorkOrder(String applyUserId) throws Exception{
		return (List<CmpWorkOrder>)dao.findForList("CmpWorkOrderMapper.getCmpWorkOrderList", applyUserId);
	}
}