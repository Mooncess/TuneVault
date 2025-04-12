import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';
import Footer from '../components/Footer';
import '../styles/ProfilePage.css';
import axiosInstance from '../utils/AxiosInstance';

const ProfilePage = () => {
  const [profile, setProfile] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const res = await axiosInstance.get(`${process.env.REACT_APP_API_GATEWAY_SERVER_URL}/mcs/api/v1/producer/profile`);
        setProfile(res.data);
      } catch (error) {
        if (error.response?.status === 403) {
          navigate('/login');
        }
      }
    };

    fetchProfile();
  }, [navigate]);

  const handleLogout = async () => {
    try {
      await axiosInstance.get(`${process.env.REACT_APP_API_GATEWAY_SERVER_URL}/auth/api/v1/logout`, {
        withCredentials: true,
      });
      navigate('/');
    } catch (error) {
      console.error('Ошибка при выходе:', error);
    }
  };

  if (!profile) {
    return <div className="profile-loading">Загрузка профиля...</div>;
  }

  return (
    <div className="layout">
      <Navbar />
      <main className="main-content">
        <div className="profile-container">
          <div className="profile-card">
            <div className="profile-left">
              <img
                src={`${process.env.REACT_APP_API_GATEWAY_SERVER_URL}/media?name=${profile.logoUri}&type=logo`}
                alt="Логотип"
                className="profile-logo"
              />
            </div>
            <div className="profile-right">
              <h2 className="profile-nickname">{profile.nickname}</h2>
              <p className="profile-about">{profile.about}</p>
              <div className="profile-buttons">
                <button onClick={() => alert('Обновление информации')} className="profile-btn">
                  Обновить информацию о себе
                </button>
                <button onClick={() => alert('Обновление логотипа')} className="profile-btn">
                  Обновить лого
                </button>
                <button onClick={() => navigate('/music-resource-managment')} className="profile-btn">
                  Управление ресурсами
                </button>
                <button onClick={() => navigate('/music-resource/create')} className="profile-btn add-resource-btn">
                 Добавить новый ресурс
                </button>
                <button onClick={handleLogout} className="profile-btn logout-btn">
                  Выход
                </button>
              </div>
            </div>
            <div className="profile-balance">
              Баланс: {profile.balance}₽
              <button className="balance-btn" onClick={() => navigate('/my-balance')}>
                Подробнее
              </button>
            </div>
          </div>
        </div>
      </main>
      <Footer />
    </div>
  );
};

export default ProfilePage;
