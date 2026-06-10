package com.jizhang;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

public class OcrTestBase64 {

    private static final String API_KEY = "sk-xaMeqz0jAlXO4ixv11rvprh6pNofJtzkWaCDAOr0U4ioYJVx";
    private static final String MODEL = "gpt-5.4";
    private static final String API_URL = "https://api.d66.asia/v1/chat/completions";

    public static void main(String[] args) {
        String testImageUrl = "https://ai-ledger-invoices.oss-us-west-1.aliyuncs.com/ledger/F9802B57149ECDDB25ECC1EC6B409B01.jpg";
        
        System.out.println("测试 OCR 功能（Base64方式）...");
        System.out.println("图片URL: " + testImageUrl);
        System.out.println();

        try {
            System.out.println("下载图片...");
            String base64Image = downloadAndEncode(testImageUrl);
            System.out.println("图片大小: " + base64Image.length() + " 字符");
            System.out.println();

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build();

            String prompt = "请识别这张小票或账单图片，提取以下信息并以JSON格式返回：\n" +
                    "1. amount（金额，数字类型）\n" +
                    "2. date（日期，格式：yyyy-MM-dd）\n" +
                    "3. merchant（商家名称或交易描述）\n" +
                    "4. category（消费类目，从以下选项中选择：餐饮美食、交通出行、购物消费、娱乐休闲、生活缴费、医疗健康、学习教育、其他支出）\n\n" +
                    "如果无法识别某个字段，请返回null。\n" +
                    "只返回JSON，不要其他说明文字。格式示例：\n" +
                    "{\"amount\": 123.45, \"date\": \"2026-06-10\", \"merchant\": \"肯德基\", \"category\": \"餐饮美食\"}";

            JSONObject requestBody = new JSONObject();
            requestBody.put("model", MODEL);
            requestBody.put("max_tokens", 500);

            JSONArray messages = new JSONArray();
            JSONObject message = new JSONObject();
            message.put("role", "user");

            JSONArray contentArray = new JSONArray();

            JSONObject textContent = new JSONObject();
            textContent.put("type", "text");
            textContent.put("text", prompt);
            contentArray.add(textContent);

            JSONObject imageContent = new JSONObject();
            imageContent.put("type", "image_url");
            JSONObject imageUrlObj = new JSONObject();
            imageUrlObj.put("url", "data:image/jpeg;base64," + base64Image);
            imageContent.put("image_url", imageUrlObj);
            contentArray.add(imageContent);

            message.put("content", contentArray);
            messages.add(message);
            requestBody.put("messages", messages);

            System.out.println("发送请求到: " + API_URL);
            System.out.println();

            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(RequestBody.create(
                            requestBody.toJSONString(),
                            MediaType.parse("application/json")))
                    .addHeader("Authorization", "Bearer " + API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                System.out.println("响应状态码: " + response.code());
                
                if (!response.isSuccessful()) {
                    System.out.println("请求失败: " + response.message());
                    if (response.body() != null) {
                        System.out.println("错误响应: " + response.body().string());
                    }
                    return;
                }

                String responseBody = response.body().string();
                System.out.println("响应内容: " + responseBody);
                System.out.println();

                JSONObject result = JSON.parseObject(responseBody);
                JSONArray choices = result.getJSONArray("choices");
                
                if (choices != null && !choices.isEmpty()) {
                    String content = choices.getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content");
                    
                    System.out.println("识别结果: " + content);
                    System.out.println("\n测试成功！✅");
                } else {
                    System.out.println("响应中没有 choices 数据");
                }
            }

        } catch (Exception e) {
            System.out.println("测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String downloadAndEncode(String imageUrl) throws Exception {
        URL url = new URL(imageUrl);
        try (InputStream is = url.openStream();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            
            byte[] imageBytes = baos.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
        }
    }
}
