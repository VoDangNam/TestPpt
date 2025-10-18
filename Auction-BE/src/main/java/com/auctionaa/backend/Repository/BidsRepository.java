package com.auctionaa.backend.Repository;

import com.auctionaa.backend.Entity.Bids;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BidsRepository extends MongoRepository<Bids, String> {

    // Lấy bid mới nhất của user trên 1 phiên đấu giá (tức artwork đó)
    Optional<Bids> findTopByAuctionSessionIdAndUserIdOrderByBidTimeDesc(String auctionSessionId, String userId);
}