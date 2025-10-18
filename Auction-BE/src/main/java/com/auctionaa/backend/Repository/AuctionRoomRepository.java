package com.auctionaa.backend.Repository;

import com.auctionaa.backend.DTO.Response.AuctionRoomLiveDTO;
import com.auctionaa.backend.Entity.AuctionRoom;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AuctionRoomRepository extends MongoRepository<AuctionRoom, String> {
    // Tìm phòng theo memberId
    List<AuctionRoom> findByMemberIdsContaining(String memberId);

    @Aggregation(pipeline = {
            "{ $project: { roomName: 1, description: 1, memberIds: 1, imageAuctionRoom: 1, type: 1, status: 1, createdAt: 1, updatedAt: 1, membersCount: { $size: '$memberIds' } } }",
            "{ $sort: { membersCount: -1 } }",
            "{ $limit: 6 }"
    })
    List<AuctionRoom> findTop6ByMembersCount();

    // (nên sửa tên) dùng contains: List<AuctionRoom> findByMemberIdsContains(String memberId);

    @Aggregation(pipeline = {
            "{ $lookup: { " +
                    "   from: 'auction_sessions', " +
                    "   let: { roomId: '$_id' }, " +
                    "   pipeline: [" +
                    "     { $match: { $expr: { $and: [ { $eq: ['$auctionRoomId', '$$roomId'] }, { $eq: ['$status', ?0] } ] } } }," +
                    "     { $sort: { startTime: -1 } }, { $limit: 1 } ], " +
                    "   as: 'live' } }",
            "{ $addFields: { live: { $first: '$live' } } }",
            "{ $project: { " +
                    "   _id: 1, roomName: 1, imageAuctionRoom: 1, type: 1, status: 1, memberIds: 1," + // 👈 thêm memberIds
                    "   sessionId: '$live._id', startTime: '$live.startTime', endTime: '$live.endTime', " +
                    "   startingPrice: '$live.startingPrice', currentPrice: '$live.currentPrice' } }"
    })
    List<AuctionRoomLiveDTO> findRoomsWithLivePrices(int runningStatus);


}
