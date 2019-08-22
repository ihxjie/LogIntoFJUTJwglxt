package l;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Connect {

    private String url = "https://webvpn.fjut.edu.cn";
    private String url_jwxt;
    private String url_jwxt_webvpnNetwork = "https://jwxt-443.webvpn.fjut.edu.cn";//webvpn访问
    private String url_jwxt_campusNetwork = "http://jwxt.fjut.edu.cn";//校园网访问
    //private String url_jwxt_weChatNetwork = "http://jwxtwx.fjut.edu.cn";//微信接口
    private String modulus,exponent,authenticity_token,csrftoken;
    private String user,password;

    private Map<String,String> cookies = new HashMap<>();
    private Connection connection;
    private Connection.Response response;
    private Document document;

    public Connect(String user, String password) {
        this.user = user;
        this.password = password;
    }
    public Map<String,String> link(){
        url_jwxt = url_jwxt_campusNetwork;
        getCsrftoken();
        getRSApublickey();
        login();
        return getCookies();
    }
    public Map<String,String> webvpn_link(){
        url_jwxt = url_jwxt_webvpnNetwork;
        getAuthenticityToken();
        loginHome();
        getCsrftoken();
        getRSApublickey();
        login();
        return getCookies();
    }
    public void getAuthenticityToken() {
        connection = Jsoup.connect(url+"/users/sign_in");
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36");//待考量
        try{
            response = connection.execute();
        }catch (Exception e){
            e.printStackTrace();
        }

        cookies = response.cookies();
        document = Jsoup.parse(response.body());
        authenticity_token = document.select("input[name=authenticity_token]").val();
        System.out.println(authenticity_token);
    }

    public void loginHome(){
        connection = Jsoup.connect(url+"/users/sign_in");
        connection.header("Content-Type","application/x-www-form-urlencoded;charset=utf-8");
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36");

        connection.data("utf8","✓");
        connection.data("authenticity_token",authenticity_token);
        connection.data("user[login]",user);
        connection.data("user[password]",password);
        connection.data("user[dymatice_code]","unknown");
        connection.data("commit","登录 Login");
        try{

            connection.cookies(cookies).ignoreContentType(true).method(Connection.Method.POST).execute();
            response = connection.execute();
            cookies = response.cookies();

        }catch (Exception e){
            e.printStackTrace();
        }
        document = Jsoup.parse(response.body());
        //System.out.println(document);
        if (document.getElementsByClass("alert fade in alert-danger ").text().equals("")){
            System.out.println("WELCOME "+document.select("a[href='#']").text());
        }else {
            System.out.println(document.getElementsByClass("alert fade in alert-danger ").text());
        }

    }

    public void getCsrftoken() {
        connection = Jsoup.connect(url_jwxt+"/jwglxt/xtgl/login_slogin.html?language=zh_CN&_t="+new Date().getTime());
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36");
        try{
            response = connection.cookies(cookies).ignoreContentType(true).execute();

        }catch (Exception e){
            e.printStackTrace();
        }
        cookies.put("JSESSIONID",response.cookies().get("JSESSIONID"));
        document = Jsoup.parse(response.body());

        csrftoken = document.getElementById("csrftoken").val();
        System.out.println(csrftoken);
    }

    public void getRSApublickey() {
        connection = Jsoup.connect(url_jwxt+ "/jwglxt/xtgl/login_getPublicKey.html?time=" + new Date().getTime());
        connection.header("Content-Type","application/x-www-form-urlencoded;charset=utf-8");
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36");
        try{
            response = connection.cookies(cookies).ignoreContentType(true).execute();
        }catch (Exception e){
            e.printStackTrace();
        }
        JSONObject jsonObject = JSON.parseObject(response.body());
        modulus = jsonObject.getString("modulus");
        exponent = jsonObject.getString("exponent");
        password = RSAEncoder.RSAEncrypt(password, B64.b64tohex(modulus), B64.b64tohex(exponent));
        password = B64.hex2b64(password);

    }
    public void login() {

        connection = Jsoup.connect(url_jwxt+ "/jwglxt/xtgl/login_slogin.html");
        connection.header("Content-Type","application/x-www-form-urlencoded;charset=utf-8");
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36");

        connection.data("csrftoken",csrftoken);
        connection.data("yhm",user);
        connection.data("mm",password);
        connection.data("mm",password);
        try{
            connection.cookies(cookies).ignoreContentType(true).method(Connection.Method.POST).execute();
            response = connection.execute();
        }catch (Exception e){
            e.printStackTrace();
        }

        document = Jsoup.parse(response.body());
        if(document.getElementById("tips") == null){
            System.out.println("LOGIN SUCCESS");
        }else{
            System.out.println(document.getElementById("tips").text());
        }
    }
    public Map<String,String> getCookies(){
        return cookies;
    }
    public void setCookies(Map<String,String> cookies){
        this.cookies = cookies;
    }
    public void getCourse(){
        connection = Jsoup.connect(url_jwxt+"/jwglxt/kbcx/xskbcx_cxXsKb.html?gnmkdm=N2151");
        connection.header("Content-Type","application/x-www-form-urlencoded;charset=utf-8");
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36");

        connection.data("xnm","2019");
        connection.data("xqm","3");

        try {
            connection.cookies(cookies).ignoreContentType(true).method(Connection.Method.POST).execute();
            response = connection.execute();
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(cookies);
        document = Jsoup.parse(response.body());

        JSONObject jsonObject = JSON.parseObject(response.body());
        if(jsonObject.get("kbList") == null){
            System.out.println("暂时没有安排课程");
        }
        JSONArray timeTable = JSON.parseArray(jsonObject.getString("kbList"));

        for (Iterator iterator = timeTable.iterator(); iterator.hasNext();) {
            JSONObject lesson = (JSONObject) iterator.next();
            System.out.println(lesson.getString("xqjmc") + " " + lesson.getString("jc") +
                    " " + lesson.getString("kcmc") + " " + lesson.getString("xm") + " " +
                    lesson.getString("xqmc") + " " + lesson.getString("cdmc") + " " +
                    lesson.getString("zcd"));
        }

    }

}
