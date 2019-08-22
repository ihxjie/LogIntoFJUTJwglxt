package l;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.Map;

public class QcxConnect {
    private String url = "https://qcxapi.fjut.edu.cn";
    private String account,password;

    private Map<String,String> cookies = new HashMap<>();
    private Connection connection;
    private Connection.Response response;
    private Document document;

    public QcxConnect(String account, String password) {
        this.account = account;
        this.password = password;
    }
    public void loginHome(){
        connection = Jsoup.connect(url+"/user/login");
        connection.header("Content-Type","application/x-www-form-urlencoded;charset=utf-8");
        connection.header("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36");

        try{

            connection.ignoreContentType(true).method(Connection.Method.POST);
            response = connection.data("account",account).data("password",password).execute();
            cookies = response.cookies();
            document = Jsoup.parse(response.body());

            connection = Jsoup.connect(url+"/user/getInfo");
            connection.cookies(cookies).ignoreContentType(true).method(Connection.Method.POST).execute();
            response = connection.execute();

            document = Jsoup.parse(response.body());
            System.out.println(document);

            connection = Jsoup.connect(url+"/scoreDetail/query");
            connection.cookies(cookies).data("stuId",account).ignoreContentType(true).method(Connection.Method.POST).execute();
            response = connection.execute();

            document = Jsoup.parse(response.body());
            System.out.println(document);


        }catch (Exception e){
            e.printStackTrace();
        }



    }
    /*public void getInfo(){
        connection = Jsoup.connect(url+"/user/getInfo");
        connection.header("Referer","https://qcx.fjut.edu.cn/student/home");
        try{
            connection.cookies(cookies).method(Connection.Method.POST).execute();
            response = connection.execute();
        }catch (Exception e){
            e.printStackTrace();
        }
        document = Jsoup.parse(response.body());
        System.out.println(document);
        System.out.println(response.cookies());

    }
    public void getscoreDetail(){
        connection = Jsoup.connect(url+"/scoreDetail/query");
        connection.header("Referer","https://qcx.fjut.edu.cn/student/home");
        try{
            connection.data("stuId","3171911234");
            response = connection.method(Connection.Method.POST).execute();
        }catch (Exception e){
            e.printStackTrace();
        }
        document = Jsoup.parse(response.body());
        System.out.println(document);
        System.out.println(response.cookies());

    }*/
}
