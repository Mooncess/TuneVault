import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';
import Footer from '../components/Footer';
import axiosInstance from '../utils/AxiosInstance';
import EditProfileModal from '../components/EditProfileModal';
import UpdateLogoModal from '../components/UpdateLogoModal';
import styles from '../styles/ProfilePage.module.css'; // Импорт стилей как модуля

const ProfilePage = () => {
  const [profile, setProfile] = useState(null);
  const [balance, setBalance] = useState(null);
  const [isEditOpen, setIsEditOpen] = useState(false);
  const [isLogoModalOpen, setIsLogoModalOpen] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const res = await axiosInstance.get(
          `${process.env.REACT_APP_API_GATEWAY_SERVER_URL}/mcs/api/v1/producer/profile`
        );
        setProfile(res.data);
      } catch (error) {
        if (error.response?.status === 403) {
          navigate('/login');
        }
      }
    };

    const fetchBalance = async () => {
      try {
        const res = await axiosInstance.get(
          `${process.env.REACT_APP_API_GATEWAY_SERVER_URL}/tos/api/v1/producer-balance`,
          { withCredentials: true }
        );
        setBalance(res.data);
      } catch (error) {
        console.error("Ошибка при получении баланса", error);
      }
    };

    fetchProfile();
    fetchBalance();
  }, [navigate]);

  const handleLogout = async () => {
    try {
      await axiosInstance.get(
        `${process.env.REACT_APP_API_GATEWAY_SERVER_URL}/auth/api/v1/logout`,
        { withCredentials: true }
      );
      navigate('/');
    } catch (error) {
      console.error('Ошибка при выходе:', error);
    }
  };

  if (!profile) {
    return <div className={styles['profile-loading']}>Загрузка профиля...</div>;
  }

  return (
    <div className={styles.layout}>
      <Navbar />
      <main className={styles['main-content']}>
        <div className={styles['profile-container']}>
          <div className={styles['profile-card']}>
            <div className={styles['profile-left']}>
              <img
                src={
                  profile.logoUri && profile.logoUri !== 'default-logo-uri.jpg'
                    ? `${process.env.REACT_APP_API_FILE_SERVER_URL}/s3/api/v1/media?name=${profile.logoUri}&type=logo`
                    : 'img/Logo.png'
                }
                alt="Логотип"
                className={styles['profile-logo']}
              />
            </div>
            <div className={styles['profile-right']}>
              <h2 className={styles['profile-nickname']}>{profile.nickname}</h2>
              <p className={styles['profile-about']}>{profile.about}</p>
              <div className={styles['profile-buttons']}>
                <button onClick={() => setIsEditOpen(true)} className={styles['profile-btn']}>
                  Обновить информацию о себе
                </button>
                <button onClick={() => setIsLogoModalOpen(true)} className={styles['profile-btn']}>
                  Обновить лого
                </button>
                <button onClick={() => navigate('/music-resource-managment')} className={styles['profile-btn']}>
                  Управление ресурсами
                </button>
                <button onClick={() => navigate('/music-resource/create')} className={`${styles['profile-btn']} ${styles['add-resource-btn']}`}>
                  Добавить новый ресурс
                </button>
                <button onClick={handleLogout} className={`${styles['profile-btn']} ${styles['logout-btn']}`}>
                  Выход
                </button>
              </div>
            </div>
            <div className={styles['profile-balance']}>
              Баланс: {balance !== null ? `${balance}₽` : 'Загрузка...'}
              <button className={styles['balance-btn']} onClick={() => navigate('/my-balance')}>
                Подробнее
              </button>
            </div>
          </div>
        </div>
      </main>
      <Footer />

      {isEditOpen && (
        <EditProfileModal profile={profile} onClose={() => setIsEditOpen(false)} />
      )}

      {isLogoModalOpen && (
        <UpdateLogoModal onClose={() => setIsLogoModalOpen(false)} />
      )}
    </div>
  );
};

export default ProfilePage;
