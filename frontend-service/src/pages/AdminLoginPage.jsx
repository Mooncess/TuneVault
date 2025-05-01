import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axiosInstance from '../utils/AdminAxiosInstance';
import MyFooter from '../components/Footer';
import MyNavbar from '../components/AdminNavbar';
import styles from '../styles/LoginPage.module.css';

const LoginPage = () => {
    const navigate = useNavigate();
    const [login, setLogin] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');

    const handleLogin = async () => {
        try {
            const response = await axiosInstance.post(`${process.env.REACT_APP_API_GATEWAY_SERVER_URL}/auth/api/v1/admin-login`, { login, password }, { withCredentials: true });

            if (response.status === 200) {
                console.log("Успешный вход");
                localStorage.setItem('access', response.data.accessToken);
                navigate('/admin/panel');
            } else {
                console.log("Что-то пошло не так");
            }
        } catch (error) {
            console.error('Ошибка при запросе на сервер:', error);
            if (error.response.status === 500) {
                setError('Неверный email или пароль');
            }
        }
    };

    const handleRegistrationRedirect = () => {
        navigate('/registration');
    };

    return (
        <div>
            <MyNavbar />
            <div className={styles['login-container']}>
                <h2 className={styles['title']}>Вход</h2>
                <input 
                    required 
                    type="login" 
                    placeholder="Email" 
                    value={login} 
                    className={styles['log-input']} 
                    onChange={(e) => setLogin(e.target.value)} 
                />
                <input 
                    required 
                    type="password" 
                    placeholder="Пароль" 
                    value={password} 
                    className={styles['log-input']} 
                    onChange={(e) => setPassword(e.target.value)} 
                />
                
                {error && <p className={styles['error-message']}>{error}</p>}

                <button onClick={handleLogin} className={styles['black-button']}>Войти</button>
                <button onClick={handleRegistrationRedirect} className={styles['black-button']}>Зарегистрироваться</button>
            </div>
            <MyFooter />
        </div>
    );
};

export default LoginPage;
