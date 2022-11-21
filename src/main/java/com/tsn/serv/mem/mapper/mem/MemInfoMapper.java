package com.tsn.serv.mem.mapper.mem;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.tsn.common.utils.web.core.IBaseMapper;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.serv.mem.entity.mem.MemInfo;
import com.tsn.serv.mem.entity.reqData.UserDataStatisDto;

public interface MemInfoMapper extends IBaseMapper<MemInfo>{
	
	MemInfo getMemInfoByDeviceNo(String deviceNo);
	
	MemInfo selectAllByPrimaryKey(String memId);
	
	MemInfo selectMemByPhone(String phone);
	
	MemInfo selectMemByEmail(String memEmail);
	
	MemInfo selectParentByMemId(String memId);
	
	MemInfo selectMemByInviCode(String inviCode);

	Map<String,Object> getChartData(UserDataStatisDto userDataStatisList);

	List<Map<String,Object>> getChargeData(UserDataStatisDto userDataStatisDto);

	List<Map<String,Object>> getInvitationData(UserDataStatisDto userDataStatisDto);

	List<Map<String,Object>> getUserDataList(Page page);
	
	List<Map<String,Object>> getUserList(Page page);

    List<Map<String,Object>> getOneProxyMemList(Page page);

	List<Map<String,Object>> getTwoProxyMemList(Page page);

	List<Map<String,Object>> getSubordinateProxy(List<String> firstIdList);

	List<MemInfo> getSubordinateMember(List<String> firstIdList);

	void updateMemByPhone(MemInfo memInfo);

	String selectSumMoneyByMemId(@Param("memId") String memId);

    MemInfo queryProxyByPhone(String phone);

    List<MemInfo> getInvitationRecord(Page page);
    
    /**
     * 通过父级Id获取下级数量
     * @param parentMemId
     * @return
     */
    int getInvitationUserCount(String memId);
    
    /**
     * 通过父级Id下级所有充值用户的数据
     * @param parentMemId
     * @return
     */
    int getInvitationMemCount(String memId);

    List<MemInfo> getMemBinding(Page page);

    List<Map> todayMemInfoList(Page page);

    Map<String,Object> getTodayAddMem(String selectDate);

	Map<String,Object> getTodayAddChargeMem(String selectDate);

	Map<String,Object> getTodayAddCharge(String selectDate);

    List<Map<String,Object>> getMemLineChart(String monthDate);

	List<Map<String,Object>> getMemLineChartTo(String monthDate);

	List<String> getMemBarChart(String yearDate);

    List<Map<String,Object>> getMemExpire();

	void updateSuspenDate(MemInfo memInfo);

	int updateBeInvitedByMemId(MemInfo memInfo);

	void updateSuspenDateMinute(MemInfo memInfo);

	List<Map<String,Object>> recentUseMem(Page page);
	
	/**
	 * 根据邀请ID和小时时间段查询用户列表
	 * @param inviterUserId
	 * @param YMDHTime
	 * @return
	 */
	List<MemInfo> getUserListByInvitCodeAndHour(@Param("inviterUserId") String inviterUserId, @Param("YMDHTime") String YMDHTime);
	
	Map<String, Object> getUserStatisDataTodayBefore(@Param("todayTime") String todayTime);
	
	Map<String, Object> getUserStatisDataToday(@Param("todayTime") String todayTime);
	
	List<Map<String,Object>> getUserRegStatisGroupByDay(String monthDate);
	
	List<Map<String,Object>> queryProxyAllPage(Page page);
	
	List<Map<String,Object>> queryMemInvitAndProxyListByIds(@Param("currTime") String currTime, @Param("ids") List<String> ids);
	
	List<Map<String,Object>> queryMemInvitAndProxyListByTime(@Param("invitStartTime") String invitStartTime, @Param("invitEndTime") String invitEndTime, @Param("regTime") String regTime, @Param("memPhone") String memPhone, @Param("isProxy") String isProxy, @Param("memType") String memType);
}