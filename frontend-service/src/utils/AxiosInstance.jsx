import axios from 'axios';

const axiosInstance = axios.create({
    withCredentials: true,
    headers: {
        'Content-Type': 'application/json',
    },
});

axiosInstance.interceptors.response.use(
    response => response,
    async error => {
        const originalRequest = error.config;

        if (error.response?.status === 403 && !originalRequest._retry) {
            console.log("STEP 1");
            originalRequest._retry = true;

            try {
                const res = await axios.post(
                    `${process.env.REACT_APP_API_GATEWAY_SERVER_URL}/auth/api/v1/token`,
                    null,
                    { withCredentials: true }
                );

                return axiosInstance(originalRequest);
            } catch (tokenError) {
                console.log("STEP 2");
                try {
                    const res = await axios.post(
                        `${process.env.REACT_APP_API_GATEWAY_SERVER_URL}/auth/api/v1/refresh`,
                        null,
                        { withCredentials: true }
                    );


                    return axiosInstance(originalRequest);
                } catch (refreshError) {
                    console.log("STEP 3");
                    console.warn('Не удалось обновить токены. Перенаправление на /login.');
                    window.location.href = '/login';
                    return Promise.reject(refreshError);
                }
            }
        }

        return Promise.reject(error);
    }
);

export default axiosInstance;
