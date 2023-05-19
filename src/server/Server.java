package server;

import client.Cliente;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import util.Alocacao;
import util.Unidade;

/**
 *
 * @author Camila Florao Barcellos
 */
public class Server {

    private ServerSocket serverSocket;
    private int cont;

    private ArrayList<User> usuarios;
    private HashMap<Integer, Unidade> poolDeRecursos;
    private HashMap<Integer, Cliente> clientes;
    private ArrayList<Alocacao> alocacoes;
    private Musica musica;
    private HashMap<String, Musica> poolDeMusicas;
    private Float contabilidadeTotal;
    private Double tempoTotal;

    public Server() {
        usuarios = new ArrayList<>();

        poolDeRecursos = new HashMap<>();
        clientes = new HashMap<>();
        alocacoes = new ArrayList<>();

        poolDeMusicas = new HashMap<>();

        usuarios.add(new User("camila", "123456"));
        usuarios.add(new User("admin", "admin"));

        contabilidadeTotal = 0.0f;
        tempoTotal = 0.0;
    }

    /*- Criar o servidor de conexoes*/
    private void criarServerSocket(int porta) throws IOException {
        serverSocket = new ServerSocket(porta);
        cont = 0;
    }

    protected User getUser(String nome) {
        for (User u : usuarios) {
            if (u.getNome().equals(nome)) {
                return u;
            }

        }
        return null;
    }

    /*2 -Esperar um pedido de conexao;
     Outro processo*/
    private Socket esperaConexao() throws IOException {
        Socket socket = serverSocket.accept();
        return socket;
    }

    /**
     * Loop de conexoes que conecta os novos clientes ao servidor, verifica a
     * disponibilidade de unidades no pool de recursos, cria uma nova caso
     * necesario e inicia a thread de tratamento
     *
     * @author Camila Florao Barcellos
     */
    public void connectionLoop() throws IOException {
        int id = 0;
        Random random = new Random(); // vai randomizar a chave de unidade entre 4 e 50
        while (true) {
            try {
                // aguarda e aceita a conexao do cliente
                System.out.println("Aguardando conexao de clientes...");
                Socket socket = esperaConexao();//protocolo
                System.out.println("\nNovo cliente em " + socket.getRemoteSocketAddress() + "\n");

                // adiciona cliente na lista de clientes do servidor
                Cliente cliente = new Cliente(socket, id);
                clientes.put(id, cliente);

                // caso nao haja unidade disponivel, cria uma nova
                if (buscaUnidadeFirstFit() == null) {
                    Unidade novaUn = new Unidade(5.0f, 100);

                    poolDeRecursos.put((random.nextInt(12) + 4), novaUn);
                    System.out.println("-> NOVA UNIDADE " + (random.nextInt(12) + 4) + " CRIADA PARA ATENDER O CLIENTE!");
                }

                // cria thread que tratara a conexao -> busca unidade disponivel
                TrataConexao tc = new TrataConexao(this, socket, id++, cliente, buscaUnidadeFirstFit());
                Thread th = new Thread(tc);
                th.start();
            } catch (IOException e) {
                System.out.println("Erro IOException no cliente: " + e);
            }
        }
    }

    /**
     * Método de alocacao de recurso que cria uma nova alocacao e liga a sua
     * unidade correspondente
     *
     * @author Camila Florao Barcellos
     * @param cliente Cliente - Cliente da alocacao
     * @param unidade Unidade - Unidade alocada pelo cliente
     */
    protected Alocacao alocaRecurso(Cliente cliente, Unidade unidade) {
        // aloca nova unidade ao cliente
        Alocacao novaAloc = new Alocacao(cliente, unidade);

        // liga a unidade
        novaAloc.getUnidade().setLigado(true);

        System.out.println("\n-> Nova unidade alocada em "
                + novaAloc.getInicio() + "ms");

        return novaAloc;
    }

    /**
     * Método de desalocacao de recurso que finaliza a alocacao, desliga a
     * unidade correspondente, calcula o custo total da alocacao para atribuir a
     * contabilidade do cliente e desaloca a unidade (limpa todos os valores),
     * exibindo a contabilidade de custo e tempo total do servidor
     *
     * @author Camila Florao Barcellos
     * @param cliente Cliente - Cliente da alocacao
     * @param unidade Unidade - Unidade alocada pelo cliente
     * @param aloc - Alocacao da unidade correspondente ao cliente
     */
    protected void desalocaRecurso(Cliente cliente, Unidade unidade, Alocacao aloc) {
        // define fim da alocacao
        aloc.setFim();

        // desliga a unidade
        aloc.getUnidade().setLigado(false);

        // captura o custo da alocacao da unidade
        Float custo = aloc.getUnidade().getContabilidade();

        // incrementa o custo da unidade a contabilidade total do servidor
        this.contabilidadeTotal += custo;

        // atribui custo a contabilidade do cliente
        cliente.setContabilidade(custo);

        System.out.println("\n-> Unidade desalocada em "
                + aloc.getFim() + "ms");

        System.out.println("Custo total de alocacao: " + custo
                + "\nTempo de alocacao: "
                + String.format("%.2f",
                        ((double) (aloc.getFim() - aloc.getInicio()) / 60000))
                + "min");

        // incrementa o tempo de alocacao ao tempo total do servidor
        this.tempoTotal += (double) (aloc.getFim() - aloc.getInicio()) / 60000;

        // desaloca unidade
        aloc.desalocarUnidade();

        // exibe constantemente a contabilidade e o tempo total de alocacoes do servidor
        System.out.println("\n----------------------------------------------"
                + "\n-> Contabilidade total de alocacoes: " + this.contabilidadeTotal
                + "\n-> Tempo total de alocacao: "
                + String.format("%.2f", this.tempoTotal) + "min"
                + "\n----------------------------------------------");
    }

