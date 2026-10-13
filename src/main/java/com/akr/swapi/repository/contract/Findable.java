package com.akr.swapi.repository.contract;

import java.util.List;
import java.util.Optional;

public interface Findable<T, ID> {

    List<T> findAll();

    Optional<T> findById(ID id);

}
