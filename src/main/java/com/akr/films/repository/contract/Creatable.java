package com.akr.films.repository.contract;

public interface Creatable<T> {

    T insert(T data);

}
