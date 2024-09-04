import React, { useState, useEffect } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { useSelector } from "react-redux";
import Heading from "./../headerfooter/Heading";
import Footer from "./../headerfooter/Footer";
import { getCookie } from "../../util/cookieUtil";

function MyQna() {
  const [myqnaList, setMyqnaList] = useState([]);
  const [paging, setPaging] = useState({});
  const [beginend, setBeginend] = useState([]);
  const navigate = useNavigate();

  // 쿠키에서 userid 가져오기
  const userid = getCookie("user")?.userid;

  // Redux에서 로그인 사용자 정보 가져오기
  const loginUser = useSelector((state) => state.user);
  const reduxUserid = loginUser.userid;

  // 로그인 사용자 ID 결정 (쿠키에서 가져오거나 Redux에서 가져오거나)
  const currentUserid = userid || reduxUserid;

  useEffect(() => {
    axios
      .get("/api/qna/qnaList/1")
      .then((result) => {
        const filteredQnaList = result.data.qnaList.filter(
          (qna) => qna.userid === currentUserid,
        );
        const sortedQnaList = filteredQnaList.sort((a, b) => a.qseq - b.qseq);
        setMyqnaList(sortedQnaList);
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
      })
      .catch((err) => {
        console.error(err);
      });
  }, [currentUserid]);

  function onPageMove(page) {
    axios
      .get(`/api/qna/qnaList/${page}`)
      .then((result) => {
        const filteredQnaList = result.data.qnaList.filter(
          (qna) => qna.userid === currentUserid,
        );
        const sortedQnaList = filteredQnaList.sort((a, b) => a.qseq - b.qseq);
        setMyqnaList(sortedQnaList);
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
      })
      .catch((err) => {
        console.error(err);
      });
  }

  // 비밀번호 확인 절차 없이 바로 QnA 상세보기로 이동
  async function onQnaView(qseq) {
    navigate(`/qnaView/${qseq}`);
  }

  return (
    <>
      <Heading />
      <div>
        <div className="background"></div>
      </div>
      <div className="flex-1 w-full max-w-[1500px] mx-auto px-1 mt-[80px]">
        <div className="bg-white bg-opacity-90 p-8 rounded-lg shadow-lg h-200 min-h-full">
          <div className="mt-14">
            <div style={{ display: "flex", justifyContent: "space-between" }}>
              <div className="qnacenter">MY QNA</div>
              <button
                className="button1"
                onClick={() => {
                  navigate("/writeQna");
                }}
              >
                1:1 문의 작성
              </button>
            </div>
            <ul>
              <li className="flex font-bold justify-center items-center text-black border-b border-gray-300 pb-2 mb-2">
                <div className="coll" style={{ flex: "2" }}>
                  번호
                </div>
                <div className="coll" style={{ flex: "4" }}>
                  제목
                </div>
                <div className="coll" style={{ flex: "2" }}>
                  등록일
                </div>
                <div className="coll" style={{ flex: "2" }}>
                  답변여부
                </div>
              </li>
              {myqnaList.length > 0 ? (
                myqnaList.map((qna, idx) => (
                  <li className="flex justify-center items-center text-black border-b border-gray-300 pb-2 mb-2" key={qna.qseq}>
                    <span className="coll" style={{ flex: "2" }}>
                      {qna.qseq}
                    </span>
                    <span
                      className="coll"
                      style={{ flex: "4" }}
                      onClick={() => {
                        onQnaView(qna.qseq);
                      }}
                    >
                      {qna.subject}
                      {qna.security === "Y" ? (
                        <img
                          style={{
                            verticalAlign: "middle",
                            marginLeft: "10px",
                          }}
                          src="api/images/key.png"
                        />
                      ) : null}
                    </span>
                    <span className="coll" style={{ flex: "2" }}>
                      {qna.indate.substring(0, 10)}
                    </span>
                    <span className="coll" style={{ flex: "2" }}>
                      {qna.reply ? (
                        <div>답변완료</div>
                      ) : (
                        <div>질문 확인 중</div>
                      )}
                    </span>
                  </li>
                ))
              ) : (
                <span>등록된 문의가 없습니다.</span>
              )}
              <li id="paging" style={{ textAlign: "center", padding: "10px" }}>
                {paging.prev ? (
                  <span
                    style={{ cursor: "pointer" }}
                    onClick={() => {
                      onPageMove(paging.beginPage - 1);
                    }}
                  >
                    ◀
                  </span>
                ) : (
                  <div></div>
                )}
                {beginend.length > 0 ? (
                  beginend.map((page, idx) => (
                    <span
                      style={{ cursor: "pointer", margin: "0 5px" }}
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
                    style={{ cursor: "pointer" }}
                    onClick={() => {
                      onPageMove(paging.endPage + 1);
                    }}
                  >
                    ▶
                  </span>
                ) : (
                  <span></span>
                )}
              </li>
            </ul>
          </div>
        </div>
      </div>
      <Footer />
    </>
  );
}

export default MyQna;
