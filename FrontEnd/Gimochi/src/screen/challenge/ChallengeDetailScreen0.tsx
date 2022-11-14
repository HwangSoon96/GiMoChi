import React, { useState, useEffect, useRef } from 'react';
import { Text, ScrollView, View, TouchableOpacity, StyleSheet } from 'react-native';
import {
  BottomSheet,
  Button,
  ListItem,
  Input,
  Tab,
  TabView,
  ThemeProvider,
  createTheme,
  Slider,
  SpeedDial,
  Dialog,
  Icon,
} from '@rneui/themed';
import axios from 'axios';
import Config from 'react-native-config';
import { useSelector } from 'react-redux';
import { RootState } from '../../store/reducer';
import { useAppDispatch } from '../../store';
import reloadSlice from '../../slices/reload';

function ChallengeDetailScreen0({ route, navigation }) {
  const [time, setTime] = useState(new Date());
  const [caltime, setCalTime] = useState();
  const [visibleDialogF, setVisibleDialogF] = useState(false);
  const [index, setIndex] = useState(0);

  const toggleDialogF = () => {
    setVisibleDialogF(!visibleDialogF);
  };
  // useEffect(() => {
  //   console.log('10초');
  //   setTimeout(() => {
  //     setTime(new Date());
  //   }, 10000);
  // });

  const interval = useRef(null);
  useEffect(() => {
    console.log('5초');
    interval.current = setInterval(() => {
      console.log(new Date());
      console.log(SSTT - new Date() - 32400000);
      setTime(new Date());
      setCalTime(SSTT - new Date() - 32400000);
    }, 5000);
    // 타이머 컴포넌트가 언마운트 될 때 실행
    return () => clearInterval(interval.current);
  });
  // Start End Time 테스트용  !! 한국 표준시 9시간 빼줘야함 -32400000
  // 86400000 24시간 3600000 1시간  60000 1분  1000 1초
  const SSTT = new Date('2022-11-16');
  const EETT = new Date('2022-11-09');

  // var result = parseInt(13 / 5); // 값은 2
  // var remainder = 13 % 5; // 값은 3

  const dd = parseInt(caltime / 86400000);
  const d = parseInt(caltime / 86400000) + 1;
  var tt = caltime - dd * 86400000;
  const h = parseInt(tt / 3600000);
  var tt = caltime - dd * 86400000 - h * 3600000;
  const m = parseInt(tt / 60000);
  var tt = caltime - dd * 86400000 - h * 3600000 - m * 60000;
  const t = parseInt(tt / 1000);
  console.log(dd, h, m, t);

  console.log(h);

  const userId = useSelector((state: RootState) => state.user.userId);
  // console.log(userId);

  const challegneId = route.params.challengeId;
  // console.log(challegneId);

  const goMain = () => {
    navigation.navigate('ChallengeMainScreen');
  };

  return (
    <View style={{ backgroundColor: '#fff', flex: 1 }}>
      <ScrollView>
        <Text>Current Date Time</Text>
        <Text>
          {time.getHours()}시 {time.getMinutes()}분 {time.getSeconds()}초
        </Text>
        <Text>
          SSTT | D-{d ? d : ''} | {h ? h : ''} | {m ? m : ''} | {t ? t : ''}
        </Text>
        <View
          style={{
            flexDirection: 'row',
            height: 80,
            padding: 20,
            marginRight: 60,
          }}
        >
          <Text style={{ fontSize: 25, marginTop: 5, fontFamily: 'Regular' }}>제목{'  '}:</Text>
        </View>
        <View
          style={{
            flexDirection: 'row',
            height: 80,
            padding: 20,
            marginRight: 60,
          }}
        >
          <Text style={{ fontSize: 25, marginTop: 5, fontFamily: 'Regular' }}>기간{'  '}: S~E매일</Text>
        </View>
        <View
          style={{
            flexDirection: 'row',
            height: 150,
            marginRight: 60,
          }}
        >
          <Text style={{ fontSize: 25, marginTop: 5, fontFamily: 'Regular' }} onPress={() => toggleDialogF()}>
            참가 인원 1명 | 친구초대(친구목록(보내기/취소하기)|초대현황(대기중/참가중/취소아이콘)):
          </Text>
          <Dialog isVisible={visibleDialogF} onBackdropPress={toggleDialogF} overlayStyle={{ height: 300 }}>
            {/* <Dialog.Title title='친구친구' />
            <Text>참가 인원 1명 |</Text>
            <Text>참가 인원 1명 |</Text> */}

            <Tab
              // index 0 :포인트  1 :기프티콘
              value={index}
              onChange={(e) => setIndex(e)}
              indicatorStyle={{
                height: 0,
              }}
              style={{
                borderRadius: 21,
                backgroundColor: '#F6F6F6',
                marginTop: 21,
                marginHorizontal: 10,
                height: 42,
              }}
              variant='primary'
            >
              <Tab.Item
                title='초대 현황'
                containerStyle={{
                  borderRadius: 20,
                  backgroundColor: index == 0 ? 'white' : '#F6F6F6',
                  margin: 2,
                  height: 38,
                  padding: 0,
                }}
                titleStyle={{
                  fontSize: 20,
                  color: index == 0 ? '#FFA401' : '#686868',
                  paddingHorizontal: 0,
                  paddingVertical: 0,
                  fontWeight: '900',
                }}
              />
              <Tab.Item
                title='친구 목록'
                containerStyle={{
                  borderRadius: 20,
                  backgroundColor: index == 1 ? 'white' : '#F6F6F6',
                  margin: 2,
                  height: 38,
                  padding: 0,
                }}
                titleStyle={{
                  fontSize: 20,
                  color: index == 1 ? '#FFA401' : '#686868',
                  paddingHorizontal: 0,
                  paddingVertical: 0,
                }}
              />
            </Tab>

            <TabView value={index} onChange={setIndex} animationType='spring'>
              {index == 0 ? (
                <TabView.Item style={{ backgroundColor: 'red', width: '100%', height: 100 }}>
                  <ScrollView style={{ backgroundColor: '#F6F6', marginHorizontal: 10, borderRadius: 20 }}>
                    <>
                      <Text>대기중</Text>
                      <Text>수락완료</Text>
                      <Icon
                        name='close'
                        type='fontisto'
                        color='#FFE7BC'
                        size={25}
                        iconStyle={{ fontSize: 33 }}
                      />
                    </>
                  </ScrollView>
                </TabView.Item>
              ) : (
                <TabView.Item></TabView.Item>
              )}
              {index == 1 ? (
                <TabView.Item style={{ backgroundColor: 'blue', width: '100%', height: 100 }}>
                  <ScrollView style={{ backgroundColor: '#F6F6', marginHorizontal: 10, borderRadius: 20 }}>
                    <>
                      <Text>초대하기</Text>
                      <Text>취소하기</Text>
                    </>
                  </ScrollView>
                </TabView.Item>
              ) : (
                <TabView.Item></TabView.Item>
              )}
            </TabView>
          </Dialog>
        </View>
        <View
          style={{
            flexDirection: 'row',
            height: 80,
            padding: 20,
            marginRight: 60,
          }}
        >
          <Text style={{ fontSize: 25, marginTop: 5, fontFamily: 'Regular' }}>
            누적 상금|기프티콘 /?아이콘
          </Text>
        </View>
        <View
          style={{
            flexDirection: 'row',
            height: 80,
            padding: 20,
            marginRight: 60,
          }}
        >
          <Text style={{ fontSize: 25, marginTop: 5, fontFamily: 'Regular' }}>인증 방법 확인하기 (모달)</Text>
        </View>
        <View
          style={{
            flexDirection: 'row',
            height: 80,
            padding: 20,
            marginRight: 60,
          }}
        >
          <Text style={{ fontSize: 25, marginTop: 5, fontFamily: 'Regular' }}>
            타임스탬프 00되면 자동시작
          </Text>
        </View>
      </ScrollView>
      <Icon
        name='delete'
        type='material'
        color='#FFE7BC'
        size={25}
        reverse
        reverseColor='#FFA401'
        onPress={() => goMain()}
        iconStyle={{ fontSize: 33 }}
        containerStyle={{ position: 'absolute', top: '85%', left: '5%' }}
      />
    </View>
  );
}

export default ChallengeDetailScreen0;
