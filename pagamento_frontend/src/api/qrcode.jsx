import axios from "axios";

export async function criarPagamento() {
  const response = await axios.post("http://localhost:8080/pagamentos/pix", {
    valor: 9.99,
    email: "teste@email.com"
  });

  return response.data;
}