/* eslint-disable @typescript-eslint/no-unsafe-call */
/* eslint-disable @typescript-eslint/restrict-template-expressions */
/* eslint-disable @typescript-eslint/no-unsafe-argument */
/* eslint-disable @typescript-eslint/no-unsafe-member-access */
/* eslint-disable @typescript-eslint/no-unsafe-assignment */
import React, { useEffect, useState } from 'react';
import { View, Text } from 'react-native';
import axios from 'axios';
import Config from 'react-native-config';
import { useSelector } from 'react-redux';
import { RootState } from '../../store/reducer';
import { List } from 'reselect/es/types';
import Modal from 'react-native-modal';
import styled from 'styled-components/native';
import { FontAwesomeIcon } from '@fortawesome/react-native-fontawesome';
import { faMessage } from '@fortawesome/free-solid-svg-icons';
import { useAppDispatch } from '../../store';
import screenSlice from '../../slices/screen';

function RPDetailScreen({ navigation, route }) {
  const sessionId: number = route.params.RPId;
  const sessionTypeId: number = route.params.sessionTypeId;
  const reload = useSelector((state: RootState) => state.reload.reload);
  const [messageList, setMessageList] = useState([]);
  const [modal, setModal] = useState<boolean>(false);
  const [nickname, setNickname] = useState('');
  const [text, setText] = useState('');
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(
      screenSlice.actions.addScreen({
        screen: 'RollingpaperScreen',
      }),
    );
    return () => {
      console.log('unmount');
      dispatch(screenSlice.actions.deleteScreen());
    };
  }, []);

  useEffect(() => {
    navigation.setOptions({
      title:
        sessionTypeId === 1
          ? '생일 추카포카'
          : sessionTypeId === 2
          ? '졸업 추카포카'
          : sessionTypeId === 3
          ? '크리스마스 추카포카'
          : sessionTypeId === 4
          ? '설날 추카포카'
          : '기타 추카포카',
    });
  }, [navigation]);

  useEffect(() => {
    axios
      .get(`${Config.API_URL}/session/${sessionId}`)
      .then(function (response) {
        console.log(response);
        console.log(response.data.data.anniversary);
        console.log(response.data.data.sessionMessagesList);
        setMessageList(response.data.data.sessionMessagesList);
      })
      .catch(function (error) {
        console.log(error);
      });
  }, [reload]);

  const goMessageDetail = (index) => {
    // setIdx(index);
    setNickname(messageList[index].nickname);
    setText(messageList[index].field);
    setModal(true);
  };

  const onPress = () => {
    navigation.navigate('RPMessageWriteScreen', { RPId: sessionId });
  };

  return (
    <RPDetailContainer>
      <Text>{sessionId}번 추카포카</Text>
      <MessageListContainer>
        {messageList.map((message, index) => (
          <MessageItemButton key={index} onPress={() => goMessageDetail(index)}>
            <Text>{index}번 메시지</Text>
          </MessageItemButton>
        ))}
      </MessageListContainer>
      <CreateButton onPress={onPress}>
        <FontAwesomeIcon icon={faMessage} size={50} color={'#ffa401'} />
      </CreateButton>
      <Modal
        animationType='fade'
        transparent={true}
        visible={modal}
        // presentationStyle={'pageSheet'}
        onRequestClose={() => {
          // setModalVisible(!modalVisible);
          setModal(false);
        }}
        onBackdropPress={() => setModal(false)}
      >
        <ModalContainer>
          <ModalText>{nickname}님이 보낸 선물</ModalText>
          <ModalText>대충 선물</ModalText>
          <ModalText>{nickname}님이 보낸 메시지</ModalText>
          <ModalText>{text}</ModalText>
        </ModalContainer>
      </Modal>
    </RPDetailContainer>
  );
}

const RPDetailContainer = styled.View`
  background-color: #ffffff;
  flex: 1;
`;

const MessageListContainer = styled.View``;

const MessageItemButton = styled.TouchableOpacity``;

const CreateButton = styled.TouchableOpacity`
  position: absolute;
  left: 320px;
  top: 480px;
`;

const ModalContainer = styled.View`
  margin: 15%;
  width: 70%;
  height: 70%;
  background-color: #ffe7bc;
  border-radius: 15px;
  border: 1px solid #000;
  align-items: center;
`;

const ModalText = styled.Text`
  margin: 5% auto 5% 10%;
  font-family: 'Regular';
  font-size: 15px;
`;

export default RPDetailScreen;
