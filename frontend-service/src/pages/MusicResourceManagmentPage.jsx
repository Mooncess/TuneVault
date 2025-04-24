import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axiosInstance from '../utils/AxiosInstance';
import Navbar from '../components/Navbar';
import Footer from '../components/Footer';
import styles from '../styles/MusicResourceManagmentPage.module.css'; // Импорт стилей как модуля

function MusicResourceManagmentPage() {
  const [resources, setResources] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchResources = async () => {
      try {
        const response = await axiosInstance.get(
          `${process.env.REACT_APP_API_GATEWAY_SERVER_URL}/mcs/api/v1/music-resource/by-producer`,
          { withCredentials: true }
        );
        setResources(response.data);
      } catch (error) {
        console.error('Ошибка при загрузке ресурсов продюсера:', error);
      }
    };

    fetchResources();
  }, []);

  const handleRowClick = (resourceId) => {
    navigate('/music-resource-managment/edit', { state: { id: resourceId } });
  };

  return (
    <div className={styles.layout}>
      <Navbar />
      <main className={styles['main-content']}>
        <div className={styles['page-container']}>
          <h1 className={styles['page-title']}>Мои ресурсы</h1>
          <div className={styles['table-wrapper']}>
            <table className={styles['resource-table']}>
              <thead>
                <tr>
                  <th>Название</th>
                  <th>Жанр</th>
                  <th>Тип</th>
                  <th>Цена</th>
                  <th>Статус</th>
                  <th>Дата создания</th>
                </tr>
              </thead>
              <tbody>
                {resources.map((resource) => (
                  <tr
                    key={resource.id}
                    onClick={() => handleRowClick(resource.id)}
                    className={styles['table-row']}
                  >
                    <td>{resource.name}</td>
                    <td>{resource.genre}</td>
                    <td>{resource.type}</td>
                    <td>{resource.price}₽</td>
                    <td>{resource.status}</td>
                    <td>{resource.creationDate?.slice(0, 10)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </main>
      <Footer />
    </div>
  );
}

export default MusicResourceManagmentPage;
