import React, { useState } from "react";
import styles from '../styles/UpdateLogoModal.module.css'; // импорт стилей
import axiosInstance from '../utils/AxiosInstance';

const UpdateLogoModal = ({ onClose }) => {
  const [logoFile, setLogoFile] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleUpload = async () => {
    if (!logoFile) {
      alert("Пожалуйста, выберите файл.");
      return;
    }

    const formData = new FormData();
    formData.append("logo", logoFile);

    setIsSubmitting(true);

    try {
      await axiosInstance.put(
        `${process.env.REACT_APP_API_FILE_SERVER_URL}/s3/api/v1/update/logo`,
        formData,
        {
          headers: { "Content-Type": "multipart/form-data" },
          withCredentials: true,
        }
      );
      onClose();
      window.location.reload();
    } catch (error) {
      console.error("Ошибка при загрузке логотипа:", error);
      alert("Не удалось загрузить логотип.");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className={styles['update-logo-modal-overlay']}>
      <div className={styles['update-logo-modal']}>
        <h2>Обновить логотип</h2>
        <form className={styles['update-logo-form']} onSubmit={(e) => e.preventDefault()}>
          <input
            type="file"
            accept="image/*"
            onChange={(e) => setLogoFile(e.target.files[0])}
            className={styles['update-logo-input']}
          />
          <div className={styles['update-logo-buttons']}>
            <button
              type="button"
              className={`${styles['update-logo-btn']} ${styles['confirm']}`}
              onClick={handleUpload}
              disabled={isSubmitting}
            >
              {isSubmitting ? "Загрузка..." : "Загрузить"}
            </button>
            <button
              type="button"
              className={`${styles['update-logo-btn']} ${styles['cancel']}`}
              onClick={onClose}
            >
              Отмена
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default UpdateLogoModal;
