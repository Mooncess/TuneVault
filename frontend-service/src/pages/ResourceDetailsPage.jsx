import React, { useEffect, useState, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import MyFooter from '../components/Footer';
import MyNavbar from '../components/Navbar';
import '../styles/ResourceDetailsPage.css';
import AudioPlayer from 'react-h5-audio-player';
import 'react-h5-audio-player/lib/styles.css';
import PurchaseModal from '../components/PurchaseModal'; // модалка покупки

const ResourceDetailsPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [music, setMusic] = useState(null);
  const [coverUrl, setCoverUrl] = useState('');
  const [demoUrl, setDemoUrl] = useState('');
  const [isModalOpen, setIsModalOpen] = useState(false);
  const audioRef = useRef(null);

  useEffect(() => {
    const fetchMusic = async () => {
      try {
        const res = await axios.get(`${process.env.REACT_APP_API_GATEWAY_SERVER_URL}/mcs/api/v1/music-resource/${id}`);
        setMusic(res.data);

        if (res.data.coverURI) {
          const coverRes = await axios.get(
            `${process.env.REACT_APP_API_FILE_SERVER_URL}/s3/api/v1/media?name=${res.data.coverURI}&type=cover`,
            { responseType: 'blob' }
          );
          setCoverUrl(URL.createObjectURL(coverRes.data));
        }

        if (res.data.demoURI) {
          const demoRes = await axios.get(
            `${process.env.REACT_APP_API_FILE_SERVER_URL}/s3/api/v1/media?name=${res.data.demoURI}&type=demo`,
            { responseType: 'blob' }
          );
          setDemoUrl(URL.createObjectURL(demoRes.data));
        }
      } catch (err) {
        console.error('Ошибка при загрузке данных:', err);
      }
    };

    fetchMusic();

    return () => {
      if (coverUrl) URL.revokeObjectURL(coverUrl);
      if (demoUrl) URL.revokeObjectURL(demoUrl);
    };
  }, [id]);

  if (!music) return <div className="loading">Загрузка...</div>;

  const handleProducerClick = () => {
    navigate(`/producer/${music.producer.id}`);
  };

  const openModal = () => setIsModalOpen(true);
  const closeModal = () => setIsModalOpen(false);

  return (
    <>
      <MyNavbar />
      <div className="music-details">
        <div className="details-top">
          <div className="cover-section">
            {coverUrl && <img src={coverUrl} alt="Обложка" className="cover-img" />}
          </div>
          <div className="info-section">
            <h2>{music.name}</h2>
            <p className="producer-name">
              by <span onClick={handleProducerClick}>{music.producer.nickname}</span>
            </p>
            <p><strong>Тип:</strong> {music.type}</p>
            <p><strong>Жанр:</strong> {music.genre}</p>
            <p><strong>Цена:</strong> {music.price}₽</p>

            <button className="buy-button" onClick={openModal}>
              Купить
            </button>
          </div>
        </div>

        {demoUrl && (
          <div className="audio-section">
            <AudioPlayer
              src={demoUrl}
              onPlay={() => console.log('Playing')}
              className="custom-audio-player"
              layout="horizontal-reverse"
              customAdditionalControls={[]}
              customVolumeControls={[]}
              showJumpControls={false}
            />
          </div>
        )}

        <div className="meta-section">
          <p><strong>BPM:</strong> {music.bpm}</p>
          <p><strong>Ключ:</strong> {music.key}</p>
          <p><strong>Дата создания:</strong> {music.creationDate}</p>
        </div>
      </div>

      {isModalOpen && (
        <PurchaseModal
          resourceId={music.id}
          onClose={closeModal}
        />
      )}

      <MyFooter />
    </>
  );
};

export default ResourceDetailsPage;
