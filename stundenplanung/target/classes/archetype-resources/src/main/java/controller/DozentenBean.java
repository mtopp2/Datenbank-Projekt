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
import model.Dozenten;
 
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

@ManagedBean(name="DozentenBean")
@RequestScoped

public class DozentenBean{
	
	@PersistenceUnit
	private EntityManagerFactory emf;
	
	public DozentenBean(){
		
	}
	public List<Dozenten> getDozentenList(){
	EntityManager em = emf.createEntityManager();
	TypedQuery<Dozenten> query = em.createNamedQuery("Dozenten.findAll", Dozenten.class);
	return query.getResultList();
	}
	
}
