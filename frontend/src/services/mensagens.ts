import api from './api';

export interface TccMensagem {
  id: string;
  autorId: string;
  autorNome: string;
  autorEmail: string;
  conteudo: string;
  criadoEm: string; // ISO Date string
}

export interface CriarMensagemRequest {
  conteudo: string;
}

export const listarMensagens = async (tccId: string): Promise<TccMensagem[]> => {
  const response = await api.get<TccMensagem[]>(`/api/tccs/${tccId}/mensagens`);
  return response.data;
};

export const criarMensagem = async (tccId: string, data: CriarMensagemRequest): Promise<TccMensagem> => {
  const response = await api.post<TccMensagem>(`/api/tccs/${tccId}/mensagens`, data);
  return response.data;
};

