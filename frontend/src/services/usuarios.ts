import api from './api';
import { Page, PapelUsuario, Usuario, UsuarioCreateRequest, LoginRequest } from '@/interfaces';
import { useSessionStore } from '@/store/session';

// Define the expected response structure for authentication
interface AuthResponse {
  token: string;
  refreshToken: string;
  usuario: {
    id: string;
    nome: string;
    email: string;
    papel: PapelUsuario;
  };
}

export const listUsuarios = async (params: { page?: number; size?: number; papel?: PapelUsuario, nome?: string, email?: string } = {}): Promise<Page<Usuario>> => {
  const response = await api.get('/api/usuarios', { params });
  return response.data;
};

export const getUsuario = async (id: string): Promise<Usuario> => {
  const response = await api.get(`/api/usuarios/${id}`);
  return response.data;
};

export const createUsuario = async (data: UsuarioCreateRequest): Promise<Usuario> => {
  const response = await api.post('/api/usuarios', data);
  return response.data;
};

export const updateUsuario = async (id: string, data: Partial<UsuarioCreateRequest>): Promise<Usuario> => {
  const response = await api.patch(`/api/usuarios/${id}`, data);
  return response.data;
};

export const listOrientadoresDisponiveis = async (params: { page?: number; size?: number } = {}): Promise<Page<Usuario>> => {
  const response = await api.get('/api/usuarios/orientadores-disponiveis', { params });
  return response.data;
};

export const updateDisponibilidade = async (id: string, disponivel: boolean): Promise<Usuario> => {
  const response = await api.put(`/api/usuarios/${id}`, { disponivelParaOrientacao: disponivel });
  return response.data;
};

export const login = async (data: LoginRequest): Promise<AuthResponse> => {
    console.log("Tentando logar com", data);
    const response = await api.post<AuthResponse>('/api/auth/login', data);
    return response.data;
};

export const signup = async (data: UsuarioCreateRequest): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>('/api/auth/register', data);
    return response.data;
};

