# API Central de Validação MFA e Auditoria 🔐

## 🎯 Sobre o Projeto

Este projeto consiste em uma API REST robusta e segura, construída com Java 21 e Spring Boot 3, que atua como um serviço centralizado para fornecer Autenticação de Múltiplos Fatores (MFA) como um Serviço.

A solução foi desenhada para ser facilmente integrada a sistemas legados ou novos (como o Protheus), abstraindo toda a complexidade de geração de segredos TOTP, criação de QR Codes e validação de códigos, além de manter um registro de auditoria completo para todas as operações.

## ✨ Funcionalidades Principais

- 🔐 **Gerenciamento de Segredo MFA (Enrolment)**: Endpoint para cadastrar um dispositivo para um usuário, gerando um segredo único e retornando um QR Code para ser escaneado por aplicativos como Google Authenticator.

- ✅ **Validação de Código TOTP**: Endpoint principal que valida um código de 6 dígitos fornecido pelo usuário, retornando uma resposta binária (true/false).

- 📝 **Registro de Auditoria**: Todas as tentativas de validação são registradas em um log detalhado, contendo ID do usuário, timestamp, resultado, endereço IP de origem e User-Agent.

- 🔒 **Segurança em Camadas**:
    - **Nível de Aplicação**: Acesso à API protegido por uma chave secreta (X-API-KEY).
    - **Nível de Dados**: Segredos dos usuários armazenados de forma criptografada (AES) no banco de dados.

## 🛠️ Stack Tecnológico

| Finalidade | Tecnologia / Biblioteca |
|------------|-------------------------|
| **Linguagem & Framework** | Java 21, Spring Boot 3 |
| **Persistência de Dados** | Spring Data JPA / Hibernate |
| **Banco de Dados** | PostgreSQL (provisionado via Neon) |
| **Segurança da API** | Spring Security 6 (Filtro customizado para API Key) |
| **Lógica TOTP** | `dev.samstevens.totp:totp-spring-boot-starter:1.7.1` |
| **Geração de QR Code** | `com.google.zxing:javase:3.5.3` |
| **Redução de Boilerplate** | Lombok |
| **Build Tool** | Gradle |
| **Containerização** | Docker |
| **Deploy na Nuvem** | Render |

## 🏗️ Arquitetura

O projeto segue os princípios de uma arquitetura limpa e em camadas para garantir a separação de responsabilidades e a manutenibilidade:

```
Controller Layer (/web) → Service Layer (/service) → Repository Layer (/domain)
```

- **Controller**: Recebe as requisições HTTP, valida os DTOs (Data Transfer Objects) e delega a lógica de negócio.
- **Service**: Orquestra a lógica de negócio principal, como a geração de segredos, validação de códigos e comunicação com outros serviços (criptografia, auditoria).
- **Repository**: Interface de acesso aos dados, gerenciada pelo Spring Data JPA.

## 🔑 Modelo de Segurança

A segurança da API é garantida por dois mecanismos distintos:

1. **Autenticação de Sistema (API Key)**: Toda e qualquer requisição à API deve conter o cabeçalho `X-API-KEY` com a chave secreta pré-configurada. Isso garante que somente sistemas autorizados possam consumir os endpoints.

2. **Criptografia de Segredos**: Os segredos TOTP dos usuários nunca são armazenados em texto plano. Eles são criptografados usando o algoritmo `AES/CBC/PKCS5PADDING` antes de serem persistidos no banco de dados.

## 📡 Endpoints da API

A URL base para o ambiente de produção é: `https://passwordgenerator-xcyb.onrender.com`

### 1. Cadastrar Dispositivo MFA (/enrol)

| Detalhe | Descrição |
|---------|-----------|
| **Método** | `POST` |
| **URL** | `/api/v1/mfa/enrol` |
| **Header Obrigatório** | `X-API-KEY: <sua_chave_secreta>` |
| **Corpo (Request)** | `{"userId": "identificador_do_usuario"}` |
| **Resposta (Success)** | `200 OK` com `{"qrCode": "data:image/png;base64,..."}` |

### 2. Validar Código TOTP (/validate)

| Detalhe | Descrição |
|---------|-----------|
| **Método** | `POST` |
| **URL** | `/api/v1/mfa/validate` |
| **Header Obrigatório** | `X-API-KEY: <sua_chave_secreta>` |
| **Corpo (Request)** | `{"userId": "identificador_do_usuario", "code": "123456"}` |
| **Resposta (Success)** | `200 OK` com `{"valid": true}` ou `{"valid": false}` |

## 🚀 Como Executar Localmente

### Pré-requisitos:
- Java (JDK) 21
- Gradle 8.5 ou superior
- Acesso a uma instância de banco de dados PostgreSQL.

### 1. Clone o repositório:
```bash
git clone https://github.com/bioolegar1/GeradordeSenhaMFA.git
cd GeradordeSenhaMFA
```

### 2. Configure as variáveis de ambiente:
Crie um arquivo `application.properties` em `src/main/resources/` e adicione as seguintes propriedades, substituindo os valores:

```properties
spring.datasource.url=jdbc:postgresql://SEU_HOST:5432/SEU_DB
spring.datasource.username=SEU_USUARIO
spring.datasource.password=SUA_SENHA

mfa.encryption.key=UMA_CHAVE_DE_32_CARACTERES_AQUI
mfa.encryption.init-vector=UM_IV_DE_16_CARAC_AQUI

api.security.api-key=UMA_CHAVE_DE_API_SECRETA_AQUI
```

### 3. Execute a aplicação com Gradle:
```bash
./gradlew bootRun
```

A API estará disponível em `http://localhost:8080`.

## 🐳 Executando com Docker

O projeto já inclui um Dockerfile multi-estágio otimizado para produção.

### 1. Construa a imagem Docker:
```bash
docker build -t auditabr-mfa-api .
```

### 2. Execute o container:
Lembre-se de passar todas as variáveis de ambiente necessárias para o container.

```bash
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL="jdbc:postgresql://SEU_HOST:5432/SEU_DB" \
  -e SPRING_DATASOURCE_USERNAME="SEU_USUARIO" \
  -e SPRING_DATASOURCE_PASSWORD="SUA_SENHA" \
  -e MFA_ENCRYPTION_KEY="UMA_CHAVE_DE_32_CARACTERES_AQUI" \
  -e MFA_ENCRYPTION_INIT_VECTOR="UM_IV_DE_16_CARAC_AQUI" \
  -e API_SECURITY_API_KEY="UMA_CHAVE_DE_API_SECRETA_AQUI" \
  auditabr-mfa-api
```

## ☁️ Variáveis de Ambiente para Produção (Render)

Para fazer o deploy no Render ou em qualquer outro provedor de nuvem, configure as seguintes variáveis de ambiente no painel do seu serviço:

| Chave (KEY) | Descrição |
|-------------|-----------|
| `SPRING_DATASOURCE_URL` | URL JDBC completa do seu banco de dados PostgreSQL. |
| `SPRING_DATASOURCE_USERNAME` | Usuário do banco de dados. |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco de dados. |
| `MFA_ENCRYPTION_KEY` | Chave de 32 bytes para a criptografia AES. |
| `MFA_ENCRYPTION_INIT_VECTOR` | Vetor de Inicialização de 16 bytes para AES/CBC. |
| `API_SECURITY_API_KEY` | Chave secreta para autenticação de sistemas. |

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

**Feito com ❤️ por Vanderson H. Santos.**