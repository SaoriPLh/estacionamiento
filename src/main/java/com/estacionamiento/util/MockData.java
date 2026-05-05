package com.estacionamiento.util;

import com.estacionamiento.modelo.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Datos de prueba para MODO_DEMO.
 * Cuando SessionManager.MODO_DEMO == true, los controladores
 * usan estos objetos en lugar de consultar la base de datos.
 */
public class MockData {

    private static final Random RNG = new Random(42); // seed fija = resultados reproducibles

    // ===== SESIÓN =====

    /** Inyecta una sesión de Admin demo en SessionManager */
    public static void inyectarSesionDemo() {
        Empresa empresa = new Empresa();
        empresa.setIdEmpresa(1);
        empresa.setNombreComercial("CyberPark Demo");

        Rol rol = new Rol();
        rol.setIdRol(MisConstantes.ROL_ADMIN);
        rol.setNombreRol("Administrador");

        Persona admin = new Persona(empresa, rol,
                "Admin", "Demo", "Sistema",
                "admin", "demo", false, 25000.0);
        admin.setIdPersona(1);

        Estacionamiento sede = getSede();

        EstadoPermiso estadoPermiso = new EstadoPermiso(MisConstantes.PERMISO_ACTIVO, "Activo");
        Permiso permiso = new Permiso(1, sede, admin, estadoPermiso, new java.util.Date());
        admin.agregarPermiso(permiso);

        SessionManager.getInstance().setUsuario(admin);
        SessionManager.getInstance().setEmpresa(empresa);
        SessionManager.getInstance().setEstacionamiento(sede);

        System.out.println("[DEMO] Sesión inyectada: admin@CyberPark → Sede: " + sede.getNombre());
    }

    // ===== SEDE =====

    public static Estacionamiento getSede() {
        Estacionamiento sede = new Estacionamiento();
        sede.setIdEstacionamiento(1);
        sede.setNombre("Comercial Centro");
        sede.setActivo(true);
        return sede;
    }

    // ===== ESPACIOS (30 cajones con estados aleatorios) =====

    public static List<Espacio> getEspacios() {
        List<Espacio> lista = new ArrayList<>();
        // Distribución fija: 18 disponibles, 8 ocupados, 4 pensión
        int[] estados = new int[30];
        for (int i = 0; i < 18; i++) estados[i] = MisConstantes.ESPACIO_DISPONIBLE;
        for (int i = 18; i < 26; i++) estados[i] = MisConstantes.ESPACIO_OCUPADO;
        for (int i = 26; i < 30; i++) estados[i] = MisConstantes.ESPACIO_PENSION;
        // mezclar
        for (int i = 29; i > 0; i--) {
            int j = RNG.nextInt(i + 1);
            int tmp = estados[i]; estados[i] = estados[j]; estados[j] = tmp;
        }

        for (int i = 0; i < 30; i++) {
            EstadoEspacio estado = new EstadoEspacio();
            estado.setIdEstadoEspacio(estados[i]);
            estado.setDescripcion(MisConstantes.getNombreEstadoEspacio(estados[i]));

            TipoEspacio tipo = new TipoEspacio();
            tipo.setIdTipoEspacio(estados[i] == MisConstantes.ESPACIO_PENSION
                    ? MisConstantes.TIPO_ESPACIO_RESERVA : MisConstantes.TIPO_ESPACIO_NORMAL);

            Espacio esp = new Espacio();
            esp.setIdEspacio(i + 1);
            esp.setCodigo(String.valueOf(i + 1));
            esp.setEstadoEspacio(estado);
            esp.setTipoEspacio(tipo);
            esp.setEstacionamiento(getSede());
            lista.add(esp);
        }
        return lista;
    }

    // ===== REGISTROS ACTIVOS (para búsqueda de salida) =====

