import { PapelUsuario } from '@/interfaces';

export const isOrientador = (papel?: PapelUsuario) => papel === PapelUsuario.ORIENTADOR;
export const isAluno = (papel?: PapelUsuario) => papel === PapelUsuario.ALUNO;
export const isCoordenador = (papel?: PapelUsuario) => papel === PapelUsuario.COORDENADOR;
