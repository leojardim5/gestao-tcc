import api from './api';
import { Page, Tcc, TccCreateRequest, TccUpdateRequest, StatusTcc } from '@/interfaces';

interface ListTccsParams {
  page?: number;
  size?: number;
  status?: StatusTcc;
  orientadorId?: string;
  alunoId?: string;
  termo?: string;
}

export const listTccs = async (params: ListTccsParams = {}): Promise<Page<Tcc>> => {
  const response = await api.get('/tccs', { params });
  return response.data;
};

export const getTcc = async (id: string): Promise<Tcc> => {
  const response = await api.get(`/tccs/${id}`);
  return response.data;
};

export const createTcc = async (data: TccCreateRequest): Promise<Tcc> => {
  const response = await api.post('/tccs', data);
  return response.data;
};

export const updateTcc = async (id: string, data: TccUpdateRequest): Promise<Tcc> => {
  const response = await api.put(`/tccs/${id}`, data);
  return response.data;
};

export const changeTccStatus = async ({ id, status }: { id: string; status: StatusTcc }): Promise<Tcc> => {
  const response = await api.patch(`/tccs/${id}/status`, { status });
  return response.data;
};

export const assignOrientador = async ({ tccId, orientadorId }: { tccId: string; orientadorId: string }): Promise<Tcc> => {
  const response = await api.patch(`/tccs/${tccId}/orientador`, { orientadorId });
  return response.data;
};

export const removeTcc = async (id: string): Promise<void> => {
    await api.delete(`/tccs/${id}`);
};
