import api from './api';
import { Reuniao, ReuniaoCreateRequest } from '@/interfaces';

export const listReunioesByTcc = async (tccId: string): Promise<Reuniao[]> => {
  const response = await api.get(`/tccs/${tccId}/reunioes`);
  return response.data;
};

export const scheduleReuniao = async (data: ReuniaoCreateRequest): Promise<Reuniao> => {
  const response = await api.post('/reunioes', data);
  return response.data;
};

export const cancelReuniao = async (id: string): Promise<void> => {
  await api.delete(`/reunioes/${id}`);
};
