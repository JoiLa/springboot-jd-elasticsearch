package com.learning.springboot.controller;

import com.learning.springboot.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

//请求编写
@RestController
public class ContentController {
    @Autowired
    private ContentService contentService;

    @GetMapping("/parse/{keyword}")
    public Boolean parse(@PathVariable("keyword") String Keyword) throws IOException {
        return contentService.ParseContent(Keyword);
    }

    @GetMapping("/search")
    public List<Map<String, Object>> search(
            @RequestParam(name = "Keyword") String Keyword,
            @RequestParam(required = false, defaultValue = "0", name = "Page") int Page,
            @RequestParam(required = false, defaultValue = "10", name = "PageSize") int PageSize
    ) throws IOException {
        return contentService.searchPage(Keyword, Page, PageSize);
    }
}
