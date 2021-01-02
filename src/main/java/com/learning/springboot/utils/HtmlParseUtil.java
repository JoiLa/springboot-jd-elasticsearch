package com.learning.springboot.utils;

import com.learning.springboot.pojo.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

//网页解析单元

@Component
public class HtmlParseUtil {

    //解析京东搜索的数据
    final public List<Content> parseJd(String Keyword) throws IOException {
        String url = "https://search.jd.com/Search?keyword=" + Keyword + "&enc=utf-8";
        //解析网页 Jsoup返回 Document 就是浏览器 Document 对象
        Document document = Jsoup.parse(new URL(url), 30000);
        //所有在 js 中可以使用的方法，这里都能用！
        Element elementGoodsList = document.getElementById("J_goodsList");
        //获取所有
        Elements           elementGoodsLi = elementGoodsList.getElementsByTag("li");
        ArrayList<Content> goodsList      = new ArrayList<>();
        for (Element li : elementGoodsLi) {
            String img   = li.getElementsByTag("img").attr("data-lazy-img");
            String price = li.getElementsByClass("p-price").eq(0).text();
            String title = li.getElementsByClass("p-name").eq(0).text();
            goodsList.add(new Content(title, img, price));
           /* System.out.println("img->" + img);
            System.out.println("price->" + price);
            System.out.println("title->" + title);
            System.out.println("================================");*/
        }
        return goodsList;
    }
}
