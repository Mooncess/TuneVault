import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import Navbar from '../components/Navbar';
import Footer from '../components/Footer';
import styles from '../styles/ListProducerPage.module.css'; // Импорт стилей как модуля

const ProducerCard = ({ producer }) => {
  const navigate = useNavigate();

  const handleClick = () => {
    navigate(`/producer/${producer.id}`);
  };

  return (
    <div className={styles['producer-card']} onClick={handleClick}>
      <div className={styles['logo-container']}>
        <img
          src={
            producer.logoUri && producer.logoUri !== 'default-logo-uri.jpg'
              ? `${process.env.REACT_APP_API_FILE_SERVER_URL}/s3/api/v1/media?name=${producer.logoUri}&type=logo`
              : 'img/Logo.png'
          }
          alt="Логотип"
          className={styles['profile-logo']}
        />
      </div>
      <h3 className={styles['nickname']}>{producer.nickname}</h3>
    </div>
  );
};

const ListProducerPage = () => {
  const [producers, setProducers] = useState([]);

  useEffect(() => {
    const fetchProducers = async () => {
      try {
        const res = await axios.get(
          `${process.env.REACT_APP_API_GATEWAY_SERVER_URL}/mcs/api/v1/producer/`
        );
        setProducers(res.data);
      } catch (err) {
        console.error('Ошибка при загрузке данных продюсеров:', err);
      }
    };

    fetchProducers();
  }, []);

  return (
    <div className={styles.layout}>
      <Navbar />
      <main className={styles['main-content']}>
        <h1 className={styles['title']}>Продюсеры</h1>
        <div className={styles['producer-list']}>
          {producers.map((producer) => (
            <ProducerCard key={producer.id} producer={producer} />
          ))}
        </div>
      </main>
      <Footer />
    </div>
  );
};

export default ListProducerPage;
