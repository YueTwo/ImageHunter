package util;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 数据库操作类
 * URL：
 * 数据库类型：mysql
 * ip：124.221.95.230
 * 数据库：124_221_95_230
 * USER：
 * 用户：124_221_95_230
 * PWD：
 * 密码：CNCTeZwdKXLhy6aE
 */
public class DBUtils {
    //CLS配置时需要注意 mysql 5.x版本和8.x版本的配置是不同的
    public static final String CLS = "com.mysql.jdbc.Driver";
    public static final String URL = "jdbc:mysql://124.221.95.230:3309/124_221_95_230?useUnicode=true&characterEncoding=utf-8";
    public static final String USER = "user";
    public static final String PWD = "123456";
    public static Connection connection = null;

    /**
     * 此函数用于用户上传视频/图片到ftp时，把相关信息插入到数据库的video/image_table表中
     *
     * @param user_login_name 用户的邮箱
     * @param file_name       文件的名字
     * @param localPath       文件的本地路径
     * @param tag             标志 1：传入的是图片   2：传入的是视频
     * @return file_id 自动生成的文件id
     */
    public int upload_db(String user_login_name, int user_id, String file_name, String localPath, int tag) throws ClassNotFoundException, SQLException {
        Class.forName(CLS);
        connection = DriverManager.getConnection(URL, USER, PWD);
        String sql = null;
        String remotePath = "/www/wwwroot/124.221.95.230/wp-content/uploads/users/" + user_login_name;
        if (tag == 1) {
            remotePath += "/image";
            sql = "insert into image_table(user_id, imageName, imageLocalPath,imageRemotePath)values (?,?,?,?);";
        } else if (tag == 2) {
            remotePath += "/video";
            sql = "insert into video_table(user_id, videoName, videoLocalPath, videoRemotePath) values (?,?,?,?);";
        }
        PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setObject(1, user_id);
        preparedStatement.setObject(2, file_name);
        preparedStatement.setObject(3, localPath);
        preparedStatement.setObject(4, remotePath);
        int rows = preparedStatement.executeUpdate();
        //下面用于返回自动生成的文件id(主键)
        ResultSet resultSet = preparedStatement.getGeneratedKeys();
        resultSet.next();
        int file_id = resultSet.getInt(1);
        Log.d("lyt数据库操作", "插入成功，影响行数为" + rows);
        connection.close();
        return file_id;
    }

    /**
     * 用户点击检索后，上传检索文本到数据库
     *
     * @param user_id
     * @param searchText
     * @param tag
     * @return int text_id 返回插入后自动生成的文本的主键
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public int search_text_db(int user_id, String searchText, int tag) throws SQLException, ClassNotFoundException {
        Class.forName(CLS);
        connection = DriverManager.getConnection(URL, USER, PWD);
        String sql = "insert into search_text_table(user_id, searchText, tag)values (?,?,?);";
        PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setObject(1, user_id);
        preparedStatement.setObject(2, searchText);
        preparedStatement.setObject(3, tag);
        int rows = preparedStatement.executeUpdate();
        ResultSet resultSet = preparedStatement.getGeneratedKeys();
        resultSet.next();
        int text_id = resultSet.getInt(1);
        Log.d("lyt数据库操作", "插入成功，影响行数为" + rows);
        return text_id;
    }

    /**
     * 当视频或者图片的置信度传回的时候，外部的监听调用此函数查询对应检索文本的检索结果
     *
     * @param tag     1:图片  2:视频
     * @param text_id 检索文本的主键
     * @return ResultSet 返回检索的结果(视频或者图片的id,置信度)
     */
    public ResultSet search_result(int tag, int text_id) throws ClassNotFoundException, SQLException {
        Class.forName(CLS);
        connection = DriverManager.getConnection(URL, USER, PWD);
        String sql = null;
        if (tag == 1) {
            sql = "select i.imageLocalPath path, s.confidence confidence FROM image_table i, image_search_result_table s where i.image_id =s.image_id  and s.text_id=?;";
        } else if (tag == 2) {
            sql = "select v.videoLocalPath path, s.confidence confidence FROM video_table v, video_search_result_table s where v.video_id =s.video_id  and s.text_id=?;";
        }
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet resultSet = null;
        preparedStatement.setObject(1, text_id);

        resultSet = preparedStatement.executeQuery();
        Log.d("lyt数据库操作", "查询成功");

        return resultSet;

    }

    /**
     * 此函数用于将用户的生成请求传入到generate_request_table
     *
     * @param user_id 用户id
     * @param file_id 文件id
     * @param tag     标志
     * @return request_id 返回请求id
     */
    public int generate_request_db(int user_id, int file_id, int tag) throws ClassNotFoundException, SQLException {
        Class.forName(CLS);
        connection = DriverManager.getConnection(URL, USER, PWD);
        String sql = "insert into generate_request_table (user_id, file_id, tag) values (?,?,?);";
        PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setObject(1, user_id);
        preparedStatement.setObject(2, file_id);
        preparedStatement.setObject(3, tag);
        preparedStatement.executeUpdate();
        ResultSet resultSet = preparedStatement.getGeneratedKeys();
        resultSet.next();
        int request_id = resultSet.getInt(1);
        Log.d("lyt数据库操作", "插入生成请求表成功成功");
//        connection.close();
        return request_id;

    }

    /**
     *
     * @param request_id
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public ResultSet text_generation_select(int request_id) throws SQLException, ClassNotFoundException {
        Class.forName(CLS);
        connection = DriverManager.getConnection(URL, USER, PWD);
        String sql = "select generation_text from text_generation_table where request_id=?;";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setObject(1, request_id);
        preparedStatement.execute();
        ResultSet resultSet = preparedStatement.getResultSet();
        return resultSet;
    }

    /**
     * 判断用户是否上传过此文件
     *
     * @param file_name
     * @return 上传过：file_id   没上传过：0
     */
    public int search_file_by_name(int user_id, String file_name, int tag) throws ClassNotFoundException, SQLException {
        Class.forName(CLS);
        String sql = "";
        connection = DriverManager.getConnection(URL, USER, PWD);
        if (tag == 1) {
            sql = "select image_id id from image_table where imageName=? and user_id = ?;";
        } else if (tag == 2) {
            sql = "select video_id id from video_table where videoName=? and user_id = ?;";
        }
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setObject(1, file_name);
        preparedStatement.setObject(2, user_id);
        preparedStatement.execute();
        ResultSet resultSet = preparedStatement.getResultSet();
        if (resultSet.next()) {
            int file_id = resultSet.getInt("id");
            connection.close();
            return file_id;
        }
        connection.close();
        return 0;
    }

    /**
     * 非及时生成的时候，利用user_id tag 和 file_id找到生成的文本
     */

    public String search_generate_text(int tag, int file_id) throws ClassNotFoundException, SQLException {
        String sql = "";
        if (tag == 1) {
            sql = "select generate_text from image_table where image_id=?;";
        } else if (tag == 2) {
            sql = "select generate_text from video_table where video_id=?";
        } else {
            return null;
        }
        Class.forName(CLS);
        connection = DriverManager.getConnection(URL, USER, PWD);
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setObject(1, file_id);
        preparedStatement.execute();
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet == null) {
            return "未检索到生成结果";
        }
        resultSet.next();
        String generate_text = resultSet.getString(1);
        connection.close();
        return generate_text;

    }


}

