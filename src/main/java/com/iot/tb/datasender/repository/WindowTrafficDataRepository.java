package com.iot.tb.datasender.repository;

import com.iot.tb.datasender.entity.WindowTrafficData;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WindowTrafficDataRepository extends CassandraRepository<WindowTrafficData> {

    @Query("SELECT * FROM traffickeyspace.total_traffic WHERE recorddate = ?0 ALLOW FILTERING")
    Iterable<WindowTrafficData> findTrafficDataByDate(String date);

}
