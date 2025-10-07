import axios, { AxiosError } from 'axios';

const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Optional: Add interceptors for logging or error handling
api.interceptors.response.use(
  (response) => response,
  (error: AxiosError) => {
    console.error('API Error:', error.response?.data || error.message);
    return Promise.reject(error);
  }
);

export interface ApiError {
    message: string;
    status?: number;
}
  
export const handleApiError = (error: unknown): ApiError => {
    if (axios.isAxiosError(error)) {
        return {
            message: (error.response?.data as { message?: string })?.message || error.message,
            status: error.response?.status,
        };
    }
    return { message: 'An unexpected error occurred' };
};

export default api;
