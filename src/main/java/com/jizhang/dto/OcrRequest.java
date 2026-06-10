package com.jizhang.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "图片识别请求")
public class OcrRequest {

    @Schema(description = "图片URL", required = true, example = "https://your-bucket.oss-cn-hangzhou.aliyuncs.com/ledger/xxx.jpg")
    @NotBlank(message = "图片URL不能为空")
    private String imageUrl;
}