    public static List<Registro> getRegistrosActivos() {
        List<Registro> lista = new ArrayList<>();
        String[] placas = {"ABC-123", "XYZ-789", "DEF-456", "GHI-012", "JKL-345",
                           "MNO-678", "PQR-901", "STU-234"};
        List<Espacio> espacios = getEspacios();
        List<Tarifa> tarifas = getTarifas();

        int idx = 0;
        for (Espacio esp : espacios) {
            if (esp.getEstadoEspacio().getIdEstadoEspacio() == MisConstantes.ESPACIO_OCUPADO
                    || esp.getEstadoEspacio().getIdEstadoEspacio() == MisConstantes.ESPACIO_PENSION) {

                Vehiculo v = new Vehiculo();
                v.setPlaca(idx < placas.length ? placas[idx] : "DEMO-" + idx);
                Marca marca = new Marca(); marca.setIdMarca(1); marca.setNombre("Toyota");
                v.setMarca(marca);

                EstadoRegistro estadoReg = new EstadoRegistro();
                estadoReg.setIdEstadoRegistro(MisConstantes.REGISTRO_ACTIVO);
                estadoReg.setNombreEstado("Activo");

                Registro r = new Registro();
                r.setIdRegistro(idx + 1);
                r.setVehiculo(v);
                r.setEspacio(esp);
                r.setTarifa(tarifas.get(idx % tarifas.size()));
                r.setHoraEntrada(LocalDateTime.now().minusHours(1 + idx));
                r.setEstadoRegistro(estadoReg);
                r.setMonto(tarifas.get(idx % tarifas.size()).getPrecio());
                lista.add(r);
                idx++;
            }
        }
        return lista;
    }

    // ===== HISTORIAL (15 registros mixtos activos + finalizados) =====

    public static List<Registro> getHistorial() {
        List<Registro> lista = new ArrayList<>();
        String[] placas = {"ABC-123", "XYZ-789", "DEF-456", "GHI-012", "JKL-345",
                           "MNO-678", "PQR-901", "STU-234", "VWX-567", "YZA-890",
                           "BCD-111", "EFG-222", "HIJ-333", "KLM-444", "NOP-555"};
        List<Tarifa> tarifas = getTarifas();
        List<Espacio> espacios = getEspacios();

        for (int i = 0; i < 15; i++) {
            boolean finalizado = i < 10;

            Vehiculo v = new Vehiculo();
            v.setPlaca(placas[i]);
            Marca marca = new Marca(); marca.setIdMarca(1); marca.setNombre("Toyota");
            v.setMarca(marca);

            EstadoRegistro estadoReg = new EstadoRegistro();
            estadoReg.setIdEstadoRegistro(finalizado
                    ? MisConstantes.REGISTRO_FINALIZADO : MisConstantes.REGISTRO_ACTIVO);
            estadoReg.setNombreEstado(finalizado ? "Finalizado" : "Activo");

            Tarifa tarifa = tarifas.get(i % tarifas.size());
            LocalDateTime entrada = LocalDateTime.now().minusHours(i + 2);
            LocalDateTime salida = finalizado ? entrada.plusHours(2) : null;
            double monto = finalizado ? tarifa.getPrecio() + (i * 5.0) : 0;

            Registro r = new Registro();
            r.setIdRegistro(i + 100);
            r.setVehiculo(v);
            r.setEspacio(espacios.get(i % espacios.size()));
            r.setTarifa(tarifa);
            r.setHoraEntrada(entrada);
            r.setHoraSalida(salida);
            r.setEstadoRegistro(estadoReg);
            r.setMonto(monto);
            lista.add(r);
        }
        return lista;
    }

    // ===== TARIFAS =====

    public static List<Tarifa> getTarifas() {
        List<Tarifa> lista = new ArrayList<>();
        Object[][] datos = {
            {MisConstantes.TARIFA_NORMAL,   MisConstantes.TIPO_COBRO_HORA,      "Normal",   "Por Hora",   35.0},
            {MisConstantes.TARIFA_PENSION,  MisConstantes.TIPO_COBRO_MENSUAL,   "Pensión",  "Mensual",  1200.0},
            {MisConstantes.TARIFA_ESPECIAL, MisConstantes.TIPO_COBRO_HORA,      "Especial", "Por Hora",   50.0},
           
        };
        int id = 1;
        for (Object[] d : datos) {
            TipoTarifa tipo = new TipoTarifa();
            tipo.setIdTipoTarifa((int) d[0]);
            tipo.setDescripcion((String) d[2]);

            TipoCobro cobro = new TipoCobro();
            cobro.setIdTipoCobro((int) d[1]);
            cobro.setNombre((String) d[3]);

            Tarifa t = new Tarifa();
            t.setIdTarifa(id++);
            t.setTipoTarifa(tipo);
            t.setTipoCobro(cobro);
            t.setPrecio((double) d[4]);
            t.setEstacionamiento(getSede());
            lista.add(t);
        }
        return lista;
    }

