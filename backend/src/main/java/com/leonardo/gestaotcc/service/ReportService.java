package com.leonardo.gestaotcc.service;

import com.leonardo.gestaotcc.entity.Tcc;
import com.leonardo.gestaotcc.entity.Submissao;
import com.leonardo.gestaotcc.entity.Usuario;
import com.leonardo.gestaotcc.enums.StatusTcc;
import com.leonardo.gestaotcc.enums.StatusSubmissao;
import com.leonardo.gestaotcc.repository.TccRepository;
import com.leonardo.gestaotcc.repository.SubmissaoRepository;
import com.leonardo.gestaotcc.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final TccRepository tccRepository;
    private final SubmissaoRepository submissaoRepository;
    private final UsuarioRepository usuarioRepository;

    public byte[] generateTccReport() throws IOException {
        List<Tcc> tccs = tccRepository.findAll();
        
        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>Relatório de TCCs</title></head><body>");
        html.append("<h1>Relatório de TCCs</h1>");
        html.append("<p>Data de geração: ").append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("</p>");
        
        // Estatísticas gerais
        Map<StatusTcc, Long> statusCount = new HashMap<>();
        for (StatusTcc status : StatusTcc.values()) {
            statusCount.put(status, tccRepository.countByStatus(status));
        }
        
        html.append("<h2>Estatísticas Gerais</h2>");
        html.append("<table border='1' style='border-collapse: collapse; width: 100%;'>");
        html.append("<tr><th>Status</th><th>Quantidade</th></tr>");
        for (Map.Entry<StatusTcc, Long> entry : statusCount.entrySet()) {
            html.append("<tr><td>").append(entry.getKey().name()).append("</td><td>").append(entry.getValue()).append("</td></tr>");
        }
        html.append("</table>");
        
        // Lista de TCCs
        html.append("<h2>Lista de TCCs</h2>");
        html.append("<table border='1' style='border-collapse: collapse; width: 100%;'>");
        html.append("<tr><th>Título</th><th>Aluno</th><th>Orientador</th><th>Status</th><th>Data Início</th><th>Prazo</th></tr>");
        
        for (Tcc tcc : tccs) {
            html.append("<tr>");
            html.append("<td>").append(tcc.getTitulo()).append("</td>");
            html.append("<td>").append(tcc.getAluno() != null ? tcc.getAluno().getNome() : "N/A").append("</td>");
            html.append("<td>").append(tcc.getOrientador() != null ? tcc.getOrientador().getNome() : "N/A").append("</td>");
            html.append("<td>").append(tcc.getStatus().name()).append("</td>");
            html.append("<td>").append(tcc.getDataInicio() != null ? tcc.getDataInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A").append("</td>");
            html.append("<td>").append(tcc.getDataEntregaPrevista() != null ? tcc.getDataEntregaPrevista().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A").append("</td>");
            html.append("</tr>");
        }
        html.append("</table>");
        html.append("</body></html>");
        
        return convertHtmlToPdf(html.toString());
    }

    public byte[] generateSubmissionReport() throws IOException {
        List<Submissao> submissoes = submissaoRepository.findAll();
        
        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>Relatório de Submissões</title></head><body>");
        html.append("<h1>Relatório de Submissões</h1>");
        html.append("<p>Data de geração: ").append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("</p>");
        
        // Estatísticas de submissões
        Map<StatusSubmissao, Long> statusCount = new HashMap<>();
        for (StatusSubmissao status : StatusSubmissao.values()) {
            statusCount.put(status, submissaoRepository.countByStatus(status));
        }
        
        html.append("<h2>Estatísticas de Submissões</h2>");
        html.append("<table border='1' style='border-collapse: collapse; width: 100%;'>");
        html.append("<tr><th>Status</th><th>Quantidade</th></tr>");
        for (Map.Entry<StatusSubmissao, Long> entry : statusCount.entrySet()) {
            html.append("<tr><td>").append(entry.getKey().name()).append("</td><td>").append(entry.getValue()).append("</td></tr>");
        }
        html.append("</table>");
        
        // Lista de submissões
        html.append("<h2>Lista de Submissões</h2>");
        html.append("<table border='1' style='border-collapse: collapse; width: 100%;'>");
        html.append("<tr><th>TCC</th><th>Versão</th><th>Status</th><th>Data Envio</th></tr>");
        
        for (Submissao submissao : submissoes) {
            html.append("<tr>");
            html.append("<td>").append(submissao.getTcc().getTitulo()).append("</td>");
            html.append("<td>").append(submissao.getVersao()).append("</td>");
            html.append("<td>").append(submissao.getStatus().name()).append("</td>");
            html.append("<td>").append(submissao.getEnviadoEm().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("</td>");
            html.append("</tr>");
        }
        html.append("</table>");
        html.append("</body></html>");
        
        return convertHtmlToPdf(html.toString());
    }

    public byte[] generateUserReport() throws IOException {
        List<Usuario> usuarios = usuarioRepository.findAll();
        
        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>Relatório de Usuários</title></head><body>");
        html.append("<h1>Relatório de Usuários</h1>");
        html.append("<p>Data de geração: ").append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("</p>");
        
        // Estatísticas de usuários
        long totalUsuarios = usuarios.size();
        long usuariosAtivos = usuarios.stream().mapToLong(u -> u.isAtivo() ? 1 : 0).sum();
        
        html.append("<h2>Estatísticas de Usuários</h2>");
        html.append("<table border='1' style='border-collapse: collapse; width: 100%;'>");
        html.append("<tr><th>Total de Usuários</th><th>Usuários Ativos</th><th>Usuários Inativos</th></tr>");
        html.append("<tr><td>").append(totalUsuarios).append("</td><td>").append(usuariosAtivos).append("</td><td>").append(totalUsuarios - usuariosAtivos).append("</td></tr>");
        html.append("</table>");
        
        // Lista de usuários
        html.append("<h2>Lista de Usuários</h2>");
        html.append("<table border='1' style='border-collapse: collapse; width: 100%;'>");
        html.append("<tr><th>Nome</th><th>Email</th><th>Papel</th><th>Status</th><th>Data Criação</th></tr>");
        
        for (Usuario usuario : usuarios) {
            html.append("<tr>");
            html.append("<td>").append(usuario.getNome()).append("</td>");
            html.append("<td>").append(usuario.getEmail()).append("</td>");
            html.append("<td>").append(usuario.getPapel().name()).append("</td>");
            html.append("<td>").append(usuario.isAtivo() ? "Ativo" : "Inativo").append("</td>");
            html.append("<td>").append(usuario.getCriadoEm().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("</td>");
            html.append("</tr>");
        }
        html.append("</table>");
        html.append("</body></html>");
        
        return convertHtmlToPdf(html.toString());
    }

    private byte[] convertHtmlToPdf(String html) throws IOException {
        // Implementação simplificada - em produção, usar biblioteca como iText ou Flying Saucer
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        // Por enquanto, retornar HTML como bytes
        // Em produção, implementar conversão real para PDF
        outputStream.write(html.getBytes("UTF-8"));
        
        return outputStream.toByteArray();
    }
}
