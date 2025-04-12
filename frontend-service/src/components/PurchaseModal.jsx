import React, { useState } from 'react';
import '../styles/PurchaseModal.css';
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
        `${process.env.REACT_APP_API_GATEWAY_SERVER_URL}/mcs/api/v1/purchase/${resourceId}?email=${encodeURIComponent(email)}`
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
    <div className="modal-overlay">
      <div className="modal-content">
        <button className="close-btn" onClick={onClose}>×</button>
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
        />
        {error && <p className="error-msg">{error}</p>}
        {successMessage && <p className="success-msg">{successMessage}</p>}
        <button className="submit-btn" onClick={handlePurchase}>Перейти к оформлению</button>
      </div>
    </div>
  );
};

export default PurchaseModal;