    // ===== MARCAS =====

    public static List<Marca> getMarcas() {
        String[] nombres = {"Toyota", "Honda", "Nissan", "Chevrolet", "Ford",
                            "Volkswagen", "Kia", "Hyundai", "Mazda", "BMW"};
        List<Marca> lista = new ArrayList<>();
        for (int i = 0; i < nombres.length; i++) {
            Marca m = new Marca(); m.setIdMarca(i + 1); m.setNombre(nombres[i]);
            lista.add(m);
        }
        return lista;
    }

    // ===== CLIENTES =====

    public static List<Cliente> getClientes() {
        String[][] datos = {
            {"Laura",   "García",   "López"},
            {"Carlos",  "Martínez", "Ruiz"},
            {"Ana",     "Sánchez",  "Torres"},
            {"Pedro",   "Ramírez",  "Flores"},
        };
        List<Tarifa> tarifas = getTarifas();
        List<Cliente> lista = new ArrayList<>();
        for (int i = 0; i < datos.length; i++) {
            Cliente c = new Cliente();
            c.setIdCliente(i + 1);
            c.setNombre(datos[i][0]);
            c.setApellidoPaterno(datos[i][1]);
            c.setApellidoMaterno(datos[i][2]);
            c.setTarifa(tarifas.get(MisConstantes.TARIFA_PENSION - 1));
            lista.add(c);
        }
        return lista;
    }

    // ===== PERSONAL =====

    public static List<Persona> getPersonal() {
        Empresa empresa = new Empresa();
        empresa.setIdEmpresa(1);
        empresa.setNombreComercial("CyberPark Demo");

        Object[][] datos = {
            {MisConstantes.ROL_EMPLEADO, "Juan",    "Pérez",    "Díaz",    "juanp",    18000.0},
            {MisConstantes.ROL_EMPLEADO, "María",   "González", "López",   "mariag",   18000.0},
            {MisConstantes.ROL_EMPLEADO, "Roberto", "Hernández","Castro",  "robertoh", 20000.0},
            {MisConstantes.ROL_ADMIN,    "Saori",   "Admin",    "Sistema", "saori",    30000.0},
        };

        List<Persona> lista = new ArrayList<>();
        for (int i = 0; i < datos.length; i++) {
            Rol rol = new Rol();
            rol.setIdRol((int) datos[i][0]);
            rol.setNombreRol(MisConstantes.getNombreRol((int) datos[i][0]));

            Persona p = new Persona(empresa, rol,
                    (String) datos[i][1], (String) datos[i][2], (String) datos[i][3],
                    (String) datos[i][4], "demo", false, (Double) datos[i][5]);
            p.setIdPersona(i + 1);

            EstadoPermiso ep = new EstadoPermiso(MisConstantes.PERMISO_ACTIVO, "Activo");
            Permiso perm = new Permiso(i + 1, getSede(), p, ep, new java.util.Date());
            p.agregarPermiso(perm);
            lista.add(p);
        }
        return lista;
    }

    // ===== RESUMEN GANANCIAS =====

    public static ResumenGananciasDTO getResumenGanancias() {
        ResumenGananciasDTO r = new ResumenGananciasDTO();
        r.numeroTarifaNormal    = 42;
        r.numeroPensiones       = 12;
        r.numeroTarifaEspecial  = 8;
        r.numeroTarifaConvenios = 5;
        r.gananciasTotales      = 42 * 35.0 + 12 * 1200.0 + 8 * 50.0 + 5 * 800.0;
        r.totalRegistros        = 67;
        return r;
    }

    // ===== REGISTRO DE SALIDA ENCONTRADO (para buscar por placa en demo) =====

    public static Registro getRegistroActivoParaSalida(String termino) {
        for (Registro r : getRegistrosActivos()) {
            String placa   = r.getVehiculo().getPlaca();
            String espacio = r.getEspacio().getCodigo();
            if (placa.equalsIgnoreCase(termino) || espacio.equalsIgnoreCase(termino)) {
                return r;
            }
        }
        // Si no coincide exactamente, devuelve el primero para que siempre haya resultado en demo
        List<Registro> activos = getRegistrosActivos();
        return activos.isEmpty() ? null : activos.get(0);
    }
}
