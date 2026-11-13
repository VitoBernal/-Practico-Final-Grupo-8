package ar.edu.unju.fi.poo.dto.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import ar.edu.unju.fi.poo.dto.CuotaResponseDto;
import ar.edu.unju.fi.poo.dto.DetalleVentaResponseDto;
import ar.edu.unju.fi.poo.dto.VentaResponseDto;
import ar.edu.unju.fi.poo.model.Cuota;
import ar.edu.unju.fi.poo.model.DetalleVenta;
import ar.edu.unju.fi.poo.model.VentaContado;
import ar.edu.unju.fi.poo.model.VentaFinanciada;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface VentaMapper {

	@Mapping(target = "modalidad", constant = "CONTADO")
	@Mapping(target = "clienteId", source = "cliente.id")
	@Mapping(target = "clienteNombreCompleto", source = "cliente.nombreCompleto")
	@Mapping(target = "clienteNumeroIdentificacion", source = "cliente.numeroIdentificacion")
	@Mapping(target = "cuponCodigo", source = "cupon.codigo")
	@Mapping(target = "porcentajeRecargo", ignore = true)
	@Mapping(target = "montoRecargo", ignore = true)
	@Mapping(target = "cuotas", ignore = true)
	VentaResponseDto ventaContadoADto(VentaContado venta);

	@Mapping(target = "modalidad", constant = "FINANCIADA")
	@Mapping(target = "clienteId", source = "cliente.id")
	@Mapping(target = "clienteNombreCompleto", source = "cliente.nombreCompleto")
	@Mapping(target = "clienteNumeroIdentificacion", source = "cliente.numeroIdentificacion")
	@Mapping(target = "cuponCodigo", ignore = true)
	@Mapping(target = "montoDescuento", ignore = true)
	VentaResponseDto ventaFinanciadaADto(VentaFinanciada venta);

	@Mapping(target = "productoId", source = "producto.id")
	@Mapping(target = "productoNombre", source = "producto.nombre")
	DetalleVentaResponseDto detalleADto(DetalleVenta detalle);

	@Mapping(target = "ventaId", source = "venta.id")
	@Mapping(target = "ventaCodigoComprobante", source = "venta.codigoComprobante")
	CuotaResponseDto cuotaADto(Cuota cuota);

	List<CuotaResponseDto> cuotasADto(List<Cuota> cuotas);
}
