package com.example.nearblood;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("maps/api/directions/json")
    Single<Results> getDirections(@Query("mode") String mode,
                                  @Query("transit_routing_preference") String preference,
                                  @Query("origin") String origin,
                                  @Query("destination") String destination,
                                  @Query("key") String key);
}
