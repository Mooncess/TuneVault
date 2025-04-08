import axios from 'axios';

const axiosInstance = axios.create({
    withCredentials: true,
    headers: {
        'Content-Type': 'application/json',
    },
});

axiosInstance.interceptors.request.use(config => {
    const accessToken = localStorage.getItem('access');

    if (accessToken) {
        config.headers['Authorization'] = `Bearer ${accessToken}`;
    }

    return config;
});

axiosInstance.interceptors.response.use(
    response => {
        return response;
    },
    async error => {
        if (error.response && error.response.status === 401) {
            console.log('Ошибка 401: Пользователь не авторизован. Пожалуйста, выполните вход.');

            try {
                const response = await axios.post(`${process.env.REACT_APP_API_GATEWAY_SERVER_URL}/auth/api/v1/token`, null, {
                    withCredentials: true,
                });
                console.log('Access token refreshed successfully:', response.data);
                localStorage.setItem('access', response.data.accessToken);
                const accessToken = localStorage.getItem('access');
                error.config.headers['Authorization'] = `Bearer ${accessToken}`;
                
                console.log(response.status);

                // Повторяем исходный запрос с обновленным токеном
                return axios(error.config);
            } catch (accessError) {
                if (accessError.response && accessError.response.status === 500) {
                  try {
                    const response = await axios.post(`${process.env.REACT_APP_API_GATEWAY_SERVER_URL}/auth/api/v1/refresh`, null, {
                        withCredentials: true,
                    });
                    console.log("Обновил рефреш токен");
                    localStorage.setItem('access', response.data.accessToken);
                    const accessToken = localStorage.getItem('access');
                    error.config.headers['Authorization'] = `Bearer ${accessToken}`;
    
                    // Повторяем исходный запрос с обновленным токеном
                    return axiosInstance(error.config);
                } catch (refreshError) {
                    window.location.href = '/login';
                    // Можно выполнить дополнительные действия при ошибке обновления токена
                    return Promise.reject(refreshError);
                }
                }
                // Можно выполнить дополнительные действия при ошибке обновления токена
                return Promise.reject(accessError);
            }
        } else if (error.response && error.response.status === 403) {
            return error.response;
        }
        return Promise.reject(error);
    }
);

export default axiosInstance;