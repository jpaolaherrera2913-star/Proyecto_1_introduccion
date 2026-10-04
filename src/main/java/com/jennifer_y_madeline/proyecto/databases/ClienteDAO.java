package com.jennifer_y_madeline.proyecto.databases;

import jakarta.persistence.EntityManager;
import java.util.List;

public class ClienteDAO {

    public List<TipoCliente> listarTipos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT t FROM TipoCliente t ORDER BY t.nombre", TipoCliente.class)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    public List<MetodoPago> listarMetodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT m FROM MetodoPago m ORDER BY m.nombre", MetodoPago.class)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Cliente> listar() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT c FROM Cliente c ORDER BY c.nombre", Cliente.class)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    public void guardar(Cliente c) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (c.getId() == null) {
                em.persist(c);
            } else {
                em.merge(c);
            }
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public void eliminar(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Cliente c = em.find(Cliente.class, id);
            if (c != null) {
                em.remove(c);
            }
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}
