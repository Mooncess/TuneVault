import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import MyNavbar from '../components/Navbar';
import MyFooter from '../components/Footer';
import MusicCard from '../components/MusicCard';
import { GENRES, TYPES } from '../config/MusicOptions';
import styles from '../styles/ProducerDetailPage.module.css';

const initialFilters = {
  name: '',
  genre: '',
  minPrice: '',
  maxPrice: '',
  type: '',
  page: 0,
  size: 10,
  sort: 'name,asc',
};

const ProducerDetailPage = () => {
  const { id } = useParams();
  const [producer, setProducer] = useState(null);
  const [resources, setResources] = useState([]);
  const [playingId, setPlayingId] = useState(null);
  const [filters, setFilters] = useState(initialFilters);
  const [totalPages, setTotalPages] = useState(1);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFilters(prev => ({ ...prev, [name]: value }));
  };

  const handlePlay = (id) => {
    setPlayingId(prevId => (prevId === id ? null : id));
  };

  const handleSearch = async () => {
    const params = new URLSearchParams();
    Object.entries(filters).forEach(([key, value]) => {
      if (value !== '') params.append(key, value);
    });

    try {
      const res = await fetch(
        `${process.env.REACT_APP_API_GATEWAY_SERVER_URL}/mcs/api/v1/music-resource/by-producer/${id}/filtered?${params.toString()}`
      );
      const data = await res.json();
      setResources(data.content);
      setTotalPages(data.totalPages);
    } catch (error) {
      console.error('Ошибка при загрузке ресурсов:', error);
    }
  };

  const handleClearFilters = () => {
    setFilters({ ...initialFilters });
  };

  useEffect(() => {
    const fetchProducer = async () => {
      try {
        const res = await fetch(`${process.env.REACT_APP_API_GATEWAY_SERVER_URL}/mcs/api/v1/producer/${id}`);
        const data = await res.json();
        setProducer(data);
      } catch (error) {
        console.error('Ошибка при загрузке продюсера:', error);
      }
    };

    fetchProducer();
  }, [id]);

  useEffect(() => {
    handleSearch();
  }, [filters.page, filters.size, filters.sort]);

  if (!producer) {
    return <div>Загрузка...</div>; // Показываем индикатор загрузки, пока данные не получены
  }

  return (
    <div className={styles.layout}>
      <MyNavbar />
      <main className={styles.main}>
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

        {/* Фильтры и поиск */}
        <div className={styles['search-row']}>
          <button onClick={handleSearch} className={styles['search-btn']}>Поиск</button>
          <button onClick={handleClearFilters} className={styles['clear-filters-btn']}>Очистить</button>
        </div>

        <div className={styles['filter-row']}>
          <select name="type" onChange={handleInputChange} value={filters.type} className={styles['filter-select']}>
            <option value="">Тип</option>
            {TYPES.map(type => (
              <option key={type} value={type}>{type}</option>
            ))}
          </select>

          <select name="genre" onChange={handleInputChange} value={filters.genre} className={styles['filter-select']}>
            <option value="">Жанр</option>
            {GENRES.map(genre => (
              <option key={genre} value={genre}>{genre}</option>
            ))}
          </select>

          <input
            type="number"
            name="minPrice"
            placeholder="Мин. цена"
            value={filters.minPrice}
            onChange={handleInputChange}
            className={styles['filter-input']}
          />
          <input
            type="number"
            name="maxPrice"
            placeholder="Макс. цена"
            value={filters.maxPrice}
            onChange={handleInputChange}
            className={styles['filter-input']}
          />
        </div>

        <div className={styles['sort-pagination-row']}>
          <select name="sort" onChange={handleInputChange} value={filters.sort} className={styles['sort-select']}>
            <option value="name,asc">Название (A-Z)</option>
            <option value="name,desc">Название (Z-A)</option>
            <option value="price,asc">Цена ↑</option>
            <option value="price,desc">Цена ↓</option>
          </select>

          <select name="size" onChange={handleInputChange} value={filters.size} className={styles['size-select']}>
            <option value="5">5 на страницу</option>
            <option value="10">10 на страницу</option>
            <option value="20">20 на страницу</option>
          </select>
        </div>

        {/* Карточки ресурсов */}
        <div className={styles.resources}>
          {resources && resources.length > 0 ? (
            resources.map(resource => (
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
            ))
          ) : (
            <p>Нет доступных ресурсов</p>
          )}
        </div>

        {/* Пагинация */}
        <div className={styles['pagination-row']}>
          <button
            disabled={filters.page === 0}
            onClick={() => setFilters(prev => ({ ...prev, page: prev.page - 1 }))}
            className={styles['pagination-btn']}
          >
            &larr; Назад
          </button>
          <span>Страница {resources.length > 0 ? filters.page + 1 : 1} из {totalPages || 1}</span>
          <button
            disabled={filters.page + 1 >= totalPages}
            onClick={() => setFilters(prev => ({ ...prev, page: prev.page + 1 }))}
            className={styles['pagination-btn']}
          >
            Вперед &rarr;
          </button>
        </div>
      </main>
      <MyFooter />
    </div>
  );
};

export default ProducerDetailPage;
