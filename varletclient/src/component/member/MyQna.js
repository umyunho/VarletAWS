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

  function fetchQna(page) {
    axios
      .get("/api/qna", {
        params: { page: page - 1, size: 20, userid: currentUserid },
      })
      .then((result) => {
        const filteredQnaList = result.data.qnaList.filter(
          (qna) => qna.userid === currentUserid,
        );
        const sortedQnaList = filteredQnaList.sort((a, b) => a.qseq - b.qseq);
        setMyqnaList(sortedQnaList);
        setPaging(result.data.paging);

        const { beginPage, endPage } = result.data.paging;
        const pageArr = [];
        for (let i = beginPage; i <= endPage; i++) {
          pageArr.push(i);
        }
        setBeginend(pageArr);
      })
      .catch((err) => {
        console.error(err);
      });
  }

  // 두번째 인자인 의존성 배열에 들어있는 값이 기존과 다르면, 첫번째 인자의 함수를 실행
  // 처음에 아무 값도 없을 때 새로운 값으로 들어온 것으로 인식해서 최초 1회는 항상 실행
  useEffect(() => {
    fetchQna(1);
  }, [currentUserid]);

  // 비밀번호 확인 절차 없이 바로 QnA 상세보기로 이동
  async function onQnaView(qseq) {
    navigate(`/qnaView/${qseq}`);
  }

  return (
    <>
      <Heading />

      <div className="relative z-10 bg-white bg-opacity-70 p-5 rounded-lg w-[1100px] mx-auto shadow-lg text-center pt-[100px] mt-[80px] flex-1">
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
                      fetchQna(paging.beginPage - 1);
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
                        fetchQna(page);
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
                      fetchQna(paging.endPage + 1);
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
      <Footer />
    </>
  );
}

export default MyQna;
