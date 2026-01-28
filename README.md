# 🏨 Hotel Management System API

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3-green)
![CI Status](https://github.com/MReis05/HotelManagementSystem-API/actions/workflows/pipeline.yaml/badge.svg)

## 📖 Sobre o Projeto

Este projeto é uma **API RESTful** desenvolvida para gerenciar as operações fundamentais de um hotel. O objetivo é automatizar fluxos de **Check-in, Check-out, Reservas e Consumo de hóspedes**, garantindo integridade nos dados e agilidade no atendimento.

O sistema foi modelado para resolver problemas reais de negócio, como validação de datas, cálculo automático de diárias e controle de status dos quartos.

---

## 🚀 Tecnologias Utilizadas

* **Java 21** (LTS)
* **Spring Boot 3**
* **Spring Data JPA** (Persistência de dados)
* **H2 Database** (Banco em memória para testes rápidos)
* **MySQL** (Banco de dados de produção)
* **JUnit 5 & Mockito** (Testes Unitários e de Integração)
* **GitHub Actions** (CI/CD Pipeline)
* **Maven** (Gerenciamento de dependências)

---

## ⚙️ Arquitetura e CI/CD

Este projeto adota práticas de **DevOps** e **Integração Contínua (CI)**.
A cada *push* ou *Pull Request* para a branch principal, um pipeline automatizado é disparado no GitHub Actions para garantir a qualidade do software.

**O Pipeline executa:**
1.  ✅ Configuração do ambiente (Java 21).
2.  ✅ Compilação do projeto.
3.  ✅ Execução de **Testes Unitários** automatizados.
4.  ✅ Geração do artefato final (`.jar`).

Isso garante que **nenhum código quebrado chegue à produção**, mantendo a estabilidade do sistema.

---

## 🛠️ Funcionalidades Principais

A API está organizada em recursos que refletem o domínio hoteleiro:

* **Hóspedes (Guests):** Cadastro e gerenciamento de histórico.
* **Reservas (Reservations):** Criação de reservas futuras com validação de disponibilidade.
* **Estadias (Stays):**
    * **Check-in:** Validação rigorosa de data (só permite check-in na data correta).
    * **Check-out:** Encerramento da estadia e liberação do quarto.
    * **Consumo (Incidentals):** Adição de produtos/serviços à conta do quarto (ex: Frigobar).
* **Quartos (Rooms):** Controle de tipos (Solteiro/Casal) e status (Livre/Ocupado/Manutenção).
* **Pagamentos:** Processamento e registro financeiro.

---

## 📦 Como Rodar o Projeto

### Pré-requisitos
* Java 21 instalado
* Maven

### Passo a passo
1.  Clone o repositório:
    ```bash
    git clone [https://github.com/MReis05/HotelManagementSystem-API.git](https://github.com/MReis05/HotelManagementSystem-API.git)
    ```
2.  Entre na pasta:
    ```bash
    cd HotelManagementSystem-API
    ```
3.  Execute o projeto:
    ```bash
    ./mvnw spring-boot:run
    ```

A API estará disponível em `http://localhost:8080`.

---

## 🧪 Rodando os Testes

Para executar a bateria de testes automatizados e verificar a integridade da aplicação:

```bash
./mvnw clean test
```

Autor
Matheus Reis Cardoso

**LinkedIn:** https://www.linkedin.com/in/matheus-reis-cardoso-6a619120b/

**GitHub:** https://github.com/MReis05