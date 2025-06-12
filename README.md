# Plataforma de Soluções Colaborativas

Sistema colaborativo para gerenciamento de projetos desenvolvido em Java com persistência em MySQL.

## Integrantes do Grupo
| Integrante | Matrícula |
| ------------- | ------------- |
| Guilherme Dias  | 202402972091  |
| Guilherme Pardelhas  | 202402697668  |
| Guilherme Resende  | 202402075365  |

## 📋 Pré-requisitos

- Java 21 ou superior
- MySQL 8.0 ou superior
- Maven

## ⚙️ Configuração do Banco de Dados

1. Crie um banco MySQL:
```sql
CREATE SCHEMA plataforma_de_solucoes_colaborativas;
```

2. Configure a conexão em src/main/resources/application.properties:
```
jdbc.url=jdbc:mysql://localhost:3306/plataforma_solucoes
jdbc.username=root
jdbc.password=Bnk03112005@
```

🚀 Execução
A classe principal é: *src/Main.java*

Para executar:
3. Clone o repositório:

```
git clone https://github.com/gl-dias/plataforma-de-solucoes-colaborativa.git
```

4. Entre no diretório:
```
cd plataforma-de-solucoes-colaborativa
```

5. Compile:
```
mvn clean install
```

6. Execute:
```
java -cp target/classes Main
```

📁 Estrutura
```
src/
├── model/      # Classes de domínio
├── dao/        # Classes de acesso a dados
├── bd/         # Configs do banco
└── Main.java   # Classe principal
```

💻 Funcionalidades
- Gerenciamento de usuários  
- Criação e gestão de projetos  
- Registro de tarefas  
- Propostas de soluções  
- Sistema de avaliações  
