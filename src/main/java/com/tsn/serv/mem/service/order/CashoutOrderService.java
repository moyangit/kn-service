package com.tsn.serv.mem.service.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tsn.common.utils.exception.BusinessException;
import com.tsn.common.utils.utils.tools.time.DateUtils;
import com.tsn.common.utils.utils.tools.valid.ValidTools;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.common.utils.web.utils.env.Env;
import com.tsn.common.utils.web.utils.gener.SnowFlakeManager;
import com.tsn.serv.common.code.mem.MemCode;
import com.tsn.serv.common.code.order.OrderCode;
import com.tsn.serv.common.enm.comm.FlowStatusEum;
import com.tsn.serv.common.enm.id.GenIDEnum;
import com.tsn.serv.common.enm.mem.AccRecordTypeEum;
import com.tsn.serv.common.enm.mem.MemTypeEum;
import com.tsn.serv.common.enm.order.PayTypeEum;
import com.tsn.serv.mem.entity.account.MemAccount;
import com.tsn.serv.mem.entity.account.MemAccountRecord;
import com.tsn.serv.mem.entity.mem.MemCashoutInfo;
import com.tsn.serv.mem.entity.mem.MemInfo;
import com.tsn.serv.mem.entity.order.CashoutOrder;
import com.tsn.serv.mem.mapper.account.MemAccountMapper;
import com.tsn.serv.mem.mapper.account.MemAccountRecordMapper;
import com.tsn.serv.mem.mapper.order.CashoutOrderMapper;
import com.tsn.serv.mem.service.mem.MemService;

@Service
public class CashoutOrderService {
	
	@Autowired
	private CashoutOrderMapper cashoutOrderMapper;
	
	@Autowired
	private MemService memService;
	
	@Autowired
	private MemAccountMapper memAccountMapper;

	@Autowired
	private MemAccountRecordMapper memAccountRecordMapper;

	@Autowired
	private ChargeOrderService chargeOrderService;

	private SnowFlakeManager snowFlakeManager = SnowFlakeManager.build();
	
	public void queryCashoutOrderByPage(Page page) {
		List<CashoutOrder> cashoutOrderList = cashoutOrderMapper.selectByPage(page);
		page.setData(cashoutOrderList);
	}
	
