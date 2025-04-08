import React from 'react';
import { Link } from 'react-router-dom';
import styles from '../styles/Navbar.module.css';

const MyNavbar = () => {
    return (
        <nav className={styles.navbar}>
            <ul className={styles.navbarList}>
                <li className={styles.navbarItem}><Link to="/">Каталог</Link></li>
                <li className={styles.navbarItem}><Link to="/producer">Продюсеры</Link></li>
                <li className={styles.navbarItem}><Link to="/profile">Профиль</Link></li>
            </ul>
        </nav>
    );
};

export default MyNavbar;
