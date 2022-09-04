package com.wds.reggie.controller;

import com.wds.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * @author WDs , wds8.24@outlook.com
 * @version 1.0
 * @since 2022-08-22 12:28
 */
@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController {


    /**
     * 文件上传
     *
     * @param file 文件
     */
    @Value("${reggie.up-path}")
    private String basePath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) throws IOException {
        //原始文件名
        String fileName = file.getOriginalFilename();
        //获取后缀
        assert fileName != null;
        String format = fileName.substring(fileName.lastIndexOf('.'));
        //重命名
        fileName = UUID.randomUUID().toString() + format;

        //basePath不存在则创建
        File basePathCreate = new File(basePath);
        if (!basePathCreate.exists()) {
            basePathCreate.mkdir();
        }
        //文件存储 你
        file.transferTo(new File(basePath + fileName));
        return R.success(fileName);

    }

    /**
     * 文件下载
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) throws IOException {
        FileInputStream inputStream = new FileInputStream(basePath + name);

        ServletOutputStream outputStream = response.getOutputStream();
        response.setContentType("image/jpeg");

        int len = 0;
        byte[] data = new byte[1024];

        while ((len = inputStream.read(data)) != -1) {
            outputStream.write(data,0,len);
        }
        outputStream.close();
        inputStream.close();
    }




}
