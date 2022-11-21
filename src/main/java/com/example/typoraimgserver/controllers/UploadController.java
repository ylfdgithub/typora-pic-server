package com.example.typoraimgserver.controllers;

import com.example.typoraimgserver.beans.RestBean;
import com.example.typoraimgserver.utils.UploadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/file")
public class UploadController {
    @Autowired
    private UploadUtils utils;

    @PostMapping("upload")
    public RestBean upload(@RequestParam MultipartFile files[]) throws IOException {
        List<String> urls = utils.fileInput(files);
        return new RestBean(200,urls);
    }
    @PostMapping("uploadtest")
    public String uploadtest(@RequestParam MultipartFile files){
        System.out.println(files.getOriginalFilename());
        System.out.println(files.getContentType());
        System.out.println(files.getName());
        return "";
    }

    @GetMapping("test")
    public String test() throws IOException {
//        String filePath = environment.getProperty("filePath");
//        return filePath;
        String test = utils.test();
        return test;
    }


}
