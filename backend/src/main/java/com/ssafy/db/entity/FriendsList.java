package com.ssafy.db.entity;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Getter;

import javax.persistence.*;


@Entity
@Getter
@NoArgsConstructor
public class FriendsList{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "friends_list_id")
    private Long friendsListId;

    // 팔로워 (나)
    @Column(name = "follower_id", nullable = false)
    private Long followerId;

    // 팔로잉 (상대방 아이디)
    @Column(name = "following_id", nullable = false)
    private Long followingId;

    @Builder
    public FriendsList(Long followerId, Long followingId) {
        this.followerId = followerId;
        this.followingId = followingId;
    }
}
