package comwb.rpc.server.test;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class TestMain {

    private static final ObjectMapper objectMapper = new ObjectMapper();

//    static {
//        objectMapper = new ObjectMapper();
//        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);// 反序列化多字段，可以正常反序列化
//        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
//        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//    }


    public static void main(String[] args) throws IOException {
        String s = "{\"ver\":0,\"cd\":0,\"time\":1564478062495,\"cmd\":\"noviceGuidanceTask\",\"body\":{\"taskDesc\":\"给房主赠送辣条礼物（0/1）\",\"currentTaskState\":0,\"allTaskState\":0,\"rewardDesc\":\"奖励：语音房弹幕卡x3\",\"bubbleDesc\":\"送了辣条你最靓\",\"bubblePosition\":3,\"showGuideBubble\":1,\"taskId\":10}}";

        LongConnectionResponseMsg a = objectMapper.readValue(s, LongConnectionResponseMsg.class);
        String aa = "";

    }
}