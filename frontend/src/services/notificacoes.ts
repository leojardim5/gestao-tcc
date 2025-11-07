import api from './api';
import { Notificacao } from '@/interfaces';

interface ListNotificacoesParams {
  usuarioId: string;
  lidas?: boolean;
}

export const listNotificacoes = async (params: ListNotificacoesParams): Promise<Notificacao[]> => {
  const response = await api.get('/api/notificacoes', {
    params: {
      ...params,
      page: 0,
      size: 100,
    },
  });
  // Backend retorna Page<Notificacao>; fallback para array direto se não paginado
  return response.data?.content ?? response.data ?? [];
};

export const markNotificacaoAsRead = async (id: string): Promise<Notificacao> => {
  const response = await api.patch(`/api/notificacoes/${id}/lida`);
  return response.data;
};
