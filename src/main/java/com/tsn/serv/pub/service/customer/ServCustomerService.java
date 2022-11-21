package com.tsn.serv.pub.service.customer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.tsn.common.utils.utils.tools.json.JsonUtils;
import com.tsn.common.utils.web.utils.gener.SnowFlakeManager;
import com.tsn.serv.common.enm.id.GenIDEnum;
import com.tsn.serv.pub.entity.customer.ServiceCustomer;
import com.tsn.serv.pub.entity.customer.ServiceCustomerRefUser;
import com.tsn.serv.pub.mapper.customer.ServiceCustomerMapper;
import com.tsn.serv.pub.mapper.customer.ServiceCustomerRefUserMapper;

@Service
public class ServCustomerService {
	
	@Autowired
	private ServiceCustomerMapper servCustomerMapper;
	
	@Autowired
	private ServiceCustomerRefUserMapper serviceCustomerRefUserMapper;
	
	private String cusType = "wx";
	
	public List<ServiceCustomer> getServCustomerList() {
		return servCustomerMapper.selectList();
	}
	
	public void addServCustomer(ServiceCustomer serviceCustomer) {
		String id = SnowFlakeManager.build().create(GenIDEnum.DEF.name()).getIdByPrefix(GenIDEnum.DEF.getPreFix());
		serviceCustomer.setId(Long.parseLong(id));
		serviceCustomer.setCreateTime(new Date());
		serviceCustomer.setUpdateTime(serviceCustomer.getCreateTime());
		serviceCustomer.setStatus((byte) 0);
		servCustomerMapper.insert(serviceCustomer);
	}
	
	public void updateServCustomer(ServiceCustomer serviceCustomer) {
		serviceCustomer.setUpdateTime(new Date());
		servCustomerMapper.updateByPrimaryKeySelective(serviceCustomer);
	}
	
	public void deleteServCustomerById(long id) {
		servCustomerMapper.deleteByPrimaryKey(id);
	}
	
	public List<ServiceCustomer> getServCustomerListByCusType(String userId) {
		
		List<ServiceCustomer> result = new ArrayList<ServiceCustomer>();
		
		if (StringUtils.isEmpty(userId)) {
			//目前只取微信
			List<ServiceCustomer> customerList = servCustomerMapper.selectListActiveStatus(cusType);
			
			if (customerList == null || customerList.isEmpty()) {
				return new ArrayList<ServiceCustomer>();
			}
			
			//目前只取一个
			ServiceCustomer serviceCustomer = customerList.get(new Random().nextInt(customerList.size()));
			
			result.add(serviceCustomer);
			return result;
		}
		
		ServiceCustomerRefUser serviceCustomerRefUser = serviceCustomerRefUserMapper.selectByPrimaryKey(userId);
		
		if (serviceCustomerRefUser == null) {
			
			//目前只取微信
			List<ServiceCustomer> customerList = servCustomerMapper.selectListActiveStatus(cusType);
			
			if (customerList == null || customerList.isEmpty()) {
				return new ArrayList<ServiceCustomer>();
			}
			
			//目前只取一个
			ServiceCustomer serviceCustomer = customerList.get(new Random().nextInt(customerList.size()));
			
			ServiceCustomerRefUser serviceCustomerRefUserTemp = new ServiceCustomerRefUser();
			serviceCustomerRefUserTemp.setUserId(userId);
			serviceCustomerRefUserTemp.setCreateTime(new Date());
			serviceCustomerRefUserTemp.setUpdateTime(serviceCustomerRefUserTemp.getCreateTime());
			
			serviceCustomerRefUserTemp.setCusIds(JsonUtils.objectToJson(Stream.of(serviceCustomer.getId()).collect(Collectors.toList())));
			
			serviceCustomerRefUserMapper.insert(serviceCustomerRefUserTemp);
			
			result.add(serviceCustomer);
			return result;
		}
		
		
		String cusIds = serviceCustomerRefUser.getCusIds();
		List<Long> cusIdList = JsonUtils.jsonToList(cusIds, Long.class);
		
		//如果为空，则要更新
		if (cusIdList == null || cusIdList.isEmpty()) {
			
			//目前只取微信
			List<ServiceCustomer> customerList = servCustomerMapper.selectListActiveStatus(cusType);
			
			if (customerList == null || customerList.isEmpty()) {
				return new ArrayList<ServiceCustomer>();
			}
			
			//目前只取一个
			ServiceCustomer serviceCustomer = customerList.get(new Random().nextInt(customerList.size()));
			
			ServiceCustomerRefUser serviceCustomerRefUserTemp = new ServiceCustomerRefUser();
			serviceCustomerRefUserTemp.setUserId(userId);
			serviceCustomerRefUserTemp.setCreateTime(new Date());
			serviceCustomerRefUserTemp.setUpdateTime(serviceCustomerRefUserTemp.getCreateTime());
			
			serviceCustomerRefUserTemp.setCusIds(JsonUtils.objectToJson(Stream.of(serviceCustomer.getId()).collect(Collectors.toList())));
			
			serviceCustomerRefUserMapper.updateByPrimaryKeySelective(serviceCustomerRefUserTemp);
			
			result.add(serviceCustomer);
			
			return result;
		}
		
		//如果关联的Id查询有值直接返回，如果没有值，随机取一个更新
		ServiceCustomer tempServiceCustomer = servCustomerMapper.selectByPrimaryKey(cusIdList.get(0));
		
		if (tempServiceCustomer != null) {
			result.add(tempServiceCustomer);
			return result;
		}
		
		//目前只取微信
		List<ServiceCustomer> customerList = servCustomerMapper.selectListActiveStatus(cusType);
		
		if (customerList == null || customerList.isEmpty()) {
			return new ArrayList<ServiceCustomer>();
		}
		
		//目前只取一个
		ServiceCustomer serviceCustomer = customerList.get(new Random().nextInt(customerList.size()));
		
		ServiceCustomerRefUser serviceCustomerRefUserTemp = new ServiceCustomerRefUser();
		serviceCustomerRefUserTemp.setUserId(userId);
		serviceCustomerRefUserTemp.setCreateTime(new Date());
		serviceCustomerRefUserTemp.setUpdateTime(serviceCustomerRefUserTemp.getCreateTime());
		
		serviceCustomerRefUserTemp.setCusIds(JsonUtils.objectToJson(Stream.of(serviceCustomer.getId()).collect(Collectors.toList())));
		
		serviceCustomerRefUserMapper.updateByPrimaryKeySelective(serviceCustomerRefUserTemp);
		
		result.add(serviceCustomer);
		
		return result;
		
	}

}
