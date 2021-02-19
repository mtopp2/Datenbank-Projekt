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
    
    @Inject
    private Account Account;
    
    
    private String accountName;
    private String accountPassword;
    private Benutzergruppe userGroup;
    
    private boolean isLoggedIn=false;
    private boolean logOutPerformed = false;
    private boolean nameFound = false;
    private boolean isAdm;
    private boolean isNob;
    private boolean isSgl;
    private boolean isRzp;

    private Account current;
    private String componentId;
    private String summery;
    
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
    
    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        if(accountName!=null){
            if(findUser(accountName)){
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
                this.accountName = accountName;
            }
            else{
                FacesMessage message = new FacesMessage("Account nicht vorhanden");
                FacesContext.getCurrentInstance().addMessage("loginForm:idName_login", message);
                //String msg = "Account nicht vorhanden";
                //addMessage("idName_login", msg);
            }
        }
    }
    
    public String getAccountPassword() {
        return accountPassword;
    }

    public void setAccountPassword(String accountPassword) {
        if(accountPassword!=null){
            if(checkPwd(accountPassword)){
                this.accountPassword = accountPassword;
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

    public Benutzergruppe getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(Benutzergruppe userGroup) {       
        this.userGroup = userGroup;
    }

    public boolean getIsLoggedIn() {
        return isLoggedIn;
    }

    public void setIsLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }


    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
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
            userGroup = (Benutzergruppe)query.getSingleResult();
        }
        catch(Exception e){   
        }
        return userGroup;
    }
    
    //Überprüfen ob das eingebene Passwort zum einegeben Namen passt
    private boolean checkPwd(String password) {
        boolean found = false;
        if(nameFound==true){
           String pwd = current.getAccPwd();
            if(pwd.equalsIgnoreCase(password)&& nameFound==true){
                found=true;
                userGroup = current.getBenutzergruppe();
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
    public String logout(){
        accountName="";
        if(isLoggedIn==true){
            FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
            FacesContext.getCurrentInstance().getExternalContext().getSession(true);
            isLoggedIn=false;
            logOutPerformed =true;
            nameFound=false;
            return "/login.xhtml";
        }
		return "/index.xhtml";
    }
      
    //Message an die View senden
    private void addMessage(String loginformidName, String msg) {
        FacesMessage message = new FacesMessage(msg);
        FacesContext.getCurrentInstance().addMessage(loginformidName, message);     
    }
    
}
