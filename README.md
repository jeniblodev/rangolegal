# RangoLegal

### Descrição do problema
O projeto tem origem na necessidade de um grupo de restaurantes da nossa região, que buscam reduzir os custos operacionais utilizando um sistema único e compartilhado. O foco é permitir que os estabelecimentos gerenciem suas operações enquanto os clientes podem consultar informações, fazer avaliações e realizar pedidos. Assim é possível garantir que a competição individual entre os restaurantes seja com base na qualidade da comida oferecida, e não pela qualidade do sistema de gestão individual.

### Objetivo do projeto
Desenvolver um backend robusto utilizando Spring Boot, Docker Compose e Maven para gerenciar usuários (Clientes e Donos de Restaurante), garantindo segurança no armazenamento de senhas, integridade de dados e uma API padronizada.

## Instrução de Instalação do projeto

**Pré-requisitos**

Antes de começar, você precisará ter instalado:
* Java 21
* JDK
* Maven
* Docker Compose
* Postman

**Configuração do Banco de Dados**

O projeto utiliza Docker para gerenciar o banco de dados.

* Navegue até a raiz do projeto.
* Execute o comando para subir o container do banco: `docker-compose up -d`

**Clone o repositório:**

* `git clone https://github.com/jeniblodev/rangolegal.git`

**Entre na pasta do projeto:**

* cd seu-projeto
* Compile e rode a aplicação usando o Maven Wrapper: `./mvnw spring-boot:run`

A aplicação estará disponível em http://localhost:8080.

## Instrução de uso

**Para utilizar a API:**
| Endpoint | Método | Descrição |
| :--- | :---: | :--- |
| /v1/users?name= | GET | Buscar usuário pelo nome através de Query Params (?name=) |
| /v1/users/{id} | GET | Buscar dados do usuário por ID |
| /v1/auth/login | POST | Validar credenciais e autorizar o acesso ao sistema |
| /v1/users | POST | Registrar novo usuário |
| /v1/users/{id}/data | PATCH | Atualizar dados cadastrais básicos (nome, login, endereço) |
| /v1/users/{id}/password | PATCH | Atualizar senha |
| /v1/users/{id} | DELETE | Deletar usuário |
| /V1/user-types/{id} | GET | Buscar tipo de usuário por ID
| /V1/user-types/{id} | PUT | Atualizar tipo de usuário por ID
| /V1/user-types/{id} | DELETE | Remover tipo de usuário por ID
| /V1/user-types | GET | Listar tipos de usuário
| /V1/user-types | POST | Registrar novo tipo de usuário
| /V1/menu_item | GET | Registrar nova refeição no cardário
| /V1/menu_item | POST | Registrar refeição no cardário
| /V1/menu_item | PATCH | Atualizar refeição pelo nome no cardário
| /V1/menu_item | DELETE | Deletar refeição no cardário
| /V1/restaurant| GET | Buscar restaurantes pelo nome
| /V1/restaurant | POST | Registrar novo restaurante
| /V1/restaurant/{id}/data | PATCH | Atualizar dados do restaurantes pelo ID
| /V1/restaurant/{id} | GET | Remover restaurantes pelo ID
| /V1/restaurant/{id} | DELETE | Remover restaurantes pelo ID

**Para acessar ao banco mySql**
docker exec -it db_tech_challenge mysql -u root -p
a senha que está no docker compose
após dê o comando `USE rangolegal;` para acessar a tabela e o SELECT que precisa.

## Licença
Permissão de uso para fins educativos, não comercial
