package l;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Mark {
    private Map<String,String> cookies = new HashMap<>();
    private Connection.Response response;
    private Document document;

    public Mark(Connection connection, Map<String,String> cookies) throws IOException {

        String mark_url = "http://jwxt.fjut.edu.cn/jwglxt/cjcx/cjcx_cxDgXscj.html?doType=query&gnmkdm=N305005";
        connection = Jsoup.connect(mark_url);

        connection.data("xnm","2018");
        connection.data("xqm","12");
        connection.data("queryModel.showCount","100");

        connection.cookies(cookies).ignoreContentType(true).method(Connection.Method.POST).execute();

        response = connection.execute();
        document = Jsoup.parse(response.body());
        System.out.println(document);
        JSONObject jsonObject = JSON.parseObject(response.body());
        if(jsonObject.get("items") == null){
            System.out.println("暂时没有");

        }
        JSONArray timeTable = JSON.parseArray(jsonObject.getString("items"));


        for (Iterator iterator = timeTable.iterator(); iterator.hasNext();) {
            JSONObject lesson = (JSONObject) iterator.next();
            System.out.println(lesson.getString("kcmc") + " " + lesson.getString("cj"));
        }
    }
}
