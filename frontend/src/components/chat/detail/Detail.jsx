
import styled from "styled-components";
//import "./detail.css";

const Wrapper = styled.div`
flex: 1;
width: 100%;
height: 100%;   

.user {
    padding: 30px 20px;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 15px;
    border-bottom: 1px solid #dddddd35;

    img {
      width: 100px;
      height: 100px;
      border-radius: 50%;
      object-fit: cover;
    }
  }

  .info {
    padding: 20px;
    display: flex;
    flex-direction: column;
    gap: 25px;

    .option {
      .title {
        display: flex;
        align-items: center;
        justify-content: space-between;

        img {
          width: 30px;
          height: 30px;
          background-color: rgba(17, 25, 40, 0.3);
          padding: 10px;
          border-radius: 50%;
          cursor: pointer;
        }
      }

      .photos {
        display: flex;
        flex-direction: column;
        gap: 20px;
        margin-top: 20px;

        .photoItem {
          display: flex;
          align-items: center;
          justify-content: space-between;

          .photoDetail {
            display: flex;
            align-items: center;
            gap: 20px;

            img {
              width: 40px;
              height: 40px;
              border-radius: 5px;
              object-fit: cover;
            }

            span {
              font-size: 14px;
              color: lightgray;
              font-weight: 300;
            }
          }

          .icon {
            width: 30px;
            height: 30px;
            background-color: rgba(17, 25, 40, 0.3);
            padding: 10px;
            border-radius: 50%;
            cursor: pointer;
          }
        }
      }
    }
    button {
      padding: 15px;
      background-color: rgba(230, 74, 105, 0.553);
      color: white;
      border: none;
      border-radius: 5px;
      cursor: pointer;

      &:hover {
        background-color: rgba(220, 20, 60, 0.796);
      }

      &.logout{
        padding: 10px;
        background-color: #1a73e8;
      }
    }
  }
`;

const Detail = () => {
  
  
  // const handleLogout = () => {
  //   auth.signOut();
  //   resetChat()
  // };

  return (
    <Wrapper>
      <div className="user">
        {/* <img src={user?.avatar || "./avatar.png"} alt="" />
        <h2>{user?.username}</h2> */}
        <p>Lorem ipsum dolor sit amet.</p>
      </div>
      <div className="info">
        <div className="option">
          <div className="title">
            <span>Chat Settings</span>
            <img src="./arrowUp.png" alt="" />
          </div>
        </div>
        <div className="option">
          <div className="title">
            <span>Chat Settings</span>
            <img src="./arrowUp.png" alt="" />
          </div>
        </div>
        <div className="option">
          <div className="title">
            <span>Privacy & help</span>
            <img src="./arrowUp.png" alt="" />
          </div>
        </div>
        <div className="option">
          <div className="title">
            <span>Shared photos</span>
            <img src="./arrowDown.png" alt="" />
          </div>                  
        </div>
        <div className="option">
          <div className="title">
            <span>Shared Files</span>
            <img src="./arrowUp.png" alt="" />
          </div>
        </div>
        <button >
          {/* {isCurrentUserBlocked
            ? "You are Blocked!"
            : isReceiverBlocked
            ? "User blocked"
            : "Block User"} */}
        </button>
        <button className="logout" >
          Logout
        </button>
      </div>
      </Wrapper>
  );
};

export default Detail;
