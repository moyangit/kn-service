package com.vpn;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tsn.serv.mem.entity.mem.SerialNo;
import com.tsn.serv.mem.service.mem.SerialNoService;

/*package com.vpn;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

*//**
 * Unit test for simple App.
 *//*
public class AppTest 
{
    *//**
     * Rigorous Test :-)
     *//*
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }
}
*/

public class AppTest {
	
	 public static void main(String[] args) throws NoSuchAlgorithmException {
			
	    	SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
			
	    	for (int i = 0 ; i < 100; i++) {
	    		int a = secureRandom.nextInt(999999) + 1000000;
		    	
		    	System.out.println(a);
	    	}
	    	
	    	
		}
	 
	 /*@Component
	    public static class InitSql{
	    	
	    	@Autowired
	    	private SerialNoService serialNoService;
	    	
	    	@PostConstruct
	    	public void init(){
	    		
	    		
	    		try {
					
					SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
					
					for (int count = 0 ; count < 100; count ++) {
						
						List<SerialNo> dataList = new ArrayList<SerialNo>();
						for (int i = 0 ; i < 10000; i++) {
				    		int a = secureRandom.nextInt(999999) + 1000000;
				    		
				    		SerialNo serialNo = new SerialNo();
				    		serialNo.setSerialNo(a);
				    		dataList.add(serialNo);
					    	
				    	}
						
						serialNoService.testBatchInsertNo(dataList);
					}
					
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		
	    	}
	    	
	    }*/
	
}