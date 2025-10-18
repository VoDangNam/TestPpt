package com.auctionaa.backend.Repository;

public interface VietQrService {
    public abstract String buildQrPngUrl(String bankCode, String accountNo, String accountName, Long amount, String addInfo);
}
