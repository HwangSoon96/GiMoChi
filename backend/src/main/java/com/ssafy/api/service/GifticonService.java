package com.ssafy.api.service;

import com.google.protobuf.ByteString;
import com.ssafy.api.dto.GifticonInfoReqDto;
import com.ssafy.api.dto.GifticonPresentReq;
import com.ssafy.api.dto.OcrResDto;
import com.ssafy.common.exception.CustomException;
import com.ssafy.common.exception.ErrorCode;
import com.ssafy.common.util.AmazonS3Util;
import com.ssafy.common.util.VisionApiUtil;
import com.ssafy.db.entity.Gifticon;
import com.ssafy.db.entity.User;
import com.ssafy.db.repository.GifticonRepository;
import com.ssafy.db.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service("GiftconService")
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class GifticonService {

    private final UserRepository userRepository;

    private final AmazonS3Util amazonS3Util;

    private final GifticonRepository gifticonRepository;

    public OcrResDto detect(Long userId, MultipartFile multipartFile) {
        User user = userRepository.findByUserId(userId).
                orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        try {
            VisionApiUtil visionApiUtil = new VisionApiUtil();
            ByteString imgBytes = ByteString.readFrom(multipartFile.getInputStream());
            List<String> words = visionApiUtil.detectText(imgBytes);
            for(String s : words) log.info(s);

            /*
            * 분석 로직 들어가야함
           * */

            OcrResDto ocrResDto = OcrResDto.builder()
                    .user(user)
                    .gifticonStore(words.get(0))
                    .gifticonPeriod("2022-12-31(아직 미완성)")
                    .build();

            return ocrResDto;
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return null;

    }

    @Transactional
    public Gifticon createGifticonInfo(GifticonInfoReqDto gifticonInfoReqDto) {

        User user = userRepository.findByUserId(gifticonInfoReqDto.getUserId()).
                orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Gifticon gifticon = Gifticon.builder()
                .user(user)
                .gifticonStore(gifticonInfoReqDto.getGifticonStore())
                .gifticonPeriod(LocalDate.parse(gifticonInfoReqDto.getGifticonPeriod()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                .gifticonUsed(false)
                .gifticonPath("notAllocated")
                .build();

        return gifticonRepository.save(gifticon);
    }

    @Transactional
    public Gifticon createGifticonImg(Long gifticonId, MultipartFile multipartFile) {

        // 기프티콘 존재하는지 확인
        Gifticon gifticon = gifticonRepository.findById(gifticonId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REQUEST)); // 수정 필요

        // 유저 존재하는지 확인
        User user = userRepository.findByUserId(gifticon.getUser().getUserId()).
                orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String url = null;

        try {
            url = amazonS3Util.upload(multipartFile, user.getUserSocialToken());
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        gifticon.changeGifticonPath(url);

        return gifticonRepository.save(gifticon);
    }

    public List<Gifticon> getGifticonByUserId(Long userId) {
        return gifticonRepository.findAllByUserUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REQUEST)); // 수정 필요
    }

    public List<Gifticon> getGifticonByGifticond(Long gifticonId) {
        return gifticonRepository.findAllByGifticonId(gifticonId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REQUEST)); // 수정 필요
    }

    @Transactional
    public boolean deleteGifticon(Long userId, Long gifticonId) {

        // 유저 존재하는지 확인
        User user = userRepository.findByUserId(userId)
                        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 기프티콘 존재하는지 확인
        Gifticon gifticon = gifticonRepository.findById(gifticonId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REQUEST)); // 수정 필요

        // 넘어온 유저 정보와 기프티콘의 유저정보가 같은지 비교
        if(gifticon.getUser().getUserId() != userId) {
            throw new CustomException(ErrorCode.INVALID_REQUEST); // 수정 필요
        }

        try {
            gifticonRepository.delete(gifticon); // db 삭제
            boolean isDeleteObject = amazonS3Util.delete(gifticon.getGifticonPath()); // 아마존 S3 객체 삭제
            if(isDeleteObject) {
                return true;
            } else {
                return false;
            }
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
        }
        return false;
    }

    @Transactional
    public Gifticon updateGifticon(GifticonPresentReq gifticonPresentReq) {

        Long senderId = gifticonPresentReq.getUserIdSend();
        Long receiverId = gifticonPresentReq.getUserIdReceive();
        Long gifticonId = gifticonPresentReq.getGifticonId();

        User userSender = userRepository.findByUserId(senderId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        User userReceiver = userRepository.findByUserId(receiverId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 기프티콘 존재하는지 확인
        Gifticon gifticon = gifticonRepository.findById(gifticonId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REQUEST)); // 수정 필요

        // 넘어온 선물 보내는 유저 정보와 기프티콘의 유저정보가 같은지 비교
        if(gifticon.getUser().getUserId() != senderId) {
            throw new CustomException(ErrorCode.INVALID_REQUEST); // 수정 필요
        }

        gifticon.changeGifticonUser(userReceiver); // 기프티콘 소유자를 받는 사람으로 변경

        return gifticon;

    }

/*
 * 파일과 reqDto 동시에 받아서 처리하는 서비스 로직 (나중에 다시 해보자)
    @Transactional
    public Gifticon createGifticon(GifticonReqDto gifticonReqDto, MultipartFile multipartFile) {

        User user = userRepository.findByUserId(gifticonReqDto.getUserId()).
                orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String url = null;

        try {
            url = amazonS3Util.upload(multipartFile, user.getUserSocialToken());
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        Gifticon gifticon = Gifticon.builder()
                .user(user)
                .gifticonStore(gifticonReqDto.getGifticonStore())
                .gifticonPeriod(LocalDate.parse(gifticonReqDto.getGifticonPeriod()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                .gifticonUsed(false)
                .gifticonPath(url)
                .build();

        return gifticonRepository.save(gifticon);
    }
*/

}
