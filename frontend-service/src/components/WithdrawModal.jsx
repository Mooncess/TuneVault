import React, { useState } from 'react';
import styles from '../styles/WithdrawModal.module.css';
import { DESTINATION } from '../config/MusicOptions';
import axios from 'axios';

const WithdrawModal = ({ onClose, onSuccess }) => {
  const [amount, setAmount] = useState('');
  const [destination, setDestination] = useState(DESTINATION[0]);
  const [error, setError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleWithdraw = async () => {
    if (isSubmitting) return;
    setError('');
    setIsSubmitting(true);
    try {
      await axios.post(
        `${process.env.REACT_APP_API_GATEWAY_SERVER_URL}/tos/api/v1/withdraw/create`,
        null,
        {
          params: { amount, destination },
          withCredentials: true
        }
      );
      onSuccess();
    } catch (err) {
      setError('Ошибка при выводе средств');
      setIsSubmitting(false); // разрешаем повторную попытку
    }
  };

  return (
    <div className={styles['modal-overlay']}>
      <div className={styles['modal-content']}>
        <button className={styles['close-btn']} onClick={onClose}>×</button>
        <h3>Вывод средств</h3>
        <p>Выберите место назначения и сумму для вывода.</p>

        <select
          className={styles['select']}
          value={destination}
          onChange={(e) => setDestination(e.target.value)}
        >
          {DESTINATION.map((dest, idx) => (
            <option key={idx} value={dest}>{dest}</option>
          ))}
        </select>

        <input
          type="number"
          placeholder="Сумма"
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
          className={styles['input']}
        />

        {error && <p className={styles['error-msg']}>{error}</p>}
        <button
          className={styles['submit-btn']}
          onClick={handleWithdraw}
          disabled={isSubmitting}
        >
          {isSubmitting ? 'Обработка...' : 'Подтвердить'}
        </button>
      </div>
    </div>
  );
};

export default WithdrawModal;
