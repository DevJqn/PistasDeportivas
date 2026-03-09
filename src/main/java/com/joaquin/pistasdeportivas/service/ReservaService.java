// service/ReservaService.java
package com.joaquin.pistasdeportivas.service;

import com.joaquin.pistasdeportivas.dto.*;
import com.joaquin.pistasdeportivas.entity.*;
import com.joaquin.pistasdeportivas.exception.*;
import com.joaquin.pistasdeportivas.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReservaService {

    // Precios por hora
    private static final BigDecimal PRECIO_PICO  = new BigDecimal("15.00");
    private static final BigDecimal PRECIO_VALLE = new BigDecimal("8.00");

    @Autowired private ReservaRepository reservaRepository;
    @Autowired private PistaRepository   pistaRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private EmailService      emailService;

    // ─── CASOS DE USO ───────────────────────────────────────────────────────

    public ReservaResponseDTO crear(ReservaRequestDTO dto, String emailUsuario) {

        // Validaciones de tiempo
        if (!dto.getHoraFin().isAfter(dto.getHoraInicio())) {
            throw new ReservaException("La hora de fin debe ser posterior a la de inicio");
        }
        long minutos = ChronoUnit.MINUTES.between(dto.getHoraInicio(), dto.getHoraFin());
        if (minutos < 30)  throw new ReservaException("Mínimo 30 minutos de reserva");
        if (minutos > 120) throw new ReservaException("Máximo 2 horas por reserva");

        // Disponibilidad
        if (reservaRepository.existsConflicto(
                dto.getPistaId(), dto.getFecha(), dto.getHoraInicio(), dto.getHoraFin())) {
            throw new PistaNoDisponibleException(
                    "La pista ya está reservada en ese horario");
        }

        Pista   pista   = getOrThrowPista(dto.getPistaId());
        Usuario usuario = getOrThrowUsuario(emailUsuario);

        BigDecimal precio = calcularPrecio(dto.getFecha(), dto.getHoraInicio(), dto.getHoraFin());

        Reserva reserva = Reserva.builder()
                .fecha(dto.getFecha())
                .horaInicio(dto.getHoraInicio())
                .horaFin(dto.getHoraFin())
                .precio(precio)
                .pista(pista)
                .usuario(usuario)
                .build();

        reserva = reservaRepository.save(reserva);
        emailService.enviarConfirmacion(reserva);
        return toDTO(reserva);
    }

    public ReservaResponseDTO cancelar(Long id, String emailUsuario) {
        Reserva reserva = getOrThrow(id);
        Usuario usuario = getOrThrowUsuario(emailUsuario);

        boolean esAdmin = usuario.getRol() == Rol.ADMIN;
        if (!esAdmin && !reserva.getUsuario().getId().equals(usuario.getId())) {
            throw new ReservaException("No tienes permiso para cancelar esta reserva");
        }
        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new ReservaException("La reserva ya está cancelada");
        }

        BigDecimal reembolso = calcularReembolso(reserva);
        reserva.setEstado(EstadoReserva.CANCELADA);
        reserva.setReembolso(reembolso);
        reserva = reservaRepository.save(reserva);

        emailService.enviarCancelacion(reserva, reembolso);
        return toDTO(reserva);
    }

    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> listarPorUsuario(String emailUsuario) {
        Usuario u = getOrThrowUsuario(emailUsuario);
        return reservaRepository.findByUsuarioOrderByFechaAscHoraInicioAsc(u)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> listarTodas() {
        return reservaRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> listarActivas() {
        return reservaRepository.findByEstadoOrderByFechaAscHoraInicioAsc(EstadoReserva.ACTIVA)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public IngresosPistaDTO calcularIngresos(Long pistaId, LocalDate inicio, LocalDate fin) {
        Pista pista = getOrThrowPista(pistaId);
        BigDecimal total = reservaRepository.calcularIngresos(pistaId, inicio, fin);
        return IngresosPistaDTO.builder()
                .pistaId(pistaId).pistaNombre(pista.getNombre())
                .fechaInicio(inicio).fechaFin(fin)
                .totalIngresos(total)
                .build();
    }

    // ─── LÓGICA DE NEGOCIO ──────────────────────────────────────────────────

    /**
     * Pico: fin de semana o entre 17:00 y 22:00 en día laboral.
     * Valle: resto de horarios.
     */
    private BigDecimal calcularPrecio(LocalDate fecha, LocalTime inicio, LocalTime fin) {
        boolean esFinDeSemana = fecha.getDayOfWeek() == DayOfWeek.SATURDAY
                             || fecha.getDayOfWeek() == DayOfWeek.SUNDAY;
        boolean esHoraPico    = !inicio.isBefore(LocalTime.of(17, 0))
                             && inicio.isBefore(LocalTime.of(22, 0));

        BigDecimal precioPorHora = (esFinDeSemana || esHoraPico) ? PRECIO_PICO : PRECIO_VALLE;

        long mins = ChronoUnit.MINUTES.between(inicio, fin);
        BigDecimal horas = BigDecimal.valueOf(mins)
                .divide(BigDecimal.valueOf(60), 4, RoundingMode.HALF_UP);

        return precioPorHora.multiply(horas).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Política de cancelación:
     * >24 h  → 100 %  |  12–24 h → 50 %  |  <12 h → 0 %
     */
    private BigDecimal calcularReembolso(Reserva reserva) {
        LocalDateTime inicio = LocalDateTime.of(reserva.getFecha(), reserva.getHoraInicio());
        long horas = ChronoUnit.HOURS.between(LocalDateTime.now(), inicio);

        if (horas > 24)  return reserva.getPrecio();
        if (horas >= 12) return reserva.getPrecio()
                .multiply(new BigDecimal("0.50")).setScale(2, RoundingMode.HALF_UP);
        return BigDecimal.ZERO;
    }

    // ─── helpers ────────────────────────────────────────────────────────────

    private Reserva getOrThrow(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada: " + id));
    }
    private Pista getOrThrowPista(Long id) {
        return pistaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pista no encontrada: " + id));
    }
    private Usuario getOrThrowUsuario(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + email));
    }

    public ReservaResponseDTO toDTO(Reserva r) {
        return ReservaResponseDTO.builder()
                .id(r.getId())
                .fecha(r.getFecha()).horaInicio(r.getHoraInicio()).horaFin(r.getHoraFin())
                .precio(r.getPrecio()).estado(r.getEstado().name()).reembolso(r.getReembolso())
                .pistaNombre(r.getPista().getNombre()).tipoPista(r.getPista().getTipo().name())
                .usuarioNombre(r.getUsuario().getNombre()).usuarioEmail(r.getUsuario().getEmail())
                .build();
    }
}

