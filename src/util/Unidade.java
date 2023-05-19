package util;

import java.util.Calendar;

// pool de recursos do servidor
// unidade computacional
public class Unidade {

    Boolean ligado;
    Float custoPorTempo, contabilidade; // contabilidade é quanto custou
    Integer capacidade;
    Long tempoInicio, tempoFim;

    public Unidade(Float custoPorTempo, Integer capacidade) {
        this.ligado = false;
        this.custoPorTempo = custoPorTempo;
        this.capacidade = capacidade;
    }

    public Boolean getLigado() {
        return ligado;
    }

    public void setLigado(Boolean ligado) {
        this.ligado = ligado;
        if (ligado) {
            // pegar tempo de referência quando liga
            this.tempoInicio = Calendar.getInstance().getTimeInMillis();
        } else {
            // pegar tempo de referência do fim
            this.tempoFim = Calendar.getInstance().getTimeInMillis();
        }
    }

    public Float getCustoPorTempo() {
        return custoPorTempo;
    }

    public void setCustoPorTempo(Float custoPorTempo) {
        this.custoPorTempo = custoPorTempo;
    }

    public Integer getCapacidadeTotal() {
        return capacidade;
    }

    public void setCapacidadeTotal(Integer capacidade) {
        this.capacidade = capacidade;
    }

    public Long getTempoInicio() {
        return tempoInicio;
    }

    public void setTempoInicio(Long tempoInicio) {
        this.tempoInicio = tempoInicio;
    }

    public Long getTempoFim() {
        return tempoFim;
    }

    public void setTempoFim(Long tempoFim) {
        this.tempoFim = tempoFim;
    }

    // contabilidade = custo x tempo
    public Float getContabilidade() {
        return custoPorTempo * ((tempoFim - tempoInicio) / 60000);
    }

    public void setContabilidade(Float contabilidade) {
        this.contabilidade = contabilidade;
    }
}
