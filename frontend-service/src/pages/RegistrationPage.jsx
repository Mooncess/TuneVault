import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axiosInstance from '../utils/AxiosInstance';
import MyFooter from '../components/Footer';
import MyNavbar from '../components/Navbar';
import '../styles/RegistrationPage.css';

const RegistrationPage = () => {
    const navigate = useNavigate();
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [nickname, setNickname] = useState('');
    const [error, setError] = useState('');

    const handleRegister = async () => {
        try {
            const response = await axiosInstance.post(
                `${process.env.REACT_APP_API_GATEWAY_SERVER_URL}/auth/api/v1/registration`,
                { username, password, nickname },
                { withCredentials: true }
            );

            if (response.status === 201 || response.status === 200) {
                console.log('Регистрация успешна');
                navigate('/profile');
            } else {
                console.log('Что-то пошло не так');
            }
        } catch (error) {
            console.error('Ошибка при регистрации:', error);
            setError('Ошибка при регистрации. Проверьте введённые данные.');
        }
    };

    const handleLoginRedirect = () => {
        navigate('/login');
    };

    return (
        <div>
            <MyNavbar />
            <div className="registration-container">
                <h2>Регистрация</h2>
                <input
                    required
                    type="email"
                    placeholder="Email"
                    value={username}
                    className="reg-input"
                    onChange={(e) => setUsername(e.target.value)}
                />
                <input
                    required
                    type="password"
                    placeholder="Пароль"
                    value={password}
                    className="reg-input"
                    onChange={(e) => setPassword(e.target.value)}
                />
                <input
                    required
                    type="text"
                    placeholder="Никнейм"
                    value={nickname}
                    className="reg-input"
                    onChange={(e) => setNickname(e.target.value)}
                />

                {error && <p style={{ color: 'red' }}>{error}</p>}

                <button onClick={handleRegister} className="black-button">Зарегистрироваться</button>
                <button onClick={handleLoginRedirect} className="black-button">Уже есть аккаунт</button>
            </div>
            <MyFooter />
        </div>
    );
};

export default RegistrationPage;
