package com.shori.shoriexpress.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shori.shoriexpress.model.Factura;
import com.shori.shoriexpress.repository.FacturaRepository;

@Service
@Transactional
public class FacturaService {

    @Autowired
    private FacturaRepository facturaRepository;

    public List<Factura> listarTodas() {
        return facturaRepository.findAllByOrderByFechaEmisionDesc();
    }

    public Optional<Factura> buscarPorId(Long id) {
        return facturaRepository.findById(id);
    }

    public Factura guardar(Factura factura) {
        if (factura.getNumeroFactura() == null || factura.getNumeroFactura().isEmpty()) {
            factura.setNumeroFactura(generarNumeroFactura());
        }
        return facturaRepository.save(factura);
    }

    public Factura actualizar(Long id, Factura factura) {
        Optional<Factura> existente = facturaRepository.findById(id);
        if (existente.isPresent()) {
            Factura facturaActualizada = existente.get();
            facturaActualizada.setSubtotal(factura.getSubtotal());
            facturaActualizada.setIva(factura.getIva());
            facturaActualizada.setTotal(factura.getTotal());
            facturaActualizada.setMetodoPago(factura.getMetodoPago());
            return facturaRepository.save(facturaActualizada);
        }
        return null;
    }

    public void eliminar(Long id) {
        facturaRepository.deleteById(id);
    }

    public Optional<Factura> buscarPorNumeroFactura(String numeroFactura) {
        return facturaRepository.findByNumeroFactura(numeroFactura);
    }

    public Optional<Factura> buscarPorPedido(Long idPedido) {
        return facturaRepository.findByPedido_IdPedido(idPedido);
    }

    public List<Factura> buscarPorMetodoPago(Long idMetodoPago) {
        return facturaRepository.findByMetodoPago_IdMetodoPago(idMetodoPago);
    }

    private String generarNumeroFactura() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String numero = "FAC-" + timestamp;
        
        // Verificar si existe y agregar sufijo si es necesario
        int sufijo = 1;
        String numeroFinal = numero;
        while (facturaRepository.existsByNumeroFactura(numeroFinal)) {
            numeroFinal = numero + "-" + sufijo;
            sufijo++;
        }
        
        return numeroFinal;
    }

    public Factura crearFacturaDesdeTotal(Long idPedido, BigDecimal totalPedido, Long idMetodoPago) {
        BigDecimal ivaRate = new BigDecimal("0.19"); // 19% IVA
        BigDecimal subtotal = totalPedido.divide(BigDecimal.ONE.add(ivaRate), 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal iva = totalPedido.subtract(subtotal);
        
        Factura factura = new Factura();
        factura.setSubtotal(subtotal);
        factura.setIva(iva);
        factura.setTotal(totalPedido);
        
        return factura;
    }
}