package controller;

import model.Account;
//import model.Studiengang;
import model.Benutzergruppe;
import model.Faculty;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import javax.faces.bean.ManagedBean;
//import javax.faces.bean.SessionScoped;

/**
 *
 * @author Anil
 */
@Named(value="registerController")
//@ManagedBean(name="RegisterController")
@SessionScoped
public class RegisterController implements Serializable {
	private static final long serialVersionUID = 1L;

	@PersistenceUnit
    private EntityManagerFactory emf;
    
    @Resource
    private UserTransaction ut;
    
    //@Inject
    private Account Account;
    private Faculty Faculty;
    //private ArrayList StudiengangNamensListe;
    ArrayList<String> FacultyListe = new ArrayList<>();

    
    @PostConstruct /* Initialisiere bei Anwendungsstart */
    public void init(){
        //StudiengangNamensListe = new ArrayList();		?
        EntityManager em = emf.createEntityManager();
        Query q = em.createNamedQuery("Faculty.findAll");
        List FList = q.getResultList();
        for (Object FListitem : FList)
        {
            Faculty fac =(Faculty)FListitem;
            FacultyListe.add(fac.getFacName());
        }
    }

    private String accPwd;
    private String accName;
    private String facName;
    private String accEmail;
    private boolean name_ok=false;
    private boolean pw_ok = false;
    private boolean email_ok = false;
    private boolean register_ok = false;
    
    private Benutzergruppe BGruppe;


    public RegisterController() {

    }

    
    //getter und setter - Methoden 
    
    public ArrayList<String> getFacultyListe() {
        return FacultyListe;
    }

    public void setFacultyListe(ArrayList<String> FacultyListe) {
        this.FacultyListe = FacultyListe;
    }
    
    public String getAccName() {
        return accName;
    }

    public boolean isRegister_ok() {
        return register_ok;
    }

    public void setRegister_ok(boolean register_ok) {
        this.register_ok = register_ok;
    }

    public void setAccName(String name) {
        if(name!=null){
            if(checkName(name)==false){
                this.accName = name;
                name_ok=true;
            }
            else{
                String msg = "Account bereits vorhanden";
                addMessage("idName_reg",msg);
            }
        }
    }

    public String getAccPwd() {
        return accPwd;
    }

    public void setAccPwd(String accPwd) {
        if(accPwd!=null){
            this.accPwd = accPwd;
            pw_ok=true;
        }
        else{
        	String msg="Bitte ein Passwort vergeben";
            addMessage("idPwd_reg",msg);
        }
    }

    public String getFacName() {
        return facName;
    }

    public void setFacName(String facName) {
        this.facName = facName;
    }
    

    public String getAccEmail() {
        return accEmail;
    }

    public void setAccEmail(String accEmail) {
        if(accEmail!=null){
            this.accEmail = accEmail;
            email_ok=true;
        }
        else{
        	String msg="Bitte eine Email Adresse Angeben";
            addMessage("idEmail_reg",msg);
        }
    }
    
    public UIComponent getReg() {
        return reg;
    }

    public void setReg(UIComponent reg) {
        this.reg = reg;
    }

    public Account getAccount() {
        return Account;
    }

    public void setAccount(Account Account) {
        this.Account = Account;
    }


    public Faculty getFaculty() {
        return Faculty;
    }

    public void setFaculty(Faculty Faculty) {
        this.Faculty = Faculty;
    }
    
    public Benutzergruppe getBGruppe() {
        return BGruppe;
    }

    public void setBGruppe(Benutzergruppe BGruppe) {       
        this.BGruppe = BGruppe;
    }
    
    //weitere Methoden
    
    //Beim drücken des Register Buttons auf registerUser leiten wenn alle Eingaben IO
   public String register_button() throws SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception{
       if(name_ok==true && pw_ok==true && email_ok==true){
           registerUser();
           accName="";
           accEmail="";
           return "login.xhtml";
       }
       else{
            name_ok=false;
            pw_ok = false;
            email_ok = false;
       }
        name_ok=false;
        pw_ok = false;
        email_ok = false;
        return"register.xhtml";
   }

    //Eingegebene Daten an die Datenbank übermitteln
    private UIComponent reg;    
    public void registerUser() throws IllegalStateException, SecurityException, SystemException, NotSupportedException, RollbackException, HeuristicMixedException, HeuristicRollbackException, Exception  {
            EntityManager em = emf.createEntityManager();
            List<Account> user_temp = new ArrayList<>();   
            try {
                TypedQuery<Account> queryGet = em.createNamedQuery("Account.findByAccName", Account.class).setParameter("accName", this.accName);   
                user_temp = queryGet.getResultList();  
            } 
            catch (Exception e) {
            }
            if (user_temp.isEmpty()) {  
                Account newUser = new Account();  
                newUser.setAccName(accName);    
                newUser.setAccPwd(accPwd);      
                newUser.setAccEmail(accEmail);
                //Alle User sind als erstes Nobodys
                newUser.setBenutzergruppe(findBGID());
                //Dropdown Menü
                newUser.setFaculty(findFac(facName));
                try {
                    ut.begin();   
                    em.joinTransaction();  
                    em.persist(newUser);  
                    ut.commit(); 
                }
                catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException e) {
                    try {
                        ut.rollback();
                    } 
                    catch (IllegalStateException | SecurityException | SystemException ex) {
                    }
                }
            } 
            else {
                FacesMessage message = new FacesMessage("Account bereits vorhanden");
                FacesContext.getCurrentInstance().addMessage("registerform:idName_reg", message);
            }
            em.close();
    }
    
    //Überprüfen ob Name schon vergeben
    private boolean checkName(String uName) {
        boolean found = false;
        try{
            EntityManager em = emf.createEntityManager(); 
            TypedQuery<Account> query
                = em.createNamedQuery("Account.findByBname",Account.class);
            query.setParameter("accName", uName);
            Account = (Account)query.getSingleResult();
            found=true; //Account gefunden!
        }
        catch(Exception e){   
        }
        return found;
    }
    
    
    private Benutzergruppe findBGID() {
        try{
        	EntityManager em;
        	String query;
        	Query q;
            em = emf.createEntityManager();
            query = "SELECT b FROM Benutzergruppe b WHERE b.groupID = 2";
            q = em.createQuery(query);
            BGruppe = (Benutzergruppe)q.getSingleResult();
            /*TypedQuery<Benutzergruppe> query
                = em.createNamedQuery("Benutzergruppe.findByID", Benutzergruppe.class);
            query.setParameter("id", id);
            BGruppe = (Benutzergruppe)query.getSingleResult();*/
        }
        catch(Exception e){   
        }
        return BGruppe;
    }
    
    private Faculty findFac(String fac) {
        try{
            EntityManager em = emf.createEntityManager(); 
            TypedQuery<Faculty> query
                = em.createNamedQuery("Faculty.findByFacName",Faculty.class);
            query.setParameter("facName", fac);
            Faculty = (Faculty)query.getSingleResult();
        }
        catch(Exception e){   
        }
        return Faculty;
    }
    
    //Nachrichten an die View senden
     private void addMessage(String loginformidName, String msg) {
        FacesMessage message = new FacesMessage(msg);
        FacesContext.getCurrentInstance().addMessage(loginformidName, message);     
    }
    
}

