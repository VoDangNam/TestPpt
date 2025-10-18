package com.auctionaa.backend.DTO.Response;

import com.auctionaa.backend.Entity.MbTxn;
import lombok.Data;

import java.util.List;
@Data
public class MbTxnResponse {
    private Integer code;
    private String message;
    private DataNode data;

    @Data
    public static class DataNode {
        private List<MbTxn> transactions;
    }
}
