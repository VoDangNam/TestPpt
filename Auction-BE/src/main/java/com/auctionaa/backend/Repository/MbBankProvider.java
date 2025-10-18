package com.auctionaa.backend.Repository;

import com.auctionaa.backend.Entity.MbTxn;

import java.time.Instant;
import java.util.Optional;

public interface MbBankProvider {
    Optional<MbTxn> findIncomingByAddInfo(String accountNo, Long amount, String addInfo, Instant fromTime);
}
