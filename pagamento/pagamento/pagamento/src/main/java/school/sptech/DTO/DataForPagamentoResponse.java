package school.sptech.DTO;

import java.math.BigDecimal;

public class DataForPagamentoResponse {
    private BigDecimal valor;
    private String descricao;
    private String email;
    private String nome;
    private String sobrenome;

    public DataForPagamentoResponse() {
    }

    public DataForPagamentoResponse(BigDecimal valor, String descricao, String email, String nome, String sobrenome) {
        this.valor = valor;
        this.descricao = descricao;
        this.email = email;
        this.nome = nome;
        this.sobrenome = sobrenome;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }
}
