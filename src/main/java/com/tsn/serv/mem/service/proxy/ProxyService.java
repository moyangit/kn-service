package com.tsn.serv.mem.service.proxy;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tsn.common.utils.exception.BusinessException;
import com.tsn.common.utils.utils.cons.ResultCode;
import com.tsn.common.utils.utils.tools.time.DateUtils;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.common.utils.web.utils.gener.SnowFlakeManager;
import com.tsn.serv.common.code.mem.MemCode;
import com.tsn.serv.common.code.order.OrderCode;
import com.tsn.serv.common.enm.charge.ChargeTypeEum;
import com.tsn.serv.common.enm.comm.EnableStatus;
import com.tsn.serv.common.enm.comm.FlowStatusEum;
import com.tsn.serv.common.enm.id.GenIDEnum;
import com.tsn.serv.common.enm.mem.AccRecordTypeEum;
import com.tsn.serv.common.enm.mem.MemInvitorTypeEum;
import com.tsn.serv.common.enm.mem.MemTypeEum;
import com.tsn.serv.common.enm.order.OrderStatusEum;
import com.tsn.serv.common.enm.order.PayTypeEum;
import com.tsn.serv.mem.entity.account.MemAccount;
import com.tsn.serv.mem.entity.account.MemAccountRecord;
import com.tsn.serv.mem.entity.charge.MemCharge;
import com.tsn.serv.mem.entity.mem.MemInfo;
import com.tsn.serv.mem.entity.order.ChargeOrder;
import com.tsn.serv.mem.entity.proxy.ProxyInfo;
import com.tsn.serv.mem.entity.reqData.ProxyBingdingDto;
import com.tsn.serv.mem.entity.reqData.ProxyRechargeDto;
import com.tsn.serv.mem.mapper.account.MemAccountMapper;
import com.tsn.serv.mem.mapper.account.MemAccountRecordMapper;
import com.tsn.serv.mem.mapper.charge.MemChargeMapper;
import com.tsn.serv.mem.mapper.flow.FlowLimitMapper;
import com.tsn.serv.mem.mapper.mem.MemInfoMapper;
import com.tsn.serv.mem.mapper.order.ChargeOrderMapper;
import com.tsn.serv.mem.mapper.proxy.ProxyInfoMapper;
import com.tsn.serv.mem.service.mem.MemActiviService;
import com.tsn.serv.mem.service.order.ChargeOrderService;

@Service
public class ProxyService {

    @Autowired
    private MemAccountMapper accountMapper;

    @Autowired
    private MemInfoMapper memInfoMapper;

    @Autowired
    private MemAccountRecordMapper accountRecordMapper;

    @Autowired
    private ChargeOrderMapper chargeOrderMapper;

    @Autowired
    private MemChargeMapper memChargeMapper;

    @Autowired
    private MemAccountRecordMapper memAccountRecordMapper;

    @Autowired
    private ChargeOrderService chargeOrderService;

    @Autowired
    private FlowLimitMapper flowLimitMapper;

    @Autowired
    private ProxyInfoMapper proxyInfoMapper;
    
    @Autowired
    private MemActiviService memActiviService;

    private SnowFlakeManager snowFlakeManager = SnowFlakeManager.build();

    /**
     * 根据ID查询代理账户余额
     * @param memId
     * @return
     */
    public MemAccount getAccountMoney(String memId) {
        return accountMapper.queryMemAccountByMemId(memId);
    }

    public void getTableList(Page page) {
        Map<String, Object> requestMap = JSONObject.parseObject(JSON.toJSONString(page.getParamObj()));
        // 根据tableType获取对应的表数据
        String tableType = requestMap.get("tableType").toString();

        if (tableType.equals("0")){// 0：邀请记录
            List<MemInfo> memInfoList = memInfoMapper.getInvitationRecord(page);
            page.setData(memInfoList);
        } else if (tableType.equals("1")){// 1:用户列表
            List<MemInfo> memInfoList = memInfoMapper.getInvitationRecord(page);
            page.setData(memInfoList);
        } else if (tableType.equals("2")){// 2:返利记录
            List<MemAccountRecord> accountRecordList = accountRecordMapper.getPcAccRecord(page);
            page.setData(accountRecordList);
        } else if (tableType.equals("3")){// 3:充值记录
            List<ChargeOrder> chargeOrderList = chargeOrderMapper.getChargeOrderByProxy(page);
            page.setData(chargeOrderList);
        } else if (tableType.equals("4")){// 4:用户绑定
            // 查询二十四小时内，没有被邀请的试用会员
            List<MemInfo> memInfoList = memInfoMapper.getMemBinding(page);
            page.setData(memInfoList);
        }

    }

