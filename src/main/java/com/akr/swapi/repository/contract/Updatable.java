package com.akr.swapi.repository.contract;

public interface Updatable<T> {

    T update(T data);

}