	@Transactional
	public void addCashoutOrder(MemCashoutInfo cashoutInfo) {

		// 允许提现时间校验
		cashoutCheck(cashoutInfo.getMemId());
		
		if (!ValidTools.isInteger(cashoutInfo.getMoney())) {
			// throw new RequestParamValidException("cashout money must be number");
			throw new BusinessException(OrderCode.CASHOUT_MONEY_MUST_BE_NUMBER, "cashout money must be number");
		}
		
		String minLimitMoney = Env.getVal("mem.cashout.momey.limit.min.val");
		String maxLimitMoney = Env.getVal("mem.cashout.momey.limit.max.val");
		
		minLimitMoney = StringUtils.isEmpty(minLimitMoney) ? "10" : minLimitMoney;
		
		if (new BigDecimal(cashoutInfo.getMoney()).compareTo(new BigDecimal(minLimitMoney)) < 0) {
			//return Response.retn(code, message, data)
			throw new BusinessException(OrderCode.ORDER_MONEY_DAYU_0, "cashout money can not < " + minLimitMoney, minLimitMoney);
		}
		
		if (!StringUtils.isEmpty(maxLimitMoney)) {
			if (new BigDecimal(cashoutInfo.getMoney()).compareTo(new BigDecimal(maxLimitMoney)) > 0) {
				throw new BusinessException(OrderCode.ORDER_MONEY_XIAOYU_0, "cashout money can not >" + maxLimitMoney, maxLimitMoney);
			}
		}
		
		String orderNo = snowFlakeManager.create(GenIDEnum.CASH_ORDER_NO.name()).getIdByPrefix(GenIDEnum.CASH_ORDER_NO.getPreFix());
		
		MemInfo memInfo = memService.queryMemById(cashoutInfo.getMemId());
		
		//如果是游客
		if (MemTypeEum.guest_mem.getCode().equals(memInfo.getMemType())) {
			throw new BusinessException(MemCode.MEM_GUEST_CAHSOUT_ERROR, "guest people can not cashout!");
		}
		
		CashoutOrder cashoutOrder = new CashoutOrder();
		cashoutOrder.setOrderNo(orderNo);
		
		if (StringUtils.isEmpty(cashoutInfo.getCashType())) {
			cashoutOrder.setCashType(PayTypeEum.alipay.name());
		}
		
		cashoutOrder.setCashAccNo(cashoutInfo.getCashAccNo());
		cashoutOrder.setRealName(cashoutInfo.getRealName());
		cashoutOrder.setRealPhone(cashoutInfo.getUserPhone());
		cashoutOrder.setWxAccNo(cashoutInfo.getWxNo());
		
		MemAccount account = memAccountMapper.queryMemAccountByMemId(cashoutInfo.getMemId());
		
		if (account == null) {
			throw new BusinessException(MemCode.MEM_IS_NOT_PROXY, "this mem is not proxy, memId =" + cashoutInfo.getMemId());
		}
		
		BigDecimal cashoutMoney = new BigDecimal(cashoutInfo.getMoney());
		
		if (account.getAccountMoney().compareTo(cashoutMoney) < 0) {
			throw new BusinessException(OrderCode.ORDER_MONEY_NOMATCH_ACC, "cash money > account money！！");
		}

		//账户余额 - 提现金额
		BigDecimal accountMoney = account.getAccountMoney().subtract(cashoutMoney);

		cashoutOrder.setMemAccountNo(account.getAccountNo());
		cashoutOrder.setMemId(cashoutInfo.getMemId());
		cashoutOrder.setMemName(memInfo.getMemNickName());
		cashoutOrder.setMemPhone(memInfo.getMemPhone());
		cashoutOrder.setMemType(memInfo.getMemType());
		cashoutOrder.setRemark("");
		cashoutOrder.setCashMoney(cashoutMoney);
		cashoutOrder.setOrderStatus("0");
		cashoutOrder.setCreateTime(new Date());
		cashoutOrder.setUpdateTime(cashoutOrder.getCreateTime());
		
		cashoutOrderMapper.insert(cashoutOrder);

		//修改账户表账户余额
		account.setAccountMoney(accountMoney);
		account.setUpdateTime(new Date());

		memAccountMapper.updateByPrimaryKeySelective(account);

		//新增流水表流水
		MemAccountRecord memAccountRecord = new MemAccountRecord();

		memAccountRecord.setAccountNo(account.getAccountNo());
		//type:提现流水
		memAccountRecord.setRecordType(AccRecordTypeEum.cashout.getCode());
		//提现属于支出流水 0：收入 1：支出
		memAccountRecord.setRecordWay((byte)1);
		memAccountRecord.setRecordStatus(FlowStatusEum.success.getStatus());
		memAccountRecord.setOrderNo(orderNo);
		memAccountRecord.setChangeMoney(cashoutMoney);
		memAccountRecord.setBalanceMoney(accountMoney);
		memAccountRecord.setRemark("");
		memAccountRecord.setMemId(cashoutInfo.getMemId());
		memAccountRecord.setMemPhone(memInfo.getMemPhone());
		memAccountRecord.setMemName(memInfo.getMemNickName());
		memAccountRecord.setCreateTime(new Date());
		memAccountRecord.setUpdateTime(memAccountRecord.getCreateTime());

		memAccountRecordMapper.insert(memAccountRecord);

		// 计算更新更新允许会员下一次提现时间
		String limitDay = Env.getVal("mem.cashout.limit.day");
		if (!StringUtils.isEmpty(limitDay)) {
			Date lastCashoutDate = chargeOrderService.addDate(cashoutOrder.getCreateTime(), Integer.parseInt(limitDay));
			memInfo.setLastCashoutDate(lastCashoutDate);
			memService.updateMemInfo(memInfo);
		}

		memService.updateMemCashInfo(cashoutInfo.getMemId(), cashoutInfo);
	}
	
	public void funancialManageList(Page page) {
		List<CashoutOrder> cashoutOrderList = cashoutOrderMapper.funancialManageList(page);
		page.setData(cashoutOrderList);
	}

