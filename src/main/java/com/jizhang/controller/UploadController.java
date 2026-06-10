package com.jizhang.controller;

import com.jizhang.common.Result;
import com.jizhang.dto.UploadResponse;
import com.jizhang.service.OssService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@Api(tags = "文件上传接口")
public class UploadController {

    @Autowired
    private OssService ossService;

    @PostMapping("/upload")
    @ApiOperation("上传图片到OSS")
    public Result<UploadResponse> uploadImage(@RequestParam("file") MultipartFile file) {
        String imageUrl = ossService.uploadImage(file);
        return Result.success(new UploadResponse(imageUrl));
    }
}
