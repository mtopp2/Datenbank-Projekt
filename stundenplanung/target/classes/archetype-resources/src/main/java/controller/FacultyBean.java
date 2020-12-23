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
import model.Faculty;
 
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

@ManagedBean(name="FacultyBean")
@RequestScoped

public class FacultyBean{
	
	@PersistenceUnit
	private EntityManagerFactory emf;
	
	public FacultyBean(){
		
	}
	public List<Faculty> getFacultyList(){
	EntityManager em = emf.createEntityManager();
	TypedQuery<Faculty> query = em.createNamedQuery("Faculty.findAll", Faculty.class);
	return query.getResultList();
	}
	
}
