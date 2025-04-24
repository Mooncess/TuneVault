import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import styles from '../styles/ConfirmPage.module.css'; // Импорт стилей как модуля

const ConfirmPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState('');

  const handlePayment = async () => {
    try {
      await axios.put(`${process.env.REACT_APP_API_GATEWAY_SERVER_URL}/tos/api/v1/sale/confirm/${id}`);
      setSuccess(true);
      setTimeout(() => navigate('/'), 5000);
    } catch (err) {
      setError('Ошибка при подтверждении оплаты. Попробуйте позже.');
    }
  };

  return (
    <div className={styles['container']}>
      <h2>Платежная форма</h2>
      {!success ? (
        <>
          <button
            onClick={handlePayment}
            className={styles['pay-btn']}
          >
            Оплатить
          </button>
          {error && <p className={styles['error-message']}>{error}</p>}
        </>
      ) : (
        <p className={styles['success-message']}>Оплата прошла успешно! Возвращаем на главную...</p>
      )}
    </div>
  );
};

export default ConfirmPage;