    /**
     * Método de criacao de 3 unidades para o pool de recursos
     *
     * @author Camila Florao Barcellos
     */
    private void criarRecursos() {
        Unidade unidade = new Unidade(10.0f, 100);
        poolDeRecursos.put(1, unidade);
        unidade = new Unidade(15.0f, 200);
        poolDeRecursos.put(2, unidade);
        unidade = new Unidade(5.0f, 50);
        poolDeRecursos.put(3, unidade);
    }

    public Unidade getUnidade(Integer unidade) {
        return this.poolDeRecursos.get(unidade);
    }

    /**
     * Método de retorno da primeira unidade disponível (!ligado) no pool de
     * recursos, caso nao haja retorna nulo
     *
     * @author Camila Florao Barcellos
     * @return u - Unidade disponível
     */
    public Unidade buscaUnidadeFirstFit() {
        for (Unidade u : poolDeRecursos.values()) {
            if (!u.getLigado()) {
                return u;
            }
        }

        return null;
    }

    /**
     * @param args the command line arguments
     * @throws java.lang.ClassNotFoundException
     */
    public static void main(String[] args) throws ClassNotFoundException {

        try {
            Server server = new Server();
            server.criarServerSocket(5555);

            System.out.println(". . . . . . . . . . . . . . . . . . . . .");
            System.out.println(".    MIDDLEWARE DE MUSICAS - SERVIDOR   .");
            System.out.println(". . . . . . . . . . . . . . . . . . . . .");
            System.out.println(". Camila F Barcellos                    .");
            System.out.println(". Sistemas Distribuidos I               .");
            System.out.println(". Prof. Elder Bernardi                  .");
            System.out.println(". . . . . . . . . . . . . . . . . . . . .");

            // adiciona musicas previas
            server.addPrevMusicas();

            // cria pool de recursos
            server.criarRecursos();

            // inicia looping de conexoes
            server.connectionLoop();
        } catch (IOException e) {
            //trata excecao
            System.out.println("Erro no servidor: " + e.getMessage());
        }
    }

    /**
     * Método de adicao de um objeto musica no hash
     *
     * @author Camila Florao Barcellos
     * @param titulo String - Titulo da musica
     * @param musica Musica - Música
     */
    public void addMusica(String titulo, Musica musica) {
        this.poolDeMusicas.put(titulo, musica);
    }

    /**
     * Método de retorno de um objeto musica no hash pela chave de número que,
     * caso nao encontre, devolve um objeto nulo
     *
     * @author Camila Florao Barcellos
     * @param titulo String - Titulo da musica
     * @return musica - Musica com aquele titulo
     */
    public Musica getMusica(String titulo) {
        return this.poolDeMusicas.get(titulo);
    }

    /**
     * Método de remocao de um objeto musica no hash pela chave de titulo
     *
     * @author Camila Florao Barcellos
     * @param titulo String - Titulo da musica
     */
    public void removeMusica(String titulo) {
        this.poolDeMusicas.remove(titulo);
    }

    /**
     * Método de listagem dos objetos musica do hash
     *
     * @author Camila Florao Barcellos
     */
    public void listarMusicas() {
        System.out.println("-> Listando musicas:");
        for (Musica m : poolDeMusicas.values()) {
            System.out.println("Titulo: " + m.getTitulo()
                    + "\nArtista: " + m.getArtista()
                    + "\nAlbum: " + m.getAlbum()
                    + "\nDuracao: " + m.getDuracao() + "min\n"
            );
        }
    }

    /**
     * Método para adicionar objetos musica no hash
     *
     * @author Camila Florao Barcellos
     */
    public void addPrevMusicas() {
        Musica musica = new Musica(
                "Bohemian Rhapsody", "Queen", "A Night at the Opera", 5.54
        );
        this.addMusica("Bohemian Rhapsody", musica);

        musica = new Musica(
                "Despacito", "Luis Fonsi", "Despacito", 3.49
        );
        this.addMusica("Despacito", musica);

        musica = new Musica(
                "Stairway to Heaven", "Led Zeppelin", "Led Zeppelin IV", 8.18
        );
        this.addMusica("Stairway to Heaven", musica);

        musica = new Musica(
                "Complicated", "Avril Lavigne", "Let Go", 4.04
        );
        this.addMusica(musica.getTitulo(), musica);
    }

}
