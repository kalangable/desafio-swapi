package com.akr.swapi.repository.contract;

public interface Creatable<T> {

    T insert(T data);

}
