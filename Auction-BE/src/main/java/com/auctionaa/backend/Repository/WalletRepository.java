package com.auctionaa.backend.Repository;

import com.auctionaa.backend.Entity.Wallet;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface WalletRepository extends MongoRepository<Wallet, String> {
    Optional<Wallet> findById(String id);
    Optional<Wallet> findByUserId(String userId);
}