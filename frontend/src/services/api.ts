import axios, { AxiosError } from 'axios';

const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8081',
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use(
  (config) => {
    // Try to get token from Zustand store first
    const sessionData = localStorage.getItem("session-storage");
    if (sessionData) {
      try {
        const parsed = JSON.parse(sessionData);
        const token = parsed?.state?.token;
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
      } catch (error) {
        console.warn("Failed to parse session storage:", error);
      }
    }
    
    // Fallback to old authToken key for backward compatibility
    if (!config.headers.Authorization) {
      const token = localStorage.getItem("authToken");
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
    }
    
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

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
