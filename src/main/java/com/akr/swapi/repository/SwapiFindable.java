package com.akr.swapi.repository;

import java.util.List;
import java.util.Optional;

public interface SwapiFindable<T, ID> {

    List<T> findAll();

    Optional<T> findById(ID id);

}
