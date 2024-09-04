import React, { useState, useEffect } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import Heading from "./../headerfooter/Heading";
import Footer from "./../headerfooter/Footer";
import "../../style/customer.css";
import { getCookie } from "../../util/cookieUtil";
function Qna() {
  const [qnaList, setQnaList] = useState([]);
  const [paging, setPaging] = useState({});
  const [beginend, setBeginend] = useState([]);
  const navigate = useNavigate();
  const usercookie = getCookie("user");

  useEffect(() => {
    axios
      .get("/api/qna/qnaList/1")
      .then((result) => {
        setQnaList(result.data.qnaList);
        setPaging(result.data.paging);

        const pageArr = [];
        for (
          let i = result.data.paging.beginPage;
          i <= result.data.paging.endPage;
          i++
        ) {
          pageArr.push(i);
        }
        setBeginend(pageArr);
        console.log("Paging:", paging);
        console.log("Beginend:", beginend);
      })
      .catch((err) => {
        console.error(err);
      });
  }, []);

  function onPageMove(page) {
    // 페이지 표시방식
    axios
      .get(`/api/qna/qnaList/${page}`)
      .then((result) => {
        setQnaList([...result.data.qnaList]);
        setPaging(result.data.paging);

        const pageArr = [];
        for (
          let i = result.data.paging.beginPage;
          i <= result.data.paging.endPage;
          i++
        ) {
          pageArr.push(i);
        }
        setBeginend([...pageArr]);
      })
      .catch((err) => {
        console.error(err);
      });
  }

  async function onQnaView(qseq) {
    let result = await axios.get(`/api/qna/getQnaView/${qseq}`);
    if (result.data.qna.security == "N") {
      let inputPass = window.prompt("패스워드를 입력하세요", "");
      if (inputPass === null) {
        return;
      }
      let res = await axios.post(`/api/qna/passCheck`, null, {
        params: { qseq, inputPass },
      });
      console.log(res.data.msg);

      if (res.data.msg == "OK") {
        navigate(`/QnaView/${qseq}`);
      } else {
        return alert("패스워드가 일치하지 않습니다.");
      }
    } else {
      navigate(`/qnaView/${qseq}`);
    }
  }

  async function writeQna() {
    if (usercookie == null) {
      return alert("로그인 후 이용해주세요");
    } else {
      if (usercookie != null) {
        navigate("/writeQna");
      }
    }
  }

  return (
    <>
      <Heading />
      <div className="relative z-10 bg-white bg-opacity-70 p-5 rounded-lg w-[1100px] mx-auto shadow-lg text-center pt-[100px] mt-[80px] flex-1">
        <div className="w-[1000px] mx-auto flex flex-col items-center">
          <div className="flex justify-between w-full mb-5">
            <div className="text-5xl font-black -mt-[90px]">고객센터</div>
            <button
              className="bg-blue-500 text-white h-10 rounded-md text-lg px-5 transition duration-300 ease-in-out hover:bg-blue-600 mr-7 -mt-[70px]"
              onClick={() => {
                writeQna();
              }}
            >
              1:1 문의 작성
            </button>
          </div>
          <div className="w-full border-b border-black flex justify-between font-black">
            <div className="flex-[2]">번호</div>
            <div className="flex-[4]">제목</div>
            <div className="flex-[3]">등록일</div>
            <div className="flex-[1.5]">답변여부</div>
          </div>
          {qnaList
            ? qnaList.map((qna, idx) => {
                return (
                  <div
                    className="w-full py-2 border-b border-gray-300 flex justify-between text-center"
                    key={idx}
                  >
                    <div
                      className="flex-[2] cursor-pointer"
                      style={{ flex: "2", cursor: "pointer" }}
                    >
                      {qna.qseq}
                    </div>
                    <div
                      className="flex-[4] cursor-pointer flex items-center justify-center"
                      onClick={() => {
                        onQnaView(qna.qseq);
                      }}
                    >
                      <span>{qna.subject}</span>
                      {qna.security == "N" && (
                        <img src="/api/uploads/key.png" className="w-5 h-5 ml-2" />
                      )}
                    </div>
                    <div className="flex-[3]">{qna.indate.substring(0, 10)}</div>
                    <div className="flex-[1.5]">
                      {qna.reply ? <div>답변완료</div> : <div>질문 확인 중</div>}
                    </div>
                  </div>
                );
              })
            : null}
          <div className="text-center py-2 flex-1">
            {paging.prev ? (
              <span
                className="cursor-pointer"
                onClick={() => {
                  onPageMove(paging.beginPage - 1);
                }}
              >
                ◀
              </span>
            ) : (
              <div></div>
            )}
            {beginend ? (
              beginend.map((page, idx) => (
                <span
                  className="cursor-pointer mx-1"
                  key={idx}
                  onClick={() => {
                    onPageMove(page);
                  }}
                >
                  {page}
                </span>
              ))
            ) : (
              <span>1</span>
            )}
            {paging.next ? (
              <span
                className="cursor-pointer"
                onClick={() => {
                  onPageMove(paging.endPage + 1);
                }}
              >
                ▶
              </span>
            ) : (
              <div></div>
            )}
          </div>
        </div>
      </div>
      <Footer />
    </>
  );
  
}

export default Qna;
