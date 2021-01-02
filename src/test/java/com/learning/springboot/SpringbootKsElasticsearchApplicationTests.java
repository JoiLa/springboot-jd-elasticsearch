package com.learning.springboot;

import com.alibaba.fastjson.JSON;
import com.learning.springboot.pojo.User;
import com.learning.springboot.service.ContentService;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


@SpringBootTest
class SpringbootKsElasticsearchApplicationTests {

    @Qualifier(value = "restHighLevelClient")
    @Autowired
    private RestHighLevelClient client;

    final static String EsIndex = "test233";

    @Test
    void contextLoads() {
    }

    //测试创建索引
    @Test
    void testCreateIndex() throws IOException {
        CreateIndexRequest  request             = new CreateIndexRequest(EsIndex);
        CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(createIndexResponse);
    }

    //测试索引是否存在
    @Test
    void testExistsIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest(EsIndex);
        boolean         exists  = client.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    //测试删除索引
    @Test
    void testDeleteIndex() throws IOException {
        DeleteIndexRequest   request = new DeleteIndexRequest(EsIndex);
        AcknowledgedResponse delete  = client.indices().delete(request, RequestOptions.DEFAULT);
        System.out.println(delete);
    }

    //测试添加文档
    @Test
    void testAddDocument() throws IOException {
        User user = new User("李昂君", 23);
        //创建请求
        IndexRequest request = new IndexRequest(EsIndex);
        //规则 put /test233/_doc/1
        request.id("1");
        request.timeout(TimeValue.timeValueSeconds(15));
        //request.timeout("1s");
        //将我们的数据，放入请求 json 中
        IndexRequest source = request.source(JSON.toJSONString(user), XContentType.JSON);
        //客户端发送请求，获得响应结果
        IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
        //打印信息
        System.out.println(indexResponse.toString());
    }

    ///测试是否存在文档
    @Test
    void testExistsDocument() throws IOException {
        GetRequest getRequest = new GetRequest(EsIndex, "1");
        //不获取返回的 _source 的上下文了
        getRequest.fetchSourceContext(new FetchSourceContext(false));
        getRequest.storedFields("_none_");
        boolean exists = client.exists(getRequest, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    ///测试获取文档信息
    @Test
    void testGetDocument() throws IOException {
        GetRequest  getRequest  = new GetRequest(EsIndex, "1");
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        System.out.println(getResponse.getSourceAsString());
        System.out.println(getResponse);
    }

    ///测试获取文档信息
    @Test
    void testUpdateDocument() throws IOException {
        UpdateRequest updateRequest = new UpdateRequest(EsIndex, "1");
        updateRequest.timeout(TimeValue.timeValueSeconds(15));
        User user = new User("李昂君233", 23);
        updateRequest.doc(JSON.toJSONString(user), XContentType.JSON);
        UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);
        System.out.println(updateResponse.status());
    }

    ///测试删除文档信息
    @Test
    void testDeleteDocument() throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest(EsIndex, "1");
        deleteRequest.timeout(TimeValue.timeValueSeconds(15));
        DeleteResponse deleteResponse = client.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println(deleteResponse);
    }

    //测试批量请求
    @Test
    void testBulkRequest() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout(TimeValue.timeValueSeconds(15));
        //创建数组队列
        ArrayList<User> userList = new ArrayList<>();
        //生成一百个用户
        for (int i = 0; i < 100; i++) {
            userList.add(new User("张三" + i, 12 + i));
        }
        //一百个用户数据加入到请求队列
        for (int i = 0; i < userList.size(); i++) {
            bulkRequest.add(
                    new IndexRequest(EsIndex)
                            .id(String.valueOf(i + 1))
                            .source(
                                    JSON.toJSONString(userList.get(i)), XContentType.JSON
                            )
            );
        }
        //批量请求
        BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(bulkResponse.hasFailures());//为 false 则是成功
    }

    //测试查询
    @Test
    void testSearch() throws IOException {
        SearchRequest searchRequest = new SearchRequest(EsIndex);
        //构建搜索条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //查询条件，我们可以使用 QueryBuilders 工具来实现
        sourceBuilder.query(QueryBuilders.matchQuery("age", "49"));
        sourceBuilder.from(0);
        sourceBuilder.size(10);
        sourceBuilder.timeout(TimeValue.timeValueSeconds(15));
        System.out.println(sourceBuilder);
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(JSON.toJSONString(searchResponse.getHits()));
        System.out.println("-------------------------------");
        for (SearchHit documentFields : searchResponse.getHits().getHits()) {
            System.out.println(documentFields.getSourceAsMap());
        }
    }


}
