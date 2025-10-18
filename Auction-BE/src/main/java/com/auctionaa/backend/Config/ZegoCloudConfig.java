package com.auctionaa.backend.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ZegoCloudConfig {

    @Value("${zegocloud.app-id}")
    private long appId;

    @Value("${zegocloud.server-secret}")
    private String serverSecret;

    @Value("${zegocloud.token-ttl-seconds:1800}")
    private long tokenTtl;

    public long getAppId() {
        return appId;
    }

    public String getServerSecret() {
        return serverSecret;
    }

    public long getTokenTtl() {
        return tokenTtl;
    }
}
