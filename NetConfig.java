package com.example.esc.worksigninapplication;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by fuhaijing on 2016/12/14.
 */

public class NetConfig {
    // Get方式请求
    public static String requestByGet(String server_address,String ucode) throws Exception {
        String path =server_address;
        // 新建一个URL对象
        URL url = new URL(path);
        // 打开一个HttpURLConnection连接
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        // 设置连接超时时间
        urlConn.setConnectTimeout(5 * 1000);
        // 开始连接
        urlConn.connect();
        String TAG_GET = null ;
        //urlConn.setRequestMethod("GET");
        String data = null ;
        // 判断请求是否成功
        if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            // 获取返回的数据
            data =InputStreamTOString(urlConn.getInputStream(),ucode);// readStream(urlConn.getInputStream());
        } else {
            //Log.i("ss", "Get方式请求失败");
        }
        // 关闭连接
        urlConn.disconnect();
        return data;
    }

    /**
     * 将InputStream转换成某种字符编码的String
     * @param in
     * @param encoding
     * @return
     * @throws Exception
     */
    public static String InputStreamTOString(InputStream in, String encoding) throws Exception{

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int count = -1;
        while((count = in.read(data,0,1024)) != -1)
            outStream.write(data, 0, count);
        data = null;
        return new String(outStream.toByteArray(),encoding);
    }
}
