import React, { useEffect, useState } from 'react';
import axiosInstance from '../utils/AdminAxiosInstance';
import { useNavigate } from 'react-router-dom';
import MyNavbar from '../components/AdminNavbar';
import styles from '../styles/AdminPanelPage.module.css';

const AdminPanelPage = () => {
  const [claims, setClaims] = useState([]);
  const [statusFilter, setStatusFilter] = useState(0); // 0 = NOT_PROCESSED, 1 = REVIEWED, 2 = ACCEPTED
  const [sortOrder, setSortOrder] = useState(0); // 0 = ASC, 1 = DESC
  const navigate = useNavigate();

  const fetchClaims = async () => {
    try {
      const res = await axiosInstance.get(`${process.env.REACT_APP_API_GATEWAY_SERVER_URL}/aps/api/v1/claim/`, {
        params: {
          status: statusFilter,
          sort: sortOrder
        },
        withCredentials: true
      });
      setClaims(res.data);
    } catch (err) {
      console.error('Ошибка при загрузке жалоб:', err);
    }
  };

  useEffect(() => {
    fetchClaims();
  }, [statusFilter, sortOrder]);

  const handleStatusChange = (newStatus) => {
    setStatusFilter(newStatus);
  };

  const toggleSortOrder = () => {
    setSortOrder((prev) => (prev === 0 ? 1 : 0));
  };

  const handleClaimClick = (id) => {
    navigate(`/admin/claim/${id}`);
  };

  const handleLogout = async () => {
    try {
      await axiosInstance.get(
        `${process.env.REACT_APP_API_GATEWAY_SERVER_URL}/auth/api/v1/logout`,
        { withCredentials: true }
      );
      navigate('/admin/login');
    } catch (err) {
      console.error('Ошибка при выходе:', err);
    }
  };

  return (
    <div>
      <MyNavbar />
      <div className={styles.container}>
        <div className={styles.header}>
          <h1 className={styles.title}>Панель администратора — Жалобы</h1>
          <button className={styles.logoutButton} onClick={handleLogout}>
            Выйти
          </button>
        </div>

        <div className={styles.buttonGroup}>
          <button
            className={`${styles.button} ${statusFilter === 0 ? styles.active : ''}`}
            onClick={() => handleStatusChange(0)}
          >
            Не обработаны
          </button>
          <button
            className={`${styles.button} ${statusFilter === 1 ? styles.active : ''}`}
            onClick={() => handleStatusChange(1)}
          >
            Рассмотрены
          </button>
          <button
            className={`${styles.button} ${statusFilter === 2 ? styles.active : ''}`}
            onClick={() => handleStatusChange(2)}
          >
            Приняты
          </button>
          <button className={styles.button} onClick={toggleSortOrder}>
            Сортировка: {sortOrder === 0 ? '↑ по дате' : '↓ по дате'}
          </button>
        </div>

        <ul className={styles.list}>
          {claims.map((claim) => (
            <li
              key={claim.id}
              className={styles.listItem}
              onClick={() => handleClaimClick(claim.id)}
            >
              <p><strong>ID:</strong> {claim.id}</p>
              <p><strong>Email отправителя:</strong> {claim.senderEmail}</p>
              <p><strong>Описание:</strong> {claim.description}</p>
              <p><strong>Дата создания:</strong> {claim.createdDate}</p>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
};

export default AdminPanelPage;
