package com.example.typoraimgserver.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
public class UploadUtils {
    @Autowired
    private Environment environment;
    public String test() throws IOException {
//        String filePath = environment.getProperty("filePath");
//        return filePath;
//        String nowIP1 = getNowIP();
//        return nowIP1;

//        String uuid = getUUID();
//        return uuid;
        return "";
    }

    public List<String> fileInput(MultipartFile files[]) throws IOException {
        String ipAddress = getNowIP();
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            String contentType = file.getContentType();
            String[] split = contentType.split("/");
            String suffix = "";
            if (split[0].equals("image")){
                String[] splits = file.getOriginalFilename().split(".");
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


//    private BufferedImage mFileToBImage(MultipartFile file) {
//        BufferedImage srcImage = null;
//        try {
//            FileInputStream in = (FileInputStream) file.getInputStream();
//            srcImage = javax.imageio.ImageIO.read(in);
//        } catch (IOException e) {
//            System.out.println("文件类型转化异常！" + e.getMessage());
//        }
//        return srcImage;
//    }

    public static String saveMultipartFile(MultipartFile file, String targetDirPath){

        File toFile = null;
        if (file.equals("") || file.getSize() <= 0) {
            return null;
        } else {

            /*获取文件原名称*/
            String originalFilename = file.getOriginalFilename();
            /*获取文件格式*/
            String fileFormat = originalFilename.substring(originalFilename.lastIndexOf("."));

            String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");
            toFile = new File(targetDirPath + File.separator + uuid + fileFormat);

            String absolutePath = null;
            try {
                absolutePath = toFile.getCanonicalPath();

                /*判断路径中的文件夹是否存在，如果不存在，先创建文件夹*/
                String dirPath = absolutePath.substring(0, absolutePath.lastIndexOf(File.separator));
                File dir = new File(dirPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                InputStream ins = file.getInputStream();

                inputStreamToFile(ins, toFile);
                ins.close();

            } catch (IOException e) {
                e.printStackTrace();
            }



            return absolutePath;
        }

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
