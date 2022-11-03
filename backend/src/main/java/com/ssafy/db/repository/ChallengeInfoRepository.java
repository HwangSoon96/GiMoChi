package com.ssafy.db.repository;

import com.ssafy.db.entity.Challenge;
import com.ssafy.db.entity.ChallengeInfo;
import com.ssafy.db.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChallengeInfoRepository extends JpaRepository<ChallengeInfo, Long> {

    @Query("select ch from ChallengeInfo ch join fetch ch.challenge where ch.challenge.challengeId = :challengeId")
    //@Query(value= "select ch.user_id from ChallengeInfo ch left join Challenge c on ch.challenge_id=c.challenge_id")
    Optional<List<ChallengeInfo>> findUserListByChallengeId(Long challengeId);

}
