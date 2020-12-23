package controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import java.io.Serializable;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.persistence.*;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
//import model.Produkte;
//import model.User;
import model.Benutzergruppe;
 
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

@ManagedBean(name="BenutzergruppeBean")
@RequestScoped

public class BenutzergruppeBean{
	
	@PersistenceUnit
	private EntityManagerFactory emf;
	
	public BenutzergruppeBean(){
		
	}
	public List<Benutzergruppe> getBenutzergruppeList(){
	EntityManager em = emf.createEntityManager();
	TypedQuery<Benutzergruppe> query = em.createNamedQuery("Benutzergruppe.findAll", Benutzergruppe.class);
	return query.getResultList();
	}
	
}
