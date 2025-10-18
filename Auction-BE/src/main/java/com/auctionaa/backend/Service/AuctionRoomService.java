package com.auctionaa.backend.Service;

import com.auctionaa.backend.DTO.Request.AuctionRoomRequest;
import com.auctionaa.backend.DTO.Response.AuctionRoomLiveDTO;
import com.auctionaa.backend.Entity.AuctionRoom;
import com.auctionaa.backend.Entity.User;
import com.auctionaa.backend.Repository.AuctionRoomRepository;
import com.auctionaa.backend.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuctionRoomService {
    @Autowired
    private AuctionRoomRepository auctionRoomRepository;

    @Autowired
    private UserRepository userRepository;

    public List<AuctionRoom> getByOwnerEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return auctionRoomRepository.findByMemberIdsContaining(user.getId());
    }

    public List<AuctionRoom> getAllAuctionRoom() {
        return auctionRoomRepository.findAll();
    }

    // Hàm thêm auction room mới
    public AuctionRoom createAuctionRoom(AuctionRoomRequest req, String email) {
        User creator = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        AuctionRoom room = new AuctionRoom();
        room.generateId();
        room.setRoomName(req.getRoomName());
        room.setDescription(req.getDescription());
        room.setImageAuctionRoom(req.getImageAuctionRoom());
        room.setType(req.getType());
        room.setStatus(req.getStatus());
        room.setCreatedAt(LocalDateTime.now());
        room.setUpdatedAt(LocalDateTime.now());

        // admin = người tạo
        room.setAdminId(creator.getId());

        // memberIds ban đầu = danh sách được request + cả admin
        List<String> memberIds = req.getMemberIds() != null ? req.getMemberIds() : new ArrayList<>();
        if (!memberIds.contains(creator.getId())) {
            memberIds.add(creator.getId());
        }
        room.setMemberIds(memberIds);

        return auctionRoomRepository.save(room);
    }

    public List<AuctionRoom> getTop6HotAuctionRooms() {
        return auctionRoomRepository.findTop6ByMembersCount();
    }

    // AuctionRoomService.java
    private static final int SESSION_STATUS_RUNNING = 1;

    public List<AuctionRoomLiveDTO> getRoomsWithLivePrices() {
        return auctionRoomRepository.findRoomsWithLivePrices(SESSION_STATUS_RUNNING);
    }

}
