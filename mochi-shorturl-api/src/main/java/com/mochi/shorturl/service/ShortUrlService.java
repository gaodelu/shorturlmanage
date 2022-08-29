package com.mochi.shorturl.service;

import com.mochi.shorturl.exception.BizException;
import com.mochi.shorturl.model.request.ShortUrlAddRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequest;

import javax.servlet.http.HttpServletRequest;

public interface ShortUrlService {

    String addShortUrl(ShortUrlAddRequest shortUrlAddRequest, HttpServletRequest httpRequest);

    boolean delShortUrl(ShortUrlAddRequest shortUrlAddRequest);

    String getLongUrl(String shortUrl);
}
