package com.estacionamiento.servicio;

import com.estacionamiento.modelo.Registro;
import com.estacionamiento.modelo.Tarifa;
import java.time.*;

public class CalculoServicio {

    public double calcularMontoTotal(Registro registro) {
        if (registro == null) return 0.0;

        // El monto base ya viene calculado desde el registro de entrada; no se recalcula
        double montoBase = registro.getMonto();

        double recargoExceso = 0.0;
        LocalDateTime finPlan = registro.getFecha_fin_plan();
        if (finPlan != null) {
            LocalDateTime ahora = LocalDateTime.now();
            // Una sola tolerancia de 5 minutos al final del plan
            if (ahora.isAfter(finPlan.plusMinutes(5))) {
                long minutosExcedidos = Duration.between(finPlan, ahora).toMinutes();
                double precioUnitario = registro.getTarifa() != null ? registro.getTarifa().getPrecio() : 0.0;

                long bloques = minutosExcedidos / 30;
                long resto   = minutosExcedidos % 30;

                // Bloques completos de 30 min: par → media tarifa, impar → tarifa completa
                double recargo = 0;
                for (long i = 0; i < bloques; i++) {
                    recargo += (i % 2 == 0) ? precioUnitario / 2.0 : precioUnitario;
                }
                // Minutos sobrantes → siempre media tarifa
                if (resto > 0) {
                    recargo += precioUnitario / 2.0;
                }
                recargoExceso = recargo;
                  System.out.println("Recargo exceso "+recargo);
                System.out.println("[CalculoServicio] Minutos excedidos: " + minutosExcedidos);
                System.out.println("[CalculoServicio] Bloques completos: " + bloques + " | Resto: " + resto + " min");
                System.out.println("[CalculoServicio] Recargo por exceso: $" + recargoExceso);
            }
        }

        double total = montoBase + recargoExceso;

        // Descuento porcentual si existe en la tarifa (aplica principalmente a pensiones)
        Tarifa tarifa = registro.getTarifa();
        if (tarifa != null) {
            Double valorDesc = tarifa.getValorDescuento();
            if (valorDesc != null && valorDesc > 0.0) {
                total -= total * valorDesc;
            }
        }

        System.out.println("[CalculoServicio] Base: $" + montoBase + " | Total final: $" + total);
        return Math.max(0, total);
    }
}