package com.cmp.controller;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.cmp.activiti.service.ActivitiService;
import com.cmp.entity.Project;
import com.cmp.service.CmpDictService;
import com.cmp.service.CmpOpServeService;
import com.cmp.service.CmpOrderService;
import com.cmp.service.CmpWorkOrderService;
import com.cmp.service.ProjectService;
import com.cmp.service.resourcemgt.ClusterService;
import com.cmp.service.resourcemgt.DatacenterService;
import com.cmp.sid.CmpDict;
import com.cmp.sid.CmpWorkOrder;
import com.cmp.sid.RelateTask;
import com.cmp.workorder.IWorkorderHandler;
import com.cmp.workorder.WorkorderHelper;
import com.fh.controller.base.BaseController;
import com.fh.entity.Page;
import com.fh.entity.system.User;
import com.fh.service.system.user.UserManager;
import com.fh.util.Const;
import com.fh.util.Jurisdiction;
import com.fh.util.ObjectExcelView;
import com.fh.util.PageData;

@Controller
public class CmpWorkOrderController extends BaseController{
	@Resource
	private CmpWorkOrderService cmpWorkOrderService;
	
	@Resource(name="userService")
	private UserManager userService;
	
	@Resource(name="cmpDictService")
	private CmpDictService cmpDictService;
	
	@Resource(name="projectService")
	private ProjectService projectService;
	
	@Resource
	private ActivitiService activitiService;
	
	@Resource
	private CmpOpServeService cmpOpServeService;
	
	
	@Resource
	private CmpOrderService cmpOrderService;
	
	@Resource 
	private WorkorderHelper  workorderHelper;
	
	@Resource
	private DatacenterService datacenterService;
	
	@Resource
	private ClusterService clusterService;
	
	
	
	@RequestMapping(value="/queryUserApplyWorkOrderPre")
	public ModelAndView querUserApplyWorkOrderPre(Page page) throws Exception{
		Session session = Jurisdiction.getSession();
		ModelAndView mv = new ModelAndView();
		User userr = (User)session.getAttribute(Const.SESSION_USERROL);				//读取session中的用户信息(含角色信息)
		if (userr == null) {
			User user = (User)session.getAttribute(Const.SESSION_USER);						//读取session中的用户信息(单独用户信息)
			if (user == null) {
				mv.setViewName("system/index/login");
				return mv;
			}
			userr = userService.getUserAndRoleById(user.getUSER_ID());				//通过用户ID读取用户信息和角色信息
			session.setAttribute(Const.SESSION_USERROL, userr);						//存入session	
		}
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("USERNAME", userr.getUSERNAME());
		page.setPd(pd);
		
		
		//工单查询  根据传入的查询参数， 查询部分或者全部     queryType :  不传或者1 = 全部，  2=今日的工单    3 = 待执行工单
		List<PageData> workOrderList = new ArrayList<PageData>();
		if (pd.get("queryType") == null ||  pd.get("queryType")=="" || pd.get("queryType").equals("1")) {
			workOrderList = cmpWorkOrderService.listUserWorkorderByPd(page);
		}else if (pd.get("queryType").equals("2")){
			workOrderList = cmpWorkOrderService.queryUserCurrentdayWorkorder(page);;
		}else if (pd.get("queryType").equals("3")) {
			workOrderList = cmpWorkOrderService.queryUserToDoWorkorder(page);;
		}
		
		//中文转化
		Map appTypeNameMap =  getAppTypeNameMap();
		Map workStatusMap = getWorkorderStatusNameMap();
		Map projectNameMap = getProjectNameMap();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (PageData workorderpd : workOrderList) {
			workorderpd.put("projectName", projectNameMap.get(workorderpd.get("projectCode") == null ? "" : workorderpd.get("projectCode")));
			workorderpd.put("statusName", workStatusMap.get(workorderpd.get("status") == null ? "" : workorderpd.get("status")));
			workorderpd.put("appTypeName", appTypeNameMap.get(workorderpd.get("appType") == null ? "" : workorderpd.get("appType")));
			workorderpd.put("createTime", workorderpd.get("createTime") == null ? "" : df.format(workorderpd.get("createTime")));
			
		}
		//工单类型
		List<CmpDict> workorderTypeList =  cmpDictService.getCmpDictList("workorder_type");
		//工单状态
		List<CmpDict> workorderStatusList =  cmpDictService.getCmpDictList("workorder_status");
		//项目名称
		List<Project>  projectList = projectService.listAllProject();
		
		//获取登录用户类型
		Map<String, String> qxMap = new HashMap<String, String>();
		String userType = userr.getRole().getTYPE();
		if (userType != null && (userType.equals("applicant") || userType.equals("admin")) ) {
			qxMap.put("query", "1");
		}else if (userType != null && userType.equals("audit")) {
			qxMap.put("check", "1");
		}else if (userType != null && userType.equals("executor")) {
			qxMap.put("execute", "1");
		}
		
		mv.addObject("workOrderList", workOrderList);
		mv.addObject("workorderTypeList", workorderTypeList);
		mv.addObject("workorderStatusList", workorderStatusList);
		mv.addObject("projectList", projectList);
		mv.addObject("appTypeNameMap", getAppTypeNameMap());
		mv.addObject("workorderStatusNameMap", getWorkorderStatusNameMap());
		mv.addObject("QX", qxMap); // 右侧按钮权限
		mv.addObject("pd", pd);
		mv.setViewName("workorder/query_user_workorder");
		return mv;
	}
	
