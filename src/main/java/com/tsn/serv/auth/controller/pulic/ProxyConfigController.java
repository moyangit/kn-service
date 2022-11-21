/*package com.tsn.serv.auth.controller.pulic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tsn.common.utils.utils.tools.json.JsonUtils;
import com.tsn.common.utils.web.entity.Response;
import com.tsn.common.utils.web.utils.env.Env;

@RestController
@RequestMapping("public/proxy/config")
public class ProxyConfigController {

	@GetMapping("def")
	public Response<?> getVersionForNo(){
		List<Map> objList = JsonUtils.jsonToList(Env.getVal("proxy.config"), Map.class);
		return Response.ok(objList);
	}

}
*/