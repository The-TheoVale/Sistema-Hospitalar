# 🏥 Sistema Hospitalar / Gestão de Clínica Médica

> 🎓 **Projeto desenvolvido para a disciplina de Programação Orientada a Objetos (POO).**  
> 👥 **Autores:**  
> - [Theodorico Vale](https://github.com/The-TheoVale)  
> - [Miguel Maia](https://github.com/MiguelmxDev)  

---

## 📌 Sobre o Projeto

O **Sistema Hospitalar** é uma aplicação desktop desenvolvida em **Java** com interface gráfica **Java Swing**. O sistema permite o gerenciamento completo de consultas médicas, prontuários, lista de espera automática, faturamento e avaliações de médicos em uma clínica médica.

A aplicação conta com autenticação segregada para **Pacientes** e **Médicos**, oferecendo jornadas personalizadas para cada perfil de usuário com persistência local de dados em arquivos CSV.

---

## 🛠️ Tecnologias e Conceitos de POO

### Tecnologias
- **Linguagem:** Java (JDK 17+)
- **Interface Gráfica (GUI):** Java Swing / AWT
- **Persistência de Dados:** Arquivos CSV (`dados/`)
- **IDE Compatível:** VS Code, IntelliJ IDEA, Eclipse

### Conceitos de Programação Orientada a Objetos Aplicados

- **Abstração & Herança:**  
  - Classe abstrata `Usuario`, base para `Paciente` e `Medico`.
  - Classe abstrata `Medico`, especializada em classes de especialidades médicas (`Cardiologista`, `ClinicoGeral`, `Pediatra`).
- **Polimorfismo:**  
  - Sobrescrita do método `getValorConsulta()` nas subclasses de `Medico` para adição de taxas específicas de especialidade (ex.: valor adicional para consultas com cardiologista).
- **Encapsulamento:**  
  - Controle rígido de acesso a atributos via modificadores `private`/`protected` e exposição por getters e setters adequados.
- **Tratamento de Exceções Customizadas:**  
  - Exceções de domínio bem especificadas para consistência de fluxo, como `LoginInvalidoException`, `UsuarioDuplicadoException`, `ConsultaInexistenteException`, `AvaliacaoInvalidaException` e `EspecialidadeInvalidaException`.
- **Arquitetura MVC / Camadas:**  
  - **`model`**: Classes de domínio e entidades do sistema.
  - **`repositorio`**: Camada de persistência e manipulação de arquivos CSV.
  - **`service`**: Regras de negócio, validações e orquestração.
  - **`ui`**: Componentes de interface gráfica Swing.
  - **`exception`**: Exceções de domínio da aplicação.

---

## ✨ Funcionalidades Principais

### 👤 Perfil do Paciente
- 🔐 **Autenticação & Cadastro:** Criação de conta com opção de informar plano de saúde.
- 📅 **Agendamento de Consultas:** Busca de médicos por especialidade e seleção de datas disponíveis.
- ⏳ **Lista de Espera Inteligente:** Inclusão automática na lista de espera caso o médico atinja o limite diário de consultas.
- 🔔 **Notificações:** Alerta automático ao ser promovido da lista de espera após o cancelamento de uma consulta por outro paciente.
- 💳 **Pagamento:** Liquidação de consultas particulares ou convênio médico.
- ⭐️ **Avaliação de Atendimento:** Atribuição de notas (1 a 5 estrelas) e parecer sobre a consulta realizada.
- ✏️ **Atualização de Perfil:** Alteração de dados cadastrais (nome e senha).

### 👨‍⚕️ Perfil do Médico
- 🩺 **Painel da Agenda:** Visualização de consultas marcadas para o dia e histórico.
- 📝 **Realização de Consulta:** Registro de prontuário, descrição do atendimento, prescrição de medicamentos (receita) e exames solicitados.
- 📊 **Painel Estatístico:** Indicadores de desempenho, faturamento total, total de consultas realizadas/canceladas e média de avaliações recebidas.
- 💬 **Visualização de Feedbacks:** Leitura das avaliações e comentários deixados pelos pacientes.

---

## 📂 Estrutura do Projeto

```text
Sistema Hospital/
├── src/
│   ├── App.java                   # Ponto de entrada (método main)
│   └── clinica/
│       ├── exception/             # Exceções customizadas do sistema
│       ├── model/                 # Modelos de domínio (Usuario, Medico, Paciente, Consulta, etc.)
│       ├── repositorio/           # Leitura e gravação nos arquivos CSV
│       ├── service/               # Serviços e lógica de negócio
│       └── ui/                    # Telas e formulários Swing
├── dados/                         # Banco de dados em arquivos CSV
│   ├── avaliacoes.csv
│   ├── consultas.csv
│   ├── contas.csv
│   ├── listaespera.csv
│   ├── medicos.csv
│   ├── notificacoes.csv
│   └── pacientes.csv
├── lib/                           # Bibliotecas e dependências externas (.jar)
└── bin/                           # Arquivos Java compilados (.class)
```

---

## 🚀 Como Executar o Projeto

### Pré-requisitos
- **Java Development Kit (JDK 17+)** instalado e configurado no PATH.

### Passos para Execução

1. **Clonar o repositório:**
   ```bash
   git clone https://github.com/The-TheoVale/Sistema-Hospitalar.git
   cd Sistema-Hospitalar
   ```

2. **Compilar o código fonte:**
   ```bash
   javac -d bin -sourcepath src src/App.java
   ```

3. **Executar a aplicação:**
   ```bash
   java -cp bin App
   ```

