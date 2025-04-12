import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';

const ConfirmPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState('');

  const handlePayment = async () => {
    try {
      await axios.put(`${process.env.REACT_APP_API_GATEWAY_SERVER_URL}/mcs/api/v1/purchase/confirm/${id}`);
      setSuccess(true);
      setTimeout(() => navigate('/'), 5000);
    } catch (err) {
      setError('Ошибка при подтверждении оплаты. Попробуйте позже.');
    }
  };

  return (
    <div style={{ padding: '2rem', textAlign: 'center' }}>
      <h2>Платежная форма</h2>
      {!success ? (
        <>
          <button
            onClick={handlePayment}
            style={{
              marginTop: '1rem',
              padding: '0.5rem 1.5rem',
              fontSize: '1rem',
              backgroundColor: '#4CAF50',
              color: 'white',
              border: 'none',
              borderRadius: '8px',
              cursor: 'pointer'
            }}
          >
            Оплатить
          </button>
          {error && <p style={{ color: 'red', marginTop: '1rem' }}>{error}</p>}
        </>
      ) : (
        <p style={{ color: 'green', fontSize: '1.2rem' }}>Оплата прошла успешно! Возвращаем на главную...</p>
      )}
    </div>
  );
};

export default ConfirmPage;
