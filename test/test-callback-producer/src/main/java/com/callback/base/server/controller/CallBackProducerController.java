package com.callback.base.server.controller;

import com.callback.base.server.model.TestMessage;
import com.callback.base.constants.CallBackPlatformTypeEnums;
import com.callback.base.model.CallBackProtocol;
import com.callback.base.model.CallBackSyncResponse;
import com.callback.base.sdk.exception.CallBackFailException;
import com.callback.base.starter.client.CallbackClient;
import com.callback.base.utils.MessageUniqueKeyUtils;
import com.l.rpc.exception.BaseBusinessException;
import com.l.rpc.exception.SystemCode;
import com.l.rpc.json.LJSON;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
            log.error("CallBackProducerController doHandle error ! param : {}", param, e);
            throw BaseBusinessException.system(SystemCode.E90001);
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
            log.error("CallBackProducerController callbackSync error ! testMessage : {}", LJSON.toJson(testMessage), e);
            throw BaseBusinessException.system(SystemCode.E90001);
        }
    }
}
