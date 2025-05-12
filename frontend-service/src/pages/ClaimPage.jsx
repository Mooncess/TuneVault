import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axiosInstance from '../utils/AdminAxiosInstance';
import MyNavbar from '../components/AdminNavbar';
import styles from '../styles/ClaimPage.module.css';

const ClaimPage = () => {
  const { id } = useParams();
  const [claim, setClaim] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchClaim = async () => {
      try {
        const res = await axiosInstance.get(
          `${process.env.REACT_APP_API_GATEWAY_SERVER_URL}/aps/api/v1/claim/${id}`,
          { withCredentials: true }
        );
        setClaim(res.data);
      } catch (err) {
        console.error('Ошибка при загрузке жалобы:', err);
      }
    };

    fetchClaim();
  }, [id]);

  const handleAccept = async () => {
    try {
      await axiosInstance.put(
        `${process.env.REACT_APP_API_GATEWAY_SERVER_URL}/aps/api/v1/claim/${id}/accept`,
        {},
        { withCredentials: true }
      );
      alert('Жалоба принята. Ресурс заблокирован.');
      navigate('/admin/panel');
    } catch (err) {
      console.error('Ошибка при принятии жалобы:', err);
    }
  };

  const handleReview = async () => {
    try {
      await axiosInstance.put(
        `${process.env.REACT_APP_API_GATEWAY_SERVER_URL}/aps/api/v1/claim/${id}/review`,
        {},
        { withCredentials: true }
      );
      alert('Жалоба отклонена.');
      navigate('/admin/panel');
    } catch (err) {
      console.error('Ошибка при отклонении жалобы:', err);
    }
  };

  const handleBlockProducer = async () => {
    try {
      await axiosInstance.put(
        `${process.env.REACT_APP_API_GATEWAY_SERVER_URL}/aps/api/v1/claim/${id}/block`,
        {},
        { withCredentials: true }
      );
      alert('Пользователь заблокирован.');
      navigate('/admin/panel');
    } catch (err) {
      console.error('Ошибка при блокировке пользователя:', err);
    }
  };

  const goToResource = () => {
    navigate(`/music-resource/${claim.musicResourceId}`);
  };

  const goToProducer = () => {
    navigate(`/producer/${claim.producerId}`);
  };

  if (!claim) return <div>Загрузка...</div>;

  return (
    <div>
      <MyNavbar />
      <div className={styles.container}>
        <h1 className={styles.title}>Жалоба #{claim.id}</h1>
        <div className={styles.content}>
          <p><strong>Отправитель:</strong> {claim.senderEmail}</p>
          <p><strong>Описание:</strong> {claim.description}</p>
          <p><strong>Дата создания:</strong> {claim.createdDate}</p>
          <p><strong>ID ресурса:</strong> {claim.musicResourceId}</p>
          <p><strong>ID продюсера:</strong> {claim.producerId}</p>
        </div>

        <div className={styles.buttonRow}>
          <button className={styles.accept} onClick={handleAccept}>Принять жалобу</button>
          <button className={styles.reject} onClick={handleReview}>Отклонить</button>
          <button className={styles.block} onClick={handleBlockProducer}>Заблокировать пользователя</button>
        </div>

        <div className={styles.buttonRow}>
          <button className={styles.go} onClick={goToResource}>Перейти на страницу ресурса</button>
          <button className={styles.go} onClick={goToProducer}>Перейти на страницу продюсера</button>
        </div>
      </div>
    </div>
  );
};

export default ClaimPage;
