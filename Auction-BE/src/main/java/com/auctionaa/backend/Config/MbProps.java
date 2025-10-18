package com.auctionaa.backend.Config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "mb")
public class MbProps {
    private String bankCode;
    private String accountNo;
    private String accountName;
    private String username;
    private String password;
    private String numberMb;
    private Api api = new Api();
    @Data public static class Api { private String baseUrl; }
}
