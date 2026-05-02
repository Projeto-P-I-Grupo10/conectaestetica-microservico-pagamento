package school.sptech.DTO;

public class PagamentoResponse {
    private String qrCodeImage;
    private String qrCode;

    public PagamentoResponse(String qrCodeImage, String qrCode) {
        this.qrCodeImage = qrCodeImage;
        this.qrCode = qrCode;
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
}
