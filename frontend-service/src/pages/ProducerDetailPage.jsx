import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import MyNavbar from '../components/Navbar';
import MyFooter from '../components/Footer';
import MusicCard from '../components/MusicCard';
import styles from '../styles/ProducerDetailPage.module.css';

const ProducerDetailPage = () => {
  const { id } = useParams();
  const [producer, setProducer] = useState(null);
  const [resources, setResources] = useState([]);
  const [playingId, setPlayingId] = useState(null);

  const handlePlay = (id) => {
    setPlayingId(prevId => (prevId === id ? null : id));
  };

  useEffect(() => {
    // Загрузка данных продюсера
    const fetchProducer = async () => {
      try {
        const res = await fetch(`${process.env.REACT_APP_API_GATEWAY_SERVER_URL}/mcs/api/v1/producer/${id}`);
        const data = await res.json();
        setProducer(data);
      } catch (error) {
        console.error('Ошибка при загрузке продюсера:', error);
      }
    };

    // Загрузка ресурсов продюсера
    const fetchResources = async () => {
      try {
        const res = await fetch(`${process.env.REACT_APP_API_GATEWAY_SERVER_URL}/mcs/api/v1/music-resource/by-producer/${id}`);
        const data = await res.json();
        setResources(data);
      } catch (error) {
        console.error('Ошибка при загрузке ресурсов:', error);
      }
    };

    fetchProducer();
    fetchResources();
  }, [id]);

  return (
    <div className={styles.layout}>
      <MyNavbar />
      <main className={styles.main}>
        {producer && (
          <div className={styles.header}>
            <img
              src={
                producer.logoUri && producer.logoUri !== 'default-logo-uri.jpg'
                  ? `${process.env.REACT_APP_API_FILE_SERVER_URL}/s3/api/v1/media?name=${producer.logoUri}&type=logo`
                  : '/img/Logo.png'
              }
              alt="Логотип продюсера"
              className={styles.logo}
            />
            <h2 className={styles.nickname}>{producer.nickname}</h2>
            <p className={styles.about}>{producer.about}</p>
          </div>
        )}

        <div className={styles.resources}>
          {resources.map(resource => (
            <MusicCard
              key={resource.id}
              id={resource.id}
              name={resource.name}
              price={resource.price}
              coverURI={resource.coverURI}
              demoURI={resource.demoURI}
              type={resource.type}
              producer={producer?.nickname || ''}
              playingId={playingId}
              handlePlay={handlePlay}
            />
          ))}
        </div>
      </main>
      <MyFooter />
    </div>
  );
};

export default ProducerDetailPage;
