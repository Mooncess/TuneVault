import React, { useEffect, useState } from 'react';
import MyFooter from '../components/Footer';
import MyNavbar from '../components/Navbar';
import MusicCard from '../components/MusicCard';
import { GENRES, TYPES } from '../config/MusicOptions';
import styles from '../styles/CatalogPage.module.css'; // Импорт как модуль

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

const Catalog = () => {
  const [musicItems, setMusicItems] = useState([]);
  const [filters, setFilters] = useState(initialFilters);
  const [totalPages, setTotalPages] = useState(1);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFilters(prev => ({ ...prev, [name]: value }));
  };

  const [playingId, setPlayingId] = useState(null); // ID текущего проигрываемого трека

  const handlePlay = (id) => {
    setPlayingId(prevId => (prevId === id ? null : id)); // если повторно нажали — остановить
  };

  const handleSearch = async () => {
    const params = new URLSearchParams();
    Object.entries(filters).forEach(([key, value]) => {
      if (value !== '') params.append(key, value);
    });

    try {
      const response = await fetch(
        `${process.env.REACT_APP_API_GATEWAY_SERVER_URL}/mcs/api/v1/music-resource?${params.toString()}`
      );
      const data = await response.json();
      setMusicItems(data.content);
      setTotalPages(data.totalPages);
    } catch (error) {
      console.error('Ошибка загрузки данных:', error);
    }
  };

  const handleClearFilters = () => {
    setFilters({ ...initialFilters });
  };

  // Загружаем данные при первом рендере и при изменении пагинации, размера, сортировки
  useEffect(() => {
    handleSearch();
  }, [filters.page, filters.size, filters.sort]);

  return (
    <div className={styles['layout']}>
      <MyNavbar />
      <main className={styles['main-content']}>

        <div className={styles['search-row']}>
          <input
            type="text"
            name="name"
            placeholder="Поиск по названию"
            value={filters.name}
            onChange={handleInputChange}
            className={styles['search-input']}
          />
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

        <div className={styles['catalog-container']}>
          {musicItems.map(item => (
            <MusicCard
              key={item.id}
              id={item.id}
              name={item.name}
              price={item.price}
              coverURI={item.coverURI}
              demoURI={item.demoURI}
              type={item.type}
              producer={item.producer.nickname}
              playingId={playingId}
              handlePlay={handlePlay}
            />
          ))}
        </div>

        <div className={styles['pagination-row']}>
          <button
            disabled={filters.page === 0}
            onClick={() => setFilters(prev => ({ ...prev, page: prev.page - 1 }))}
            className={styles['pagination-btn']}
          >
            &larr; Назад
          </button>
          <span>Страница {filters.page + 1} из {totalPages}</span>
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

export default Catalog;
