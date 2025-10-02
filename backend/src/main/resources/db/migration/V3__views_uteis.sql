-- Prompt 3: Views úteis para consultas comuns

-- View para obter a última submissão de cada TCC
CREATE OR REPLACE VIEW v_ultima_submissao AS
WITH ranked_submissoes AS (
    SELECT
        s.tcc_id,
        s.numero,
        s.enviado_em,
        ROW_NUMBER() OVER(PARTITION BY s.tcc_id ORDER BY s.numero DESC) as rn
    FROM
        submissoes s
)
SELECT
    rs.tcc_id,
    rs.numero,
    rs.enviado_em
FROM
    ranked_submissoes rs
WHERE
    rs.rn = 1;

-- View para listar prazos não cumpridos nos próximos 7 dias
CREATE OR REPLACE VIEW v_proximos_prazos AS
SELECT
    p.id,
    p.tcc_id,
    p.titulo,
    p.descricao,
    p.data_limite,
    p.cumprido,
    p.criado_por,
    p.criado_em
FROM
    prazos p
WHERE
    p.cumprido = FALSE
    AND p.data_limite BETWEEN NOW() AND NOW() + INTERVAL '7 days'
ORDER BY
    p.data_limite ASC;
