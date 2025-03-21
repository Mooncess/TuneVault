package ru.mooncess.media_catalog_service.exception;

public class NoSuchProducerException extends RuntimeException{
    public NoSuchProducerException(String msg) {
        super(msg);
    }
}
