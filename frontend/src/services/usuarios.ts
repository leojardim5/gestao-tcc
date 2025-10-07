import api from './api';
import { Page, PapelUsuario, Usuario, UsuarioCreateRequest } from '@/interfaces';

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

export const deactivateUsuario = async (id: string): Promise<void> => {
  await api.patch(`/usuarios/${id}`, { ativo: false });
};
