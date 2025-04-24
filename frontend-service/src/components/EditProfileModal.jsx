import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import styles from '../styles/EditProfileModal.module.css';
import axiosInstance from '../utils/AxiosInstance';

const EditProfileModal = ({ profile, onClose }) => {
  const [nickname, setNickname] = useState(profile.nickname || '');
  const [about, setAbout] = useState(profile.about || '');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSave = async () => {
    if (!nickname.trim()) {
      setError('Никнейм не может быть пустым.');
      return;
    }

    try {
      const res = await axiosInstance.put(
        `${process.env.REACT_APP_API_GATEWAY_SERVER_URL}/mcs/api/v1/producer/update-info`,
        { nickname, about },
        { withCredentials: true }
      );
      if (res.status === 200) {
        onClose();
        window.location.reload();
      } else {
        setError('Произошла ошибка. Попробуйте позже.');
      }
    } catch (err) {
      setError('Не удалось обновить профиль. Попробуйте позже.');
    }
  };

  return (
    <div className={styles['edit-modal-overlay']}>
      <div className={styles['edit-modal-content']}>
        <button className={styles['edit-close-btn']} onClick={onClose}>×</button>
        <h2>Редактирование профиля</h2>

        <label htmlFor="nickname">Никнейм</label>
<input
  id="nickname"
  type="text"
  placeholder="Никнейм"
  value={nickname}
  onChange={(e) => {
    setNickname(e.target.value);
    setError('');
  }}
  className={styles['input-field']}
/>

<label htmlFor="about">О себе</label>
<textarea
  id="about"
  placeholder="О себе"
  value={about}
  onChange={(e) => {
    setAbout(e.target.value);
    setError('');
  }}
  className={styles['textarea-field']}
/>


        {error && <p className={styles['edit-error']}>{error}</p>}

        <button className={styles['edit-save-btn']} onClick={handleSave}>Сохранить</button>
      </div>
    </div>
  );
};

export default EditProfileModal;
