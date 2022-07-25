package com.wb.base.callback.server.controller;

import com.wb.base.callback.constants.CallBackPlatformTypeEnums;
import com.wb.base.callback.model.CallBackProtocol;
import com.wb.base.callback.model.CallBackSyncResponse;
import com.wb.base.callback.sdk.exception.CallBackFailException;
import com.wb.base.callback.server.model.TestMessage;
import com.wb.base.callback.starter.client.CallbackClient;
import com.wb.base.callback.utils.MessageUniqueKeyUtils;
import com.wb.common.exception.WbPlatformException;
import com.wb.common.exception.WbPlatformSystemCode;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by @author
 */
@RestController
@RequestMapping("/callback/producer")
public class CallBackProducerController {

    @PostMapping("/doHandle")
    public String doHandle(@Validated @RequestBody String param) {
        try {
            String callbackMessageId = CallbackClient.getInstance().callback(
                    CallBackProtocol.builder()
                            .messageId(MessageUniqueKeyUtils.createMessageId())
                            .platformType(CallBackPlatformTypeEnums.TEST)
                            .businessType("TEST")
                            .build(),
                    new TestMessage()
            );
            return callbackMessageId;
        } catch (CallBackFailException e) {
            e.printStackTrace();
            throw WbPlatformException.system(WbPlatformSystemCode.E90001);
        }
    }

    @PostMapping("/callbackSync")
    public List<CallBackSyncResponse> callbackSync(@Validated @RequestBody TestMessage testMessage) {
        try {
            return CallbackClient.getInstance().callbackSync(
                    CallBackProtocol.builder()
                            .messageId(MessageUniqueKeyUtils.createMessageId())
                            .platformType(CallBackPlatformTypeEnums.TEST)
                            .businessType("TEST_HTTP")
                            .build(),
                    new TestMessage()
            );
        } catch (CallBackFailException e) {
            e.printStackTrace();
            throw WbPlatformException.system(WbPlatformSystemCode.E90001);
        }
    }
}
