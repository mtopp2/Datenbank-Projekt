/**
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;


import model.Account;
import model.Benutzergruppe;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import javax.annotation.Resource;
import javax.faces.application.FacesMessage;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.TypedQuery;
import javax.transaction.UserTransaction;

import javax.faces.bean.ManagedBean;
//import javax.faces.bean.SessionScoped;

/**
 *
 * @author Anil
 */

@Named(value = "loginController")
//@ManagedBean(name="LoginController")
@SessionScoped
public class LoginController implements Serializable {
	//Serial Version ID Es kann sein, dass man die nicht braucht.
	private static final long serialVersionUID = 1L;
    
   @PersistenceUnit
    private EntityManagerFactory emf;
    
    @Resource
    private UserTransaction ut;
    
    //@Inject
    //private Account Account;
    
    
    private String accName;
    private String accPwd;
    private Benutzergruppe BGruppe;
    private int FK_GroupID;
    private boolean isLoggedIn=false;
    private boolean logOutPerformed = false;
    private boolean nameFound = false;
    private boolean isAdm;
    private boolean isNob;
    private boolean isSgl;
    private boolean isRzp;

    private Account current;
    private String componentID;
    private String summery;
    
    private Benutzergruppe bg;
    private int id;
 
    
//Getter und Setter Methoden
    
    public boolean isIsAdm() {
        return isAdm;
    }

    public void setIsAdm(boolean isAdm) {
        this.isAdm = isAdm;
    }
    
    public boolean isIsNob() {
        return isNob;
    }

    public void setIsNob(boolean isNob) {
        this.isNob = isNob;
    }    
    
    public boolean isIsSgl() {
        return isSgl;
    }

    public void setIsSgl(boolean isSgl) {
        this.isSgl = isSgl;
    }    
    
    public boolean isIsRzp() {
        return isRzp;
    }

    public void setIsRzp(boolean isRzp) {
        this.isRzp = isRzp;
    }    
    
    public String getAccName() {
        return accName;
    }

    public void setAccName(String accName) {
        if(accName!=null){
            if(findUser(accName)){
                nameFound=true;
                if(nameFound==true && current.getBenutzergruppe().getGroupID() == 1){
                    isAdm = true;
                    
                }
                if(nameFound==true && current.getBenutzergruppe().getGroupID() == 2){
                    isNob = true;
                
                }
                if(nameFound==true && current.getBenutzergruppe().getGroupID() == 3){
                    isSgl = true;
                
                }
                if(nameFound==true && current.getBenutzergruppe().getGroupID() == 9){
                    isRzp = true;
                
                }
                this.accName = accName;
            }
            else{
                FacesMessage message = new FacesMessage("Account nicht vorhanden");
                FacesContext.getCurrentInstance().addMessage("loginForm:idName_login", message);
                //String msg = "Account nicht vorhanden";
                //addMessage("idName_login", msg);
            }
        }
    }
    
    public String getAccPwd() {
        return accPwd;
    }

    public void setAccPwd(String accPwd) {
        if(accPwd!=null){
            if(checkPwd(accPwd)){
                this.accPwd = accPwd;
                isLoggedIn = true;
            }
            else{
                FacesMessage message = new FacesMessage("Passwort Falsch");
                FacesContext.getCurrentInstance().addMessage("loginForm:idPwd_login", message);
                //String msg="Password Falsch";
                //addMessage("idPwd_login",msg);
            }
        }
    }

    public Benutzergruppe getBGruppe() {
        return BGruppe;
    }

    public void setBGruppe(Benutzergruppe BGruppe) {       
        this.BGruppe = BGruppe;
    }

    public boolean getIsLoggedIn() {
        return isLoggedIn;
    }

    public void setIsLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }


    public String getComponentID() {
        return componentID;
    }

    public void setComponentID(String componentID) {
        this.componentID = componentID;
    }

    public String getSummery() {
        return summery;
    }

    public void setSummery(String summery) {
        this.summery = summery;
    }
    public boolean isLogOutPerformed() {
        return logOutPerformed;
    }

    public void setLogOutPerformed(boolean logOutPerformed) {
        this.logOutPerformed = logOutPerformed;
    }
    
    
    
    
//Weitere Methoden
    
    //Überprüfen ob der User mit dem eingebenen Namen vorhanden ist
    private boolean findUser(String uName) {
        boolean found = false;
        try{
            EntityManager em = emf.createEntityManager(); 
            TypedQuery<Account> query
                = em.createNamedQuery("Account.findByAccName",Account.class);
            query.setParameter("accName", uName);
            current = (Account)query.getSingleResult();
            found=true; //Account gefunden!
        }
        catch(Exception e){   
        }
        return found;
    }
    
    
    private Benutzergruppe findBGID(int id) {
        try{
            EntityManager em = emf.createEntityManager(); 
            TypedQuery<Benutzergruppe> query
                = em.createNamedQuery("Benutzergruppe.findByID",Benutzergruppe.class);
            query.setParameter("id", id);
            bg = (Benutzergruppe)query.getSingleResult();
        }
        catch(Exception e){   
        }
        return bg;
    }
    
    //Überprüfen ob das eingebene Passwort zum einegeben Namen passt
    private boolean checkPwd(String password) {
        boolean found = false;
        if(nameFound==true){
           String pwd = current.getAccPwd();
        
            if(pwd.equalsIgnoreCase(password)&& nameFound==true){
                found=true;
                BGruppe = current.getBenutzergruppe();
            }
            else{
            return found;
            } 
        }
        
        return found;
        
    }   
    
    //Was beim drücken des login Buttons passieren soll
    public String login(){
        String returnvalue;
        if(isLoggedIn==true){

            returnvalue= "index.xhtml";
            
        }
        else{
            returnvalue="";
        }

        
        return returnvalue;
    }
      
    //Beim drücken des Logout Buttons
    public void logout(){
        accName="";
        if(isLoggedIn==true){
            FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
            FacesContext.getCurrentInstance().getExternalContext().getSession(true);
            isLoggedIn=false;
            logOutPerformed =true;
            nameFound=false;
        }
    }
      
    //Message an die View senden
    private void addMessage(String loginformidName, String msg) {
        FacesMessage message = new FacesMessage(msg);
        FacesContext.getCurrentInstance().addMessage(loginformidName, message);     
    }
    
}
