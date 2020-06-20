package com.zs.rtspface.vo;

import lombok.Data;

import java.util.Date;

/**
 * @Auther: zs
 * @Date: 2020/6/17 16:38
 * @Description:
 */
@Data
public class FaceVo {

    /*base64图片信息*/
    private String img;

    /*人员唯一标示*/
    private String id;
    /*人员姓名*/
    private String name;
    /*提示消息*/
    private String msg;
    /*结果标示  1：未重复的熟人，2：未重复的陌生人，3：重复的熟人，4：重复的陌生人     （陌生人会保存到陌生人库里）*/
    private Integer code = -1;
    /*相似度*/
    private Double score;
    /*年级*/
    private String grade;
    /*班级*/
    private String clazz;
}
