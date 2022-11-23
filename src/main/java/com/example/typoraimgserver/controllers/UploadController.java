package com.example.typoraimgserver.controllers;

import com.example.typoraimgserver.beans.RestBean;
import com.example.typoraimgserver.utils.UploadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin
@RestController
public class UploadController {
    @Autowired
    private UploadUtils utils;

    @PostMapping("upload")
    public RestBean upload(@RequestParam MultipartFile files[]) throws IOException {
        List<String> urls = utils.fileInput(files);
        return new RestBean(200,urls);
    }
}
