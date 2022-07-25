package com.wb.base.callback.server.controller;

import com.wb.base.callback.model.CallBackSyncResponse;
import com.wb.base.callback.rpc.client.CallBackRpcClient;
import com.wb.base.callback.rpc.client.model.CallBackMessageDTO;
import com.wb.base.callback.server.exception.CallBackExceptionErrorCode;
import com.wb.base.callback.service.domain.CallBackMessageService;
import com.wb.base.callback.service.exception.ErrorConfigException;
import com.wb.base.callback.service.exception.NoConfigException;
import com.wb.common.exception.WbPlatformException;
import com.wb.common.exception.WbPlatformSystemCode;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/callback/message")
public class CallBackController implements CallBackRpcClient {

    @Autowired
    private CallBackMessageService callBackMessageService;

    @Override
    @PostMapping("/callback")
    public void callback(@Validated @RequestBody CallBackMessageDTO callBackMessage) {
        if (null == callBackMessage.getProtocol().getPlatformType() || StringUtils.isBlank(callBackMessage.getProtocol().getBusinessType()) || StringUtils.isBlank(callBackMessage.getProtocol().getMessageId())) {
            throw WbPlatformException.system(WbPlatformSystemCode.E90003);
        }
        try {
            callBackMessageService.saveCallBackMessage(callBackMessage.getProtocol(), callBackMessage.getData());
        } catch (NoConfigException e) {
            throw WbPlatformException.business(CallBackExceptionErrorCode.NO_CONFIG);
        }
    }

    @Override
    @PostMapping("/callbackSync")
    public List<CallBackSyncResponse> callbackSync(@Validated @RequestBody CallBackMessageDTO callBackMessage) {
        if (null == callBackMessage.getProtocol().getPlatformType() || StringUtils.isBlank(callBackMessage.getProtocol().getBusinessType()) || StringUtils.isBlank(callBackMessage.getProtocol().getMessageId())) {
            throw WbPlatformException.system(WbPlatformSystemCode.E90003);
        }
        try {
            return callBackMessageService.callBackSync(callBackMessage.getProtocol(), callBackMessage.getData());
        } catch (ErrorConfigException e) {
            throw WbPlatformException.business(CallBackExceptionErrorCode.ERROR_CONFIG);
        } catch (NoConfigException e) {
            throw WbPlatformException.business(CallBackExceptionErrorCode.NO_CONFIG);
        }
    }

    @PostMapping("/reset")
    public void reset(@Validated @RequestBody CallBackMessageDTO callBackMessage) {
        if (null == callBackMessage.getProtocol().getPlatformType() || StringUtils.isBlank(callBackMessage.getProtocol().getBusinessType()) || StringUtils.isBlank(callBackMessage.getProtocol().getMessageId())) {
            throw WbPlatformException.system(WbPlatformSystemCode.E90003);
        }
        try {
            callBackMessageService.saveCallBackMessage(callBackMessage.getProtocol(), callBackMessage.getData());
        } catch (NoConfigException e) {
            throw WbPlatformException.business(CallBackExceptionErrorCode.NO_CONFIG);
        }
    }
}
