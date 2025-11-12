import api from "./api";
import { CronogramaEtapa, CronogramaEtapaCreateRequest, CronogramaResumo, StatusCronogramaEtapa } from "@/interfaces";

export const listCronogramaEtapas = async (tccId: string): Promise<CronogramaEtapa[]> => {
  const response = await api.get(`/api/tccs/${tccId}/cronograma`);
  return response.data;
};

export const createCronogramaEtapa = async (tccId: string, data: CronogramaEtapaCreateRequest): Promise<CronogramaEtapa> => {
  const response = await api.post(`/api/tccs/${tccId}/cronograma/etapas`, data);
  return response.data;
};

interface UpdateCronogramaStatusPayload {
  status: StatusCronogramaEtapa;
  observacao?: string;
}

export const updateCronogramaEtapaStatus = async (
  tccId: string,
  etapaId: string,
  payload: UpdateCronogramaStatusPayload,
): Promise<CronogramaEtapa> => {
  const response = await api.patch(`/api/tccs/${tccId}/cronograma/etapas/${etapaId}/status`, payload);
  return response.data;
};

export const getCronogramaResumo = async (tccId: string): Promise<CronogramaResumo> => {
  const response = await api.get(`/api/tccs/${tccId}/cronograma/resumo`);
  return response.data;
};

export const getCronogramaResumos = async (tccIds: string[]): Promise<Record<string, CronogramaResumo>> => {
  if (tccIds.length === 0) {
    return {};
  }
  const response = await api.get("/api/tccs/cronograma/resumos", {
    params: { ids: tccIds },
    paramsSerializer: (params) => {
      const ids = (params.ids as string[]).map((id) => `ids=${encodeURIComponent(id)}`).join("&");
      return ids;
    },
  });
  return response.data ?? {};
};

