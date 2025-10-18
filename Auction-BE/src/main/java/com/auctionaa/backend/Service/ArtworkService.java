package com.auctionaa.backend.Service;

import com.auctionaa.backend.DTO.Response.ArtworkResponse;
import com.auctionaa.backend.Entity.Artwork;
import com.auctionaa.backend.Entity.User;
import com.auctionaa.backend.Repository.ArtworkRepository;
import com.auctionaa.backend.Repository.BidsRepository;
import com.auctionaa.backend.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
public class ArtworkService {

    private final ArtworkRepository artworkRepository;
    private final CloudinaryService cloudinaryService; // service tự viết để upload

    @Autowired
    private UserRepository userRepository;

    public ArtworkService(ArtworkRepository artworkRepository, CloudinaryService cloudinaryService) {
        this.artworkRepository = artworkRepository;
        this.cloudinaryService = cloudinaryService;
    }

    public List<Artwork> getFeaturedArtworks() {
        return artworkRepository.findTop6ByOrderByStartedPriceDesc();
    }

    public List<Artwork> getAllArtwork() {
        return artworkRepository.findAll();
    }

    public List<Artwork> getByOwnerEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return artworkRepository.findByOwnerId(user.getId());
    }

    public Artwork saveArtwork(Artwork artwork) {
        if (artwork.getId() == null) {
            artwork.generateId();
        }
        artwork.setCreatedAt(LocalDateTime.now());
        artwork.setUpdatedAt(LocalDateTime.now());
        return artworkRepository.save(artwork);
    }

    // Hàm mới thêm
    public Artwork createArtworkWithImages(Artwork artwork,
            MultipartFile avtFile,
            List<MultipartFile> imageFiles,
            String ownerEmail) {
        try {
            // Upload avatar
            String avtUrl = null;
            if (avtFile != null && !avtFile.isEmpty()) {
                avtUrl = cloudinaryService.uploadFile(avtFile);
            }

            // Upload list images
            List<String> urls = new ArrayList<>();
            if (imageFiles != null) {
                for (MultipartFile file : imageFiles) {
                    String url = cloudinaryService.uploadFile(file);
                    urls.add(url);
                }
            }

            // Lấy user owner
            User user = userRepository.findByEmail(ownerEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (artwork.getId() == null) {
                artwork.generateId();
            }

            artwork.setOwnerId(user.getId());
            artwork.setAvtArtwork(avtUrl); // ảnh chính diện
            artwork.setImageUrls(urls); // các ảnh còn lại

            artwork.setCreatedAt(LocalDateTime.now());
            artwork.setUpdatedAt(LocalDateTime.now());

            return artworkRepository.save(artwork);

        } catch (IOException e) {
            throw new RuntimeException("Error creating Artwork with images: " + e.getMessage());
        }
    }

    @Autowired
    private BidsRepository bidsRepository;

    public ArtworkResponse toResponse(Artwork artwork) {
        User user = userRepository.findById(artwork.getOwnerId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        ArtworkResponse dto = new ArtworkResponse();
        dto.setId(artwork.getId());
        dto.setTitle(artwork.getTitle());
        dto.setDescription(artwork.getDescription());
        dto.setAvtArtwork(artwork.getAvtArtwork());
        dto.setImageUrls(artwork.getImageUrls());
        dto.setStatus(artwork.getStatus());
        dto.setAiVerified(artwork.isAiVerified());
        dto.setStartedPrice(artwork.getStartedPrice());
        dto.setPaintingGenre(artwork.getPaintingGenre());
        dto.setYearOfCreation(artwork.getYearOfCreation());
        dto.setMaterial(artwork.getMaterial());
        dto.setSize(artwork.getSize());
        dto.setCertificateId(artwork.getCertificateId());
        dto.setCreatedAt(artwork.getCreatedAt());
        dto.setUpdatedAt(artwork.getUpdatedAt());
        dto.setOwnerName(user.getUsername());

        // Lấy bid mới nhất của chính ownerId
        bidsRepository.findTopByAuctionSessionIdAndUserIdOrderByBidTimeDesc(
                artwork.getId(),
                artwork.getOwnerId()).ifPresent(bid -> dto.setMyLatestBidAmount(bid.getAmountAtThatTime()));

        return dto;
    }

}
