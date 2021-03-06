package com.cmp.task;

import java.util.Map;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.cmp.service.bi.BIDatacenterService;
import com.cmp.service.system.TimeTaskServiceImpl;
import com.fh.controller.base.BaseController;
import com.fh.util.Logger;
import com.fh.util.PageData;

/**
 * quartz BI数据生成定时任务
 * 
 * @author
 * @date
 */
public class BIDataGenerateJob extends BaseController implements Job {

	protected Logger logger = Logger.getLogger(this.getClass());

	@SuppressWarnings("unchecked")
	public void execute(JobExecutionContext context) throws JobExecutionException {

		logger.info("BIDataGenerateJob start...---------------->>");

		// TODO Auto-generated method stub
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		Map<String, Object> parameter = (Map<String, Object>) dataMap.get("parameterList"); // 获取参数

		WebApplicationContext webctx = ContextLoader.getCurrentWebApplicationContext();
		BIDatacenterService biDataGenerateService = (BIDatacenterService) webctx.getBean("biDataGenerateService");

		try {
			biDataGenerateService.biDataGenerateHandler();
		} catch (Exception e) {
			e.printStackTrace();
		}

		logger.info("BIDataGenerateJob end.------------------->>|");
	}

	/**
	 * 把定时备份任务状态改为关闭
	 * 
	 * @param pd
	 * @param parameter
	 * @param webctx
	 */
	public void shutdownJob(JobExecutionContext context, PageData pd, Map<String, Object> parameter, WebApplicationContext webctx) {
		try {
			context.getScheduler().shutdown(); // 备份异常时关闭任务
			TimeTaskServiceImpl timeTaskService = (TimeTaskServiceImpl) webctx.getBean("timingbackupService");
			pd.put("STATUS", 2); // 改变定时运行状态为2，关闭
			pd.put("TIMINGBACKUP_ID", parameter.get("TIMINGBACKUP_ID").toString()); // 定时备份ID
			timeTaskService.changeStatus(pd);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
