    package com.estacionamiento.servicio;

    import com.estacionamiento.modelo.Registro;
    import com.estacionamiento.modelo.Tarifa;
    import com.estacionamiento.util.MisConstantes;
    import java.sql.Timestamp;
    import java.time.*;
    import java.time.LocalTime;
    import java.util.Calendar;

    public class CalculoServicio {
    // Ver si excedio - > lo necesitaremos para cuando el metodo q *calcula el monto total*, pasaremos Hora fin y Hora actual
    //compararemos la (hora de fin + 5 min) con la hora actual q nos pasaran, si se paso entonces usamos ese parametro de multa para ponerselo
        //si no se paso no usamos ese valor o  simplemente sera 0


     public double calcularMontoTotal(Registro registro) {
    double montoBase = registro.getMonto();
    double total = montoBase;

    LocalDateTime ahora = LocalDateTime.now();
    LocalDateTime finPlan = registro.getFecha_fin_plan();
    
    if (ahora.isAfter(finPlan.plusMinutes(5))) { 
        Duration duracionExcedida = Duration.between(finPlan, ahora);
        long minutosExcedidos = duracionExcedida.toMinutes();

        if (minutosExcedidos <= 30) {
    
            total += registro.getTarifa().getPrecio() / 2;
        } else if (minutosExcedidos <= 60) {
           
            total += registro.getTarifa().getPrecio();
        }
    }

  
    if (registro.getTarifa().getUnidadDescuento() != null) {
        int minutosRegalo = registro.getTarifa().getCantidad_descuento() * 
                            registro.getTarifa().getUnidadDescuento().getFactorConversionMinutos();

      
        total -= (minutosRegalo / 60.0) * registro.getTarifa().getPrecio();
    }

    if (registro.getTarifa().getValorDescuento() > 0.0) {
        total -= total * registro.getTarifa().getValorDescuento();
    }


    return Math.max(0, total);
}
   

    }