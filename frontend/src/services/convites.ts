import api from './api';
import { ConviteOrientacao, EnviarConviteRequest, ResponderConviteRequest } from '@/interfaces';

export const enviarConvite = async (alunoId: string, data: EnviarConviteRequest): Promise<ConviteOrientacao> => {
  console.log("🌐 [CONVITES] Enviando convite:", { alunoId, data });
  const response = await api.post<ConviteOrientacao>(`/api/convites/aluno/${alunoId}`, data);
  console.log("✅ [CONVITES] Resposta do servidor:", response.data);
  return response.data;
};

export const responderConvite = async (orientadorId: string, conviteId: string, data: ResponderConviteRequest): Promise<ConviteOrientacao> => {
  const response = await api.put<ConviteOrientacao>(`/api/convites/orientador/${orientadorId}/convite/${conviteId}/responder`, data);
  return response.data;
};

export const getConvitesPendentes = async (orientadorId: string): Promise<ConviteOrientacao[]> => {
  const response = await api.get(`/api/convites/orientador/${orientadorId}/pendentes`);
  return response.data.content || response.data; // Handle both Page and array responses
};

export const getConvitesAluno = async (alunoId: string): Promise<ConviteOrientacao[]> => {
  const response = await api.get(`/api/convites/aluno/${alunoId}`);
  return response.data.content || response.data; // Handle both Page and array responses
};

export const getTodosConvitesOrientador = async (orientadorId: string): Promise<ConviteOrientacao[]> => {
  const response = await api.get(`/api/convites/orientador/${orientadorId}`);
  return response.data.content || response.data; // Handle both Page and array responses
};

