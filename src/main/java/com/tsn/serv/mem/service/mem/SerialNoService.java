package com.tsn.serv.mem.service.mem;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tsn.serv.mem.entity.mem.SerialNo;
import com.tsn.serv.mem.mapper.mem.SerialNoMapper;

/**
 * <p>
 * 用户留存统计 服务实现类
 * </p>
 *
 * @author Tang longsen
 * @since 2022-06-15
 */
@Service
public class SerialNoService extends ServiceImpl<SerialNoMapper, SerialNo>{
	
	private static Lock lock = new ReentrantLock();
	
	private static ArrayBlockingQueue<Integer> seriaQueue = new ArrayBlockingQueue<Integer>(50);
	
	private ExecutorService executor = Executors.newSingleThreadExecutor();
	
	private SerialNoService serialNoService;
	
	public SerialNoService() {
		this.serialNoService = this;
	} 
	
	public Integer getSerialNo() {
		
		
			
			Integer seriaNo = seriaQueue.poll();
			
			if (seriaNo == null) {
				try {
					
					lock.lock();
					
					QueryWrapper<SerialNo> queryWrapper = new QueryWrapper<SerialNo>();
					queryWrapper.orderByAsc("id");
					queryWrapper.last("limit 1");
					SerialNo serialNo = super.getOne(queryWrapper);
					super.removeById(serialNo.getId());
					
					executor.execute(new Runnable() {
						
						@Override
						public void run() {
							QueryWrapper<SerialNo> queryWrapper = new QueryWrapper<SerialNo>();
							queryWrapper.orderByAsc("id");
							queryWrapper.last("limit 30");
							List<SerialNo> serialNoList = serialNoService.list(queryWrapper);
							
							if ((50 - seriaQueue.size()) > 30) {
								for (SerialNo no : serialNoList) {
									seriaQueue.add(no.getSerialNo());
								}
							}
							
						}
					});
					
					return serialNo.getSerialNo();
					
				} finally {
					lock.unlock();
				}
				
			}else {
				
				QueryWrapper<SerialNo> queryWrapper = new QueryWrapper<SerialNo>();
				queryWrapper.eq("serial_no", seriaNo);
				super.remove(queryWrapper);
				
				if (seriaQueue.size() < 10) {
					executor.execute(new Runnable() {
						
						@Override
						public void run() {
							QueryWrapper<SerialNo> queryWrapper = new QueryWrapper<SerialNo>();
							queryWrapper.orderByAsc("id");
							queryWrapper.last("limit 30");
							List<SerialNo> serialNoList = serialNoService.list(queryWrapper);
							
							if ((50 - seriaQueue.size()) > 30) {
								for (SerialNo no : serialNoList) {
									seriaQueue.add(no.getSerialNo());
								}
							}
							
						}
					});
				}
				
				return seriaNo;
			}
			
		
		
	}
	
	public void testBatchInsertNo(List<SerialNo> serialNoList) {
		super.getBaseMapper().insertBatchSerialNo(serialNoList);
	}
	
}
