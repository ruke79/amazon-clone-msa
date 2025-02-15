import React from 'react';
import styled from 'styled-components';
import tokenUtil from 'util/tokenUtil';
import { MyChat, OthersChat, OthersChatWithThumbnail } from './ChatMessages';


const Wrapper = styled.main`      
 
position: absolute;
flex: 1;
  top: 50px;
  bottom: 85px;  
  overflow: auto;
  width: 50%;
`;


const ChatContent = props => {

    if (!tokenUtil.getUser()) return;

    const myEmail = tokenUtil.getUser().email;


    const { chattingList, children,  messageRef } = props;

    
    console.log(chattingList);
    
    const renderChatting = chattingList && chattingList.map((chat, idx) => {
    
        const createdAt = new Date(chat.createdAt);
        const localeTime = createdAt.toLocaleTimeString();
        const localeDate = createdAt.toLocaleDateString();
        const removeSecond = localeTime.substring(0, localeTime.length - 3);
        const senderEmail = chat.email;

        

        const prevChat = idx >= 1 ? chattingList[idx - 1] : undefined;
    const prevCreatedAt = prevChat ? new Date(prevChat.createdAt) : '';
    const prevLocaleDate = prevCreatedAt
      ? prevCreatedAt.toLocaleDateString()
      : '';
    const prevLocaleTime = prevCreatedAt
      ? prevCreatedAt.toLocaleTimeString()
      : '';
    const prevRemoveSecond = prevLocaleTime
      ? prevLocaleTime.substring(0, prevLocaleTime.length - 3)
      : '';


        const isPrevSender = prevChat ? prevChat.email === senderEmail : false;
        const isSameDate = prevLocaleDate === localeDate;

         // 채팅한 날짜를 표시
    const getDate = () => {
        let weekday = new Array(7);
        weekday[0] = '일요일';
        weekday[1] = '월요일';
        weekday[2] = '화요일';
        weekday[3] = '수요일';
        weekday[4] = '목요일';
        weekday[5] = '금요일';
        weekday[6] = '토요일';
        const splitDate = localeDate.split('.');
        const day = weekday[createdAt.getDay()];
        return `${splitDate[0].trim()}년 ${splitDate[1].trim()}월 ${splitDate[2].trim()}일 ${day}`;        
      };
      // 지금 채팅 날짜가 이전에 채팅 날짜와 다르면 날짜 표시
      const date = isSameDate ? '' : getDate();

       // 마지막 채팅인 경우
    if (idx === chattingList.length - 1) {
        // 내가 보낸 채팅인 경우
        if (senderEmail === myEmail) {
          return (            
            <MyChat
              msg={chat.content}
            //   notRead={chat.not_read}
              localeTime={removeSecond}
              content={date}
              key={chat.id}
            />            
          );
        }
  
        // 이전에 보낸 채팅과 사람, 날짜가 동일한 경우
        if (isPrevSender && isSameDate) {
          return (
            <OthersChat
              msg={chat.content}
            //   notRead={chat.not_read}
              localeTime={removeSecond}
              key={chat.id}
            />
          );
        }
        return (
          <OthersChatWithThumbnail
            msg={chat.content}
            // user={sender}
            // notRead={chat.not_read}
            localeTime={removeSecond}
            content={date}
            // onImgClick={() => showProfile(sender)}
            key={chat.id}
          />
        );
      }
       /**
         채팅 시간 표시 여부를 결정하기 위해, 다음과 같은 규칙을 적용했습니다. 
         1. 기준 채팅과 다음 채팅을 보낸 사람이 다르면 시간을 표시합니다.
         2. 기준 채팅과 다음 채팅을 보낸 시간 또는 날짜가 다르면 시간을 표시합니다.
         **/
    const afterSender = chattingList[idx + 1];
    const afterCreateAt = new Date(afterSender.createdAt);
    const afterLocaleDate = afterCreateAt.toLocaleDateString();
    const afterLocaleTime = afterCreateAt.toLocaleTimeString();
    const afterRemoveSecond = afterLocaleTime.substring(
      0,
      afterLocaleTime.length - 3
    );
    const isSameTimeWithAfterTime = afterRemoveSecond === removeSecond;
    const isSameDateWithAfterTime = afterLocaleDate === localeDate;
    const time =
      afterSender.email !== senderEmail ||
      !isSameTimeWithAfterTime ||
      !isSameDateWithAfterTime
        ? removeSecond
        : '';
    // 내가 보낸 경우
    if (senderEmail === myEmail) {
      return (
        <MyChat
          msg={chat.content}
        //   notRead={chat.not_read}
          localeTime={time}
          content={date}
          key={chat.id}
        />                 
      );
    }
    // 이전 채팅과 지금 채팅이 보낸 사람, 날짜가 같고, 보낸 시간이 같을 경우
    if (isPrevSender && isSameDate && prevRemoveSecond === removeSecond) {
      return (
        <OthersChat
          msg={chat.content}
          //notRead={chat.not_read}
          localeTime={time}
          key={chat.id}
        />
      );
    }
    return (
      <OthersChatWithThumbnail
        msg={chat.content}
        // user={sender}
        // notRead={chat.not_read}
        localeTime={time}asd
        content={date}
        // onImgClick={() => showProfile(sender)}
        key={chat.id}
      />
    );

     });

    return (
        <Wrapper ref={messageRef}>
          {/* {children} */}
          {renderChatting}
        </Wrapper>
      );

}

export default ChatContent;