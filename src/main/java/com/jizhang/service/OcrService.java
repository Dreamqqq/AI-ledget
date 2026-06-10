package com.jizhang.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jizhang.dto.OcrResponse;
import com.jizhang.enums.ErrorCode;
import com.jizhang.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class OcrService {

    @Value("${openai.apiKey}")
    private String apiKey;

    @Value("${openai.model:gpt-4-vision-preview}")
    private String model;

    @Value("${openai.apiUrl:https://api.openai.com/v1/chat/completions}")
    private String apiUrl;

    private static final OkHttpClient CLIENT = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    public OcrResponse recognizeReceipt(String imageUrl) {
        try {
            String prompt = "请识别这张小票或账单图片，提取以下信息并以JSON格式返回：\n" +
                    "1. amount（金额，数字类型，正数表示收入，负数表示支出）\n" +
                    "2. date（日期，格式：yyyy-MM-dd）\n" +
                    "3. merchant（商家名称或交易描述）\n" +
                    "4. category（消费类目，从以下选项中选择：餐饮美食、交通出行、购物消费、娱乐休闲、生活缴费、医疗健康、学习教育、其他支出）\n\n" +
                    "如果无法识别某个字段，请返回null。\n" +
                    "只返回JSON，不要其他说明文字。格式示例：\n" +
                    "{\"amount\": 123.45, \"date\": \"2026-06-10\", \"merchant\": \"肯德基\", \"category\": \"餐饮美食\"}";

            String base64Image = downloadAndEncode(imageUrl);

            JSONObject requestBody = new JSONObject();
            requestBody.put("model", model);
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

            Request request = new Request.Builder()
                    .url(apiUrl)
                    .post(RequestBody.create(
                            requestBody.toJSONString(),
                            MediaType.parse("application/json")))
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = CLIENT.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("调用OpenAI API失败: {}", response.code());
                    throw new BusinessException(ErrorCode.OCR_FAILED);
                }

                String responseBody = response.body().string();
                log.info("OpenAI 响应: {}", responseBody);

                JSONObject result = JSON.parseObject(responseBody);
                JSONArray choices = result.getJSONArray("choices");
                if (choices == null || choices.isEmpty()) {
                    throw new BusinessException(ErrorCode.OCR_FAILED);
                }

                String content = choices.getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");

                return parseOcrResult(content);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("图片识别失败", e);
            throw new BusinessException(ErrorCode.OCR_FAILED);
        }
    }

    private OcrResponse parseOcrResult(String content) {
        try {
            content = content.trim();
            if (content.startsWith("```json")) {
                content = content.substring(7);
            }
            if (content.startsWith("```")) {
                content = content.substring(3);
            }
            if (content.endsWith("```")) {
                content = content.substring(0, content.length() - 3);
            }
            content = content.trim();

            JSONObject json = JSON.parseObject(content);

            OcrResponse.OcrResponseBuilder builder = OcrResponse.builder();

            if (json.containsKey("amount") && json.get("amount") != null) {
                builder.amount(json.getBigDecimal("amount"));
            }

            if (json.containsKey("date") && json.getString("date") != null) {
                try {
                    builder.date(LocalDate.parse(json.getString("date"), DateTimeFormatter.ISO_LOCAL_DATE));
                } catch (Exception e) {
                    log.warn("日期解析失败: {}", json.getString("date"));
                }
            }

            if (json.containsKey("merchant") && json.getString("merchant") != null) {
                builder.merchant(json.getString("merchant"));
            }

            if (json.containsKey("category") && json.getString("category") != null) {
                builder.category(json.getString("category"));
            }

            return builder.build();
        } catch (Exception e) {
            log.error("解析识别结果失败", e);
            throw new BusinessException(ErrorCode.OCR_PARSE_FAILED);
        }
    }

    private String downloadAndEncode(String imageUrl) throws Exception {
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
