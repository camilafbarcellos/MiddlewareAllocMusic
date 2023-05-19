package server;

import client.Cliente;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import util.Alocacao;
import util.Estados;
import util.Mensagem;
import util.Status;
import util.Unidade;

// Middleware de tratamento dos protocolos da conexao CLI x SERV
public class TrataConexao implements Runnable {

    private Server server;
    private Socket socket;
    private Cliente cliente;
    private Unidade unidade;
    private int id;
    private String user;
    User auth;

    ObjectOutputStream output;
    ObjectInputStream input;

    private Estados estado;

    public TrataConexao(Server server, Socket socket, int id, Cliente cliente, Unidade unidade) {
        this.server = server;
        this.socket = socket;
        this.id = id;
        this.cliente = cliente;
        this.unidade = unidade;
        estado = Estados.CONECTADO;
    }

    private void fechaSocket(Socket s) throws IOException {
        s.close();
    }

    private void enviaMsg(Object o, ObjectOutputStream out) throws IOException {
        out.writeObject(o);
        out.flush();
    }

    /*
         * 4 - Tratar a conversacao entre cliente e
         * servidor (tratar protocolo);
     */
    private void trataConexao() throws IOException, ClassNotFoundException {
        // protocolo da aplicacao
        try {
            /* 3 - Criar streams de entrada e saída; */

            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
            /*
             * 4 - Tratar a conversacao entre cliente e
             * servidor (tratar protocolo);
             */
            System.out.println("Tratando...");

            estado = Estados.CONECTADO;
            while (estado != Estados.SAIR) {

                Mensagem m = (Mensagem) input.readObject();
                System.out.println("-> Mensagem do cliente:\n" + m);

                String operacao = m.getOperacao();
                Mensagem reply = new Mensagem(operacao + "REPLY");

                // estados conectado autenticado
                switch (estado) {
                    case CONECTADO:
                        switch (operacao) {

                            case "LOGIN":
                                /*
                                 * LOGIN
                                 * user: string
                                 * pass: string
                                 */
                                try {
                                String user = (String) m.getParam("user");
                                String pass = (String) m.getParam("pass");

                                auth = server.getUser(user);

                                if (auth == null) {
                                    reply.setStatus(Status.ERROR);
                                    break;
                                }

                                if (auth.getNome().equals(user) && auth.getSenha().equals(pass)) {
                                    this.user = user;
                                    estado = Estados.AUTENTICADO;

                                    if (auth.getNome().equals("admin")) {
                                        estado = Estados.AUTENTICADO_ADMIN;
                                    }
                                    
                                    reply.setStatus(Status.OK);
                                } else {
                                    reply.setStatus(Status.ERROR);
                                }

                            } catch (Exception e) {
                                reply.setStatus(Status.PARAMERROR);
                                reply.setParam("msg", "Erro nos parametros do protocolo!");
                            }
                            break;

                            case "SAIR":
                                // DESIGN PATTERN STATE
                                reply.setStatus(Status.OK);
                                estado = Estados.SAIR;
                                break;

                            default:
                                // responder mensagem de erro: Nao autorizado/ou inválida
                                reply.setStatus(Status.ERROR);
                                reply.setParam("msg", "MENSAGEM NAO AUTORIZADA OU INVALIDA!");

                                break;
                        }
                        break;

                    case AUTENTICADO_ADMIN:
                        switch (operacao) {

                            case "ADICIONA":
                                /*
                                 * ADICIONA
                                 * titulo: string
                                 * artista: string
                                 * album: string
                                 * duracao: double
                                 */
                                try {
                                String titulo = (String) m.getParam("titulo");
                                String artista = (String) m.getParam("artista");
                                String album = (String) m.getParam("album");
                                Double duracao = (Double) m.getParam("duracao");

                                Musica musica = new Musica(titulo, artista, album, duracao);
                                server.addMusica(titulo, musica);

                                reply.setStatus(Status.OK);
                                reply.setParam("add", titulo);
                            } catch (Exception e) {
                                reply.setStatus(Status.PARAMERROR);
                                reply.setParam("err", "Erro na adicao: " + e.getMessage());
                            }
                            break;

                            case "LISTA":
                                /*
                                 * LISTA
                                 * -- sem parametros
                                 */
                                try {
                                server.listarMusicas();
                                reply.setStatus(Status.OK);
                            } catch (Exception e) {
                                reply.setStatus(Status.PARAMERROR);
                                reply.setParam("err", "Erro na listagem: " + e.getMessage());
                            }
                            break;

                            case "REMOVE":
                                /*
                                 * REMOVE
                                 * titulo: string
                                 */
                                try {
                                String titulo = (String) m.getParam("titulo");

                                Musica musica = server.getMusica(titulo);
                                if (musica == null) {
                                    reply.setStatus(Status.PARAMERROR);
                                    reply.setParam("err", "Erro na remocao: musica invalida");
                                    break;
                                }

                                server.removeMusica(titulo);

                                reply.setStatus(Status.OK);
                                reply.setParam("del", titulo);
                            } catch (Exception e) {
                                reply.setStatus(Status.PARAMERROR);
                                reply.setParam("err", "Erro na remocao: " + e.getMessage());
                            }
                            break;

                            case "SAIR":
                                // DESIGN PATTERN STATE
                                reply.setStatus(Status.OK);
                                estado = Estados.SAIR;
                                break;

                            default:
                                reply.setStatus(Status.ERROR);
                                reply.setParam("msg", "MENSAGEM NAO AUTORIZADA OU INVALIDA!");
                                break;
                        }
                        break;

                    case AUTENTICADO:
                        switch (operacao) {
                            case "ESCUTA":
                                /*
                                 * ESCUTA
                                 * titulo: string
                                 */
                                try {
                                String titulo = (String) m.getParam("titulo");

                                Musica musica = server.getMusica(titulo);
                                if (musica == null) {
                                    reply.setStatus(Status.PARAMERROR);
                                    reply.setParam("err", "MUSICA INVALIDA");
                                    break;
                                }

                                Thread.sleep((long) (musica.getDuracao() * 60000));

                                reply.setStatus(Status.OK);
                                reply.setParam("escutou", titulo);
                                reply.setParam("duracao", musica.getDuracao());
                            } catch (Exception e) {
                                reply.setStatus(Status.PARAMERROR);
                                reply.setParam("err", "Erro na escuta: " + e.getMessage());
                            }
                            break;

                            case "MOSTRAPLAYLIST":
                                /*
                                 * MOSTRAPLAYLIST
                                 * -- sem parametros
                                 */
                                try {

                                if (cliente.getPlaylistDeMusicas().isEmpty()) {
                                    reply.setStatus(Status.PARAMERROR);
                                    reply.setParam("err", "PLAYLIST VAZIA");
                                    break;
                                }

                                cliente.listarPlaylist();

                                reply.setStatus(Status.OK);
                            } catch (Exception e) {
                                reply.setStatus(Status.PARAMERROR);
                                reply.setParam("err", "Erro na listagem: " + e.getMessage());
                            }
                            break;

                            case "ADDPLAYLIST":
                                /*
                                 * ADDPLAYLIST
                                 * titulo: string
                                 */
                                try {
                                
                                String titulo = (String) m.getParam("titulo");

                                Musica musica = server.getMusica(titulo);
                                if (musica == null) {
                                    reply.setStatus(Status.PARAMERROR);
                                    reply.setParam("err", "MUSICA INVALIDA");
                                    break;
                                }

                                cliente.addMusicaPlaylist(titulo, musica);

                                reply.setStatus(Status.OK);
                                reply.setParam("add", titulo);
                            } catch (Exception e) {
                                reply.setStatus(Status.PARAMERROR);
                                reply.setParam("err", "Erro na adicao de musica: " + e.getMessage());
                            }
                            break;

                            case "ESCUTAPLAYLIST":
                                /*
                                 * ESCUTAPLAYLIST
                                 * -- sem parametros
                                 */
                                try {
                                    
                                if (cliente.getPlaylistDeMusicas().isEmpty()) {
                                    reply.setStatus(Status.PARAMERROR);
                                    reply.setParam("err", "PLAYLIST VAZIA");
                                    break;
                                }

                                Thread.sleep((long) (cliente.getDuracaoPlaylist() * 60000));

                                reply.setStatus(Status.OK);
                                reply.setParam("duracao", cliente.getDuracaoPlaylist());
                            } catch (Exception e) {
                                reply.setStatus(Status.PARAMERROR);
                                reply.setParam("err", "Erro na escuta: " + e.getMessage());
                            }
                            break;

                            case "DELPLAYLIST":
                                /*
                                 * DELPLAYLIST
                                 * titulo: string
                                 */
                                try {
                                if (cliente.getPlaylistDeMusicas().isEmpty()) {
                                    reply.setStatus(Status.PARAMERROR);
                                    reply.setParam("err", "PLAYLIST VAZIA");
                                    break;
                                }

                                String titulo = (String) m.getParam("titulo");

                                Musica musica = server.getMusica(titulo);
                                if (musica == null) {
                                    reply.setStatus(Status.PARAMERROR);
                                    reply.setParam("err", "MUSICA INVALIDA");
                                    break;
                                }

                                cliente.removeMusicaPlaylist(titulo);

                                reply.setStatus(Status.OK);
                                reply.setParam("del", titulo);
                            } catch (Exception e) {
                                reply.setStatus(Status.PARAMERROR);
                                reply.setParam("err", "Erro na remocao de musica: " + e.getMessage());
                            }
                            break;

                            case "SAIR":
                                // DESIGN PATTERN STATE
                                reply.setStatus(Status.OK);
                                estado = Estados.SAIR;
                                break;

                            default:
                                reply.setStatus(Status.ERROR);
                                reply.setParam("msg", "MENSAGEM NAO AUTORIZADA OU INVALIDA!");
                                break;
                        }
                        break;

                    case SAIR:
                        break;

                }

                output.writeObject(reply);
                output.flush();
            }
            // 4.2 - Fechar streams de entrada e saída
            input.close();
            output.close();
        } catch (IOException e) {
            // tratamento de falhas
            System.out.println("Problema no tratamento da conexao com o cliente: " + socket.getInetAddress());
            System.out.println("Erro: " + e.getMessage());
        } finally {
            // final do tratamento do protocolo
            /* 4.1 - Fechar socket de comunicacao entre servidor/cliente */
            fechaSocket(socket);
        }

    }

    @Override
    public void run() {
        try {
            // aloca recurso da conexao
            Alocacao aloc = server.alocaRecurso(cliente, unidade);
            // trata a conexao -> protocolos
            trataConexao();
            // desaloca recurso da conexao
            server.desalocaRecurso(cliente, unidade, aloc);

        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            System.out.println("Erro no tratamento de conexao" + e.getMessage());
        }
    }

}
