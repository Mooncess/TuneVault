import React from 'react';
import styles from '../styles/Footer.module.css';

const Footer = () => {
  return (
    <footer className={styles.footer}>
      <ul className={styles.footerList}>
        <li className={styles.footerItem}><a href="/about">О нас</a></li>
        <li className={styles.footerItem}><a href="/contact">Связаться с нами</a></li>
        <li className={styles.footerItem}><a href="/terms">Пользовательское соглашение</a></li>
      </ul>
    </footer>
  );
};

export default Footer;