import api from './api';
import { Notificacao } from '@/interfaces';

interface ListNotificacoesParams {
  usuarioId: string;
  lidas?: boolean;
}

export const listNotificacoes = async (params: ListNotificacoesParams): Promise<Notificacao[]> => {
  const response = await api.get('/notificacoes', { params });
  return response.data;
};

export const markNotificacaoAsRead = async (id: string): Promise<Notificacao> => {
  const response = await api.patch(`/notificacoes/${id}/lida`);
  return response.data;
};
