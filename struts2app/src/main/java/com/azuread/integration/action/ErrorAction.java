package com.azuread.integration.action;

import com.opensymphony.xwork2.ActionSupport;

import java.util.logging.Logger;

/**
 * Action for error handling.
 */
public class ErrorAction extends ActionSupport {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ErrorAction.class.getName());
    
    private String errorMessage;
    
    @Override
    public String execute() throws Exception {
        LOGGER.warning("Error page accessed with message: " + errorMessage);
        return SUCCESS;
    }
    
    /**
     * Get the error message to display.
     * 
     * @return The error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }
    
    /**
     * Set the error message to display.
     * 
     * @param errorMessage The error message
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
