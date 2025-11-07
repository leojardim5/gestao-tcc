import api from "./api";
import { IaSuggestionRequest, IaSuggestionResponse } from "@/interfaces";

export const sugerirOrientadoresIa = async (
  payload: IaSuggestionRequest
): Promise<IaSuggestionResponse> => {
  const response = await api.post("/api/ia/orientadores/sugerir", payload);
  return response.data;
};

