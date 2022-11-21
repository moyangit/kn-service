/*package com.tsn.serv.mem.service.feign;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.tsn.common.utils.exception.BusinessException;
import com.tsn.common.utils.utils.cons.ResultCode;
import com.tsn.common.utils.utils.tools.http.HttpPostReq;
import com.tsn.common.utils.utils.tools.json.JsonUtils;
import com.tsn.common.utils.web.entity.Response;


@Service
public class PublicFeignService {

	@Autowired
	private IPublicFeignService iPublicFeignService;
	
	
	@SuppressWarnings("unchecked")
	public List<String> uploadFiles(MultipartFile files) {
		List<String> strings = new ArrayList<String>();
		Map<String, Object> requestMap = new HashMap<String, Object>();
        try {
            requestMap.put("files", files);
            String uri = "http://192.168.0.66:2100/pub_api/upload/one";
            StringEntity entity = new StringEntity(JsonUtils.objectToJson(requestMap), "utf-8");//解决中文乱码问题
            entity.setContentEncoding("UTF-8");
            entity.setContentType("multipart/form-data");
            HttpPostReq req = new HttpPostReq(uri, null, entity);
            String result = req.excuteReturnStr();
            Response<List<String>> response = JsonUtils.jsonToPojo(result, Response.class);
            
            if (!ResultCode.OPERATION_IS_SUCCESS.equals(response.getCode())) {
    			throw new BusinessException(ResultCode.REMOTE_CALL_ERROR, "remote feign v-public uploadFiles error");
    		}
    		
    		strings = (List<String>) response.getData();

        } catch (Exception e) {
            e.printStackTrace();
        }
		
		return strings;
	}
	
	@FeignClient(name = "hb-service", url="${feign.client.url.addr}")
	public interface IPublicFeignService{

		@RequestMapping(value="upload/one",method=RequestMethod.POST)
		Response<List<String>> uploadFiles(@RequestParam("file") MultipartFile file);
		
	}
	
}
*/