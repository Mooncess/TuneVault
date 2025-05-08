// MusicResourceEditPage.jsx
import React, { useState, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { GENRES, TYPES } from "../config/MusicOptions";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import styles from "../styles/MusicResourceCreatePage.module.css";
import axiosInstance from "../utils/AxiosInstance";

const MusicResourceEditPage = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { id } = location.state || {};

  const [name, setName] = useState("");
  const [key, setKey] = useState("");
  const [bpm, setBpm] = useState("");
  const [genre, setGenre] = useState("");
  const [price, setPrice] = useState("");
  const [type, setType] = useState("");
  const [cover, setCover] = useState(null);
  const [demo, setDemo] = useState(null);
  const [source, setSource] = useState(null);
  const [status, setStatus] = useState("");
  const [authors, setAuthors] = useState([]);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const res = await axiosInstance.get(
          `${process.env.REACT_APP_API_GATEWAY_SERVER_URL}/mcs/api/v1/music-resource/${id}`,
          { withCredentials: true }
        );
        const resource = res.data;
        setName(resource.name);
        setKey(resource.key || "");
        setBpm(resource.bpm?.toString() || "");
        setGenre(resource.genre);
        setPrice(resource.price.toString());
        setType(resource.type);
        setStatus(resource.status);
      } catch (err) {
        console.error("Ошибка при загрузке данных ресурса:", err);
      }
    };

    const fetchAuthors = async () => {
      try {
        const res = await axiosInstance.get(
          `${process.env.REACT_APP_API_GATEWAY_SERVER_URL}/mcs/api/v1/music-resource/${id}/authors`,
          { withCredentials: true }
        );
        setAuthors(res.data);
      } catch (err) {
        console.error("Ошибка при загрузке авторов:", err);
      }
    };

    if (id) {
      fetchData();
      fetchAuthors();
    }
  }, [id]);

  const handleInfoSubmit = async (e) => {
    e.preventDefault();

    if (isNaN(Number(bpm)) || isNaN(Number(price))) {
      alert("BPM и Цена должны быть числами");
      return;
    }

    const updateInfo = {
      name,
      key,
      bpm: Number(bpm),
      genre,
      price: Number(price),
    };

    try {
      await axiosInstance.put(
        `${process.env.REACT_APP_API_GATEWAY_SERVER_URL}/mcs/api/v1/music-resource/update-info/${id}`,
        updateInfo,
        { withCredentials: true }
      );
      alert("Описание успешно обновлено");
    } catch (err) {
      console.error("Ошибка при обновлении описания", err);
      alert("Ошибка при обновлении описания ресурса.");
    }
  };

  const handleFileSubmit = async (e) => {
    e.preventDefault();

    const formData = new FormData();
    if (cover) formData.append("cover", cover);
    if (demo) formData.append("demo", demo);
    if (source) formData.append("source", source);

    try {
      await axiosInstance.put(
        `${process.env.REACT_APP_API_FILE_SERVER_URL}/s3/api/v1/update/${id}`,
        formData,
        {
          headers: { "Content-Type": "multipart/form-data" },
          withCredentials: true,
        }
      );
      alert("Файлы успешно обновлены");
    } catch (err) {
      console.error("Ошибка при обновлении файлов", err);
      alert("Ошибка при загрузке файлов.");
    }
  };

  const handleSetUnavailable = async () => {
    try {
      await axiosInstance.put(
        `${process.env.REACT_APP_API_GATEWAY_SERVER_URL}/mcs/api/v1/music-resource/${id}/unavailable`,
        {},
        { withCredentials: true }
      );
      setStatus("UNAVAILABLE");
      alert("Ресурс сделан недоступным");
    } catch (err) {
      console.error("Ошибка при установке ресурса недоступным", err);
    }
  };

  return (
    <div className={styles.layout}>
      <Navbar />
      <main className={styles["main-content"]}>
        <div className={styles["create-page"]}>
          <h1 className={styles["title"]}>Редактировать музыкальный ресурс</h1>

          <form onSubmit={handleInfoSubmit}>
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
              disabled
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

            <button type="submit" className={styles["submit-button"]}>
              Обновить описание
            </button>
          </form>

          <form onSubmit={handleFileSubmit}>
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
              Обновить файлы
            </button>
          </form>

          {status !== "UNAVAILABLE" && (
            <button onClick={handleSetUnavailable} className={styles["submit-button"]}>
              Сделать недоступным
            </button>
          )}

          <div className={styles["authors-section"]}>
            <h3 className={styles["subheading"]}>Авторы</h3>
            {authors.length === 0 ? (
              <p>Нет данных об авторах.</p>
            ) : (
              <ul>
                {authors.map((author, idx) => (
                  <li key={idx}>
                    {author.producer.email} — {author.percentageOfSale}%
                  </li>
                ))}
              </ul>
            )}
          </div>
        </div>
      </main>
      <Footer />
    </div>
  );
};

export default MusicResourceEditPage;
