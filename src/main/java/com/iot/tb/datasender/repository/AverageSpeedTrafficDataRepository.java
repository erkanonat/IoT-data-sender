package com.iot.tb.datasender.repository;

import com.iot.tb.datasender.entity.AverageTrafficSpeedData;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AverageSpeedTrafficDataRepository extends CassandraRepository<AverageTrafficSpeedData> {

    @Query("SELECT * FROM traffickeyspace.average_speed_traffic WHERE ptsid = ?0 LIMIT 1 ALLOW FILTERING")
    Iterable<AverageTrafficSpeedData> findAverageSpeedTrafficDataByDate(String ptsid);
}
