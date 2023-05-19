package util;

import client.Cliente;
import java.util.Calendar;

public class Alocacao {

    Unidade unidade;
    Cliente cliente;
    Long inicio, fim;

    public Alocacao(Cliente cliente, Unidade unidade) {
        this.cliente = cliente;
        this.unidade = unidade;
        this.inicio = Calendar.getInstance().getTimeInMillis();
    }

    // zera todos as propriedades
    public void desalocarUnidade() {
        this.cliente = null;
        this.unidade = null;
        this.inicio = null;
        this.fim = null;
    }

    public Unidade getUnidade() {
        return unidade;
    }

    public void setUnidade(Unidade unidade) {
        this.unidade = unidade;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Long getInicio() {
        return inicio;
    }

    public void setInicio(Long inicio) {
        this.inicio = inicio;
    }

    public Long getFim() {
        return fim;
    }

    public void setFim() {
        this.fim = Calendar.getInstance().getTimeInMillis();
    }

}
