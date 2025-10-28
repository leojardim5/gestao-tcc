import api from './api';
import { Comentario, ComentarioCreateRequest } from '@/interfaces';

export const listComentariosBySubmissao = async (submissaoId: string): Promise<Comentario[]> => {
  const response = await api.get(`/api/submissoes/${submissaoId}/comentarios`);
  return response.data;
};

export const addComentario = async (data: ComentarioCreateRequest): Promise<Comentario> => {
  const response = await api.post('/api/comentarios', data);
  return response.data;
};

export const deleteComentario = async (id: string): Promise<void> => {
  await api.delete(`/api/comentarios/${id}`);
};
