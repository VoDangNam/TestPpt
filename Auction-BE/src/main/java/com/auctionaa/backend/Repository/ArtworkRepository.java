package com.auctionaa.backend.Repository;

import com.auctionaa.backend.Entity.Artwork;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ArtworkRepository extends MongoRepository<Artwork, String> {

    // Lấy 6 artwork có giá khởi điểm cao nhất
    List<Artwork> findTop6ByOrderByStartedPriceDesc();

    // Đổi từ findByOwner_Id -> findByOwnerId
    List<Artwork> findByOwnerId(String ownerId);
}
