import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { GENRES, TYPES } from "../config/MusicOptions";
import CoAuthorForm from "../components/CoAuthorForm";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import styles from "../styles/MusicResourceCreatePage.module.css";
import axiosInstance from "../utils/AxiosInstance";

const MusicResourceCreatePage = () => {
  const navigate = useNavigate();

  const [name, setName] = useState("");
  const [key, setKey] = useState("");
  const [bpm, setBpm] = useState("");
  const [genre, setGenre] = useState("");
  const [price, setPrice] = useState("");
  const [type, setType] = useState("");
  const [cover, setCover] = useState(null);
  const [demo, setDemo] = useState(null);
  const [source, setSource] = useState(null);
  const [authors, setAuthors] = useState([]);
  const [userEmail, setUserEmail] = useState("");

  useEffect(() => {
    const fetchEmail = async () => {
      try {
        const response = await axiosInstance.get(
          `${process.env.REACT_APP_API_GATEWAY_SERVER_URL}/mcs/api/v1/producer/email`,
          { withCredentials: true }
        );
        setUserEmail(response.data);
      } catch (error) {
        console.error("Не удалось получить email продюсера", error);
      }
    };

    fetchEmail();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (isNaN(Number(bpm)) || isNaN(Number(price))) {
      alert("BPM и Цена должны быть числами");
      return;
    }

    if (!authors || authors.length === 0) {
      alert("Не указаны авторы");
      return;
    }

    const baseInfo = {
      name,
      key,
      bpm: Number(bpm),
      genre,
      price: Number(price),
      type,
      authors,
    };

    const formData = new FormData();
    formData.append("musicResourceBaseInfo", new Blob([JSON.stringify(baseInfo)], { type: "application/json" }));
    if (cover) formData.append("cover", cover);
    if (demo) formData.append("demo", demo);
    if (source) formData.append("source", source);

    try {
      await axiosInstance.post(
        `${process.env.REACT_APP_API_FILE_SERVER_URL}/s3/api/v1/upload`,
        formData,
        {
          headers: { "Content-Type": "multipart/form-data" },
          withCredentials: true,
        }
      );
      navigate("/profile");
    } catch (err) {
      console.error("Ошибка при загрузке ресурса", err);
      alert("Произошла ошибка при создании ресурса.");
    }
  };

  return (
    <div className={styles.layout}>
      <Navbar />
      <main className={styles["main-content"]}>
        <div className={styles["create-page"]}>
          <h1 className={styles["title"]}>Добавить музыкальный ресурс</h1>
          <form onSubmit={handleSubmit}>
            <input
              className={styles["input-field"]}
              name="name"
              type="text"
              placeholder="Название"
              value={name}
              onChange={(e) => setName(e.target.value)}
              required
            />
            <select
              className={styles["input-field"]}
              name="genre"
              value={genre}
              onChange={(e) => setGenre(e.target.value)}
              required
            >
              <option value="">Выберите жанр</option>
              {GENRES.map((g) => (
                <option key={g} value={g}>
                  {g}
                </option>
              ))}
            </select>
            <input
              className={styles["input-field"]}
              name="price"
              type="number"
              placeholder="Цена"
              value={price}
              onChange={(e) => setPrice(e.target.value)}
              required
            />
            <select
              className={styles["input-field"]}
              name="type"
              value={type}
              onChange={(e) => setType(e.target.value)}
              required
            >
              <option value="">Выберите тип</option>
              {TYPES.map((t) => (
                <option key={t} value={t}>
                  {t}
                </option>
              ))}
            </select>

            {type === "Loop" && (
              <>
                <input
                  className={styles["input-field"]}
                  name="bpm"
                  type="number"
                  placeholder="BPM"
                  value={bpm}
                  onChange={(e) => setBpm(e.target.value)}
                  required
                />
                <input
                  className={styles["input-field"]}
                  name="key"
                  type="text"
                  placeholder="Тональность"
                  value={key}
                  onChange={(e) => setKey(e.target.value)}
                  required
                />
              </>
            )}

            <h3 className={styles["subheading"]}>Соавторы</h3>
            <CoAuthorForm ownerEmail={userEmail} onChange={setAuthors} />

            <div className={styles["file-upload"]}>
              <label className={styles["file-label"]}>Обложка</label>
              <input type="file" accept="image/*" onChange={(e) => setCover(e.target.files[0])} />
            </div>
            <div className={styles["file-upload"]}>
              <label className={styles["file-label"]}>Демо-трек</label>
              <input type="file" accept="audio/*" onChange={(e) => setDemo(e.target.files[0])} />
            </div>
            <div className={styles["file-upload"]}>
              <label className={styles["file-label"]}>Исходник</label>
              <input type="file" onChange={(e) => setSource(e.target.files[0])} />
            </div>

            <button type="submit" className={styles["submit-button"]}>
              Создать
            </button>
          </form>
        </div>
      </main>
      <Footer />
    </div>
  );
};

export default MusicResourceCreatePage;