	public void deleteFunancialManage(List<CashoutOrder> cashoutOrderList) {
		List<String> idList = new ArrayList<>();
		cashoutOrderList.stream()
				.forEach(item -> {
					idList.add(item.getOrderId());
				});
		int i = cashoutOrderMapper.deleteFunancialManage(idList);
	}

	public void getCustomerList(Page page) {
		// 获取列表table数据
		List<CashoutOrder> cashoutOrderList = cashoutOrderMapper.getCustomerList(page);
		page.setData(cashoutOrderList);
		// 获取各个状态的订单数
		Map<String,Object> orderStatusMap = cashoutOrderMapper.getOrderStatus();
		page.setParamObj(orderStatusMap);
	}

	public void updateCustomer(CashoutOrder cashoutOrder) {
		cashoutOrderMapper.updateByPrimaryKeySelective(cashoutOrder);
	}

	@Transactional
	public void rejectedCustomer(CashoutOrder cashoutOrder) {
		// 根据会员ID获取会员账户
		MemAccount account = memAccountMapper.queryMemAccountByMemId(cashoutOrder.getMemId());
		if (account == null) {
			throw new BusinessException(MemCode.MEM_IS_NOT_PROXY, "this mem is not proxy, memId =" + cashoutOrder.getMemId());
		}

		// 获取该提现订单的提现金额
		BigDecimal cashMoney = cashoutOrder.getCashMoney();
		// 账户余额 + 提现金额
		BigDecimal accountMoney = account.getAccountMoney().add(cashMoney);
		//修改账户表账户余额
		account.setAccountMoney(accountMoney);
		account.setUpdateTime(new Date());

		memAccountMapper.updateByPrimaryKeySelective(account);

		//新增流水表流水
		MemAccountRecord memAccountRecord = new MemAccountRecord();

		memAccountRecord.setAccountNo(account.getAccountNo());
		//type:reason 提现驳回
		memAccountRecord.setRecordType(AccRecordTypeEum.reason.getCode());
		//提现属于支出流水 0：收入 1：支出
		memAccountRecord.setRecordWay((byte)0);
		memAccountRecord.setRecordStatus(FlowStatusEum.success.getStatus());
		memAccountRecord.setOrderNo(cashoutOrder.getOrderNo());
		memAccountRecord.setChangeMoney(cashMoney);
		memAccountRecord.setBalanceMoney(accountMoney);
		memAccountRecord.setRemark("");
		memAccountRecord.setMemId(cashoutOrder.getMemId());
		memAccountRecord.setMemPhone(cashoutOrder.getMemPhone());
		memAccountRecord.setMemName(cashoutOrder.getMemName());
		memAccountRecord.setCreateTime(new Date());
		memAccountRecord.setUpdateTime(memAccountRecord.getCreateTime());

		memAccountRecordMapper.insert(memAccountRecord);

		// 修改订单状态
		cashoutOrder.setOrderStatus("3");
		cashoutOrderMapper.updateByPrimaryKeySelective(cashoutOrder);
	}

	/**
	 * 提现验证
	 * @param memId
	 */
	public void cashoutCheck(String memId){
		// 获取允许提现时间
		/*MemInfo memInfo = memService.queryMemById(memId);

		if (memInfo.getLastCashoutDate() != null) {
			if (memInfo.getLastCashoutDate().getTime() > new Date().getTime()){
				throw new BusinessException(OrderCode.CASHOUT_DATE_ERREY, "cashout date errey, cashoutDate =" + memInfo.getLastCashoutDate());
			}
		}*/
		
		String day = DateUtils.getCurrYMD("dd");
		
		//如果不是01号或者 15号就不能提现
		if (!"01".equals(day) && !"15".equals(day)) {
			throw new BusinessException(OrderCode.CASHOUT_DATE_ERREY, "cashout only 1 day or 15 day");
		}
		
	}

	public void getCashoutOrderPage(Page page) {
		List<Map<String, Object>> orderList = cashoutOrderMapper.getCashoutOrderPage(page);
		page.setData(orderList);
	}
}