	@RequestMapping(value="/goWorkorderCheck")
	public ModelAndView goWorkorderCheck(String appNo) throws Exception {
		
		ModelAndView mv = new ModelAndView();
		if (appNo == null || appNo.length() == 0) {
			return null;
		}
		CmpWorkOrder toCheckWorkorder = cmpWorkOrderService.findByAppNo(appNo);
		//获取工作流程图,查询流程定义
		HistoricProcessInstance hpi = activitiService.findProcessInst(toCheckWorkorder.getProcInstId());
		ActivityImpl workorderImag = null;
		if (!toCheckWorkorder.getStatus().equals("5")) {
			//流程执行未完毕
			workorderImag = activitiService.getProcessMap(hpi.getProcessDefinitionId(), hpi.getId()); 
		}
		//获取流程信息
		List<RelateTask> relateTaskList = fetchRelateTaskList(toCheckWorkorder.getProcInstId());
		
		mv.addObject("relateTaskList", relateTaskList);
		mv.addObject("workorderImag", workorderImag);
		mv.addObject("procDefId", hpi.getProcessDefinitionId());
		
		//获取流程注释
		List<Comment> commentList = activitiService.getProcessComments(toCheckWorkorder.getProcInstId());
		mv.addObject("commentList", commentList);
		
		//是资源申请，跳资源申请详情页面或运维申请审核页面
		String toViewUrl = "";
		IWorkorderHandler workorderHandler = workorderHelper.instance(toCheckWorkorder.getAppType());
		if (workorderHandler == null) {
			toViewUrl = "/404"; 
			mv.setViewName(toViewUrl);
			return mv;
		}
		Map<String, Object> pageViewMap = workorderHandler.toWorkorderCheck(toCheckWorkorder);
		if (pageViewMap == null) {
			toViewUrl = "/404"; 
			mv.setViewName(toViewUrl);
			return mv;
		}
		for (String key : pageViewMap.keySet()) {
			mv.addObject(key, pageViewMap.get(key));
		}
		mv.setViewName((String)pageViewMap.get("toPageUrl"));
		
		//是资源申请，跳资源申请审核页面或运维申请审核页面
//		String toCheckUrl = "";
//		if (toCheckWorkorder.getAppType()!= null && toCheckWorkorder.getAppType().equals("1")) {
//			CmpOrder orderInfo = null;
//			List<CmpOrder> orderList = cmpOrderService.getOrderDtl(toCheckWorkorder.getOrderNo());
//			if (orderList != null && orderList.size() > 0) {
//				orderInfo = orderList.get(0);
//			}
//			mv.addObject("orderInfo", orderInfo);
//			toCheckUrl = "workorder/applycheck";
//		}else if (toCheckWorkorder.getAppType()!= null && toCheckWorkorder.getAppType().equals("2")) {
//			//查询工单关联的运维信息
//			CmpOpServe opServe = cmpOpServeService.findByOrderNo(toCheckWorkorder.getOrderNo());
//			cmpOpServeService.encase(opServe);  //中文填充
//			mv.addObject("opServe", opServe);
//			toCheckUrl = "workorder/opercheck";
//		}
//		mv.addObject("workorder", toCheckWorkorder);
//		mv.setViewName(toCheckUrl);
		return mv;
	}
	
