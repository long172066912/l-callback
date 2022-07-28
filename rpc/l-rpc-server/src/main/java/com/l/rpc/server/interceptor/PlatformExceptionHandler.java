package com.l.rpc.server.interceptor;

import com.l.rpc.exception.BaseBusinessException;
import com.l.rpc.exception.SystemCode;
import com.l.rpc.model.RpcBaseResponse;
import com.l.rpc.model.RpcVersion;
import com.l.rpc.server.util.ServerConfigUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
 * @Title: PlatformExceptionHandler
 * @Description: rpc 微服务 异常统一处理
 * @date 2021/3/5 10:55 AM
 */
@ControllerAdvice
@Slf4j
public class PlatformExceptionHandler {
    /**
     * 校验错误拦截处理
     *
     * @param exception 错误信息集合
     * @return 错误信息
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public RpcBaseResponse validationBodyException(HttpServletResponse response, MethodArgumentNotValidException exception) {
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
        this.exceptionHandler(response, SystemCode.E90003.name(), stringBuffer.toString());
        return RpcBaseResponse.fail(SystemCode.E90003, stringBuffer.toString());
    }

    /**
     * 校验错误拦截处理
     *
     * @param exception 错误信息集合
     * @return 错误信息
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public RpcBaseResponse validationBodyException(HttpServletResponse response, HttpMessageNotReadableException exception) {
        //按需重新封装需要返回的错误信息
        log.error("request url:{}; params error ! E90003 ! paramsErrorInfo:{}", exception.getMessage());
        this.exceptionHandler(response, SystemCode.E90003.name(), exception.getMessage());
        return RpcBaseResponse.fail(SystemCode.E90003, exception.getMessage(), null);
    }

    /**
     * 处理平台异常
     *
     * @param e
     */
    @ExceptionHandler(BaseBusinessException.class)
    @ResponseBody
    public void processPlatformBusinessException(HttpServletRequest request, HttpServletResponse response, BaseBusinessException e) {
        String code = e.getCode();
        if (e.isBussinessException()) {
            String serverErrorIdent = ServerConfigUtil.getServerErrorIdent();
            if (StringUtils.isBlank(serverErrorIdent)) {
                log.error("BaseBusinessException error ! 请配置服务异常标识对应关系 ! code:{}", e.getCode());
            }
            code = serverErrorIdent + e.getCode();
        }
        this.exceptionHandler(response, code, e.getDesc());
        log.info("request url={}; cd:{},desc:{},from:{}", request.getRequestURL().toString(), e.getCode(), e.getDesc(), request.getHeader("from"));
    }

    /**
     * 处理平台系统异常
     *
     * @param request
     * @param e
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public void processPlatformException(HttpServletRequest request, HttpServletResponse response, Exception e) {
        log.error("request url:{}; system error ! E90002 !", request.getRequestURL().toString(), e);
        this.exceptionHandler(response, SystemCode.E90002.name(), SystemCode.E90002.getDesc());
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
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
            log.warn("PlatformExceptionHandler desc encode error ! desc:{}", desc, e);
        }
    }
}