import api from './api';
import { Page, PapelUsuario, Usuario, UsuarioCreateRequest, LoginRequest } from '@/interfaces';
import { useSessionStore } from '@/store/session';

// Define the expected response structure for authentication
interface AuthResponse {
  token: string;
  id: string;
  nome: string;
  email: string;
  papel: PapelUsuario;
}

export const listUsuarios = async (params: { page?: number; size?: number; papel?: PapelUsuario, nome?: string, email?: string } = {}): Promise<Page<Usuario>> => {
  const response = await api.get('/usuarios', { params });
  return response.data;
};

export const getUsuario = async (id: string): Promise<Usuario> => {
  const response = await api.get(`/usuarios/${id}`);
  return response.data;
};

export const createUsuario = async (data: UsuarioCreateRequest): Promise<Usuario> => {
  const response = await api.post('/usuarios', data);
  return response.data;
};

export const updateUsuario = async (id: string, data: Partial<UsuarioCreateRequest>): Promise<Usuario> => {
  const response = await api.patch(`/usuarios/${id}`, data);
  return response.data;
};

export const login = async (data: LoginRequest): Promise<AuthResponse> => {
    console.log("Tentando logar com", data);
    const response = await api.post<AuthResponse>('/auth/login', data);
    return response.data;
};

export const signup = async (data: Omit<UsuarioCreateRequest, 'papel'>): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>('/auth/signup', { ...data, papel: PapelUsuario.ALUNO });
    const { token, user } = response.data;
    return response.data;
};

