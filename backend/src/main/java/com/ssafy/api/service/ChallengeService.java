package com.ssafy.api.service;

import com.ssafy.api.dto.ChallengeInfoReqDto;
import com.ssafy.api.dto.ChallengeReqDto;
import com.ssafy.common.exception.CustomException;
import com.ssafy.common.exception.ErrorCode;
import com.ssafy.db.entity.Challenge;
import com.ssafy.db.entity.ChallengeInfo;
import com.ssafy.db.entity.User;
//import com.ssafy.db.repository.ChallengeInfoRepository;
import com.ssafy.db.repository.ChallengeInfoRepository;
import com.ssafy.db.repository.ChallengeRepository;
import com.ssafy.db.repository.ChallengeRewardRepository;
import com.ssafy.db.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service("ChallengeService")
@Transactional(readOnly = true)
public class ChallengeService {

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private ChallengeInfoRepository challengeInfoRepository;
    @Autowired
    private ChallengeRewardRepository challengeRewardRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public boolean createChllenge(ChallengeReqDto challengeReqDto){

        //challenge id가 없는 경우(challenge 만들면서 info도 (방장이추가)
        Challenge challenge = Challenge.builder()
                .challengeTitle(challengeReqDto.getChallengeTitle())
                .challengeLeaderId(challengeReqDto.getChallengeLeaderId())
                .challengeDescription(challengeReqDto.getChallengeDescription())
                .challengeParticipant(challengeReqDto.getChallengeParticipant())
                .challengeStartTime(challengeReqDto.getChallengeStartTime())
                .challengeEndTime(challengeReqDto.getChallengeEndTime())
                .challengeRewardType(challengeReqDto.getChallengeRewardType())
                .build();

        challengeRepository.save(challenge);

        createChallengeInfoFirst(challenge);

        return true;
    }
    public List<Challenge> getChallengeList(){

        return challengeRepository.findAll();
    }


    public Optional<Challenge> getChallengeListByChallengeId(Long challengeId) {
        return challengeRepository.findByChallengeId(challengeId);
    }

    //challengeInfo를 먼저 찾아서 삭제하고 해야됨
    @Transactional
    public boolean deleteChallenge(Long challengeId) {
        Challenge challenge = challengeRepository.findByChallengeId(challengeId).orElseThrow(() -> new CustomException(ErrorCode.CHALLENEGE_NOT_FOUND));
        try {
            challengeRepository.delete(challenge);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public Challenge findChallengeByChallengeId(Long challengeId) {
        return challengeRepository.findByChallengeId(challengeId).orElseThrow(() -> new CustomException(ErrorCode.CHALLENEGE_NOT_FOUND));
    }

    public User findUserByUserId(Long userId) {
        return userRepository.findByUserId(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    public User findBychallengeLeaderId(Long userId){
        return challengeRepository.findByChallengeLeaderId(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public ChallengeInfo createChallengeInfoFirst(Challenge challenge) {

        User user =findUserByUserId(challenge.getChallengeLeaderId());
        ChallengeInfo challengeInfo = ChallengeInfo.builder()
                .challenge(challenge)
                .user(user)
                .build();

        return challengeInfoRepository.save(challengeInfo);
    }

    //challengeInfo 만들기
    //해당 challengeId에 유저들 추가
    @Transactional
    public ChallengeInfo addUserChallengeInfo(ChallengeInfoReqDto challengeInfoReqDto) {
        Challenge challenge= findChallengeByChallengeId(challengeInfoReqDto.getChallengeId());
        User user =findUserByUserId(challengeInfoReqDto.getUserId());
        ChallengeInfo challengeInfo = ChallengeInfo.builder()
                .challenge(challenge)
                .user(user)
                .successCnt(challengeInfoReqDto.getSuccessCnt())
                .build();

        return challengeInfoRepository.save(challengeInfo);
    }


}

