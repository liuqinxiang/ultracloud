package com.cmp.workflow.Lintener;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.cmp.entity.UserGroupUserMap;
import com.cmp.service.CmpWorkOrderService;
import com.cmp.service.UserGroupService;
import com.cmp.sid.CmpWorkOrder;
import com.fh.util.PageData;

/**
 * 审核者-任务监听器
 * 
 * @author liuweixing
 *
 */
public class AuditTaskListener implements Serializable, TaskListener {

	private static final long serialVersionUID = 667530869305257754L;
	
	private String name;

	@Override
	public void notify(DelegateTask delegateTask) {
		Map<String, Object> map = delegateTask.getVariables();
		String appNo = (String) delegateTask.getVariable("appNo");
		WebApplicationContext webctx=ContextLoader.getCurrentWebApplicationContext();
		CmpWorkOrderService cmpWorkOrderService = (CmpWorkOrderService) webctx.getBean("cmpWorkOrderService");
		CmpWorkOrder workOrder = null;
		try {
			workOrder = cmpWorkOrderService.findByAppNo(appNo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String projectCode = workOrder.getProjectCode();
		UserGroupService userGroupService = (UserGroupService) webctx.getBean("userGroupService");
		PageData pageData = new PageData();
		pageData.put("id", projectCode);
		try {
			pageData = userGroupService.findById(pageData);
			pageData.put("id", (BigInteger)pageData.get("id")+"");
			List<UserGroupUserMap> userList = userGroupService.listUserGroupUserMap(pageData);
			if (null != userList) {
				Set<String> set = new HashSet<String>();
				for (UserGroupUserMap user : userList) {
					set.add(user.getUSERNAME());
				}
				delegateTask.addCandidateUsers(set);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
