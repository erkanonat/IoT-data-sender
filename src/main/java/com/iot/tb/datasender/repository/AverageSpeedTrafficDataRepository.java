package com.iot.tb.datasender.repository;

import com.iot.tb.datasender.entity.AverageSpeedTrafficData;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

public interface AverageSpeedTrafficDataRepository extends CassandraRepository<AverageSpeedTrafficData> {


    @Query("SELECT  * FROM traffickeyspace.average_speed_traffic WHERE recorddate = ?0 ALLOW FILTERING")
    Iterable<AverageSpeedTrafficData> findAverageSpeedByDate(String date);

}