    public void updateRemarks(MemInfo memInfo) {
        memInfoMapper.updateByPrimaryKeySelective(memInfo);
    }

    public List<MemCharge> getChargeType(String memType) {
        return memChargeMapper.selectMemChargeByMemType(memType);
    }

    @Transactional
    public void recharge(ProxyRechargeDto proxyRechargeDto) {
        // 查询当前代理余额
        MemAccount memAccount = accountMapper.queryMemAccountByMemId(proxyRechargeDto.getProxyMemId());
        // 查询当前代理信息
        MemInfo proxyMemInfo = memInfoMapper.selectByPrimaryKey(proxyRechargeDto.getProxyMemId());
        
        //如果代理不是代理就不走下面
        if (!"1".equals(proxyMemInfo.getIsProxy())) {
        	throw new BusinessException(MemCode.MEM_IS_NOT_PROXY, "this mem is not proxy");
        }
        
        // 查询被充值会员信息
        MemInfo rechargeMemInfo = memInfoMapper.selectByPrimaryKey(proxyRechargeDto.getRechargeMemId());
        // 根据被充值会员类型和资费类型获取资费信息
        MemCharge memCharge = memChargeMapper.selectMemChargeOneByType(rechargeMemInfo.getMemType(), proxyRechargeDto.getChargeType());
        // 计算折扣后的资费金额
        BigDecimal FinalPrice =  memCharge.getChargeMoney().multiply(BigDecimal.valueOf(memCharge.getDiscount() * 0.01)).setScale(2,BigDecimal.ROUND_FLOOR);
        // 判断当前代理账户是否为空
        if (memAccount == null) {
            throw new BusinessException(MemCode.MEM_IS_NOT_EXISTS, "this mem is not proxy");
        }
        // 判断当前代理余额是否够充值
        if (memAccount.getAccountMoney().compareTo(FinalPrice) < 0) {
            throw new BusinessException(OrderCode.ORDER_MONEY_NOMATCH_ACC, "cash money > account money！！");
        }
        // 当被充值用户为代理时，不允许充值
        if (rechargeMemInfo.getIsProxy().equals("1")){
            throw new BusinessException(MemCode.MEM_IS_NOT_PROXY, "this mem is not proxy");
        }

        // 账户余额 - 资费金额
        BigDecimal accountMoney = memAccount.getAccountMoney().subtract(FinalPrice);

        // 生成订单信息
        ChargeOrder chargeOrder = new ChargeOrder();
        String orderNo = snowFlakeManager.create(GenIDEnum.BALANCE_ORDER_NO.name()).getIdByPrefix(GenIDEnum.BALANCE_ORDER_NO.getPreFix());
        chargeOrder.setOrderNo(orderNo);
        // 账户余额充值:agent_ba
        chargeOrder.setPayType(PayTypeEum.agent_ba.name());
        chargeOrder.setCreateTime(new Date());
        chargeOrder.setUpdateTime(new Date());
        // 订单状态:成功:pay_success
        chargeOrder.setOrderStatus(OrderStatusEum.pay_success.getStatus());
        chargeOrder.setMemBeforeSuspenDate(rechargeMemInfo.getSuspenDate());
        chargeOrder.setChargeId(memCharge.getChargeId());
        chargeOrder.setChargePrice(memCharge.getChargeMoney());
        chargeOrder.setChargeType(memCharge.getChargeType());

        ChargeTypeEum chargeTypeEum = ChargeTypeEum.getChargeTypeEnum(memCharge.getChargeType());

        chargeOrder.setSubject(chargeTypeEum.getDetail());
        chargeOrder.setMemId(rechargeMemInfo.getMemId());
        chargeOrder.setMemName(rechargeMemInfo.getMemNickName());
        chargeOrder.setMemPhone(rechargeMemInfo.getMemPhone());
        chargeOrder.setMemRealName(rechargeMemInfo.getMemReallyName());
        chargeOrder.setMemType(rechargeMemInfo.getMemType());

        //原价
        chargeOrder.setCostPrice(memCharge.getChargeMoney());
        //折扣
        chargeOrder.setDiscount(memCharge.getDiscount());
        //最终价
        chargeOrder.setFinalPrice(FinalPrice);

        // 查询被充值代理的上级邀请人
		
		//ProxyInfo proxyInfo = proxyInfoMapper.selectProxyAndGroupByProxyId(memInfo.getInviterUserId());
		ProxyInfo proxyInfo = proxyInfoMapper.selectByPrimaryKey(proxyMemInfo.getMemId());
		
		//如果是父级用户是代理
		if (proxyInfo != null && proxyInfo.getProxyLvl() != null) {
			//throw new BusinessException(ResultCode.UNKNOW_ERROR, "selectProxyAndGroupByProxyId result proxyInfo is null");
			//String rebateConfig = proxyInfo.getRebateConfig();
			//@SuppressWarnings("unchecked")
			//Map<String, String> rebateConfigMap = JsonUtils.jsonToPojo(rebateConfig, Map.class);
			chargeOrder.setRebateStatus(FlowStatusEum.create.getStatus());
			chargeOrder.setRebateUserId(proxyMemInfo.getMemId());
			chargeOrder.setRebateUserType(MemInvitorTypeEum.mem.name());
			chargeOrder.setRebateUserName(proxyMemInfo.getMemNickName());
			chargeOrder.setRebateUserPhone(proxyMemInfo.getMemPhone());
			// 默认返利等级两级,这里之前是2
			chargeOrder.setRebateLvl(1);
			chargeOrder.setRebateStatus(FlowStatusEum.create.getStatus());
			
			/*String rebate = rebateConfigMap.get("lvl" + chargeOrder.getRebateLvl());
			int rebateValue = Integer.parseInt(rebate);*/
			int rebateValue = Integer.parseInt(proxyInfo.getProxyLvl());
			chargeOrder.setRebate(rebateValue);
		}

        chargeOrderMapper.insert(chargeOrder);
        
        //---------------插入订单结束------------------------

        // 生成流水
        MemAccountRecord memAccountRecord = new MemAccountRecord();

        memAccountRecord.setAccountNo(memAccount.getAccountNo());
        //type:代理余额充值
        memAccountRecord.setRecordType(AccRecordTypeEum.recharge.getCode());
        //提现属于支出流水 0：收入 1：支出
        memAccountRecord.setRecordWay((byte)1);
        memAccountRecord.setRecordStatus(FlowStatusEum.success.getStatus());
        // 生成订单号
        memAccountRecord.setOrderNo(orderNo);
        memAccountRecord.setChangeMoney(FinalPrice);
        memAccountRecord.setBalanceMoney(accountMoney);
        memAccountRecord.setRemark("");

        memAccountRecord.setMemId(proxyMemInfo.getMemId());
        memAccountRecord.setMemPhone(proxyMemInfo.getMemPhone());
        memAccountRecord.setMemName(proxyMemInfo.getMemNickName());
        memAccountRecord.setCreateTime(new Date());
        memAccountRecord.setUpdateTime(memAccountRecord.getCreateTime());

        //更新当前代理账户余额
        memAccount.setAccountMoney(accountMoney);
        accountMapper.updateByPrimaryKeySelective(memAccount);
        memAccountRecordMapper.insert(memAccountRecord);
        
        // -----------生成代理商账户流水 结束----------------

        // 计算返利
        memActiviService.operaOrderRabate(chargeOrder.getOrderNo());

        // 更新用户会员到期时间
        int monthNum = getMonthNum(memCharge.getChargeType());

        //如果是第一次
        if (rechargeMemInfo.getFirstRechargeDate() == null) {
            rechargeMemInfo.setFirstRechargeDate(new Date());
            rechargeMemInfo.setLastRechargeDate(rechargeMemInfo.getFirstRechargeDate());
            //不管还有没有剩余时间，从充值时间开始算起
            //memInfo.setSuspenDate(DateUtils.getCurrDateAddMonth(new Date(), monthNum));
            //Date date = chargeOrderService.addDate(new Date(), monthNum * 30);
            rechargeMemInfo.setSuspenDate(DateUtils.getCurrDateAddMinTime(new Date(), monthNum * 30 * 24 * 60));
            //rechargeMemInfo.setSuspenDate(date);

        }else {//如果不是第一次，判断是续费还是充值（中间有断层）

            //如果过期，更新lastRechargteDate，
            if (rechargeMemInfo.isExpire()) {
                rechargeMemInfo.setLastRechargeDate(new Date());
                //memInfo.setSuspenDate(DateUtils.getCurrDateAddMonth(new Date(), monthNum));
                //Date date = chargeOrderService.addDate(new Date(), monthNum * 30);
                
                rechargeMemInfo.setSuspenDate(DateUtils.getCurrDateAddMinTime(new Date(), monthNum * 30 * 24 * 60));
            }else {
                //memInfo.setSuspenDate(DateUtils.getCurrDateAddMonth(memInfo.getSuspenDate(), monthNum));
                //Date date = chargeOrderService.addDate(rechargeMemInfo.getSuspenDate(), monthNum * 30);
                rechargeMemInfo.setSuspenDate(DateUtils.getCurrDateAddMinTime(rechargeMemInfo.getSuspenDate(), monthNum * 30 * 24 * 60));
                //rechargeMemInfo.setSuspenDate(date);
            }
        }

        //如果充值之前是试用会员,这时要重置流量计算方式，重新计算
        /*if (MemTypeEum.trial_mem.getCode().equals(rechargeMemInfo.getMemType())) {
            FlowLimit flowLimit = new FlowLimit();
            flowLimit.setMemId(rechargeMemInfo.getMemId());
            flowLimit.setMemType(MemTypeEum.general_mem.getCode());
            flowLimit.setCyStartTime(rechargeMemInfo.getFirstRechargeDate());
            flowLimit.setCurrCyStartTime(flowLimit.getCyStartTime());
            flowLimit.setCurrCyEndTime(DateUtils.getCurrDateAddMonth(flowLimit.getCurrCyStartTime(), 1));
            flowLimit.setCyUseUpFlow(0l);
            flowLimit.setCyUseDownFlow(0l);
            flowLimit.setCyUseTotalFlow(0l);
            flowLimitMapper.updateByPrimaryKeySelective(flowLimit);
        }*/

        //会员类型
        rechargeMemInfo.setMemType(MemTypeEum.general_mem.getCode());


        //如果是永久则给用户代理属性
        //boolean memProxyIsRebate = true;
        /*if (ChargeTypeEum.forever.getType().equals(memCharge.getChargeType())) {
            rechargeMemInfo.setIsProxy("1");

            //如果是永久用户，同时添加代理商表
            ProxyInfo proxyInfo = new ProxyInfo();
            proxyInfo.setCreateTime(new Date());
            proxyInfo.setMemType(rechargeMemInfo.getMemType());
            proxyInfo.setProxyId(rechargeMemInfo.getMemId());
            proxyInfo.setProxyCard(rechargeMemInfo.getMemCard());
            proxyInfo.setProxyGroupid("1");
            proxyInfo.setProxyName(rechargeMemInfo.getMemName());
            proxyInfo.setProxyNickName(rechargeMemInfo.getMemNickName());
            proxyInfo.setProxyPhone(rechargeMemInfo.getMemPhone());
            proxyInfo.setProxyReallyName(rechargeMemInfo.getMemReallyName());
            proxyInfo.setUpdateTime(proxyInfo.getCreateTime());

            //添加代理商用户表
            proxyInfoMapper.insert(proxyInfo);

            //如果购买的订单是代理上则添加账户
            MemAccount rechargeMemAccount = new MemAccount();
            rechargeMemAccount.setMemId(rechargeMemInfo.getMemId());
            addAccount(rechargeMemAccount);

        }*/

        memInfoMapper.updateByPrimaryKeySelective(rechargeMemInfo);
    }

