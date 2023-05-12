package util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginRegisterUtil {

    private final OkHttpClient client = new OkHttpClient();
    public RequestBody formBody = null;
    public Request request = null;


    public String login(String username, String password) {
        String jwt = null;
        this.formBody = new FormBody.Builder()
                .add("email", username)
                .add("password", password).build();
        this.request = new Request.Builder()
                .url("http://124.221.95.230/wp-json/simple-jwt-login/auth")
                .post(formBody)
                .build();
        Response response = null;
        JSONObject responseData;
        try {
            response = client.newCall(request).execute();
            responseData = JSON.parseObject(response.body().string());
            JSONObject data = responseData.getJSONObject("data");
            jwt = data.get("jwt").toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (response.isSuccessful()) {
//            System.out.println("登录成功");
            return jwt;
        } else {
//            System.out.println("登录失败");
            return "login Failed";
        }

    }

    public myPairThree<Integer,String,String> loginByJWT(String jwt) throws IOException {
        this.formBody = new FormBody.Builder()
                .add("jwt", jwt).build();
        this.request = new Request.Builder()
                .url("http://124.221.95.230/?rest_route=/simple-jwt-login/auth/validate&JWT=YOUR_JWT")
                .post(formBody).build();
        Response response = null;

        JSONObject responseData;
        response = client.newCall(request).execute();
        responseData = JSON.parseObject(response.body().string());
        int user_id=Integer.parseInt(responseData.getJSONObject("data").getJSONObject("user").getString("ID"));
        String user_login_name=responseData.getJSONObject("data").getJSONObject("user").getString("user_login");
        String user_nickname=responseData.getJSONObject("data").getJSONObject("user").getString("user_nicename");
        myPairThree<Integer,String,String> user_id_name_nickname=new myPairThree<>(user_id,user_login_name,user_nickname);
        System.out.println(user_id);
        return user_id_name_nickname;
    }

    /**
     * @param user_email
     * @param password
     * @return
     */
    public boolean register(String user_email, String password) {
        this.formBody = new FormBody.Builder()
                .add("email", user_email)
                .add("password", password)
                .add("AUTH_KEY", "MQ9^9Y97fvViGhQ&^TXEo_tjj3gv?J*vd3_ZVC1D7SsvQu6W5LX-&6mnr+SK").build();
        this.request = new Request.Builder()
                .url("http://124.221.95.230/wp-json/simple-jwt-login/users")
                .post(formBody)
                .build();
        Response response = null;
        int user_id;
        JSONObject responseData;
        try {
            response = client.newCall(request).execute();
            responseData = JSON.parseObject(response.body().string());
            user_id = Integer.parseInt(responseData.get("id").toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (response.isSuccessful()) {
//            System.out.println("注册成功");
            return true;
        } else {
//            System.out.println("注册失败");
            return false;
        }

    }


}