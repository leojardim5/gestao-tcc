
// Enums from Backend
export enum PapelUsuario {
  ALUNO = "ALUNO",
  ORIENTADOR = "ORIENTADOR",
  COORDENADOR = "COORDENADOR",
}

export enum StatusTcc {
  RASCUNHO = "RASCUNHO",
  EM_ANDAMENTO = "EM_ANDAMENTO",
  AGUARDANDO_DEFESA = "AGUARDANDO_DEFESA",
  CONCLUIDO = "CONCLUIDO",
}

export enum StatusSubmissao {
  EM_REVISAO = "EM_REVISAO",
  APROVADO = "APROVADO",
  REPROVADO = "REPROVADO",
}

export enum TipoReuniao {
  PRESENCIAL = "PRESENCIAL",
  ONLINE = "ONLINE",
}

export enum TipoNotificacao {
  PRAZO = "PRAZO",
  REUNIAO = "REUNIAO",
  COMENTARIO = "COMENTARIO",
  SISTEMA = "SISTEMA",
}

// Base Interface for pagination
export interface Page<T> {
  content: T[];
  pageable: {
    pageNumber: number;
    pageSize: number;
    sort: {
      sorted: boolean;
      unsorted: boolean;
      empty: boolean;
    };
    offset: number;
    paged: boolean;
    unpaged: boolean;
  };
  last: boolean;
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
  sort: {
    sorted: boolean;
    unsorted: boolean;
    empty: boolean;
  };
  first: boolean;
  numberOfElements: number;
  empty: boolean;
}


// DTOs aligned with Backend

export interface Usuario {
  id: string;
  nome: string;
  email: string;
  papel: PapelUsuario;
  ativo: boolean;
  criadoEm: string; // ISO Date string
}

export interface UsuarioCreateRequest {
  nome: string;
  email: string;
  senha?: string; // Not always required on update
  papel: PapelUsuario;
}

export interface Tcc {
    id: string;
    titulo: string;
    aluno: Usuario;
    orientador: Usuario;
    status: StatusTcc;
    tema: string;
    curso: string;
    dataInicio: string; // ISO Date string
    dataTermino?: string; // ISO Date string
    criadoEm: string; // ISO Date string
}

export interface TccCreateRequest {
    titulo: string;
    alunoId: string;
    orientadorId: string;
    tema: string;
    curso: string;
    dataInicio: string; // ISO Date string
}

export interface TccUpdateRequest {
    titulo?: string;
    orientadorId?: string;
    tema?: string;
    curso?: string;
    status?: StatusTcc;
    dataTermino?: string; // ISO Date string
}

export interface Submissao {
    id: string;
    tcc: Tcc;
    versao: number;
    titulo: string;
    resumo: string;
    palavrasChave: string;
    arquivoUrl: string;
    status: StatusSubmissao;
    dataSubmissao: string; // ISO Date string
    feedback?: string;
}

export interface SubmissaoCreateRequest {
    tccId: string;
    titulo: string;
    resumo: string;
    palavrasChave: string;
    arquivoUrl: string;
}

export interface SubmissaoDecisionRequest {
    status: StatusSubmissao;
    feedback: string;
}

export interface Reuniao {
    id: string;
    tcc: Tcc;
    tema: string;
    data: string; // ISO Date string
    tipo: TipoReuniao;
    local?: string;
    link?: string;
    criadoEm: string; // ISO Date string
}

export interface ReuniaoCreateRequest {
    tccId: string;
    tema: string;
    data: string; // ISO Date string
    tipo: TipoReuniao;
    local?: string;
    link?: string;
}

export interface Comentario {
    id: string;
    submissao: Submissao;
    autor: Usuario;
    comentario: string;
    criadoEm: string; // ISO Date string
}

export interface ComentarioCreateRequest {
    submissaoId: string;
    autorId: string;
    comentario: string;
}

export interface Notificacao {
    id: string;
    destinatario: Usuario;
    mensagem: string;
    lida: boolean;
    tipo: TipoNotificacao;
    criadoEm: string; // ISO Date string
}
