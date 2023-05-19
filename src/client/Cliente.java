package client;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import server.Musica;
import util.Alocacao;
import util.Mensagem;
import util.Status;
import util.Unidade;

/**
 * Cliente do servico
 *
 * @author Camila Florao Barcellos
 */
public class Cliente {

    private Socket socket;
    private float contabilidade; // quanto deve
    private int id;
    private HashMap<String, Musica> playlistDeMusicas;

    /**
     * Construtor que recebe e seta o socket do cliente
     *
     * @author Camila Florao Barcellos
     * @param socket Socket - Socket do cliente
     * @param id int - Id do cliente
     */
    public Cliente(Socket socket, int id) {
        setSocket(socket);
        this.id = id;
        this.contabilidade = 0.0f; // inicia zerado
        this.playlistDeMusicas = new HashMap<>();
    }

    /**
     * Método principal de execucao do cliente que controla o tipo de cliente
     * conectado e apresenta o menu de opcões para cada tipo, montando o
     * protocolo enviado ao servidor
     *
     * @author Camila Florao Barcellos
     * @param args String - Bloco de comandos
     */
    public static void main(String args[]) {
        try {
            System.out.println(". . . . . . . . . . . . . . . . . . . . .");
            System.out.println(".    MIDDLEWARE DE MUSICAS - CLIENTE    .");
            System.out.println(". . . . . . . . . . . . . . . . . . . . .");
            System.out.println(". Camila F Barcellos                    .");
            System.out.println(". Sistemas Distribuidos I               .");
            System.out.println(". Prof. Elder Bernardi                  .");
            System.out.println(". . . . . . . . . . . . . . . . . . . . .");

            System.out.println("Estabelecendo conexao...");
            // instancia a nova conexao na porta 5555
            Socket conexao = new Socket("localhost", 5555);
            System.out.println("Conexao estabelecida");
            boolean sair = false;
            // instancia os streams de entrada e saída para controlar o fluxo de comunicacao
            ObjectOutputStream output = new ObjectOutputStream(conexao.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(conexao.getInputStream());
            // instancia o controle de entrada do teclado
            BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Enviando mensagem...");

            // login do cliente
            System.out.println("\n. . . . . . . . . . . . . .");
            System.out.println(".     LOGIN DO CLIENTE    .");
            System.out.println(". . . . . . . . . . . . . . ");
            System.out.print("USER: ");
            String user = teclado.readLine();
            System.out.print("PASS: ");
            String pass = teclado.readLine();

            // monta o protocolo de login
            Mensagem m = new Mensagem("LOGIN");
            m.setParam("user", user);
            m.setParam("pass", pass);

            // envia a mensagem de login
            output.writeObject(m);
            output.flush();

            // le a resposta do servidor
            m = (Mensagem) input.readObject();
            System.out.println("Resposta: " + m);

            // expulsa cliente caso nao autentique
            if (m.getStatus().equals(Status.ERROR) || m.getStatus().equals(Status.PARAMERROR)) {
                System.out.println("ERRO DE AUTENTICAÇAO, TENTE NOVAMENTE!");
                sair = true;
            }

            // menu de operacoes
            while (!sair) {
                if (!user.equals("admin")) {
                    int opcaoMenu;
                    String titulo;
                    do {
                        System.out.println("\n. . . . . . . . . . . . . .");
                        System.out.println(".     MENU DE USUARIO     .");
                        System.out.println(". . . . . . . . . . . . . .");
                        System.out.println(". 1 - Escutar musica      .");
                        System.out.println(". 2 - Mostrar playlist    .");
                        System.out.println(". 3 - Add musica playlist .");
                        System.out.println(". 4 - Escutar playlist    .");
                        System.out.println(". 5 - Del musica playlist .");
                        System.out.println(". 0 - Sair                .");
                        System.out.println(". . . . . . . . . . . . . .");
                        System.out.print("Sua opcao: ");
                        opcaoMenu = Integer.parseInt(teclado.readLine());

                        switch (opcaoMenu) {
                            case 0:
                                m = new Mensagem("SAIR");
                                output.writeObject(m);
                                output.flush();

                                m = (Mensagem) input.readObject();
                                System.out.println("Resposta: " + m);
                                return;

                            case 1:
                                // realiza solicitacao para escutar musica
                                System.out.print("Titulo da musica: ");
                                titulo = teclado.readLine();

                                m = new Mensagem("ESCUTA");
                                m.setParam("titulo", titulo);

                                output.writeObject(m);
                                output.flush(); //libera buffer para envio

                                System.out.println("Mensagem " + m + " enviada.");

                                m = (Mensagem) input.readObject();
                                System.out.println("Resposta: " + m);
                                break;

                            case 2:
                                // realiza solicitacao para mostrar a playlist
                                m = new Mensagem("MOSTRAPLAYLIST");

                                output.writeObject(m);
                                output.flush(); //libera buffer para envio

                                System.out.println("Mensagem " + m + " enviada.");

                                m = (Mensagem) input.readObject();
                                System.out.println("Resposta: " + m);
                                break;

                            case 3:
                                // realiza solicitacao para adicionar musica na playlist
                                System.out.print("Titulo da musica: ");
                                titulo = teclado.readLine();

                                m = new Mensagem("ADDPLAYLIST");
                                m.setParam("titulo", titulo);

                                output.writeObject(m);
                                output.flush(); //libera buffer para envio

                                System.out.println("Mensagem " + m + " enviada.");

                                m = (Mensagem) input.readObject();
                                System.out.println("Resposta: " + m);
                                break;

                            case 4:
                                // realiza solicitacao para escutar a playlist
                                m = new Mensagem("ESCUTAPLAYLIST");

                                output.writeObject(m);
                                output.flush(); //libera buffer para envio

                                System.out.println("Mensagem " + m + " enviada.");

                                m = (Mensagem) input.readObject();
                                System.out.println("Resposta: " + m);
                                break;

                            case 5:
                                // realiza solicitacao para remover musica na playlist
                                System.out.print("Titulo da musica: ");
                                titulo = teclado.readLine();

                                m = new Mensagem("DELPLAYLIST");
                                m.setParam("titulo", titulo);

                                output.writeObject(m);
                                output.flush(); //libera buffer para envio

                                System.out.println("Mensagem " + m + " enviada.");

                                m = (Mensagem) input.readObject();
                                System.out.println("Resposta: " + m);
                                break;

                            default:
                                System.out.println("Opcao invalida!");
                        }
                    } while (opcaoMenu != 0);

                } else {
                    int opcaoMenu;
                    String titulo, artista, album;
                    Double duracao;
                    do {
                        System.out.println("\n. . . . . . . . . . . . . . . . .");
                        System.out.println(".     MENU DE ADMINISTRADOR     .");
                        System.out.println(". . . . . . . . . . . . . . . . .");
                        System.out.println(". 1 - Adicionar musica          .");
                        System.out.println(". 2 - Ler musicas               .");
                        System.out.println(". 3 - Deletar musica            .");
                        System.out.println(". 0 - Sair                      .");
                        System.out.println(". . . . . . . . . . . . . . . . .");
                        System.out.print("Sua opcao: ");
                        opcaoMenu = Integer.parseInt(teclado.readLine());

                        switch (opcaoMenu) {
                            case 0:
                                m = new Mensagem("SAIR");
                                output.writeObject(m);
                                output.flush();

                                m = (Mensagem) input.readObject();
                                System.out.println("Resposta: " + m);
                                return;

                            case 1:
                                System.out.print("Titulo da musica: ");
                                titulo = teclado.readLine();

                                System.out.print("Artista: ");
                                artista = teclado.readLine();

                                System.out.print("Album: ");
                                album = teclado.readLine();

                                System.out.print("Duracao: ");
                                duracao = Double.parseDouble(teclado.readLine());

                                m = new Mensagem("ADICIONA");
                                m.setParam("titulo", titulo);
                                m.setParam("artista", artista);
                                m.setParam("album", album);
                                m.setParam("duracao", duracao);

                                output.writeObject(m);
                                output.flush(); //libera buffer para envio

                                System.out.println("Mensagem " + m + " enviada.");

                                m = (Mensagem) input.readObject();
                                System.out.println("Resposta: " + m);
                                break;

                            case 2:
                                m = new Mensagem("LISTA");

                                output.writeObject(m);
                                output.flush(); //libera buffer para envio

                                System.out.println("Mensagem " + m + " enviada.");

                                m = (Mensagem) input.readObject();
                                System.out.println("Resposta: " + m);
                                break;

                            case 3:
                                System.out.print("Titulo da musica: ");
                                titulo = teclado.readLine();

                                m = new Mensagem("REMOVE");
                                m.setParam("titulo", titulo);

                                output.writeObject(m);
                                output.flush(); //libera buffer para envio

                                System.out.println("Mensagem " + m + " enviada.");

                                m = (Mensagem) input.readObject();
                                System.out.println("Resposta: " + m);
                                break;

                            default:
                                System.out.println("Opcao invalida!");
                        }
                    } while (opcaoMenu != 0);
                }
            }

            input.close();
            output.close();
            conexao.close();
        } catch (IOException e) {
            System.out.println("Erro IOException no cliente: " + e);
        } catch (ClassNotFoundException e) {
            System.out.println("Erro no cast: " + e.getMessage());
        }
    }

    /**
     * Método de adicao de um objeto musica na playlist
     *
     * @author Camila Florao Barcellos
     * @param titulo String - Titulo da musica
     * @param musica Musica - Musica
     */
    public void addMusicaPlaylist(String titulo, Musica musica) {
        this.playlistDeMusicas.put(titulo, musica);
    }

    /**
     * Método de retorno de um objeto musica no hash pela chave de número que,
     * caso nao encontre, devolve um objeto nulo
     *
     * @author Camila Florao Barcellos
     * @param titulo String - Titulo da musica
     * @return musica - Musica com aquele titulo
     */
    public Musica getMusicaPlaylist(String titulo) {
        return this.playlistDeMusicas.get(titulo);
    }

    /**
     * Método de remocao de um objeto musica no hash pela chave de titulo
     *
     * @author Camila Florao Barcellos
     * @param titulo String - Titulo da musica
     */
    public void removeMusicaPlaylist(String titulo) {
        this.playlistDeMusicas.remove(titulo);
    }

    /**
     * Método de listagem dos objetos musica da playlist
     *
     * @author Camila Florao Barcellos
     */
    public void listarPlaylist() {
        System.out.println("-> Listando musicas da playlist:"
        + "\n   -> Duracao: " + this.getDuracaoPlaylist() + "min");
        for (Musica m : playlistDeMusicas.values()) {
            System.out.println("Titulo: " + m.getTitulo()
                    + "\nArtista: " + m.getArtista()
                    + "\nAlbum: " + m.getAlbum()
                    + "\nDuracao: " + m.getDuracao() + "min\n"
            );
        }
    }

    /**
     * Método de contagem de duracao da playlist
     *
     * @author Camila Florao Barcellos
     * @return duracao - Duracao total da playlist
     */
    public Double getDuracaoPlaylist() {
        Double duracao = 0.0;
        for (Musica m : playlistDeMusicas.values()) {
            duracao += m.getDuracao();
        }

        return duracao;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public float getContabilidade() {
        return contabilidade;
    }

    public void setContabilidade(float custo) {
        this.contabilidade += custo;
    }

    public HashMap<String, Musica> getPlaylistDeMusicas() {
        return playlistDeMusicas;
    }

    public void setPlaylistDeMusicas(HashMap<String, Musica> playlistDeMusicas) {
        this.playlistDeMusicas = playlistDeMusicas;
    }
}
