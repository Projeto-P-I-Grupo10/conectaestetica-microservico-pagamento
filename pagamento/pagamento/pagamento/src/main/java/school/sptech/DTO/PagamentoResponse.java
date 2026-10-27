package school.sptech.DTO;

import java.time.LocalDateTime;

public class PagamentoResponse {
    private String qrCodeImage;
    private String qrCode;
    private LocalDateTime dataExpiracao;

    public PagamentoResponse(String qrCodeImage, String qrCode) {
        this.qrCodeImage = qrCodeImage;
        this.qrCode = qrCode;
    }

    public PagamentoResponse(String qrCodeImage, String qrCode, LocalDateTime dataExpiracao) {
        this.qrCodeImage = qrCodeImage;
        this.qrCode = qrCode;
        this.dataExpiracao = dataExpiracao;
    }

    public String getQrCodeImage() {
        return qrCodeImage;
    }

    public void setQrCodeImage(String qrCodeImage) {
        this.qrCodeImage = qrCodeImage;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public LocalDateTime getDataExpiracao() {
        return dataExpiracao;
    }

    public void setDataExpiracao(LocalDateTime dataExpiracao) {
        this.dataExpiracao = dataExpiracao;
    }
}
