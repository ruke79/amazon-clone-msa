import styled from 'styled-components';

export const SideBar = styled.aside`
  position: fixed;
  top: 0;
  left: 0;
  width: 100px;
  height: 100%;
  min-height: 100vh;
  background: #dfdfdf;
  padding-top: 20px;
  z-index: 2;
  & li {
    padding: 10px;
    text-align: center;
    font-size: 25px;
    color: #a6a7a8;
    cursor: pointer;
    &:hover {
      color: #888777;
    }
  }
`;
export const Main = styled.main`
  padding-left: 100px;
  width: 100%;
  min-height: 100vh;
`;
export const MainHeader = styled.section`  
  //position: fixed;
  // top: 0;
  //  left: 0;
  padding: 20px 20px 0px 20px;  
  height: 12vh;
  
  background-color: #fff;
  z-index: 1;
  & input {
    border: none;
    outline: none;
    border-radius: 10px;
    background-color: #f6f6f7;
    width: 100%;
    padding: 5px 10px;
    &:focus {
      &::placeholder {
        color: #f6f6f7;
      }
    }
  }
`;
export const TitleBlock = styled.section`
  position: relative;
  & h2 {
    font-size: 20px;
    font-weight: bold;
    margin-bottom: 10px;
  }
  & i {
    cursor: pointer;
    font-size: 20px;
    position: absolute;
    top: 5px;
    right: 0;
  }
`;
export const MainContent = styled.section`  
  flex: 1;
  max-height: 80vh;

  //overflow: scroll;  
  //bottom: 5px;
  //left: 0px;
  //width: 25%;
  overflow: auto;  
  
  
  & li {
    position: relative;
    padding: 20px 100px 20px 40px;
    & img {
      position: absolute;
      top: 18px;
      left: 120px;
      width: 45px;
      height: 45px;
      border-radius: 15px;
      cursor: pointer;
    }
    & p {
      color: #707070;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      min-height: 19px;
      font-size: 12px;
      & b {
        color: #000;
        font-weight: bold;
        font-size: 14px;
      }
    }
    &:hover {
      background-color: #eaeaeb;
    }
  }
`;

export const Notification = styled.span`
  position: absolute;
  display: inline-block;
  padding: 3px;
  color: #fff;
  background-color: #ff513d;
  border: none;
  border-radius: 20px;
  font-weight: bold;
  min-width: 25px;
  text-align: center;
`;
