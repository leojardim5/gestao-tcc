import api from './api';
import { Comentario, ComentarioCreateRequest } from '@/interfaces';

export const listComentariosBySubmissao = async (submissaoId: string): Promise<Comentario[]> => {
  const response = await api.get(`/submissoes/${submissaoId}/comentarios`);
  return response.data;
};

export const addComentario = async (data: ComentarioCreateRequest): Promise<Comentario> => {
  const response = await api.post('/comentarios', data);
  return response.data;
};

export const deleteComentario = async (id: string): Promise<void> => {
  await api.delete(`/comentarios/${id}`);
};
