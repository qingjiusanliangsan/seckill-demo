package com.ucas.seckill.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ucas.seckill.pojo.TUser;
import com.ucas.seckill.vo.RespBean;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/**
 * 生成用户工具类
 *
 * @author: LC
 * @date 2022/3/4 3:29 下午
 * @ClassName: UserUtil
 */
public class UserUtil {
    public static void createuser(int count) throws Exception{
        List<TUser> users = new ArrayList<>();
        for(int i=0;i<count;i++){
            TUser user = new TUser();
            user.setLoginCount(1);
            user.setId(13000000000l+i);
            user.setNickname("user"+i);
            user.setSalt("1a2b3c4d");
            user.setRegisterDate(new Date());
            user.setPassword(MD5Util.inputPassToDBPass("123456",user.getSalt()));
            users.add(user);
        }
        System.out.println("create Users!");

        Connection conn = UserUtil.getConn();
        String sql = "insert into t_user(login_count, nickname, register_date, salt, password, id)values(?,?,?,?,?,?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        for (int i = 0; i < users.size(); i++) {
            TUser user = users.get(i);
            pstmt.setInt(1, user.getLoginCount());
            pstmt.setString(2, user.getNickname());
            pstmt.setTimestamp(3, new Timestamp(user.getRegisterDate().getTime()));
            pstmt.setString(4, user.getSalt());
            pstmt.setString(5, user.getPassword());
            pstmt.setLong(6, user.getId());
            pstmt.addBatch();
        }
        pstmt.executeBatch();
        pstmt.close();
        conn.close();
        System.out.println("insert to db");

        //登录，生成token
        String urlString = "http://localhost:8080/login/doLogin";
        File file = new File("D:\\A_Job-learn\\MiaoSha_Proj\\config.txt");
        if (file.exists()) {
            file.delete();
        }
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        file.createNewFile();
        raf.seek(0);

        for (int i = 0; i < users.size(); i++) {
            TUser user = users.get(i);
            URL url = new URL(urlString);
            HttpURLConnection co = (HttpURLConnection) url.openConnection();
            co.setRequestMethod("POST");
            co.setDoOutput(true);
            OutputStream out = co.getOutputStream();
            String params = "mobile=" + user.getId() + "&password=" +
                    MD5Util.inputPassToFromPass("123456");
            out.write(params.getBytes());
            out.flush();
            InputStream inputStream = co.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte buff[] = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buff)) >= 0) {
                bout.write(buff, 0, len);
            }
            inputStream.close();
            bout.close();
            String response = new String(bout.toByteArray());
            ObjectMapper mapper = new ObjectMapper();
            RespBean respBean = mapper.readValue(response, RespBean.class);
            String message = (String) respBean.getMessage();
            System.out.println(message);
            String userTicket = (String) respBean.getObject();
            System.out.println("create userTicket : " + user.getId());
            String row = user.getId() + "," + userTicket;
            raf.seek(raf.length());
            raf.write(row.getBytes());
            raf.write("\r\n".getBytes());
            System.out.println("write to file : " + row);
//            System.out.println("write to file : " + user.getId());
        }
        raf.close();
        System.out.println("over");


    }
    private static Connection getConn() throws Exception {
        String url = "jdbc:mysql://127.0.0.1:3306/seckill?useSSL=false";
        String username = "root";
        String password = "123";
        String driver = "com.mysql.jdbc.Driver";
        Class.forName(driver);
        return DriverManager.getConnection(url,username,password);
    }

    public static void main(String[] args) throws Exception {
        UserUtil.createuser(500);
    }
}
