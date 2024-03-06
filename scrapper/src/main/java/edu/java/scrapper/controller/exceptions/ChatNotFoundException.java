package edu.java.scrapper.controller.exceptions;

public class ChatNotFoundException extends RuntimeException{
    public ChatNotFoundException(String message) {
        super(message);
    }
}
