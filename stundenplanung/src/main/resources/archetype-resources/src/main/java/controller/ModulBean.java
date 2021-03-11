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
import model.Modul;
 
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Named;

//@ManagedBean(name="ModulBean")
@Named(value="modulBean")
@RequestScoped

public class ModulBean{
	
	@PersistenceUnit
	private EntityManagerFactory emf;
	
	public ModulBean(){
		
	}
	public List<Modul> getModulList(){
	EntityManager em = emf.createEntityManager();
	TypedQuery<Modul> query = em.createNamedQuery("Modul.findAll", Modul.class);
	return query.getResultList();
	}
	
}
