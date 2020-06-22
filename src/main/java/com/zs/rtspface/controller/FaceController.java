package com.zs.rtspface.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zs.rtspface.utils.ExpiryMap;
import com.zs.rtspface.utils.HttpUtils;
import com.zs.rtspface.utils.ImageUtils;
import com.zs.rtspface.vo.FaceVo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.UUID;

/**
 * @Auther: zs
 * @Date: 2020/6/17 16:37
 * @Description:
 */
@RestController
@RequestMapping("/face")
public class FaceController {


    public static ExpiryMap<String, String> CacheMap = new ExpiryMap<>();// 简单缓存

    public static SimpleDateFormat sdf = new SimpleDateFormat("dd HH:mm:ss");

    @Value("${school}")
    private String schoolId;
    @Value("${strange.prefix}")
    private String strangePrefix;
    @Value("${face.score}")
    private Integer scoreNum;
    @Value("${cache.time}")
    private Integer cacheTime;

    @RequestMapping("/search1")
    public Object search1(HttpServletRequest request, FaceVo face){
        FaceVo result = null;
        if(face.getImg() != null){
            ImageUtils.Base64ToImage(face.getImg(),"C:\\Users\\zs\\Desktop\\img\\"+System.currentTimeMillis()+".png");
            result = searchFace(face.getImg(), false);
            if(-1 == result.getCode()){
                result = searchFace(face.getImg(), true);
            }
        }
        if(result == null){
            result = new FaceVo();
        }
        return result;
    }


    @RequestMapping("/search")
    public Object search(HttpServletRequest request, FaceVo face){
        String callback = request.getParameter("callback");
        FaceVo result = null;
        if(face.getImg() != null){
            ImageUtils.Base64ToImage(face.getImg(),"C:\\Users\\zs\\Desktop\\img\\"+System.currentTimeMillis()+".png");
            result = searchFace(face.getImg(), false);
            if(-1 == result.getCode()){
                result = searchFace(face.getImg(), true);
            }
        }
        if(result == null){
            result = new FaceVo();
        }
        return callback + "(" + JSON.toJSONString(result) + ")";
    }

    public FaceVo searchFace(String img,boolean isStrange){
        String id = null;
        String name = null;
        String group = isStrange?schoolId+strangePrefix:schoolId;
        FaceVo face = null;
        JSONObject res = HttpUtils.faceSearch(img, group);
        if ((Integer) res.get("error_code") == 0) {
            Object result = res.get("result");
            if(result != null){
                JSONArray users = ((JSONObject) result).get("user_list") == null?
                        null:((JSONArray)((JSONObject) result).get("user_list"));
                face = faceIsExist(users,isStrange);
            }
        }
        if(face == null) {
            face = new FaceVo();
        }
        if(-1 == face.getCode() && isStrange){//如果是陌生人  没有找到 就保存到陌生人 人脸库
            id = System.currentTimeMillis()+""+(int)(1+Math.random()*(100-1+1));
            name = "陌生人";
            res = HttpUtils.addFace(img, id, name, group);
            if ((Integer) res.get("error_code") == 0) {
                face = new FaceVo();
                face.setCode(2);
                face.setId(id);
                face.setName(name);
                CacheMap.put(id, name, cacheTime);
            }
        }
        return face;
    }



    public FaceVo faceIsExist(JSONArray users,boolean isStrange){
        FaceVo face = new FaceVo();
        if(users != null){
            for (int i = 0; i < users.size(); i++) {
                JSONObject user = (JSONObject)users.get(i);
                if (user.get("score") instanceof Integer) {
                    if ((Integer) user.get("score") == 0) {
                        continue;
                    }
                }
                double score = (double) Double.parseDouble(user.get("score").toString());
                if (score < scoreNum) {// 相似分值达到一定数值后才认为是一个人
                    continue;
                }
                String id = (String) user.get("user_id");
                String name = (String) user.get("user_info");
                face.setId(id);
                face.setName(name);

                if (CacheMap.containsKey(id)) {
                    if(isStrange){//重复 的 陌生人
                        face.setCode(4);
                    }else{//重复 的 熟人
                        face.setCode(3);
                    }
                    continue;
                }
                CacheMap.put(id, name, cacheTime);
                if(isStrange){//未重复 的 陌生人
                    face.setCode(2);
                }else{//未重复 的 熟人
                    face.setCode(1);
                }
            }
        }
        return face;
    }

}
