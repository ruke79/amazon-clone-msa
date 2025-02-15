import React, { useState } from 'react';
import styled from 'styled-components';

// const Wrapper = styled.footer`
//   position: fixed;
//   bottom: 0px;
//   left: 0px;
//   right: 0px;
//   width: 100%;
//   min-height: 50px;
//   max-height: 200px;
//   overflow: auto;
//   padding: 6px;
//   z-index: 100;
//   background-color: #eeeeee;
//   & form {
//     position: relative;
//     display: flex;
//     width: 100%;
//     height: 100%;
//     & textarea,
//     button {
//       display: inline-block;
//       border: none;
//       outline: none;
//     }
//     & textarea {
//       width: 100%;
//       resize: none;
//       height: 100%;
//       margin: 0;
//       padding: 5px 20px;
//     }
//     & button {
//       width: 50px;
//       height: 40px;
//       background: #ffeb33;
//       &.canSubmit {
//         cursor: pointer;
//         pointer-events: all;
//         color: #000;
//       }
//       &.cannotSubmit {
//         pointer-events: none;
//         color: #b4b4b4;
//       }
//     }
//   }
// `;

const Wrapper = styled.footer`

  position: fixed;
  bottom: 0px;  
  width: 50%;
  min-height: 25px;
  max-height: 200px;     
   background-color:rgb(150, 142, 142);  
   
    padding: 10px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    border-top: 1px solid #dddddd35;
    gap: 20px;
    margin-top: auto;
  
   .icons {
     padding: 10px;
      display: flex;
      gap: 20px;
    }

      img {
      width: 40px;
      height: 40px;
      cursor: pointer;
    }

    input {      
      flex: 1;
      background-color: rgba(17, 25, 40, 0.5);
      border: none;
      outline: none;
      color: white;
      padding: 20px;
      border-radius: 10px;
      font-size: 16px;

      &.disabled {
        cursor: not-allowed;
      }
    }

    &.emoji {
      position: relative;

      &.picker {
        position: absolute;
        bottom: 50px;
        left: 0;
      }
    }

    .sendButton {      
      background-color: #5183fe;
      color: white;
      padding: 10px 20px;
      border: none;
      border-radius: 5px;
      cursor: pointer;
      z-index : 100;

        &.disabled {
        background-color: #5182feb4;
        cursor: not-allowed;
      }    
    }

`;



const ChatFooter = ({ onChatSumbmit }) => {
    const [message, setMessage] = useState('');

    const [img, setImg] = useState({
      file: null,
      url: "",
    });

    // 채팅 내용이 공백이라면, 채팅을 보낼 수 없도록 설정하였습니다.
  const isCanSubmit = !!message.replace(/ |\n/g, '');
  const btnClassName = isCanSubmit ? 'canSubmit' : 'cannotSubmit';
  const onMessageChange = (event) => {
    event.preventDefault();
    const value = event.target.value;
    setMessage(value);
  };
  const requestSubmit = () => {
    if (isCanSubmit) {
      onChatSumbmit(message);
      setMessage('');
    }
  };

  const onSubmit = (event) => {
    event.preventDefault();
    requestSubmit();
  };
  const onEnterPress = (event) => {
    // shift + enter 이면 줄바꿈이 되고, enter키만 누르면 채팅 전송이 됩니다.
    if (!event.shiftKey && event.key === 'Enter') {
      event.preventDefault();
      requestSubmit();
    }
  };

  const handleImg = (e) => {
    if (e.target.files[0]) {
      setImg({
        file: e.target.files[0],
        url: URL.createObjectURL(e.target.files[0]),
      });
    }
  };

  return (
    <Wrapper>
         <div className={"icons"}>
          <label htmlFor="file">
            <img src="./img.png" alt="" />
          </label>
          <input
            type="file"
            id="file"
            style={{ display: "none" }}
            onChange={handleImg}
          />
          {/* <img src="./camera.png" alt="" />
          <img src="./mic.png" alt="" /> */}
        </div>
        <input
          type="text"
          // placeholder={
          //   isCurrentUserBlocked || isReceiverBlocked
          //     ? "You cannot send a message"
          //     : "Type a message..."
          // }
          value={message}
          onChange={onMessageChange}
          onKeyDown={onEnterPress}
          // disabled={isCurrentUserBlocked || isReceiverBlocked}
        />
         <button  type="button"
          className={"sendButton"}
          onClick={onSubmit}          
        >
          Send
        </button>
      
    </Wrapper>
  );
};

export default ChatFooter;