	@RequestMapping(value="/goWorkorderVerify")
	public ModelAndView goWorkorderVerify(String appNo) throws Exception {
		
		ModelAndView mv = new ModelAndView();
		if (appNo == null || appNo.length() == 0) {
			return null;
		}
		CmpWorkOrder toVerifyWorkorder = cmpWorkOrderService.findByAppNo(appNo);
		//获取工作流程图,查询流程定义
		HistoricProcessInstance hpi = activitiService.findProcessInst(toVerifyWorkorder.getProcInstId());
		ActivityImpl workorderImag = null;
		if (!toVerifyWorkorder.getStatus().equals("5")) {
			//流程执行未完毕
			workorderImag = activitiService.getProcessMap(hpi.getProcessDefinitionId(), hpi.getId()); 
		}
		//获取流程信息
		List<RelateTask> relateTaskList = fetchRelateTaskList(toVerifyWorkorder.getProcInstId());
		
		mv.addObject("relateTaskList", relateTaskList);
		mv.addObject("workorderImag", workorderImag);
		mv.addObject("procDefId", hpi.getProcessDefinitionId());
		
		//获取流程注释
		List<Comment> commentList = activitiService.getProcessComments(toVerifyWorkorder.getProcInstId());
		mv.addObject("commentList", commentList);
		
		//是资源申请，跳资源申请详情页面或运维申请审核页面
		String toViewUrl = "";
		IWorkorderHandler workorderHandler = workorderHelper.instance(toVerifyWorkorder.getAppType());
		if (workorderHandler == null) {
			toViewUrl = "/404"; 
			mv.setViewName(toViewUrl);
			return mv;
		}
		Map<String, Object> pageViewMap = workorderHandler.toWorkorderCheck(toVerifyWorkorder);
		if (pageViewMap == null) {
			toViewUrl = "/404"; 
			mv.setViewName(toViewUrl);
			return mv;
		}
		for (String key : pageViewMap.keySet()) {
			mv.addObject(key, pageViewMap.get(key));
		}
		mv.setViewName((String)pageViewMap.get("toPageUrl"));
		
		//是资源申请，跳资源申请审核页面或运维申请审核页面
//		String toCheckUrl = "";
//		if (toCheckWorkorder.getAppType()!= null && toCheckWorkorder.getAppType().equals("1")) {
//			CmpOrder orderInfo = null;
//			List<CmpOrder> orderList = cmpOrderService.getOrderDtl(toCheckWorkorder.getOrderNo());
//			if (orderList != null && orderList.size() > 0) {
//				orderInfo = orderList.get(0);
//			}
//			mv.addObject("orderInfo", orderInfo);
//			toCheckUrl = "workorder/applycheck";
//		}else if (toCheckWorkorder.getAppType()!= null && toCheckWorkorder.getAppType().equals("2")) {
//			//查询工单关联的运维信息
//			CmpOpServe opServe = cmpOpServeService.findByOrderNo(toCheckWorkorder.getOrderNo());
//			cmpOpServeService.encase(opServe);  //中文填充
//			mv.addObject("opServe", opServe);
//			toCheckUrl = "workorder/opercheck";
//		}
//		mv.addObject("workorder", toCheckWorkorder);
//		mv.setViewName(toCheckUrl);
		return mv;
	}
	
