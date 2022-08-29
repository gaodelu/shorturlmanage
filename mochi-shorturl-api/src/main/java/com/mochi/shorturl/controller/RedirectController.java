package com.mochi.shorturl.controller;

import com.mochi.shorturl.service.ShortUrlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@RestController
@Slf4j
public class RedirectController {

    @Autowired
    private ShortUrlService shortUrlService;

    @GetMapping("/redirect")
    public void redirectSending(String shortUrl, HttpServletResponse response) throws IOException {
        log.info("接收到重定向请求:{}", shortUrl);
        shortUrlService.getLongUrl(shortUrl);
        response.sendRedirect(shortUrlService.getLongUrl(shortUrl));
    }

    @GetMapping("/health")
    public String health() {
        log.info("健康检查:{}", new Date().getTime());
        return "健康检查通过：" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
