import api from "./api";
import { IaSuggestionRequest, IaSuggestionResponse } from "@/interfaces";

export const sugerirOrientadoresIa = async (
  payload: IaSuggestionRequest
): Promise<IaSuggestionResponse> => {
  console.log("🌐 ========== FRONTEND: Chamando API de IA ==========");
  console.log("📦 Payload enviado:", JSON.stringify(payload, null, 2));
  console.log("🔗 URL:", "/api/ia/orientadores/sugerir");
  console.log("⏰ Timestamp:", new Date().toISOString());
  
  try {
    const response = await api.post("/api/ia/orientadores/sugerir", payload);
    console.log("✅ FRONTEND: Resposta recebida com sucesso!");
    console.log("📊 Status:", response.status);
    console.log("📋 Headers:", response.headers);
    console.log("📦 Dados da resposta:", JSON.stringify(response.data, null, 2));
    console.log("🌐 ========== FIM FRONTEND API ==========");
    return response.data;
  } catch (error: any) {
    console.error("❌ FRONTEND: Erro na chamada da API!");
    console.error("🚨 Erro completo:", error);
    console.error("📊 Status code:", error?.response?.status);
    console.error("📋 Dados do erro:", error?.response?.data);
    console.error("📋 Mensagem:", error?.message);
    console.error("🌐 ========== FIM ERRO FRONTEND ==========");
    throw error;
  }
};

