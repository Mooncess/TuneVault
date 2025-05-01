import React, { useState } from 'react';
import styles from '../styles/EditProfileModal.module.css';
import axios from 'axios';

const ClaimModal = ({ musicId, onClose }) => {
  const [email, setEmail] = useState('');
  const [description, setDescription] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);

  const handleSend = async () => {
    if (!email || !description) {
      setError('Все поля обязательны');
      return;
    }

    try {
      const res = await axios.post(
        `${process.env.REACT_APP_API_GATEWAY_SERVER_URL}/aps/api/v1/claim/create`,
        {
          senderEmail: email,
          musicResourceId: musicId,
          description,
        }
      );

      if (res.status === 200) {
        setSuccess(true);
        setTimeout(onClose, 1500);
      } else {
        setError('Ошибка при отправке. Попробуйте позже.');
      }
    } catch {
      setError('Не удалось отправить жалобу.');
    }
  };

  return (
    <div className={styles['edit-modal-overlay']}>
      <div className={styles['edit-modal-content']}>
        <button className={styles['edit-close-btn']} onClick={onClose}>×</button>
        <h2>Пожаловаться</h2>

        <input
          type="email"
          placeholder="Ваш email"
          value={email}
          onChange={(e) => {
            setEmail(e.target.value);
            setError('');
          }}
          className={styles['input-field']}
        />

        <textarea
          placeholder="Опишите проблему"
          value={description}
          onChange={(e) => {
            setDescription(e.target.value);
            setError('');
          }}
          className={styles['textarea-field']}
        />

        {error && <p className={styles['edit-error']}>{error}</p>}
        {success && <p style={{ color: 'rgb(29, 205, 159)' }}>Жалоба отправлена!</p>}

        <button className={styles['edit-save-btn']} onClick={handleSend}>Отправить</button>
      </div>
    </div>
  );
};

export default ClaimModal;
