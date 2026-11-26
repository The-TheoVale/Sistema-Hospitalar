# 🏥 Sistema Hospitalar / Gestão de Clínica Médica

> 🎓 **Projeto desenvolvido para a disciplina de Banco de Dados.**
> 👥 **Autores:**
> - [Theodorico Vale](https://github.com/The-TheoVale)
> - Arthur dos Santos Sales

---

## 📌 Sobre o Projeto

O **Sistema Hospitalar** é uma aplicação desktop em **Java** com interface gráfica **Java Swing**, usada para gerenciar consultas médicas, pagamentos, receitas e estatísticas de uma clínica.

A aplicação tem autenticação separada para **Pacientes** e **Médicos**, com um fluxo próprio para cada perfil. Os dados ficam em um banco **PostgreSQL remoto (Neon)**, acessado por **JDBC**.

> **Histórico:** a primeira versão, feita para POO, guardava os dados em arquivos CSV. Depois a persistência foi migrada para o PostgreSQL, seguindo o esquema relacional do trabalho. Avaliações, lista de espera e notificações existiam só na versão em CSV e foram removidas na migração.

---

## 🛠️ Tecnologias

| Item | Tecnologia |
| --- | --- |
| Linguagem | Java (JDK 17+) |
| Interface gráfica | Java Swing / AWT |
| Banco de dados | PostgreSQL remoto hospedado na [Neon](https://neon.tech) (porta 5432, SSL obrigatório) |
| Acesso ao banco | JDBC puro, sem ORM: driver `postgresql-42.7.14.jar` (pasta `lib/`) e `PreparedStatement` |
| IDEs | IntelliJ IDEA (projeto configurado), VS Code ou Eclipse |

---

## 🧱 Arquitetura

O código é organizado em camadas, dentro de `src/clinica/`:

| Pacote | Responsabilidade |
| --- | --- |
| `config` | `Conexao`: lê o `.env` e abre a conexão JDBC com o PostgreSQL |
| `model` | Entidades de domínio: `Usuario`, `Paciente`, `Medico` (e especialidades), `Consulta`, `Conta`, `Receita`, `Plano` |
| `repositorio` | Acesso ao banco com SQL parametrizado: `PacienteRepository`, `MedicoRepository`, `ConsultaRepository`, `ContaRepository`, `ReceitaRepository`, `PlanoRepository`, `UtilizaRepository` |
| `service` | Regras de negócio: `AuthService`, `AgendamentoService`, `ConsultaService`, `EstatisticasService` |
| `ui` | Telas Swing (login, cadastro, menus, agendamento, pagamento, realização de consulta, estatísticas) |
| `exception` | Exceções de domínio: `LoginInvalidoException`, `UsuarioDuplicadoException`, `ConsultaInexistenteException`, `EspecialidadeInvalidaException` |

**Conceitos aplicados**

- **Abstração e herança:** `Usuario` (abstrata) é a base de `Paciente` e `Medico`; `Medico` (abstrata) é especializada em `Cardiologista`, `ClinicoGeral`, `Pediatra` e `Cirurgiao`.
- **Polimorfismo:** as subclasses de `Medico` sobrescrevem `getValorConsulta()` quando a especialidade tem adicional (por exemplo, `Cardiologista`).
- **Encapsulamento:** atributos privados ou protegidos, com acesso por getters e setters.
- **Tratamento de exceções:** exceções próprias para erros de domínio; erros de SQL são repassados como `RuntimeException` com mensagem clara e exibidos na interface.

---

## 🗄️ Banco de Dados

O esquema segue o modelo relacional do TP3.

| Tabela | Descrição |
| --- | --- |
| `paciente`, `medico` | Usuários do sistema (e-mail e CPF/CRM únicos) |
| `cardiologista`, `pediatra`, `cirurgiao` | Especializações de `medico` (a especialidade Clínico Geral não tem tabela própria) |
| `plano` | Planos de saúde |
| `utiliza` | Relação médico × paciente × plano (plano nulo = atendimento particular) |
| `consulta` | Consultas agendadas ou realizadas (um médico não atende dois pacientes no mesmo horário) |
| `conta` | Cobrança de uma consulta (uma conta por consulta) |
| `receita` | Prescrições ligadas a uma consulta |

**Recursos de SQL usados pela aplicação**

- `JOIN`, `GROUP BY` e `HAVING`: o painel de estatísticas lista os médicos com N ou mais consultas.
- Busca de médicos por nome com `ILIKE`.
- **Gatilho** `fn_validar_conta`: garante que a conta pertence ao paciente da consulta e define o vencimento padrão de 15 dias. Erros de integridade vindos do banco são mostrados ao usuário.
- **View** `vw_resumo_consultas` e **índice** `idx_consulta_paciente_data`.

Os scripts do TP3 (`01_create.sql`, `02_insert.sql`, `03_objects.sql` e `04_tests.sql`) criam o esquema, populam o banco, criam a view, o índice e o gatilho, e contêm os testes. Execute-os nessa ordem em um banco PostgreSQL vazio.

---

## ✨ Funcionalidades

### 👤 Perfil do Paciente
- 🔐 **Cadastro e login**, com opção de informar o plano de saúde.
- 📅 **Agendar consulta**, buscando o médico por nome e escolhendo a data em um calendário.
- ❌ **Cancelar agendamento.**
- 💳 **Pagar** a conta de uma consulta.
- ✏️ **Alterar dados** cadastrais.

### 👨‍⚕️ Perfil do Médico
- 🩺 **Realizar consulta:** registro do atendimento e emissão de receita.
- 📊 **Painel de estatísticas:** especialidade mais procurada, total de consultas realizadas, gráfico de consultas por médico e consulta de médicos com N ou mais consultas.
- ✏️ **Alterar dados** cadastrais.

---

## 📂 Estrutura do Projeto

```text
Sistema-Hospitalar/
├── src/
│   ├── App.java                   # Ponto de entrada (main)
│   └── clinica/
│       ├── config/                # Conexão com o PostgreSQL (Conexao)
│       ├── exception/             # Exceções de domínio
│       ├── model/                 # Entidades
│       ├── repositorio/           # Acesso ao banco (JDBC)
│       ├── service/               # Regras de negócio
│       └── ui/                    # Telas Swing
├── lib/
│   └── postgresql-42.7.14.jar     # Driver JDBC do PostgreSQL
├── dados/                         # CSVs da versão antiga (não são mais usados)
├── .env                           # Credenciais do banco (NÃO versionado)
└── README.md
```

---

## 🚀 Como Executar

### Pré-requisitos
- **JDK 17+** instalado e no PATH.
- Um banco **PostgreSQL** com o esquema do TP3 carregado (por exemplo, um projeto gratuito na [Neon](https://neon.tech)).

### 1. Clonar o repositório
```bash
git clone https://github.com/The-TheoVale/Sistema-Hospitalar.git
cd Sistema-Hospitalar
```

### 2. Configurar o banco
Crie um arquivo `.env` na **raiz do projeto** (ele já está no `.gitignore` e nunca deve ser enviado ao GitHub):

```properties
NEON_HOST=seu-host.neon.tech
NEON_DATABASE=nome_do_banco
NEON_USER=seu_usuario
NEON_PASSWORD=sua_senha
```

Depois, execute os scripts SQL do TP3 no banco, na ordem `01_create.sql`, `02_insert.sql`, `03_objects.sql`.

### 3. Compilar
Linux/macOS:
```bash
javac -cp lib/postgresql-42.7.14.jar -d bin -sourcepath src src/App.java
```
Windows (o separador do classpath é `;`):
```bat
javac -cp lib\postgresql-42.7.14.jar -d bin -sourcepath src src\App.java
```

### 4. Executar
Execute **a partir da raiz do projeto**, porque o `.env` é lido do diretório atual.

Linux/macOS:
```bash
java -cp "bin:lib/postgresql-42.7.14.jar" App
```
Windows:
```bat
java -cp "bin;lib\postgresql-42.7.14.jar" App
```

> No IntelliJ IDEA, basta abrir o projeto e rodar `App.java`. A biblioteca do driver já está configurada em `.idea/libraries`; confirme que o diretório de trabalho da execução é a raiz do projeto.

---

## ⚠️ Solução de problemas

- **`Arquivo .env não encontrado`:** execute o programa a partir da raiz do projeto e confira o nome do arquivo.
- **`Credenciais da Neon não configuradas corretamente`:** faltou alguma das quatro variáveis no `.env`.
- **Erro de conexão ou SSL:** confira host, usuário e senha; a conexão exige `sslmode=require`.
