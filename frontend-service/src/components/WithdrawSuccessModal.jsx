import React, { useEffect } from 'react';
import styles from '../styles/WithdrawSuccessModal.module.css';

const WithdrawSuccessModal = () => {
  useEffect(() => {
    const timer = setTimeout(() => {
      window.location.reload();
    }, 5000);

    return () => clearTimeout(timer);
  }, []);

  return (
    <div className={styles['modal-overlay']}>
      <div className={styles['modal-content']}>
        <h3>Успех</h3>
        <p>Операция успешно выполнена! Обновление страницы через 5 секунд.</p>
      </div>
    </div>
  );
};

export default WithdrawSuccessModal;
