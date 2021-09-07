package com.imooc.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
@Slf4j

@RestController
public class QiniuApi {

    @PostMapping("/thirdApi/qiniu/img")
    public String upImg(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        byte[] imgBytes = multipartFile.getBytes();
        //MultipartFile 转 字节数组
        String imgUrl = Qiniu.upLoadImage(imgBytes);
        Map map = new HashMap<>();
        map.put("imgUrl", imgUrl);
        log.info("图片上传成功");
        return imgUrl;
    }
}