	@RequestMapping(value="/goWorkorderExecute")
	public ModelAndView goWorkorderExecute(String appNo) throws Exception {
		
		ModelAndView mv = new ModelAndView();
		if (appNo == null || appNo.length() == 0) {
			return null;
		}
		CmpWorkOrder toExcuteWorkorder = cmpWorkOrderService.findByAppNo(appNo);
		
		//获取工作流程图,查询流程定义
		HistoricProcessInstance hpi = activitiService.findProcessInst(toExcuteWorkorder.getProcInstId());
		ActivityImpl workorderImag = null;
		if (!toExcuteWorkorder.getStatus().equals("5")) {
			//流程执行未完毕
			workorderImag = activitiService.getProcessMap(hpi.getProcessDefinitionId(), hpi.getId()); 
		}
		//获取流程信息
		List<RelateTask> relateTaskList = fetchRelateTaskList(toExcuteWorkorder.getProcInstId());
		
		mv.addObject("relateTaskList", relateTaskList);
		mv.addObject("workorderImag", workorderImag);
		mv.addObject("procDefId", hpi.getProcessDefinitionId());
		
		//获取流程注释
		List<Comment> commentList = activitiService.getProcessComments(toExcuteWorkorder.getProcInstId());
		mv.addObject("commentList", commentList);
		
		//是资源申请，跳资源申请详情页面或运维申请执行页面
		String toViewUrl = "";
		IWorkorderHandler workorderHandler = workorderHelper.instance(toExcuteWorkorder.getAppType());
		if (workorderHandler == null) {
			toViewUrl = "/404"; 
			mv.setViewName(toViewUrl);
			return mv;
		}
		Map<String, Object> pageViewMap = workorderHandler.toWorkorderExecute(toExcuteWorkorder);
		if (pageViewMap == null) {
			toViewUrl = "/404"; 
			mv.setViewName(toViewUrl);
			return mv;
		}
		for (String key : pageViewMap.keySet()) {
			mv.addObject(key, pageViewMap.get(key));
		}
		mv.setViewName((String)pageViewMap.get("toPageUrl"));
		
//		//是资源申请，跳资源申请审核页面或运维申请审核页面
//		String toExecuteUrl = "";
//		if (toExcuteWorkorder.getAppType()!= null && toExcuteWorkorder.getAppType().equals("1")) {
//			CmpOrder orderInfo = null;
//			List<CmpOrder> orderList = cmpOrderService.getOrderDtl(toExcuteWorkorder.getOrderNo());
//			if (orderList != null && orderList.size() > 0) {
//				orderInfo = orderList.get(0);
//			}
//			mv.addObject("orderInfo", orderInfo);
//			toExecuteUrl = "workorder/applyexecute";
//		}else if (toExcuteWorkorder.getAppType()!= null && toExcuteWorkorder.getAppType().equals("2")) {
//			//查询工单关联的运维信息
//			CmpOpServe opServe = cmpOpServeService.findByOrderNo(toExcuteWorkorder.getOrderNo());
//			cmpOpServeService.encase(opServe);  //中文填充
//			mv.addObject("opServe", opServe);
//			toExecuteUrl = "workorder/operexecute";
//		}
//
//		mv.addObject("workorder", toExcuteWorkorder);
//		mv.setViewName(toExecuteUrl);
		return mv;
	}
	
	
	@RequestMapping(value="/goWorkorderView")
	public ModelAndView goWorkorderView(String appNo) throws Exception {
		
		ModelAndView mv = new ModelAndView();
		if (appNo == null || appNo.length() == 0) {
			return null;
		}
		CmpWorkOrder toViewWorkorder = cmpWorkOrderService.findByAppNo(appNo);
		//获取工作流程图,查询流程定义
		HistoricProcessInstance hpi = activitiService.findProcessInst(toViewWorkorder.getProcInstId());
		ActivityImpl workorderImag = null;
		if (!toViewWorkorder.getStatus().equals("5")) {
			//流程执行未完毕
			workorderImag = activitiService.getProcessMap(hpi.getProcessDefinitionId(), hpi.getId()); 
		}
		//获取流程信息
		List<RelateTask> relateTaskList = fetchRelateTaskList(toViewWorkorder.getProcInstId());
		
		mv.addObject("relateTaskList", relateTaskList);
		mv.addObject("workorderImag", workorderImag);
		mv.addObject("procDefId", hpi.getProcessDefinitionId());
		
		//获取流程注释
		List<Comment> commentList = activitiService.getProcessComments(toViewWorkorder.getProcInstId());
		mv.addObject("commentList", commentList);
		
		//是资源申请，跳资源申请详情页面或运维申请详情页面
		String toViewUrl = "";
		IWorkorderHandler workorderHandler = workorderHelper.instance(toViewWorkorder.getAppType());
		if (workorderHandler == null) {
			toViewUrl = "/404"; 
			mv.setViewName(toViewUrl);
			return mv;
		}
		Map<String, Object> pageViewMap = workorderHandler.toWorkorderView(toViewWorkorder);
		if (pageViewMap == null) {
			toViewUrl = "/404"; 
			mv.setViewName(toViewUrl);
			return mv;
		}
		for (String key : pageViewMap.keySet()) {
			mv.addObject(key, pageViewMap.get(key));
		}
		mv.setViewName((String)pageViewMap.get("toPageUrl"));
//		if (toViewWorkorder.getAppType()!= null && toViewWorkorder.getAppType().equals("1")) {
//			CmpOrder orderInfo = null;
//			List<CmpOrder> orderList = cmpOrderService.getOrderDtl(toViewWorkorder.getOrderNo());
//			if (orderList != null && orderList.size() > 0) {
//				orderInfo = orderList.get(0);
//			}
//			mv.addObject("orderInfo", orderInfo);
//			toViewUrl = "workorder/applyview";
//		}else if (toViewWorkorder.getAppType()!= null && toViewWorkorder.getAppType().equals("2")) {
//			//查询工单关联的运维信息
//			CmpOpServe opServe = cmpOpServeService.findByOrderNo(toViewWorkorder.getOrderNo());
//			cmpOpServeService.encase(opServe);  //中文填充
//			mv.addObject("opServe", opServe);
//			toViewUrl = "workorder/operview";
//		}
		//mv.addObject("workorder", toViewWorkorder);
		//mv.setViewName(toViewUrl);
		return mv;
	}
	
	
	/**工单审核
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value="/doCheck")
	@ResponseBody
	public Object doCheck(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Map<String,String> map = new HashMap<String,String>();
		String resultInfo = "审核失败";
		String appNo = request.getParameter("appNo");
		String comment = request.getParameter("comment") == null? "":request.getParameter("comment");
		String rejectFlag = request.getParameter("rejectFlag"); //0拒绝  1批准 
		Session session = Jurisdiction.getSession();
		
		CmpWorkOrder toCheckWorkorder = cmpWorkOrderService.findByAppNo(appNo);
		if (toCheckWorkorder == null) {
			resultInfo = "审核失败,工单号不存在";
			map.put("result", resultInfo);	
			return map;
		}
		User userr = (User)session.getAttribute(Const.SESSION_USERROL);				//读取session中的用户信息(含角色信息)
		if (userr == null) {
			User user = (User)session.getAttribute(Const.SESSION_USER);						//读取session中的用户信息(单独用户信息)
			if (user == null) {
				map.put("result", resultInfo);	
				return map;
			}
			userr = userService.getUserAndRoleById(user.getUSER_ID());				//通过用户ID读取用户信息和角色信息
			session.setAttribute(Const.SESSION_USERROL, userr);						//存入session	
		}
		
		List<Task> userTaskList = activitiService.findGroupList(userr.getUSERNAME(), 1, 100);
		for (Task task : userTaskList) {
			if (task.getProcessInstanceId().equals(toCheckWorkorder.getProcInstId())) {
				activitiService.claimTask(task.getId(), userr.getUSERNAME());
				//写入流程注释
				activitiService.addComment(task.getId(), toCheckWorkorder.getProcInstId(), userr.getUSERNAME(), comment);
				Map<String, Object> variables = new HashMap<String, Object>();
				if (rejectFlag != null && "0".equals(rejectFlag)) {
					//拒绝流程
					variables.put("USERNAME", userr.getUSERNAME());
					variables.put("rejectFlag", 0);
					activitiService.handleTask(appNo, toCheckWorkorder.getProcInstId(), userr.getUSERNAME(), null, variables);
					
					//更新工单(流程实例ID 和 工单状态)
					Map<String, String> updateParams = new HashMap<String, String>();
					updateParams.put("status", "3");  //审批不通过,退回
					updateParams.put("procInstId", toCheckWorkorder.getProcInstId());
					cmpWorkOrderService.updateWorkOrder(appNo, updateParams);
					resultInfo = "审核完成";
					map.put("result", resultInfo);	
					return map;
				}else {
					//同意流程
					variables.put("USERNAME", userr.getUSERNAME());
					variables.put("rejectFlag", 1);
					activitiService.handleTask(appNo, toCheckWorkorder.getProcInstId(), userr.getUSERNAME(), null, variables);
					//更新工单(流程实例ID 和 工单状态)
					Map<String, String> updateParams = new HashMap<String, String>();
					updateParams.put("status", "2");  //进入运维执行状态
					updateParams.put("procInstId", toCheckWorkorder.getProcInstId());
					cmpWorkOrderService.updateWorkOrder(appNo, updateParams);
					resultInfo = "审核完成";
					map.put("result", resultInfo);	
					return map;
				}
			}
		}
		map.put("result", resultInfo);				//返回结果
		return map;
	}
	
	
	/**工单确认
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value="/doVerify")
	@ResponseBody
	public Object doVerify(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Map<String,String> map = new HashMap<String,String>();
		String resultInfo = "确认完成";
		String appNo = request.getParameter("appNo");
		String comment = request.getParameter("comment") == null? "":request.getParameter("comment");
		Session session = Jurisdiction.getSession();
		
		CmpWorkOrder toVerifyWorkorder = cmpWorkOrderService.findByAppNo(appNo);
		if (toVerifyWorkorder == null) {
			resultInfo = "审核失败,工单号不存在";
			map.put("result", resultInfo);	
			return map;
		}
		User userr = (User)session.getAttribute(Const.SESSION_USERROL);				//读取session中的用户信息(含角色信息)
		if (userr == null) {
			User user = (User)session.getAttribute(Const.SESSION_USER);						//读取session中的用户信息(单独用户信息)
			if (user == null) {
				map.put("result", resultInfo);	
				return map;
			}
			userr = userService.getUserAndRoleById(user.getUSER_ID());				//通过用户ID读取用户信息和角色信息
			session.setAttribute(Const.SESSION_USERROL, userr);						//存入session	
		}
		
		List<Task> userTaskList = activitiService.findGroupList(userr.getUSERNAME(), 1, 100);
		for (Task task : userTaskList) {
			if (task.getProcessInstanceId().equals(toVerifyWorkorder.getProcInstId())) {
				activitiService.claimTask(task.getId(), userr.getUSERNAME());
				//写入流程注释
				activitiService.addComment(task.getId(), toVerifyWorkorder.getProcInstId(), userr.getUSERNAME(), comment);
				Map<String, Object> variables = new HashMap<String, Object>();
				activitiService.handleTask(appNo, toVerifyWorkorder.getProcInstId(), userr.getUSERNAME(), null, variables);
				//更新工单(流程实例ID 和 工单状态)
				Map<String, String> updateParams = new HashMap<String, String>();
				updateParams.put("status", "5");  //工单完成
				updateParams.put("procInstId", toVerifyWorkorder.getProcInstId());
				cmpWorkOrderService.updateWorkOrder(appNo, updateParams);
				resultInfo = "确认完成";
				map.put("result", resultInfo);	
				return map;
			}
		}
		map.put("result", resultInfo);				//返回结果
		return map;
	}
	
	
	
	
	/**工单执行
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value="/doExecute")
	@ResponseBody
	public Object doExecute(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Map<String,String> map = new HashMap<String,String>();
		String resultInfo = "执行失败";
		String appNo = request.getParameter("appNo");
		String comment = request.getParameter("comment") == null? "":request.getParameter("comment");
		String rejectFlag = request.getParameter("rejectFlag"); //0拒绝  1批准 
		Session session = Jurisdiction.getSession();
		
		CmpWorkOrder toExecuteWorkorder = cmpWorkOrderService.findByAppNo(appNo);
		if (toExecuteWorkorder == null) {
			resultInfo = "执行失败,工单号不存在";
			map.put("result", resultInfo);	
			return map;
		}
		User userr = (User)session.getAttribute(Const.SESSION_USERROL);				//读取session中的用户信息(含角色信息)
		if (userr == null) {
			User user = (User)session.getAttribute(Const.SESSION_USER);						//读取session中的用户信息(单独用户信息)
			if (user == null) {
				map.put("result", resultInfo);	
				return map;
			}
			userr = userService.getUserAndRoleById(user.getUSER_ID());				//通过用户ID读取用户信息和角色信息
			session.setAttribute(Const.SESSION_USERROL, userr);						//存入session	
		}
		List<Task> userTaskList = activitiService.findGroupList(userr.getUSERNAME(), 1, 100);
		for (Task task : userTaskList) {
			if (task.getProcessInstanceId().equals(toExecuteWorkorder.getProcInstId())) {
				activitiService.claimTask(task.getId(), userr.getUSERNAME());
				//写入流程注释
				activitiService.addComment(task.getId(), toExecuteWorkorder.getProcInstId(), userr.getUSERNAME(), comment);
				Map<String, Object> variables = new HashMap<String, Object>();
				if (rejectFlag != null && "0".equals(rejectFlag)) {
					//拒绝流程
					variables.put("USERNAME", userr.getUSERNAME());
					variables.put("rejectFlag", 0);
					activitiService.handleTask(appNo, toExecuteWorkorder.getProcInstId(), userr.getUSERNAME(), null, variables);
					//更新工单(流程实例ID 和 工单状态)
					Map<String, String> updateParams = new HashMap<String, String>();
					updateParams.put("status", "3");  //退回
					updateParams.put("procInstId", toExecuteWorkorder.getProcInstId());
					cmpWorkOrderService.updateWorkOrder(appNo, updateParams);
				}else {
					variables.put("USERNAME", userr.getUSERNAME());
					variables.put("rejectFlag", 1);
					activitiService.handleTask(appNo, toExecuteWorkorder.getProcInstId(), userr.getUSERNAME(), null, variables);
					//更新工单(流程实例ID 和 工单状态)
					Map<String, String> updateParams = new HashMap<String, String>();
					updateParams.put("status", "5");  //进入运维执行状态
					updateParams.put("procInstId", toExecuteWorkorder.getProcInstId());
					cmpWorkOrderService.updateWorkOrder(appNo, updateParams);
				}
				resultInfo = "执行成功";
				map.put("result", resultInfo);	
				return map;
			}
		}
		map.put("result", resultInfo);				//返回结果
		return map;
	}
	
	

	/**执行任务
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value="/executeWork")
	@ResponseBody
	public Object executeWork() throws Exception{
		Map<String,String> map = new HashMap<String,String>();
		String resultInfo = "";
		PageData pd = new PageData();
		pd = this.getPageData();
		
		Session session = Jurisdiction.getSession();
		User userr = (User)session.getAttribute(Const.SESSION_USERROL);				//读取session中的用户信息(含角色信息)
		if (userr == null) {
			User user = (User)session.getAttribute(Const.SESSION_USER);						//读取session中的用户信息(单独用户信息)
			if (user == null) {
				resultInfo = "执行失败,请重新登录";
				map.put("result", resultInfo);	
				return map;
			}
			userr = userService.getUserAndRoleById(user.getUSER_ID());				//通过用户ID读取用户信息和角色信息
			session.setAttribute(Const.SESSION_USERROL, userr);						//存入session	
		}
		pd.put("USERNAME", userr.getUSERNAME());
		CmpWorkOrder workorder = cmpWorkOrderService.findByAppNo((String)pd.get("appNo"));
		if (workorder == null) {
			resultInfo = "执行失败,工单不存在";
			map.put("result", resultInfo);	
			return map;
		}
		
		IWorkorderHandler workorderHandler = workorderHelper.instance(workorder.getAppType());
		if (workorderHandler == null) {
			resultInfo = "执行失败,工单不存在";
			map.put("result", resultInfo);	
			return map;
		}
		
		//修改工单为执行状态
		Map<String , String> exeParams = new HashMap<String , String>();
		exeParams.put("executeStatus", "1");
		cmpWorkOrderService.updateExecuteStatus(workorder.getAppNo(), exeParams);
		Map<String, Object> pageViewMap = workorderHandler.executeWork(pd, workorder);
		if (pageViewMap == null) {
			resultInfo = "执行失败";
			map.put("result", resultInfo);	
			return map;
		}
		map.put("result", (String)pageViewMap.get("result"));				//返回结果
		return map;
	}
	
	
	
	
	/**导出工单信息到EXCEL
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value="/workorderExcel")
	public ModelAndView exportExcel(Page page) throws Exception{
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		try{
			Session session = Jurisdiction.getSession();
			User userr = (User)session.getAttribute(Const.SESSION_USERROL);				//读取session中的用户信息(含角色信息)
			if (userr == null) {
				User user = (User)session.getAttribute(Const.SESSION_USER);						//读取session中的用户信息(单独用户信息)
				if (user == null) {
					mv.setViewName("system/index/login");
					return mv;
				}
				userr = userService.getUserAndRoleById(user.getUSER_ID());				//通过用户ID读取用户信息和角色信息
				session.setAttribute(Const.SESSION_USERROL, userr);						//存入session	
			}
			pd = this.getPageData();
			pd.put("USERNAME", userr.getUSERNAME());
			page.setPd(pd);
			
			Map appTypeNameMap =  getAppTypeNameMap();
			Map workStatusMap = getWorkorderStatusNameMap();
			Map projectNameMap = getProjectNameMap();
			List<PageData> workOrderList = new ArrayList<PageData>();
			workOrderList = cmpWorkOrderService.listUserWorkorderByPd(page);
			
				Map<String,Object> dataMap = new HashMap<String,Object>();
				List<String> titles = new ArrayList<String>();
				titles.add("工单号"); 		//1
				titles.add("工单类型");  		//2
				titles.add("工单状态");			//3
				titles.add("项目名称");			//4
				titles.add("工单时间");			//5
				dataMap.put("titles", titles);
				List<PageData> varList = new ArrayList<PageData>();
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				for(int i=0;i<workOrderList.size();i++){
					PageData vpd = new PageData();
					vpd.put("var1", workOrderList.get(i).getString("appNo"));		//1
					vpd.put("var2", appTypeNameMap.get(workOrderList.get(i).getString("appType") == null ?  "" : workOrderList.get(i).getString("appType")));		//2
					vpd.put("var3", workStatusMap.get(workOrderList.get(i).getString("status") == null ? "" : workOrderList.get(i).getString("status") ));			//3
					vpd.put("var4", projectNameMap.get(workOrderList.get(i).getString("projectCode") == null ? "" : workOrderList.get(i).getString("projectCode")));	//4
					vpd.put("var5", df.format(workOrderList.get(i).get("createTime")));		//5
					varList.add(vpd);
				}
				dataMap.put("varList", varList);
				ObjectExcelView erv = new ObjectExcelView();					//执行excel操作
				mv = new ModelAndView(erv,dataMap);
		} catch(Exception e){
			logger.error(e.toString(), e);
		}
		return mv;
	}
	
	/** 
	 * 获取流程图像 
	 *@author
	 * @return 
	 */  
	@RequestMapping(value="/findworkflowPic")  
	public void findPic(String procDefId,HttpServletResponse response){  
	    //HttpServletResponse response = ServletActionContext.getResponse();  
	    try {  
	        InputStream pic = activitiService.findProcessPic(procDefId);  
	          
	        byte[] b = new byte[1024];  
	        int len = -1;  
	        while((len = pic.read(b, 0, 1024)) != -1) {  
	            response.getOutputStream().write(b, 0, len);  
	        }  
	  
	    } catch (Exception e) {  
	        logger.error("获取图片失败。。。");  
	        e.printStackTrace();  
	    }  
	}  
	
