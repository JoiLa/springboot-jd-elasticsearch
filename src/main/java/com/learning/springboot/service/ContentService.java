package com.learning.springboot.service;

import com.alibaba.fastjson.JSON;
import com.learning.springboot.pojo.Content;
import com.learning.springboot.utils.HtmlParseUtil;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//业务编写
@Service
public class ContentService {

    @Qualifier(value = "restHighLevelClient")
    @Autowired
    private RestHighLevelClient client;


    //解析数据并放入 es 索引中
    final public Boolean ParseContent(String Keyword) throws IOException {
        List<Content> contents = new HtmlParseUtil().parseJd(Keyword);
        if (contents.size() <= 0) return false;
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("3m");
        for (Content content : contents) {
            bulkRequest.add(
                    new IndexRequest("jd_goods").source(
                            JSON.toJSONString(content), XContentType.JSON
                    )
            );
        }
        //批量请求
        BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        //把查询出来的数据 放入 es中，是false才是成功
        return !bulkResponse.hasFailures();
    }

    //查询数据
    final public List<Map<String, Object>> searchPage(String Keyword, int Page, int PageSize) throws IOException {
        if (Page <= 1) Page = 0;
        SearchRequest searchRequest = new SearchRequest("jd_goods");
        //构建搜索条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //进准匹配
        sourceBuilder.query(QueryBuilders.matchQuery("title", Keyword));
        sourceBuilder.timeout(TimeValue.timeValueSeconds(30)).from(Page).size(PageSize);
        searchRequest.source(sourceBuilder);
        ArrayList<Map<String, Object>> list           = new ArrayList<>();
        SearchResponse                 searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            list.add(hit.getSourceAsMap());
        }
        return list;

    }
}
