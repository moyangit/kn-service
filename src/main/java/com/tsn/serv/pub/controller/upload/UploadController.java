package com.tsn.serv.pub.controller.upload;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tsn.common.utils.utils.tools.time.DateUtils;
import com.tsn.common.utils.web.entity.Response;
import com.tsn.common.utils.web.utils.env.Env;

/**
 * @Description
 * @Author sgl
 * @Date 2018-05-15 14:04
 */
@RestController
@RequestMapping("upload")
public class UploadController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadController.class);

    @GetMapping("/")
    public String upload() {
        return "upload";
    }

    /**
     * 单文件上传
     * @param mFile
     * @return
     */
    @PostMapping("/single")
    @ResponseBody
    public Response<?> upload(@RequestParam("file") MultipartFile mFile, @RequestParam("memPhone") String memPhone) {
        // 文件上传路径
        String filePath = Env.getVal("file.upload.path");

        if (mFile.isEmpty()) {
            return Response.retn("000001", "上传失败，请选择文件");
        }

        String yyyy = DateUtils.getCurrYMD("yyyy") + "/";
        String MM = DateUtils.getCurrYMD("MM") + "/";
        String dd = DateUtils.getCurrYMD("dd") + "/";
        File path = new File(filePath + yyyy + MM + dd + memPhone + "/");

        if (!path.exists()){
            path.mkdirs();
        }

        String fileName = mFile.getOriginalFilename();
        // 重新生成文件名称
        String newFileName = UUID.randomUUID().toString().replaceAll("-", "") + fileName.substring(fileName.lastIndexOf("."));
        File file = new File(filePath + yyyy + MM + dd + memPhone + "/" + newFileName);
        // 文件访问路径
        /*String fileVisitPath = Env.getVal("file.visit.path");
        File visitFile = new File(fileVisitPath + yyyy + MM + dd + memPhone + "/" + newFileName);*/
        // File visitFile = new File(yyyy + MM + dd + memPhone + "/" + newFileName);
        try {
            mFile.transferTo(file);
            LOGGER.info("上传成功");
        } catch (IOException e) {
            LOGGER.error(e.toString(), e);
        }
        return Response.ok(file);
    }

    /**
     * 多文件上传
     * @param mFiles
     * @return
     */
    @PostMapping("/multi")
    @ResponseBody
    public Response<?> multiUpload(@RequestParam("files") MultipartFile[] mFiles, @RequestParam("memPhone") String memPhone) {
        String filePath = Env.getVal("file.upload.path");

        List<File> retnList = new ArrayList<>();
        for (int i = 0; i < mFiles.length; i++) {
            MultipartFile mFile = mFiles[i];
            if (mFile.isEmpty()) {
                return Response.retn("000001", "上传第" + (i++) + "个文件失败");
            }

            String yyyy = DateUtils.getCurrYMD("yyyy") + "/";
            String MM = DateUtils.getCurrYMD("MM") + "/";
            String dd = DateUtils.getCurrYMD("dd") + "/";
            File path = new File(filePath + yyyy + MM + dd + memPhone + "/");

            if (!path.exists()){
                path.mkdirs();
            }

            String fileName = mFile.getOriginalFilename();
            // 重新生成文件名称
            String newFileName = UUID.randomUUID().toString().replaceAll("-", "") + fileName.substring(fileName.lastIndexOf("."));
            File file = new File(filePath + yyyy + MM + dd + memPhone + "/" + newFileName);
            retnList.add(file);
            try {
                mFile.transferTo(file);
                LOGGER.info("第" + (i + 1) + "个文件上传成功");
            } catch (IOException e) {
                LOGGER.error(e.toString(), e);
                return Response.retn("000001", "上传第" + (i++) + "个文件失败");
            }
        }
        return Response.ok(retnList);
    }

}
