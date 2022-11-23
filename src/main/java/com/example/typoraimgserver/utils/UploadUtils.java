package com.example.typoraimgserver.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



@Component
public class UploadUtils {
    @Autowired
    private Environment environment;

    public List<String> fileInput(MultipartFile files[]) throws IOException {
        String ipAddress = getNowIP();
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            String contentType = file.getContentType();
            String[] split = contentType.split("/");
            String suffix = "";
            if (split[0].equals("image")){
                String fileName = file.getOriginalFilename();
                String[] splits = fileName.split("[.]");
                suffix ="." + splits[splits.length - 1];
            }
            String storeName = getUUID();
            String urlPrefix = "http://"+ipAddress+":"+environment.getProperty("server.port")+"/";
            urls.add(urlPrefix + storeName + suffix);
            String targetDir = environment.getProperty("filePath") + File.separator + storeName + suffix;
            saveMultipartFile(file,targetDir);
        }
        return urls;
    }

    //获取UUID
    private String getUUID(){
        String uuid = UUID.randomUUID().toString().replaceAll("-","");
        return uuid;
    }
    private String getNowIP() throws IOException {
        String ip = null;
        BufferedReader br = null;
        try {
            URL url = new URL("https://v6r.ipip.net/?format=callback");
            br = new BufferedReader(new InputStreamReader(url.openStream()));
            String s = "";
            StringBuffer sb = new StringBuffer("");
            String webContent = "";
            while ((s = br.readLine()) != null) {
                sb.append(s + "\r\n");
            }
            webContent = sb.toString();
            int start = webContent.indexOf("(") + 2;
            int end = webContent.indexOf(")") - 1;
            webContent = webContent.substring(start, end);
            ip = webContent;
        } finally {
            if (br != null)
                br.close();
        }
        if (StringUtils.isEmpty(ip)) {
            throw new RuntimeException();
        }
        return ip;
    }
    public static void saveMultipartFile(MultipartFile file, String targetDirPath) throws IOException {
        File tofile = new File(targetDirPath);
        InputStream inputStream = file.getInputStream();
        inputStreamToFile(inputStream,tofile);
    }

    //获取流文件
    private static void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除本地临时文件
     *
     * @param file
     */
    public static void deleteTempFile(File file) {
        if (file != null) {
            File del = new File(file.toURI());
            del.delete();
        }
    }
}
