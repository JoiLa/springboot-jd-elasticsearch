package com.learning.springboot.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Content {
    //标题
    private String title;
    //图片地址
    private String img;
    //价格
    private String price;
}
