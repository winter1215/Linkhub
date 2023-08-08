package com.linkhub.common.config.configration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author winter
 * @create 2023-05-16 下午7:04
 */
@Data
@ConfigurationProperties(prefix = "oos")
@Configuration
public class OOSConfiguration {
    private String accessKey;
    private String secretKey;
    private String bucket;

}
