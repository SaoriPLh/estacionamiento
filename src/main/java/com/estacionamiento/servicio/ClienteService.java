package com.estacionamiento.servicio;

import com.estacionamiento.dao.ClienteDAO;
import com.estacionamiento.modelo.Cliente;
import com.estacionamiento.util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class ClienteService {

    private final ClienteDAO clienteDAO = new ClienteDAO();


    public Cliente registrarCliente(Cliente cliente) {
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            clienteDAO.insertar(cliente, con);

            con.commit();
            return cliente;

        } catch (SQLException e) {
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            throw new RuntimeException("Error al registrar cliente: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
public List<Cliente> listarClientesPensionadosPorEstacionamiento(int idEstacionamiento) {
    try {
        return clienteDAO.listarClientesPensionadosPorEstacionamiento(idEstacionamiento);
    } catch (Exception e) {
        System.err.println("Error en service (clientes por sede): " + e.getMessage());
        return new ArrayList<>();
    }
}
public List<Cliente> listarClientesPorEstacionamiento(int idEstacionamiento) {
    try {
        return clienteDAO.listarPorEstacionamiento(idEstacionamiento);
    } catch (Exception e) {
        System.err.println("Error en service (listar clientes por sede): " + e.getMessage());
        return new ArrayList<>();
    }
}
  
    public Cliente buscarPorId(int idCliente) {
        try {
            Cliente c = clienteDAO.buscarPorId(idCliente);
            if (c == null) throw new IllegalStateException("No existe cliente con id: " + idCliente);
            return c;
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar cliente: " + e.getMessage(), e);
        }
    }

    public List<Cliente> listarTodos() {
        try {
            return clienteDAO.listarTodos();
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar clientes: " + e.getMessage(), e);
        }
    }

    public boolean actualizar(Cliente cliente) {
        try {
            return clienteDAO.actualizar(cliente);
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar cliente: " + e.getMessage(), e);
        }
    }

    public boolean eliminar(int idCliente) {
        try {
            return clienteDAO.eliminar(idCliente);
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar cliente: " + e.getMessage(), e);
        }
    }
}