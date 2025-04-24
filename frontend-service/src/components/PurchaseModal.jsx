import React, { useState } from 'react';
import styles from '../styles/PurchaseModal.module.css'; // импорт стилей
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const PurchaseModal = ({ resourceId, onClose }) => {
  const [email, setEmail] = useState('');
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const navigate = useNavigate();

  const isValidEmail = (email) =>
    /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);

  const handlePurchase = async () => {
    if (!isValidEmail(email)) {
      setError('Пожалуйста, введите корректный email.');
      return;
    }
    try {
      const res = await axios.post(
        `${process.env.REACT_APP_API_GATEWAY_SERVER_URL}/tos/api/v1/sale/${resourceId}?email=${encodeURIComponent(email)}`
      );

      // Извлекаем ID из ответа и переходим на страницу подтверждения
      const confirmPath = res.data; // например, "/confirm/abc123"
      const id = confirmPath.split('/').pop(); // "abc123"
      navigate(`/confirm-page/${id}`);
    } catch (err) {
      setError('Ошибка при оформлении покупки. Попробуйте позже.');
    }
  };

  return (
    <div className={styles['modal-overlay']}>
      <div className={styles['modal-content']}>
        <button className={styles['close-btn']} onClick={onClose}>×</button>
        <h3>Оформление покупки</h3>
        <p>Укажите вашу почту. После успешной оплаты ссылка на скачивание будет отправлена именно на нее.</p>
        <input
          type="email"
          placeholder="Ваш email"
          value={email}
          onChange={(e) => {
            setEmail(e.target.value);
            setError('');
            setSuccessMessage('');
          }}
          className={styles['input']}
        />
        {error && <p className={styles['error-msg']}>{error}</p>}
        {successMessage && <p className={styles['success-msg']}>{successMessage}</p>}
        <button className={styles['submit-btn']} onClick={handlePurchase}>Перейти к оформлению</button>
      </div>
    </div>
  );
};

export default PurchaseModal;
