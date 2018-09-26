package com.onlinestore.controller;

import com.onlinestore.common.utils.JsonUtils;
import com.onlinestore.utils.FastDFSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 图片上传功能controller
 */
@Controller
public class PictureController {

    @Value("${IMAGE_SERVER_URL}")
    private String IMAGE_SERVER_URL;

    @RequestMapping("/pic/upload")
    @ResponseBody
    public String picUpload(MultipartFile uploadFile){
       try{
           //接受上传的文件
           //获取扩展名
           String originalFileName = uploadFile.getOriginalFilename();
           String extName = originalFileName.substring(originalFileName.lastIndexOf(".")+1);
           //上传图片到图片服务器
           FastDFSClient fastDFSClient = new FastDFSClient("classpath:resource/FastDFS.conf");
           String url = fastDFSClient.uploadFile(uploadFile.getBytes(),extName);
           url =IMAGE_SERVER_URL + url;
           //响应上传图片的url,如果只有少数的key-value需要保存,那么可以使用hashMap代替pojo,进行数据的接收保存
           Map result = new HashMap();
           result.put("error",0);
           result.put("url",url);
           return JsonUtils.objectToJson(result);
       }catch (Exception e){
           e.printStackTrace();
           Map result = new HashMap();
           result.put("error",1);
           result.put("message","图片上传失败");
           return JsonUtils.objectToJson(result);
       }
    }

}
