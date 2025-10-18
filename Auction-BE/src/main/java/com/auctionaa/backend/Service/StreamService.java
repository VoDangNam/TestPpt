package com.auctionaa.backend.Service;

import com.auctionaa.backend.DTO.Request.StreamStartRequest;
import com.auctionaa.backend.Entity.AuctionRoom;
import com.auctionaa.backend.Repository.AuctionRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StreamService {
    private final AuctionRoomRepository auctionRoomRepository;
    private final CloudinaryService cloudinaryService;

    public AuctionRoom startStream(StreamStartRequest rq, MultipartFile file) throws IOException {
        AuctionRoom room = new AuctionRoom();
        room.generateId();
        room.setAdminId(rq.getAdminId());
        room.setRoomName(rq.getRoomName());
        room.setDescription(rq.getDescription());
        room.setType(rq.getType());

        // Chủ phòng là member đầu tiên
        room.setMemberIds(new java.util.ArrayList<>(java.util.List.of(rq.getAdminId())));
        // upload ảnh nếu có
        if(file != null && !file.isEmpty()){
            CloudinaryService.UploadResult result =
                    cloudinaryService.uploadImage(file, "auctionaa/liveStream/" + room.getId(), "cover", null);
            room.setImageAuctionRoom(result.getUrl());
        }

        room.setStatus(1);
        room.setViewCount(0);
        room.setStartedAt(LocalDateTime.now());
        room.setCreatedAt(LocalDateTime.now());
        room.setUpdatedAt(LocalDateTime.now());

        return auctionRoomRepository.save(room);

    }


    public Optional<AuctionRoom> getRoom(String roomId){
        Optional<AuctionRoom> roomOpt = auctionRoomRepository.findById(roomId);
        roomOpt.ifPresent(room -> {
            room.setViewCount((room.getViewCount()==null ? 0 : room.getViewCount())+1);
            auctionRoomRepository.save(room);
        });
        return roomOpt;
    }

    public void stopStream(String roomId){
        auctionRoomRepository.findById(roomId).ifPresent(room->{
            room.setStoppedAt(LocalDateTime.now());
            room.setStatus(0);
            auctionRoomRepository.save(room);
        });
    }
}
