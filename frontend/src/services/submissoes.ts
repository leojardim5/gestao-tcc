import api from './api';
import { Page, Submissao, SubmissaoCreateRequest, SubmissaoDecisionRequest } from '@/interfaces';

export const listSubmissoesByTcc = async (tccId: string): Promise<Submissao[]> => {
  const response = await api.get(`/tccs/${tccId}/submissoes`);
  return response.data;
};

export const createSubmissao = async (data: SubmissaoCreateRequest): Promise<Submissao> => {
  const response = await api.post('/submissoes', data);
  return response.data;
};

export const getLatestSubmissao = async (tccId: string): Promise<Submissao | null> => {
  const response = await api.get(`/tccs/${tccId}/submissoes/latest`);
  return response.data;
};

