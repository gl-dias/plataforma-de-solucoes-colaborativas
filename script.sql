-- Criar o banco de dados
CREATE DATABASE IF NOT EXISTS plataforma_de_solucoes_colaborativas;
USE plataforma_de_solucoes_colaborativas;

-- Criar tabela de usuários
CREATE TABLE IF NOT EXISTS usuario (
    id VARCHAR(36) PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    senha_criptografada VARCHAR(100) NOT NULL,
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ativo BOOLEAN DEFAULT true
);

-- Criar tabela de perfis de usuário
CREATE TABLE IF NOT EXISTS perfil_usuario (
    id VARCHAR(36) PRIMARY KEY,
    biografia TEXT,
    foto_perfil_uri VARCHAR(255),
    habilidades TEXT,
    usuario_id VARCHAR(36),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

-- Criar tabela de projetos
CREATE TABLE IF NOT EXISTS projetos (
    id VARCHAR(36) PRIMARY KEY,
    titulo VARCHAR(100) NOT NULL,
    descricao TEXT,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_conclusao TIMESTAMP,
    usuario_id VARCHAR(36),
    status VARCHAR(20) DEFAULT 'EM_ANDAMENTO',
    FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

-- Criar tabela de tarefas
CREATE TABLE IF NOT EXISTS tarefas (
    id VARCHAR(36) PRIMARY KEY,
    titulo VARCHAR(100) NOT NULL,
    descricao TEXT,
    status VARCHAR(20) DEFAULT 'PENDENTE',
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_conclusao TIMESTAMP,
    projeto_id VARCHAR(36),
    usuario_responsavel_id VARCHAR(36),
    prioridade VARCHAR(20) DEFAULT 'MEDIA',
    FOREIGN KEY (projeto_id) REFERENCES projetos(id),
    FOREIGN KEY (usuario_responsavel_id) REFERENCES usuario(id)
);

-- Criar tabela de soluções
CREATE TABLE IF NOT EXISTS solucoes (
    id VARCHAR(36) PRIMARY KEY,
    titulo VARCHAR(100) NOT NULL,
    descricao TEXT,
    data_submissao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    tarefa_id VARCHAR(36),
    usuario_id VARCHAR(36),
    status VARCHAR(20) DEFAULT 'PENDENTE',
    FOREIGN KEY (tarefa_id) REFERENCES tarefas(id),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

-- Criar tabela de avaliações
CREATE TABLE IF NOT EXISTS avaliacoes (
    id VARCHAR(36) PRIMARY KEY,
    nota INT NOT NULL CHECK (nota >= 0 AND nota <= 5),
    comentario TEXT,
    data_avaliacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    solucao_id VARCHAR(36),
    usuario_avaliador_id VARCHAR(36),
    FOREIGN KEY (solucao_id) REFERENCES solucoes(id),
    FOREIGN KEY (usuario_avaliador_id) REFERENCES usuario(id)
);
