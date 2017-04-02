package fi.mlappi.golf.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import fi.mlappi.golf.model.Round;

@Repository
public interface RoundRepository extends CrudRepository<Round, Long> {

}
