import React, { useRef, useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import styles from '../styles/MusicCard.module.css';

const MusicCard = ({ id, name, price, coverURI, demoURI, type, producer, playingId, handlePlay }) => {
  const navigate = useNavigate();
  const [coverUrl, setCoverUrl] = useState('');
  const [demoUrl, setDemoUrl] = useState('');

  useEffect(() => {
    const fetchMedia = async () => {
      try {
        if (coverURI) {
          const coverResponse = await axios.get(
            `${process.env.REACT_APP_API_FILE_SERVER_URL}/s3/api/v1/media?name=${coverURI}&type=cover`,
            { responseType: 'blob' }
          );
          setCoverUrl(URL.createObjectURL(coverResponse.data));
        }

        if (demoURI) {
          const demoResponse = await axios.get(
            `${process.env.REACT_APP_API_FILE_SERVER_URL}/s3/api/v1/media?name=${demoURI}&type=demo`,
            { responseType: 'blob' }
          );
          setDemoUrl(URL.createObjectURL(demoResponse.data));
        }
      } catch (error) {
        console.error('Ошибка при загрузке медиа:', error);
      }
    };

    fetchMedia();

    return () => {
      if (coverUrl) URL.revokeObjectURL(coverUrl);
      if (demoUrl) URL.revokeObjectURL(demoUrl);
    };
  }, [coverURI, demoURI]);

  const audioRef = useRef(null);
  const isPlaying = playingId === id;

  const handlePlayClick = (e) => {
    e.stopPropagation();
    handlePlay(id);
  };

  useEffect(() => {
    if (!audioRef.current) return;

    if (isPlaying) {
      audioRef.current.play();
    } else {
      audioRef.current.pause();
      audioRef.current.currentTime = 0;
    }
  }, [isPlaying]);

  const handleCardClick = () => {
    navigate(`/music-resource/${id}`);
  };

  return (
    <div className={styles.musicCard} onClick={handleCardClick}>
      {coverUrl && (
        <img src={coverUrl} alt={name} className={styles.musicCover} />
      )}
      <div className={styles.musicInfo}>
        <div className={styles.musicName}>{name}</div>
        <div className={styles.musicType}><em>{type}</em></div>
        <div className={styles.musicProducer}><b>{producer}</b></div>
        <div className={styles.musicPrice}>
          {price === 0 ? 'FREE' : `${price}₽`}
        </div>
      </div>

      {demoUrl && (
        <>
          <button className={styles.playButton} onClick={handlePlayClick}>
            {isPlaying ? 'Pause' : 'Play'}
          </button>
          <audio ref={audioRef} src={demoUrl} />
        </>
      )}
    </div>
  );
};

export default MusicCard;
