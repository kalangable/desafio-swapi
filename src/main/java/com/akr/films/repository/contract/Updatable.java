package com.akr.films.repository.contract;

public interface Updatable<T> {

    T update(T data);

}
