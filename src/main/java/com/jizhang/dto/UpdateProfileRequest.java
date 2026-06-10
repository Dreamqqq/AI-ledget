package com.jizhang.dto;

import com.jizhang.enums.Gender;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@ApiModel("修改个人信息请求")
public class UpdateProfileRequest {

    @ApiModelProperty(value = "姓名", example = "李四")
    @Size(max = 50, message = "姓名长度不能超过50")
    private String name;

    @ApiModelProperty(value = "年龄", example = "26")
    @Min(value = 1, message = "年龄必须大于0")
    private Integer age;

    @ApiModelProperty(value = "职业", example = "设计师")
    @Size(max = 50, message = "职业长度不能超过50")
    private String occupation;

    @ApiModelProperty(value = "性别", example = "MALE")
    @NotNull(message = "性别不能为空")
    private Gender gender;
}
