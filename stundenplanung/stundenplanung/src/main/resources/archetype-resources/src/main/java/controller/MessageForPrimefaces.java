package controller;

public class MessageForPrimefaces {
	
    public String text;
    public int id;

    public MessageForPrimefaces(String text, int id) {
        this.text = text;
        this.id = id;
    }

    // Due to the constraints in the above link, the class must have a 
    // default constructor
    private MessageForPrimefaces() {}

}
