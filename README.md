# RangoLegal

##### Para atender a demanda do grupo de restaurantes da nossa região, vamos desenvolver um sistema único e compartilhado. Permitindo melhor interação entre os clientes e os restaurantes.

## Instrução de Instalação do projeto
Pré-requisitos
Antes de começar, você precisará ter instalado:
Java 21
JDK
Maven
Docker Compose
Postman

Configuração do Banco de Dados
O projeto utiliza Docker para gerenciar o banco de dados.

Navegue até a raiz do projeto.
Execute o comando para subir o container do banco:
Bash
docker-compose up -d

Clone o repositório:
Bash
git clone https://github.com/JsCardoso896/rangolegal-base-Jeni](https://github.com/jeniblodev/rangolegal/tree/base-project

Entre na pasta do projeto:
Bash
cd seu-projeto
Compile e rode a aplicação usando o Maven Wrapper:

Bash
./mvnw spring-boot:run
A aplicação estará disponível em http://localhost:8080.

## Instrução de uso

Para utilizar a API: 
Utilizar os endpoints:
- http://localhost:8080/v1/users?name=
Método GET para buscar usuário pelo nome
- http://localhost:8080/v1/auth/login
Método POST para fazer login
- http://localhost:8080/v1/users
Método POST para registrar novo usuário
- http://localhost:8080/v1/users/2/data
Método PATCH para atualizar dados cadastrais
- http://localhost:8080/v1/users/1/password
Método PATCH para atualizar senha
- http://localhost:8080/v1/users/1/data
Método PATCH para buscar dados do usuário por ID
- http://localhost:8080/v1/users/1
Método DELETE para deletar usuário

Para acessar ao banco mySql
Bash
docker exec -it db_tech_challenge mysql -u root -p
a senha que está no docker compose
após dê o comenado USE rangolegal; para acessar a tabela e o SELECT que precisa.

## Licença
## Permição de uso para fins educativos, não comercial
