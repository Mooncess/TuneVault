import React, { useEffect, useState } from 'react';
import axios from '../utils/AxiosInstance';
import Navbar from '../components/Navbar';
import Footer from '../components/Footer';
import styles from '../styles/BalanceHistoryPage.module.css';
import { useNavigate } from 'react-router-dom';
import WithdrawModal from '../components/WithdrawModal';
import WithdrawSuccessModal from '../components/WithdrawSuccessModal';

const BalanceHistoryPage = () => {
  const [balance, setBalance] = useState(0);
  const [entries, setEntries] = useState([]);
  const [type, setType] = useState('revenue');
  const [showWithdrawModal, setShowWithdrawModal] = useState(false);
  const [showSuccessModal, setShowSuccessModal] = useState(false);

  const navigate = useNavigate();

  const fetchBalance = async () => {
    try {
      const res = await axios.get(
        `${process.env.REACT_APP_API_GATEWAY_SERVER_URL}/tos/api/v1/producer-balance`,
        { withCredentials: true }
      );
      setBalance(res.data);
    } catch (err) {
      console.error('Ошибка при загрузке баланса:', err);
    }
  };

  const fetchEntries = async (entryType) => {
    const url =
      entryType === 'revenue'
        ? `${process.env.REACT_APP_API_GATEWAY_SERVER_URL}/tos/api/v1/revenue/find-by-producer`
        : `${process.env.REACT_APP_API_GATEWAY_SERVER_URL}/tos/api/v1/withdraw/find-by-producer`;

    try {
      const res = await axios.get(url, { withCredentials: true });
      setEntries(res.data);
      setType(entryType);
    } catch (err) {
      console.error(`Ошибка при загрузке ${entryType === 'revenue' ? 'поступлений' : 'снятий'}:`, err);
    }
  };

  useEffect(() => {
    fetchBalance();
    fetchEntries('revenue');
  }, []);

  const formatDateTime = (datetime) => {
    return new Date(datetime).toLocaleString('ru-RU');
  };

  const handleWithdrawSuccess = () => {
    setShowWithdrawModal(false);
    setShowSuccessModal(true);
  };

  return (
    <div className={styles.layout}>
      <Navbar />
      <main className={styles['main-content']}>
        <div className={styles['balance-panel']}>
          <span className={styles['balance-text']}>Текущий баланс: {balance} ₽</span>
          <button
            className={styles['withdraw-button']}
            onClick={() => setShowWithdrawModal(true)}
          >
            Вывод средств
          </button>
        </div>

        <div className={styles['toggle-buttons']}>
          <button
            className={`${styles['toggle-button']} ${type === 'revenue' ? styles.active : ''}`}
            onClick={() => fetchEntries('revenue')}
          >
            Поступления
          </button>
          <button
            className={`${styles['toggle-button']} ${type === 'withdraw' ? styles.active : ''}`}
            onClick={() => fetchEntries('withdraw')}
          >
            Снятия
          </button>
        </div>

        <div className={styles['history-list']}>
          {entries.length === 0 ? (
            <p>Нет записей</p>
          ) : (
            entries.map((entry) => (
              <div key={entry.id} className={styles['history-item']}>
                <div className={styles['item-left']}>
                  <span className={styles['amount']}>{entry.amount} ₽</span>
                  {type === 'revenue' && entry.sale && (
                    <>
                      <span className={styles['info']}>Дата: {formatDateTime(entry.sale.saleDate)}</span>
                      <span className={styles['info']}>Покупатель: {entry.sale.buyerEmail}</span>
                      <button
                        className={styles['link-button']}
                        onClick={() => navigate(`/music-resource/${entry.sale.musicResourceId}`)}
                      >
                        Товар ID: {entry.sale.musicResourceId}
                      </button>
                    </>
                  )}
                  {type === 'withdraw' && (
                    <span className={styles['info']}>Дата: {formatDateTime(entry.withdrawDate)}</span>
                  )}
                </div>
              </div>
            ))
          )}
        </div>
      </main>
      <Footer />
      {showWithdrawModal && (
        <WithdrawModal
          onClose={() => setShowWithdrawModal(false)}
          onSuccess={handleWithdrawSuccess}
        />
      )}
      {showSuccessModal && <WithdrawSuccessModal />}
    </div>
  );
};

export default BalanceHistoryPage;
