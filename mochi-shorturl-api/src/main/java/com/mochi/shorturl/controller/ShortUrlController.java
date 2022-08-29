package com.mochi.shorturl.controller;

import com.mochi.shorturl.exception.BizException;
import com.mochi.shorturl.model.PublicReponse;
import com.mochi.shorturl.model.request.ShortUrlAddRequest;
import com.mochi.shorturl.service.ShortUrlService;
import com.mochi.shorturl.util.ErrCodeConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;

@RestController
@RequestMapping("/shorturl/")
@Slf4j
public class ShortUrlController {

    @Autowired
    private ShortUrlService shortUrlService;

    @PostMapping("add")
    public PublicReponse<String> addShortUrl(@RequestBody ShortUrlAddRequest shortUrlAddRequest, HttpServletRequest httpRequest) {
        log.info("短链接新增请求参数:{}", shortUrlAddRequest);
        try {
            shortUrlAddRequest.check();
            return new PublicReponse<>(shortUrlService.addShortUrl(shortUrlAddRequest, httpRequest));
        } catch (BizException e) {
            log.error("短链接新增异常:{}", e.getErrMsg());
            return new PublicReponse<>(e);
        } catch (Exception e) {
            log.error("短链接新增异常:{}", e);
            return new PublicReponse<>(new BizException(ErrCodeConstants.SYS_ERR));
        }
    }

    @PostMapping("delete")
    public PublicReponse<Boolean> delShortUrl(@RequestBody ShortUrlAddRequest shortUrlAddRequest, HttpServletRequest httpRequest) {
        log.info("短链接删除请求参数:{}", shortUrlAddRequest);
        try {
            shortUrlAddRequest.check();
            return new PublicReponse<>(shortUrlService.delShortUrl(shortUrlAddRequest));
        } catch (BizException e) {
            log.error("短链接删除异常:{}", e.getErrMsg());
            return new PublicReponse<>(e);
        } catch (Exception e) {
            log.error("短链接删除异常:{}", e);
            return new PublicReponse<>(new BizException(ErrCodeConstants.SYS_ERR));
        }
    }

    @GetMapping("recover")
    public PublicReponse<String> recoverShortUrl(String shortUrl) {
        log.info("短链接恢复请求参数:{}", shortUrl);
        try {
            URL url = new URL(shortUrl);
            return new PublicReponse<>(shortUrlService.getLongUrl(url.getPath().substring(1)));
        } catch (MalformedURLException e) {
            throw new BizException(ErrCodeConstants.URL_IS_INCORRECT);
        }
    }

}
