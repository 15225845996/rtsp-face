package com.zs.rtspface.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zs.rtspface.controller.FaceController;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * @Auther: zs
 * @Date: 2020/6/17 15:46
 * @Description:
 */
@Component
public class HttpUtils {

    @Value("${face.appid}")
    private String appId;
    @Value("${face.apikey}")
    private String apiKey;
    @Value("${face.secretkey}")
    private String secretKey;

    public static String APP_ID;
    public static String API_KEY;
    public static String SECRET_KEY;

    public static String FACE_TOEKN_KEY = "FACE_TOKEN";

    @PostConstruct
    public void init(){
        APP_ID = this.apiKey;
        API_KEY = this.apiKey;
        SECRET_KEY = this.secretKey;
    }


    private static HttpUtils instance = null;

    private HttpUtils()
    {
    }

    public synchronized static HttpUtils getInstance()
    {
        if (instance == null)
        {
            instance = new HttpUtils();
        }
        return instance;
    }

    public synchronized HttpClient getHttpClient(){
        HttpClient httpClient = HttpClientBuilder.create().build();
        return httpClient;
    }

    public HttpPost httpPost(String httpUrl){
        HttpPost httpPost = null;
        httpPost = new HttpPost(httpUrl);
        return httpPost;
    }



    public static String getFaceAuth(){
        String faceToken = FaceController.CacheMap.get(FACE_TOEKN_KEY);
        if(faceToken == null){
            // 获取token地址
            String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
            String getAccessTokenUrl = authHost
                    // 1. grant_type为固定参数
                    + "grant_type=client_credentials"
                    // 2. 官网获取的 API Key
                    + "&client_id=" + API_KEY
                    // 3. 官网获取的 Secret Key
                    + "&client_secret=" + SECRET_KEY;
            try {
                URL realUrl = new URL(getAccessTokenUrl);
                // 打开和URL之间的连接
                HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                // 获取所有响应头字段
                Map<String, List<String>> map = connection.getHeaderFields();
                // 遍历所有的响应头字段
                for (String key : map.keySet()) {
                    System.err.println(key + "--->" + map.get(key));
                }
                // 定义 BufferedReader输入流来读取URL的响应
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String result = "";
                String line;
                while ((line = in.readLine()) != null) {
                    result += line;
                }
                /**
                 * 返回结果示例
                 */
                System.err.println("result:" + result);
                JSONObject jsonObject = JSON.parseObject(result);
                faceToken = jsonObject.getString("access_token");
                int expires = Integer.parseInt(jsonObject.getString("expires_in"));
                FaceController.CacheMap.put(FACE_TOEKN_KEY,faceToken,expires);
            } catch (Exception e) {
                System.err.printf("获取token失败！");
                e.printStackTrace(System.err);
            }
        }
        return faceToken;
    }


    public static JSONObject faceSearch(String img,String group) {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/search";
        HashMap<String, Object> map = new HashMap<>();
        map.put("image_type","BASE64");
        map.put("group_id_list",group);
        map.put("image",img);
        map.put("access_token",getFaceAuth());
        /*map.put("access_token","24.97b32f07e3897d344883fbe1b8e9a30e.2592000.1595058903.282335-11562283");*/
        String result = sendPostByForm(url,map);
        JSONObject jsonObject = null;
        if(result != null){
            jsonObject = JSON.parseObject(result);
        }
        return jsonObject;
    }


    public static JSONObject addFace(String img,String id,String name,String group) {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/add";
        HashMap<String, Object> map = new HashMap<>();
        map.put("image_type","BASE64");
        map.put("group_id",group);
        map.put("image",img);
        map.put("user_id",id);
        map.put("user_info",name);
        map.put("access_token",getFaceAuth());
        /*map.put("access_token","24.97b32f07e3897d344883fbe1b8e9a30e.2592000.1595058903.282335-11562283");*/
        String result = sendPostByForm(url,map);
        JSONObject jsonObject = null;
        if(result != null){
            jsonObject = JSON.parseObject(result);
        }
        return jsonObject;
    }








    /**
     * 通过post方式调用http接口
     * @param url     url路径
     * @param jsonParam    json格式的参数
     * @return
     * @throws Exception
     */
    public static String sendPostByJson(String url, String jsonParam) {
        //声明返回结果
        String result = "";
        HttpEntity httpEntity = null;
        HttpResponse httpResponse = null;
        HttpClient httpClient = null;
        try {
            // 创建连接
            httpClient = HttpUtils.getInstance().getHttpClient();
            // 设置请求头和报文
            HttpPost httpPost = HttpUtils.getInstance().httpPost(url);
            // 设置报文和通讯格式
            StringEntity stringEntity = new StringEntity(jsonParam,"UTF-8");
            httpPost.setEntity(stringEntity);
            //执行发送，获取相应结果
            httpResponse = httpClient.execute(httpPost);
            httpEntity= httpResponse.getEntity();
            result = EntityUtils.toString(httpEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                EntityUtils.consume(httpEntity);
            } catch (IOException e) {
            }
        }
        return result;

    }


    /**
     * 通过post方式调用http接口
     * @param url     url路径
     * @param map    json格式的参数
     * @return
     * @throws Exception
     */
    public static String sendPostByForm(String url, Map<String,Object> map) {
        //声明返回结果
        String result = "";
        HttpEntity httpEntity = null;
        UrlEncodedFormEntity entity = null;
        HttpResponse httpResponse = null;
        HttpClient httpClient = null;
        try {
            // 创建连接
            httpClient = HttpUtils.getInstance().getHttpClient();
            // 设置请求头和报文
            HttpPost httpPost = HttpUtils.getInstance().httpPost(url);
            //设置参数
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            Iterator iterator = map.entrySet().iterator();
            while(iterator.hasNext()){
                Map.Entry<String,String> elem = (Map.Entry<String, String>) iterator.next();
                list.add(new BasicNameValuePair(elem.getKey(),elem.getValue()));
            }
            entity = new UrlEncodedFormEntity(list,"UTF-8");
            httpPost.setEntity(entity);
            //执行发送，获取相应结果
            httpResponse = httpClient.execute(httpPost);
            httpEntity= httpResponse.getEntity();
            result = EntityUtils.toString(httpEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                EntityUtils.consume(httpEntity);
            } catch (IOException e) {
            }
        }
        return result;

    }
}
