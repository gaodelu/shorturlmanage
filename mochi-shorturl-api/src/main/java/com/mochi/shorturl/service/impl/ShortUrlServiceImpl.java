package com.mochi.shorturl.service.impl;

import com.mochi.shorturl.exception.BizException;
import com.mochi.shorturl.mapper.TShortUrlInfoMapper;
import com.mochi.shorturl.model.model.TShortUrlInfoModel;
import com.mochi.shorturl.model.request.ShortUrlAddRequest;
import com.mochi.shorturl.service.ShortUrlService;
import com.mochi.shorturl.util.ConvertUtil;
import com.mochi.shorturl.util.ErrCodeConstants;
import com.mochi.shorturl.util.IpAddressUtil;
import com.mochi.shorturl.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ShortUrlServiceImpl implements ShortUrlService {

    private static final String SHORT_URL_PATTERN = "http://hiyaki.cn/";

    private static final String KEY_IP_LIMIT = "SHORT_URL_IP_LIMIT_";
    private static final String KEY_SHORT_URL = "SHORT_URL_";

    @Autowired
    private TShortUrlInfoMapper tShortUrlInfoMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public String addShortUrl(ShortUrlAddRequest shortUrlAddRequest, HttpServletRequest httpRequest) {
        int count = 1;
        String ipAddress = IpAddressUtil.getIpAddress(httpRequest);
        Object countObj = redisUtil.get(KEY_IP_LIMIT + ipAddress);
        if (countObj != null) {
            count = ((Integer) countObj).intValue();
            log.info("当前IP:{},已操作短链接:{}次", ipAddress, count);
        }
        if (count > 10) {
            log.info("当前IP:{},已操作短链接{}次,超出10次限制", ipAddress, count);
            throw new BizException(ErrCodeConstants.OPERATION_FAST);
        }
        TShortUrlInfoModel tShortUrlInfoModel = tShortUrlInfoMapper.findByLongUrlAndDisableTimeAfter(shortUrlAddRequest.getLongUrl(), LocalDateTime.now());
        log.info("根据长链接查询信息:{}", tShortUrlInfoModel);
        if (tShortUrlInfoModel != null) {
            //存在，则更新失效时间
            tShortUrlInfoMapper.updateDisableTime(LocalDateTime.now().plusDays(1), tShortUrlInfoModel.getId());
            redisUtil.set(KEY_SHORT_URL + tShortUrlInfoModel.getShortUrl(), shortUrlAddRequest.getLongUrl(), 1, TimeUnit.DAYS);
            redisUtil.set(KEY_IP_LIMIT + ipAddress, count + 1, 10, TimeUnit.MINUTES);
            return SHORT_URL_PATTERN + tShortUrlInfoModel.getShortUrl();
        }
        //不存在则进行新增操作
        tShortUrlInfoModel = new TShortUrlInfoModel();
        Random random = new Random();
        int id;
        for (; ; ) {
            id = Math.abs(random.nextInt());
            Optional<TShortUrlInfoModel> byId = tShortUrlInfoMapper.findById(id);
            if (!byId.isPresent()) {
                log.info("新增短链接id：{}", id);
                break;
            }
        }
        tShortUrlInfoModel.setLongUrl(shortUrlAddRequest.getLongUrl());
        //进行url生成操作
        tShortUrlInfoModel.setCreateTime(LocalDateTime.now());
        tShortUrlInfoModel.setDisableTime(LocalDateTime.now().plusDays(1));
        tShortUrlInfoModel.setCreateIp(ipAddress);
        tShortUrlInfoModel.setId(id);
        String encode = ConvertUtil.encode(id);
        tShortUrlInfoModel.setShortUrl(encode);
        log.info("生成短链接ID：{},短链接:{}", id, encode);
        tShortUrlInfoMapper.save(tShortUrlInfoModel);
        //将短链接保存到redis
        log.info("短链接缓存到redis，Key:{}", "SHORT_URL_" + encode);
        redisUtil.set(KEY_SHORT_URL + encode, shortUrlAddRequest.getLongUrl(), 1, TimeUnit.DAYS);
        //获取当前ip，redis增加操作次数
        redisUtil.set(KEY_IP_LIMIT + ipAddress, count + 1, 10, TimeUnit.MINUTES);
        return SHORT_URL_PATTERN + encode;
    }

    @Override
    public boolean delShortUrl(ShortUrlAddRequest shortUrlAddRequest) {
        TShortUrlInfoModel tShortUrlInfoModel = tShortUrlInfoMapper.findByLongUrlAndDisableTimeAfter(shortUrlAddRequest.getLongUrl(), LocalDateTime.now());
        if (tShortUrlInfoModel != null) {
            //存在，则更新失效时间
            tShortUrlInfoMapper.updateDisableTime(LocalDateTime.now(), tShortUrlInfoModel.getId());
            redisUtil.set(KEY_SHORT_URL + tShortUrlInfoModel.getShortUrl(), shortUrlAddRequest.getLongUrl(), 1, TimeUnit.MILLISECONDS);
        } else {
            log.error("长链接{}不存在，无法删除！", shortUrlAddRequest.getLongUrl());
            throw new BizException(ErrCodeConstants.LONG_URL_IS_NOT_EXISTS);
        }
        return true;
    }

    @Override
    public String getLongUrl(String shortUrl) {
        //先从redis获取，再从数据库获取
        String longUrl = "";
        log.info("获取短链接缓存redis的key:{}", "SHORT_URL_" + shortUrl);
        Object o = redisUtil.get(KEY_SHORT_URL + shortUrl);
        log.info("redis获取的短链接信息：{}", o);
        if (o != null) {
            longUrl = (String) o;
            return longUrl;
        }

        TShortUrlInfoModel tShortUrlInfoModel = tShortUrlInfoMapper.findByShortUrlAndDisableTimeAfter(shortUrl, LocalDateTime.now());
        log.info("从数据库获取的短链接配置信息:{}", tShortUrlInfoModel);
        if (tShortUrlInfoModel == null) {
            return "http://www.baidu.com";
        }
        return tShortUrlInfoModel.getLongUrl();
    }
}