	/**
	 * 平台选择级联查询数据中心
	 * @param serviceType
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value="/onCloudplatformSelected")
	@ResponseBody
	public List<PageData> queryDataCenter(String cloudPlatformId) throws Exception{
		if (cloudPlatformId == null || cloudPlatformId.length() == 0) {
			return null;
		}
		PageData pd = new PageData();
		pd.put("cpf_id", cloudPlatformId);
		List<PageData> cloudplatformList=datacenterService.findBycpfId(pd);
		return cloudplatformList;
	}
	
	
	/**
	 * 数据中心选择级联查询集群
	 * @param serviceType
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value="/onDataCenterSelected")
	@ResponseBody
	public List<PageData> queryCluster(String dataCenterId) throws Exception{
		if (dataCenterId == null || dataCenterId.length() == 0) {
			return null;
		}
		PageData pd = new PageData();
		pd.put("datacenter_id", dataCenterId);
		List<PageData> clusterList =  clusterService.findByDataCenterId(pd);
		return clusterList;
	}
	
	
	
	
	public List<RelateTask> fetchRelateTaskList(String currentProcInstId){
		List<RelateTask> relateTaskList = new ArrayList<RelateTask>();
		List<HistoricActivityInstance> hisActInst = activitiService.getHisActInfo(currentProcInstId);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (HistoricActivityInstance hai : hisActInst) {
			if(hai.getActivityName() == null || hai.getActivityName().equals("")) {
				continue;
			}
			RelateTask rt = new RelateTask();
			rt.setTaskNo(hai.getActivityId());
			rt.setAssignee(hai.getAssignee());
			rt.setStatus(hai.getEndTime() != null ? "关闭" : "新建");
			rt.setResult(hai.getEndTime() != null ? "符合申请" : "未处理");
			rt.setTime(hai.getEndTime() == null? "无" : df.format(hai.getEndTime()));
			relateTaskList.add(rt);
		}
		return relateTaskList;
	}
	
	
	public Map getAppTypeNameMap() {
		Map<String, String> appTypeNameMap = new HashMap<String, String>();
		List<CmpDict> workorderTypeList =  cmpDictService.getCmpDictList("workorder_type");
		for (CmpDict workorderDict  : workorderTypeList) {
			appTypeNameMap.put(workorderDict.getDictCode(), workorderDict.getDictValue());
		}
		return appTypeNameMap;
	}
	
	public Map getWorkorderStatusNameMap() {
		Map<String, String> workorderStatusNameMap = new HashMap<String, String>();
		List<CmpDict> workorderStatusList = cmpDictService.getCmpDictList("workorder_status");
		for (CmpDict workorderStatusDict  : workorderStatusList) {
			workorderStatusNameMap.put(workorderStatusDict.getDictCode(), workorderStatusDict.getDictValue());
		}
		return workorderStatusNameMap;
	}
	
	
	public Map getProjectNameMap() throws Exception {
		Map<String, String> projectNameMap = new HashMap<String, String>();
		List<Project>  projectList = projectService.listAllProject();
		for (Project p : projectList) {
			projectNameMap.put(p.getId(), p.getName());
		}
		return projectNameMap;
		
	}
	
	public String getUserName(String userId) throws Exception {
		User userr = userService.getUserAndRoleById(userId);
		return userr.getNAME();
	}
	
	
	
}
