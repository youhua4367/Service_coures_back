package com.example.elm_m.Configuration;

import com.example.elm_m.Properties.AliOssProperties;
import com.example.elm_m.Utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class OssConfiguration {

    @Bean
    @ConditionalOnMissingBean()
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties) {
        log.info("初始化文件上传工具，endpoint={}，bucket={}",
                aliOssProperties.getEndpoint(), aliOssProperties.getBucketName());
        return new AliOssUtil(aliOssProperties.getEndpoint(),
                            aliOssProperties.getAccessKeyId(),
                            aliOssProperties.getAccessKeySecret(),
                            aliOssProperties.getBucketName());
    }
}
