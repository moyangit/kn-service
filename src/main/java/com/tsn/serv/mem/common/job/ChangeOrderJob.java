package com.tsn.serv.mem.common.job;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tsn.common.utils.web.utils.bean.SpringContext;
import com.tsn.serv.mem.service.order.ChargeOrderService;

public class ChangeOrderJob implements Job{

	private static Logger logger = LoggerFactory.getLogger(ChangeOrderJob.class);

	private ChargeOrderService chargeOrderService;

	public ChangeOrderJob() {
		this.chargeOrderService = (ChargeOrderService) SpringContext.getBean("chargeOrderService");

	}

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		logger.debug(">>>>>>> start ChargeOrderService {}", new Date());

		chargeOrderService.getChargeOrderByCron();
	}
}