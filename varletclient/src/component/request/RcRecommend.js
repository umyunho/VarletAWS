import React, { useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import jaxios from "../../util/jwtUtil";
import Footer from "../headerfooter/Footer";
import Heading from "../headerfooter/Heading";
import { getCookie } from "../../util/cookieUtil";

function RcRecommend() {
  const { rnum } = useParams();
  const navigate = useNavigate();
  const [content, setContent] = useState("");
  const [accommodationFiles, setAccommodationFiles] = useState([]);
  const [touristSpotFiles, setTouristSpotFiles] = useState([]);
  const [berth, setBerth] = useState(""); // 숙소 이름
  const [tour, setTour] = useState(""); // 관광지 이름
  const [removedFiles, setRemovedFiles] = useState([]);
  const [userCookie] = useState(getCookie("user"));

  const handleSubmitRec = (e) => {
    e.preventDefault();

    const formData = new FormData();
    formData.append("userid", userCookie.userid);
    formData.append("content", content);
    formData.append("berth", berth);
    formData.append("tour", tour);

    // Append accommodation files with image_type
    accommodationFiles.forEach((fileObj) => {
      formData.append("files", fileObj.file);
      formData.append("image_type", "숙소"); // Add image_type for accommodation files
    });

    // Append tourist spot files with image_type
    touristSpotFiles.forEach((fileObj) => {
      formData.append("files", fileObj.file);
      formData.append("image_type", "여행지"); // Add image_type for tourist spot files
    });

    // Append removed files
    removedFiles.forEach((filename) => {
      formData.append("removedimages", filename);
    });

    jaxios
      .postForm(`/api/rcrecommend/writeRecommend/${rnum}`, formData)
      .then((response) => {
        alert("답글 작성에 성공했습니다.");
        setContent("");
        setAccommodationFiles([]);
        setTouristSpotFiles([]);
        setBerth("");
        setTour("");
        setRemovedFiles([]);
        navigate(`/RcommunityView/${rnum}`);
      })
      .catch((error) => {
        alert("답글 작성에 실패했습니다.");
        console.error(error);
      });
  };

  const handleFileChange = (event, setFileState) => {
    const selectedFiles = Array.from(event.target.files).map((file) => ({
      file,
      src: URL.createObjectURL(file),
    }));
    setFileState((prevFiles) => [...prevFiles, ...selectedFiles]);
  };

  const handleRemoveFile = (file, setFileState) => {
    setFileState((prevFiles) =>
      prevFiles.filter((fileObj) => fileObj.file !== file),
    );
  };

  return (
    <>
      <Heading />
      <form onSubmit={handleSubmitRec} className="mt-6 w-full">
        <div className="w-full max-w-7xl mx-auto px-1 min-h-screen">
          <div className="bg-white bg-opacity-90 p-8 shadow-lg min-h-screen mt-[80px]">
            <div>답글 작성</div>
            <div className="p-4">
              {/* 숙소 입력 및 파일 선택 */}
              <div>
                <label
                  htmlFor="berth"
                  className="block mb-2 text-sm font-medium text-gray-900 dark:text-white"
                >
                  숙소
                </label>
                <input
                  type="text"
                  id="berth"
                  value={berth}
                  onChange={(e) => setBerth(e.target.value)}
                  className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500"
                  placeholder="숙소 이름"
                  required
                />
                <input
                  id="accommodation_files"
                  type="file"
                  multiple
                  onChange={(e) => handleFileChange(e, setAccommodationFiles)}
                  className="block w-full text-sm text-gray-900 border border-gray-300 rounded-lg cursor-pointer bg-gray-50 dark:text-gray-400 focus:outline-none dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400"
                />
                <label className="block mt-2">파일 미리보기</label>
                <div className="flex flex-wrap mt-4">
                  {accommodationFiles.map((fileObj, index) => (
                    <div key={index} className="relative">
                      <img
                        src={fileObj.src}
                        alt="preview"
                        style={{
                          width: "300px",
                          height: "300px",
                          objectFit: "cover",
                          border: "1px solid #ddd",
                        }}
                      />
                      <button
                        type="button"
                        onClick={() =>
                          handleRemoveFile(fileObj.file, setAccommodationFiles)
                        }
                        className="absolute top-0 right-0 bg-red-500 text-white p-1 rounded"
                      >
                        X
                      </button>
                    </div>
                  ))}
                </div>
              </div>

              {/* 관광지 입력 및 파일 선택 */}
              <div>
                <label
                  htmlFor="tour"
                  className="block mb-2 text-sm font-medium text-gray-900 dark:text-white"
                >
                  관광지
                </label>
                <input
                  type="text"
                  id="tour"
                  value={tour}
                  onChange={(e) => setTour(e.target.value)}
                  className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500"
                  placeholder="관광지 이름"
                  required
                />
                <input
                  id="tourist_spot_files"
                  type="file"
                  multiple
                  onChange={(e) => handleFileChange(e, setTouristSpotFiles)}
                  className="block w-full text-sm text-gray-900 border border-gray-300 rounded-lg cursor-pointer bg-gray-50 dark:text-gray-400 focus:outline-none dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400"
                />
                <label className="block mt-2">파일 미리보기</label>
                <div className="flex flex-wrap mt-4">
                  {touristSpotFiles.map((fileObj, index) => (
                    <div key={index} className="relative">
                      <img
                        src={fileObj.src}
                        alt="preview"
                        style={{
                          width: "300px",
                          height: "300px",
                          objectFit: "cover",
                          border: "1px solid #ddd",
                        }}
                      />
                      <button
                        type="button"
                        onClick={() =>
                          handleRemoveFile(fileObj.file, setTouristSpotFiles)
                        }
                        className="absolute top-0 right-0 bg-red-500 text-white p-1 rounded"
                      >
                        X
                      </button>
                    </div>
                  ))}
                </div>
              </div>

              {/* 답글 내용 입력 필드 */}
              <div>
                <label
                  htmlFor="content"
                  className="block mb-2 text-sm font-medium text-gray-900 dark:text-white"
                >
                  답글 내용
                </label>
                <textarea
                  id="content"
                  rows="4"
                  placeholder="답글 내용을 입력하세요"
                  value={content}
                  onChange={(e) => setContent(e.target.value)}
                  className="w-full p-2 border border-gray-300 rounded"
                ></textarea>
              </div>

              {/* 작성 완료 버튼 */}
              <div className="mt-4">
                <button
                  type="submit"
                  className="px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                  작성 완료
                </button>
              </div>
            </div>
          </div>
        </div>
      </form>
      <Footer />
    </>
  );
}

export default RcRecommend;
