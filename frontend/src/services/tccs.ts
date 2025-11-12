import api from './api';
import { Page, Tcc, TccCreateRequest, TccUpdateRequest, StatusTcc, TccWorkspaceOverview } from '@/interfaces';

interface ListTccsParams {
  page?: number;
  size?: number;
  status?: StatusTcc;
  orientadorId?: string;
  alunoId?: string;
  termo?: string;
}

export const listTccs = async (params: ListTccsParams = {}): Promise<Page<Tcc>> => {
  const response = await api.get('/api/tccs', { params });
  return response.data;
};

export const getTccById = async (id: string): Promise<Tcc> => {
  const response = await api.get(`/api/tccs/${id}`);
  return response.data;
};

export const createTcc = async (data: TccCreateRequest): Promise<Tcc> => {
  console.log("🌐 [TCCS] Criando TCC:", data);
  const response = await api.post('/api/tccs', data);
  console.log("✅ [TCCS] TCC criado:", response.data);
  return response.data;
};

export const updateTcc = async (id: string, data: TccUpdateRequest): Promise<Tcc> => {
  const response = await api.put(`/api/tccs/${id}`, data);
  return response.data;
};

export const changeTccStatus = async ({ id, status }: { id: string; status: StatusTcc }): Promise<Tcc> => {
  const response = await api.patch(`/api/tccs/${id}/status`, { status });
  return response.data;
};

export const assignOrientador = async ({ tccId, orientadorId }: { tccId: string; orientadorId: string }): Promise<Tcc> => {
  const response = await api.patch(`/api/tccs/${tccId}/orientador`, { orientadorId });
  return response.data;
};

export const removeTcc = async (id: string): Promise<void> => {
    await api.delete(`/api/tccs/${id}`);
};

export const getTccWorkspaceOverview = async (id: string): Promise<TccWorkspaceOverview> => {
  const response = await api.get(`/api/tccs/${id}/workspace/overview`);
  return response.data;
};
