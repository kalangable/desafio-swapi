package com.akr.films.repository.contract;

import com.akr.films.domain.PageQuery;
import com.akr.films.domain.PageResponse;
import java.util.Optional;

public interface Findable<T, ID> {

    PageResponse<T> findAll(PageQuery query);

    Optional<T> findById(ID id);

}
