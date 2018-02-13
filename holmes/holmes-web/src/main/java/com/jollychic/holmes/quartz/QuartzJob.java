package com.jollychic.holmes.quartz;

import com.jollychic.holmes.common.context.SpringUtil;
import com.jollychic.holmes.service.ExecutionService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

/**
 * @Description: 任务执行类
 *
 * @ClassName: QuartzJob
 * @Copyright: Copyright (c) 2014
 *
 * @author Comsys-LZP
 * @date 2014-6-26 下午03:37:11
 * @version V2.0
 */
@Slf4j
public class QuartzJob implements Job {
	@Override
	public void execute(JobExecutionContext context){
	    log.info("start QuartzJob");
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		String ruleId = jobDataMap.getString("ruleId");
        log.info("get jobDataMap ruleId: "+ruleId);
		ExecutionService executionService = SpringUtil.getBean(ExecutionService.class);
		log.info("active profile: "+SpringUtil.getApplicationContext().getEnvironment().getActiveProfiles()[0]);
		try{
			executionService.submitExecution(ruleId);
		}catch(Exception e){
			log.error(e.getMessage());
		}

	}
}