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

    if (id) fetchData();
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
      console.error("Ошибка при закрытии доступа:", err);
      alert("Ошибка при закрытии доступа к ресурсу.");
    }
  };

  const handleSetAvailable = async () => {
    try {
      await axiosInstance.put(
        `${process.env.REACT_APP_API_GATEWAY_SERVER_URL}/mcs/api/v1/music-resource/${id}/available`,
        {},
        { withCredentials: true }
      );
      setStatus("AVAILABLE");
      alert("Ресурс сделан доступным");
    } catch (err) {
      console.error("Ошибка при открытии доступа:", err);
      alert("Ошибка при открытии доступа к ресурсу.");
    }
  };

  const handleDelete = async () => {
    if (!window.confirm("Вы уверены, что хотите удалить ресурс?")) return;

    try {
      await axiosInstance.put(
        `${process.env.REACT_APP_API_GATEWAY_SERVER_URL}/mcs/api/v1/music-resource/${id}/delete`,
        {},
        { withCredentials: true }
      );
      setStatus("DELETED");
      alert("Ресурс удален");
      navigate("/profile");
    } catch (err) {
      console.error("Ошибка при удалении:", err);
      alert("Ошибка при удалении.");
    }
  };

  return (
    <div className={styles.layout}>
      <Navbar />
      <main className={styles["main-content"]}>
        <div className={styles["create-page"]}>
          <h1 className={styles.title}>Редактировать музыкальный ресурс</h1>

          <p className={styles["subheading"]}>
            <strong>Статус:</strong> {status}
          </p>

          <div style={{ marginBottom: "24px" }}>
            <button
              className={styles["submit-button"]}
              style={{ marginRight: "16px" }}
              type="button"
              onClick={handleSetAvailable}
            >
              Открыть доступ
            </button>
            <button
              className={styles["submit-button"]}
              style={{ marginRight: "16px" }}
              type="button"
              onClick={handleSetUnavailable}
            >
              Закрыть доступ
            </button>
            <button
              className={styles["submit-button"]}
              style={{ marginRight: "16px" }}
              type="button"
              onClick={handleDelete}
            >
              Удалить
            </button>
          </div>

          {/* Форма обновления описания */}
          <form onSubmit={handleInfoSubmit}>
            <input
              className={styles["input-field"]}
              type="text"
              placeholder="Название"
              value={name}
              onChange={(e) => setName(e.target.value)}
              required
            />
            <select
              className={styles["input-field"]}
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
              type="number"
              placeholder="Цена"
              value={price}
              onChange={(e) => setPrice(e.target.value)}
              required
            />
            <select
              className={styles["input-field"]}
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
                  type="number"
                  placeholder="BPM"
                  value={bpm}
                  onChange={(e) => setBpm(e.target.value)}
                  required
                />
                <input
                  className={styles["input-field"]}
                  type="text"
                  placeholder="Тональность"
                  value={key}
                  onChange={(e) => setKey(e.target.value)}
                  required
                />
              </>
            )}

            <button type="submit" className={styles["submit-button"]}>
              Сохранить описание
            </button>
          </form>

          {/* Форма обновления файлов */}
          <form onSubmit={handleFileSubmit} style={{ marginTop: "32px" }}>
            <div className={styles["file-upload"]}>
              <label className={styles["file-label"]}>Обложка (опционально)</label>
              <input type="file" accept="image/*" onChange={(e) => setCover(e.target.files[0])} />
            </div>
            <div className={styles["file-upload"]}>
              <label className={styles["file-label"]}>Демо-трек (опционально)</label>
              <input type="file" accept="audio/*" onChange={(e) => setDemo(e.target.files[0])} />
            </div>
            <div className={styles["file-upload"]}>
              <label className={styles["file-label"]}>Исходник (опционально)</label>
              <input type="file" onChange={(e) => setSource(e.target.files[0])} />
            </div>

            <button type="submit" className={styles["submit-button"]}>
              Сохранить файлы
            </button>
          </form>
        </div>
      </main>
      <Footer />
    </div>
  );
};

export default MusicResourceEditPage;
