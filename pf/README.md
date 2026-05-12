Implemente uma API REST para gerenciamento de usuários e projetos.


Entidades:
User
id
nome
cpf
papel (ADMIN, USER)
Projeto
id
nome
descricao
Requisitos:
Um usuário pode participar de vários projetos e um projeto pode ter vários usuários.
Implemente as operações CRUD para ambas as entidades.
Permita adicionar e remover usuários de projetos.
Controle de acesso:
GET liberado
POST, PUT, DELETE apenas ADMIN
Toda requisição deve conter o id do usuário no header (X-USER-ID).
Utilize Spring Data JPA.
Adicione validações básicas (ex: CPF obrigatório).
Utilize DTOs.
Implemente tratamento de erros global com respostas padronizadas (JSON com mensagem, status, timestamp, etc.).

