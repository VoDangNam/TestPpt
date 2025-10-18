package com.auctionaa.backend.Repository;

import com.auctionaa.backend.Entity.WalletTransaction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface WalletTransactionRepository extends MongoRepository<WalletTransaction, String> {
    Optional<WalletTransaction> findById(String id);
    List<WalletTransaction> findByWalletIdAndStatus(String walletId, int status);
}
