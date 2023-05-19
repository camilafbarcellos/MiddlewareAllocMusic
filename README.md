# Middleware de Alocação de Músicas

O Protocolo de Middleware desta aplicação é responsável por gerenciar a conexão e a alocação de recursos entre um ou mais clientes e um servidor, em um contexto de prestação de serviços de música.
Os clientes enviam solicitações de operações para o servidor, que responde com ações no sistema de música correspondente.
Para realizar qualquer operação, seja de usuário comum ou administrador, o cliente deve estar autenticado no servidor por meio de um processo de login.
Se as credenciais do usuário estiverem incorretas, a autenticação falhará e o cliente será desconectado, tendo que iniciar novamente o processo de conexão.

## Lista de mensagens possíveis
| MESSAGE  | DESCRIPTION |
| ------------- | ------------- |
| LOGIN  |  Cliente solicita autenticação com o servidor  |
| LOGINREPLY  |  Servidor responde e verifica as credenciais de login  |
| ADICIONA  |  Cliente admin solicita adicionar música no pool do servidor  |
| ADICIONAREPLY  |  Servidor responde e adiciona a música ao pool  |
| LISTA  |  Cliente admin solicita listar as músicas contidas no pool do servidor  |
| LISTAREPLY  |  Servidor responde e lista as músicas do pool  |
| REMOVE  |  Cliente admin solicita remover uma música do pool do servidor  |
| REMOVEREPLY  |  Servidor responde e remove a música do pool  |
| ESCUTA  |  Cliente solicita escutar uma música do pool do servidor  |
| ESCUTAREPLY  |  Servidor responde com a música para escutar  |
| MOSTRAPLAYLIST  |  Cliente solicita mostra a sua playlist de músicas  |
| MOSTRAPLAYLISTREPLY  |  Servidor responde e lista a sua playlist  |
| ADDPLAYLIST  |  Cliente solicita adicionar música na sua playlist  |
| ADDPLAYLISTREPLY  |  Servidor responde e adiciona a música na playlist  |
| ESCUTAPLAYLIST  |  Cliente solicita escutar a sua playlist  |
| ESCUTAPLAYLISTREPLY  |  Servidor responde com a playlist para escutar  |
| DELPLAYLIST  |  Cliente solicita remover música da sua playlist  |
| DELPLAYLISTREPLY  |  Servidor responde e remove a música da playlist  |
| SAIR  |  Cliente solicita o encerramento da comunicação  |
| SAIRREPLY  |  Servidor responde e encerra a comunicação com o cliente  |

## Lista de códigos de status*
| STATUS  | DESCRIPTION |
| ------------- | ------------- |
| OK  |  Operação completada com sucesso  |
| ERROR  |  Erro no servidor ao processar requisição  |
| PARAMERROR  |  Erro de parâmetros inválidos ou ausentes  |

_Made by Camila Barcellos - 2023_
