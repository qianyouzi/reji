package com.reji.controller;

import com.reji.bean.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/common")

public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    /**
     * 文件上传
     */
    @PostMapping("/upload")
    public R upload(MultipartFile file) throws IOException {
        //获取传入文件名
        String oldFile = file.getOriginalFilename();
        //获取传入文件后缀名
        String suffix = oldFile.substring(oldFile.lastIndexOf("."));
        //生成新的文件名+后缀名
        String newFile = UUID.randomUUID().toString() + suffix;
        //判断文件夹是否存在,不存在的话创建
        File file1 = new File(basePath);
        if (!file1.exists()) {
            file1.mkdirs();
        }
        //将文件存入指定文件夹+文件名中
        file.transferTo(new File(basePath + newFile));
        return R.success(newFile);
    }

    /**
     * 文件下载
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        try {
            File file = new File(basePath + name);
            FileInputStream fileInputStream = new FileInputStream(file);
            response.setContentType("image/jpeg");
            ServletOutputStream outputStream = response.getOutputStream();
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }
            outputStream.close();
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

}
