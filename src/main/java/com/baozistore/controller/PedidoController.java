package com.baozistore.controller;

import com.baozistore.model.Cliente;
import com.baozistore.model.Pedido;
import com.baozistore.model.Produto;
import com.baozistore.repository.ClienteRepository;
import com.baozistore.repository.PedidoRepository;
import com.baozistore.repository.ProdutoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Corpo esperado no POST/PUT:
 * {
 *   "cliente": { "id": 1 },
 *   "produto": { "id": 1 },
 *   "quantidade": 10
 * }
 * A resposta traz o cliente e o produto completos.
 */
@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoController(PedidoRepository pedidoRepository,
                            ClienteRepository clienteRepository,
                            ProdutoRepository produtoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
    }

    // POST /pedidos - criar
    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Pedido pedido) {
        String erro = validar(pedido);
        if (erro != null) {
            return ResponseEntity.badRequest().body(Map.of("erro", erro));
        }
        Optional<Cliente> cliente = clienteRepository.findById(pedido.getCliente().getId());
        Optional<Produto> produto = produtoRepository.findById(pedido.getProduto().getId());
        if (cliente.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Cliente nao encontrado"));
        }
        if (produto.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Produto nao encontrado"));
        }
        if (Boolean.FALSE.equals(produto.get().getEstoque())) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Produto sem estoque"));
        }

        pedido.setId(null);
        pedido.setCliente(cliente.get());
        pedido.setProduto(produto.get());
        Pedido salvo = pedidoRepository.save(pedido);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    // GET /pedidos - listar todos
    @GetMapping
    public List<Pedido> listar() {
        return pedidoRepository.findAll();
    }

    // GET /pedidos/{id} - consultar por ID
    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscarPorId(@PathVariable Long id) {
        return pedidoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT /pedidos/{id} - atualizar (opcional)
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Pedido dados) {
        Optional<Pedido> existente = pedidoRepository.findById(id);
        if (existente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        String erro = validar(dados);
        if (erro != null) {
            return ResponseEntity.badRequest().body(Map.of("erro", erro));
        }
        Optional<Cliente> cliente = clienteRepository.findById(dados.getCliente().getId());
        Optional<Produto> produto = produtoRepository.findById(dados.getProduto().getId());
        if (cliente.isEmpty() || produto.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Cliente ou produto nao encontrado"));
        }
        Pedido pedido = existente.get();
        pedido.setCliente(cliente.get());
        pedido.setProduto(produto.get());
        pedido.setQuantidade(dados.getQuantidade());
        return ResponseEntity.ok(pedidoRepository.save(pedido));
    }

    // DELETE /pedidos/{id} - apagar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagar(@PathVariable Long id) {
        if (!pedidoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        pedidoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private String validar(Pedido pedido) {
        if (pedido.getCliente() == null || pedido.getCliente().getId() == null) {
            return "Informe o cliente: { \"cliente\": { \"id\": 1 } }";
        }
        if (pedido.getProduto() == null || pedido.getProduto().getId() == null) {
            return "Informe o produto: { \"produto\": { \"id\": 1 } }";
        }
        if (pedido.getQuantidade() == null || pedido.getQuantidade() <= 0) {
            return "Quantidade deve ser maior que zero";
        }
        return null;
    }
}
