package com.callback.base.server.controller;

import com.callback.base.model.CallBackSyncResponse;
import com.callback.base.rpc.client.CallBackRpcClient;
import com.callback.base.rpc.client.model.CallBackMessageDTO;
import com.callback.base.server.exception.CallBackExceptionErrorCode;
import com.callback.base.service.domain.CallBackMessageService;
import com.callback.base.service.exception.ErrorConfigException;
import com.callback.base.service.exception.NoConfigException;
import com.l.rpc.exception.BaseBusinessException;
import com.l.rpc.exception.SystemCode;
import org.apache.commons.lang3.StringUtils;
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
            throw BaseBusinessException.system(SystemCode.E90003);
        }
        try {
            callBackMessageService.saveCallBackMessage(callBackMessage.getProtocol(), callBackMessage.getData());
        } catch (NoConfigException e) {
            throw BaseBusinessException.business(CallBackExceptionErrorCode.NO_CONFIG);
        }
    }

    @Override
    @PostMapping("/callbackSync")
    public List<CallBackSyncResponse> callbackSync(@Validated @RequestBody CallBackMessageDTO callBackMessage) {
        if (null == callBackMessage.getProtocol().getPlatformType() || StringUtils.isBlank(callBackMessage.getProtocol().getBusinessType()) || StringUtils.isBlank(callBackMessage.getProtocol().getMessageId())) {
            throw BaseBusinessException.system(SystemCode.E90003);
        }
        try {
            return callBackMessageService.callBackSync(callBackMessage.getProtocol(), callBackMessage.getData());
        } catch (ErrorConfigException e) {
            throw BaseBusinessException.business(CallBackExceptionErrorCode.ERROR_CONFIG);
        } catch (NoConfigException e) {
            throw BaseBusinessException.business(CallBackExceptionErrorCode.NO_CONFIG);
        }
    }

    @PostMapping("/reset")
    public void reset(@Validated @RequestBody CallBackMessageDTO callBackMessage) {
        if (null == callBackMessage.getProtocol().getPlatformType() || StringUtils.isBlank(callBackMessage.getProtocol().getBusinessType()) || StringUtils.isBlank(callBackMessage.getProtocol().getMessageId())) {
            throw BaseBusinessException.system(SystemCode.E90003);
        }
        try {
            callBackMessageService.saveCallBackMessage(callBackMessage.getProtocol(), callBackMessage.getData());
        } catch (NoConfigException e) {
            throw BaseBusinessException.business(CallBackExceptionErrorCode.NO_CONFIG);
        }
    }
}
