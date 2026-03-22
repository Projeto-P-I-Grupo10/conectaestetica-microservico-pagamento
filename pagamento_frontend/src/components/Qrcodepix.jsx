import { useEffect, useState } from "react";
import { criarPagamento } from "../api/qrcode";

export function Qrcodepix() {
  const [qrCode, setQrCode] = useState("");
  const [qrCodeBase64, setQrCodeBase64] = useState("");

   async function gerarPix () {
    try {

    console.log("gerouu")
      const data = await criarPagamento();
      console.log(data); 
      setQrCode(data.qr_code);
      setQrCodeBase64(data.qr_code_base64);
    } catch (error) {
      console.error("Erro ao gera qrcode:", error);
    }
  };

  useEffect(() => {
  console.log("qrCode :", qrCode);
}, [qrCode]);

useEffect(() => {
  console.log("qrCodeBase64 :", qrCodeBase64);
}, [qrCodeBase64]);

 return (
    <div>
      <h2>Pagamento via Pix</h2>

      <button onClick={gerarPix}>Gerar QR Code</button>

      {qrCodeBase64 !== ""  && (
        <div>
          <h3>Escaneie:</h3>

          <img
            src={`data:image/png;base64,${qrCodeBase64}`}
            alt="QR Code"
            width={250}
          />

          <h3>Pix Copia e Cola:</h3>

          <textarea value={qrCode} readOnly rows={4} cols={50} />

          <br />

          <button onClick={() => navigator.clipboard.writeText(qrCode)}>
            Copiar código Pix
          </button>
        </div>
      )}
    </div>
  );
}