    private int getMonthNum(String chargeType) {

        int monthNum = 1;

        if (ChargeTypeEum.month.getType().equals(chargeType)){
            monthNum = 1;
        }else if (ChargeTypeEum.quarter.getType().equals(chargeType)){
            monthNum = 3;
        }else if (ChargeTypeEum.halfYear.getType().equals(chargeType)){
            monthNum = 6;
        }else if (ChargeTypeEum.year.getType().equals(chargeType)){
            monthNum = 12;
        }else if (ChargeTypeEum.forever.getType().equals(chargeType)){
            monthNum = 12 * 50;
        }else {
            throw new BusinessException(ResultCode.UNKNOW_ERROR, "getMonthNum chargeType do not matched");
        }

        return monthNum;
    }

    private void addAccount(MemAccount memAccount) {

        if (StringUtils.isEmpty(memAccount.getMemId())) {
            throw new BusinessException(ResultCode.DATABASE_OPERATIOIN_IS_ERROR, "insert memAccount memId is not null");
        }

        memAccount.setAccountNo(snowFlakeManager.create(GenIDEnum.ACCOUNT_NO.name()).getIdByPrefix(GenIDEnum.ACCOUNT_NO.getPreFix()));
        memAccount.setAccountMoney(new BigDecimal(0));
        memAccount.setCreateTime(new Date());
        memAccount.setAccountStatus(String.valueOf(EnableStatus.enable.getCode()));

        accountMapper.insert(memAccount);

    }

    public void bingding(ProxyBingdingDto proxyBingdingDto) {
        MemInfo proxyMemInfo = memInfoMapper.selectByPrimaryKey(proxyBingdingDto.getProxyMemId());
        MemInfo bingdingMemInfo = memInfoMapper.selectByPrimaryKey(proxyBingdingDto.getBdMemId());

        bingdingMemInfo.setInviterUserId(proxyMemInfo.getMemId());
        bingdingMemInfo.setInviterUserType(MemInvitorTypeEum.mem.name());
        bingdingMemInfo.setInviterTime(new Date());

        memInfoMapper.updateByPrimaryKeySelective(bingdingMemInfo);
    }
}
