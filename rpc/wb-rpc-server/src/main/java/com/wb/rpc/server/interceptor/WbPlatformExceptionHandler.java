package com.wb.rpc.server.interceptor;

import com.wb.common.exception.WbCommonException;
import com.wb.common.exception.WbPlatformException;
import com.wb.common.exception.WbPlatformSystemCode;
import com.wb.common.model.WbPlatformResponse;
import com.wb.common.rpc.model.MsRpcResponse;
import com.wb.log.WbLogger;
import com.wb.log.WbLoggerFactory;
import com.wb.rpc.model.RpcVersion;
import com.wb.rpc.server.extend.CustomizeExceptionHandler;
import com.wb.rpc.server.util.ServerConfigUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: ParamValidateExceptionHandler
 * @Description: rpc 微服务 WbCommonException 异常统一处理
 * @date 2021/3/5 10:55 AM
 */
@ControllerAdvice
@Slf4j
public class WbPlatformExceptionHandler {

    @Autowired(required = false)
    CustomizeExceptionHandler customizeExceptionHandler;

    /**
     * 校验错误拦截处理
     *
     * @param exception 错误信息集合
     * @return 错误信息
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public CallBackResponse validationBodyException(HttpServletResponse response, MethodArgumentNotValidException exception) {
        //按需重新封装需要返回的错误信息
        StringBuffer stringBuffer = new StringBuffer();
        //解析原错误信息，封装后返回，此处返回非法的字段名称，原始值，错误信息
        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            stringBuffer.append("param[");
            stringBuffer.append(error.getField());
            stringBuffer.append("] ");
            stringBuffer.append(error.getDefaultMessage());
            stringBuffer.append(", value : ");
            stringBuffer.append(error.getRejectedValue());
            stringBuffer.append(" | ");
        }
        log.error("request url:{}; params error ! E90003 ! paramsErrorInfo:{}", stringBuffer.toString());
        this.exceptionHandler(response, WbPlatformSystemCode.E90003.name(), stringBuffer.toString());
        return CallBackResponse.fail("-1", stringBuffer.toString());
    }

    /**
     * 校验错误拦截处理
     *
     * @param exception 错误信息集合
     * @return 错误信息
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public WbPlatformResponse validationBodyException(HttpServletResponse response, HttpMessageNotReadableException exception) {
        //按需重新封装需要返回的错误信息
        LOGGER.error("request url:{}; params error ! E90003 ! paramsErrorInfo:{}", exception.getMessage());
        this.exceptionHandler(response, WbPlatformSystemCode.E90003.name(), exception.getMessage());
        return WbPlatformResponse.fail(WbPlatformSystemCode.E90003, exception.getMessage(), null);
    }

    /**
     * 处理平台异常
     *
     * @param e
     */
    @ExceptionHandler(WbPlatformException.class)
    @ResponseBody
    public void processWbPlatformBusinessException(HttpServletRequest request, HttpServletResponse response, WbPlatformException e) {
        String code = e.getCode();
        if (e.isBussinessException()) {
            String serverErrorIdent = ServerConfigUtil.getServerErrorIdent();
            if (StringUtils.isBlank(serverErrorIdent)) {
                LOGGER.error("WbPlatformException error ! 请在Apollo配置服务异常标识对应关系 ! code:{}", e.getCode());
            }
            code = serverErrorIdent + e.getCode();
        }
        this.exceptionHandler(response, code, e.getDesc());
        LOGGER.info("request url={}; cd:{},desc:{},from:{}", request.getRequestURL().toString(), e.getCode(), e.getDesc(), request.getHeader("from"));
    }

    /**
     * 处理平台系统异常
     *
     * @param request
     * @param e
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public void processWbPlatformException(HttpServletRequest request, HttpServletResponse response, Exception e) {
        LOGGER.error("request url:{}; system error ! E90002 !", request.getRequestURL().toString(), e);
        this.exceptionHandler(response, WbPlatformSystemCode.E90002.name(), WbPlatformSystemCode.E90002.getDesc());
        if (customizeExceptionHandler != null) {
            customizeExceptionHandler.handleException(request, response, e);
            return;
        }
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }


    /**
     * 处理旧通用异常，在ResponseBodyAdviceHandler去做兼容
     *
     * @param request
     * @param response
     * @param e
     */
    @ExceptionHandler(WbCommonException.class)
    @ResponseBody
    public Object processWbCommonException(HttpServletRequest request, HttpServletResponse response, WbCommonException e) {
        Object o = request.getAttribute(ResponseTypeResolverInterceptor.IS_OLD_RPC_CONTROLLER);
        if (null != o && (Boolean) o) {
            LOGGER.info("request url={}; cd:{},desc:{},data:{},", request.getRequestURL().toString(), e.getCode(), e.getMessage(), e.getData());
            return new MsRpcResponse(e.getCode(), e.getMessage(), e.getData());
        } else {
            this.exceptionHandler(response, e.getCode() + "", e.getMessage());
            return null;
        }
    }

    /**
     * Response处理异常
     *
     * @param response
     * @param cd
     * @param desc
     */
    private void exceptionHandler(HttpServletResponse response, String cd, String desc) {
        response.addHeader(RpcVersion.RPC_VERSION_KEY, RpcVersion.RPC_VERSION_1_0.getVersion());
        //请求异常放入返回header
        response.addHeader(RpcVersion.RPC_HEADER_ERROR_CD_KEY, cd);
        try {
            response.addHeader(RpcVersion.RPC_HEADER_ERROR_DESC_KEY, URLEncoder.encode(desc, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn("WbPlatformExceptionHandler desc encode error ! desc:{}", desc, e);
        }
    }
}