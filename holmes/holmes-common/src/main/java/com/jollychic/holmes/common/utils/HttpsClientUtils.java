package com.jollychic.holmes.common.utils;

import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

/**
 * Created by WIN7 on 2018/1/17.
 */
@Slf4j
public class HttpsClientUtils {
    /**
     * 发送https GET请求
     */
    public static String doGet(String url) throws Exception {
        String result = "";
        OutputStreamWriter writer = null;
        BufferedReader reader = null;
        try {
            SSLSocketFactory ssf = MyX509TrustManager.getSSFactory();
            URL realUrl= new URL(url);
            HttpsURLConnection conn = (HttpsURLConnection)realUrl.openConnection();
            conn.setSSLSocketFactory(ssf);
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestMethod("GET");// 提交模式
            //是否允许输入输出
            conn.setDoInput(true);
            conn.setDoOutput(true);
            //链接地址
            conn.connect();
            //如果应答码为200的时候，表示成功的请求带了，这里的HttpsURLConnection.HTTP_OK就是200
            if(conn.getResponseCode()==HttpsURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
                result = reader.readLine();//读取请求结果
            }
            reader.close();
        } catch (Exception e){
            log.error("https get error: "+e);
            throw e;
        }finally {
            try {
                if (writer != null) {
                    writer.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ex) {

            }
        }
        return result;
    }



    /**
     * 发送https请求共用体
     */
    public static String doPost(String url, String params) throws Exception {
        String result = "";
        OutputStreamWriter writer = null;
        BufferedReader reader = null;
        try {
            SSLSocketFactory ssf = MyX509TrustManager.getSSFactory();
            URL realUrl= new URL(url);
            HttpsURLConnection conn = (HttpsURLConnection)realUrl.openConnection();
            conn.setSSLSocketFactory(ssf);
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestMethod("POST");// 提交模式
            //是否允许输入输出
            conn.setDoInput(true);
            conn.setDoOutput(true);
            //链接地址
            conn.connect();
            writer = new OutputStreamWriter(conn.getOutputStream());
            //发送参数
            writer.write(params);
            //清理当前编辑器的左右缓冲区，并使缓冲区数据写入基础流
            writer.flush();
            if(conn.getResponseCode()==HttpsURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
                result = reader.readLine();//读取请求结果
            }
            reader.close();
        } catch (Exception e){
            log.error("https post error: "+e);
            throw e;
        }finally {
            try {
                if (writer != null) {
                    writer.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ex) {

            }
        }
        return result;
    }

}
