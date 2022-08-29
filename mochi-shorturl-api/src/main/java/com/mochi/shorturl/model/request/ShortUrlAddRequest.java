package com.mochi.shorturl.model.request;

import com.mochi.shorturl.exception.BizException;
import com.mochi.shorturl.util.ErrCodeConstants;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;

@Data
public class ShortUrlAddRequest {

    private String longUrl;

    public void check() {
        if (StringUtils.isEmpty(this.longUrl)) {
            throw new BizException(ErrCodeConstants.PARAM_CAN_NOT_BE_NULL);
        }
        try {
            URL url = new URL(this.longUrl);
        } catch (Exception e) {
            throw new BizException(ErrCodeConstants.URL_IS_INCORRECT);
        }
    }

}
