/**
 * @Title: RCSCloudAPITest.java
 * @Package com.rcscloud.rest.test
 * @Description: TODO
 * Copyright: Copyright (c) 2015
 * Company:江苏美圣信息技术有限公司
 *
 * @author HP
 * @date 2016-3-2 上午10:31:13
 * @version V1.0
 */
package com.tsn.serv.common.utils.msg;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * @ClassName: RCSCloudAPITest
 * @Description: TODO
 * @author Peng_Hu
 * @date 2016-3-2 上午10:31:13
 *
 */
public class RCSCloudAPI {
	
	//以下为接口调用的封装 ，可以直接使用
	/**
	 * 发送模板短信
	 * @param mobile 手机号码
	 * @param content 参数值，多个参数以“||”隔开   如:@1@=HY001||@2@=3281
	 * @param extno 自定义扩展码，建议1-4位，需申请开通自定义扩展
	 * @return json字符串,详细描述请参考接口文档 
	 * 
	 * String
	 */
	public String sendTplSms(String mobile,String content,String extno,String ancountSid,String ancountApiKey,String charsetUtf8,String httpUrl,String tplId){
		DefaultHttpClient httpclient = new DefaultHttpClient();
		String resultJson = "";
		try {
			//签名:Md5(sid+key+tplid+mobile+content)
			StringBuilder signStr = new StringBuilder();
			signStr.append(ancountSid).append(ancountApiKey).append(tplId).append(mobile).append(content);
                        /**
			 * 注意：发送短信返回1005错误码时，主要由于中文字符转码错误造成签名鉴权失败
			 * 在实际开发中采用采用UTF-8或者GB2312转码
			 * **/
			//String sign = md5Digest(changeCharset(signStr.toString(), "GB2312"));
			String sign = md5Digest(changeCharset(signStr.toString(), "UTF-8"),charsetUtf8);

			//创建HttpPost请求
			HttpPost httppost = new HttpPost(httpUrl +"/sms/sendtplsms.json");//?sid="+ACCOUNT_SID+"&sign="+sign
			//构建form
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("sid", ancountSid));
			nvps.add(new BasicNameValuePair("sign", sign));
			nvps.add(new BasicNameValuePair("tplid", tplId));
			nvps.add(new BasicNameValuePair("mobile", mobile));
			nvps.add(new BasicNameValuePair("content", content));
			nvps.add(new BasicNameValuePair("extno", extno));
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nvps,charsetUtf8);
			httppost.setEntity(entity);
			
			//设置请求表头信息，POST请求必须采用application/x-www-form-urlencoded否则提示415错误
			httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");
			httppost.setHeader("Content-Encoding", charsetUtf8);
			//执行请求
			HttpResponse response = httpclient.execute(httppost);
			//获取响应Entity
			HttpEntity httpEntity = response.getEntity();
			//返回JSON字符串格式，用户根据实际业务进行解析处理
			if (httpEntity != null)
				resultJson = EntityUtils.toString(httpEntity, charsetUtf8);
			
			EntityUtils.consume(entity);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}  finally {
			if (httpclient != null)
				httpclient.getConnectionManager().shutdown();
		}
		System.out.println("resultJson=" +resultJson);
		return resultJson;
	}
	
	/**
	 * MD5算法
	 * @param src 
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 * String
	 */
	public String md5Digest(String src, String charsetUtf8) throws NoSuchAlgorithmException, UnsupportedEncodingException{
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] b = md.digest(src.getBytes(charsetUtf8));
		return byte2HexStr(b);
	}

	private static String byte2HexStr(byte[] b){
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < b.length; ++i) {
			String s = Integer.toHexString(b[i] & 0xFF);
			if (s.length() == 1)
				sb.append("0");
			sb.append(s.toUpperCase());
		}
		return sb.toString();
	}
	
	/**
	 * 字符编码转换
	 * @param str
	 * @param newCharset
	 * @return
	 * @throws UnsupportedEncodingException
	 * String
	 */
	public static String changeCharset(String str, String newCharset)
			throws UnsupportedEncodingException {
		if (str != null) {
			//用默认字符编码解码字符串。
			byte[] bs = str.getBytes();
			//用新的字符编码生成字符串
			return new String(bs, newCharset);
		}
		return null;
	}
}


