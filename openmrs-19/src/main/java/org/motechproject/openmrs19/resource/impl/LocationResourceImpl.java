package org.motechproject.openmrs19.resource.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.motechproject.openmrs19.OpenMrsInstance;
import org.motechproject.openmrs19.rest.HttpException;
import org.motechproject.openmrs19.rest.RestClient;
import org.motechproject.openmrs19.resource.LocationResource;
import org.motechproject.openmrs19.resource.model.Concept;
import org.motechproject.openmrs19.resource.model.Location;
import org.motechproject.openmrs19.resource.model.LocationListResult;
import org.motechproject.openmrs19.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LocationResourceImpl implements LocationResource {

    private final RestClient restClient;
    private final OpenMrsInstance openmrsInstance;

    @Autowired
    public LocationResourceImpl(RestClient restClient, OpenMrsInstance openmrsInstance) {
        this.restClient = restClient;
        this.openmrsInstance = openmrsInstance;
    }

    @Override
    public LocationListResult getAllLocations() throws HttpException {
        String json = restClient.getJson(openmrsInstance.toInstancePath("/location?v=full"));
        return (LocationListResult) JsonUtils.readJson(json, LocationListResult.class);
    }

    @Override
    public LocationListResult queryForLocationByName(String locationName) throws HttpException {
        String json = restClient.getJson(openmrsInstance.toInstancePathWithParams("/location?q={name}&v=full",
                locationName));
        return (LocationListResult) JsonUtils.readJson(json, LocationListResult.class);

    }

    @Override
    public Location getLocationById(String uuid) throws HttpException {
        String json = restClient.getJson(openmrsInstance.toInstancePathWithParams("/location/{uuid}", uuid));
        return (Location) JsonUtils.readJson(json, Location.class);
    }

    @Override
    public Location createLocation(Location location) throws HttpException {
        Gson gson = new GsonBuilder().create();
        String jsonRequest = gson.toJson(location);
        String jsonResponse = restClient.postForJson(openmrsInstance.toInstancePath("/location"), jsonRequest);
        return (Location) JsonUtils.readJson(jsonResponse, Location.class);
    }

    @Override
    public void updateLocation(Location location) throws HttpException {
        Gson gson = new GsonBuilder().registerTypeAdapter(Concept.class, new Concept.ConceptSerializer()).create();
        // uuid cannot be set on an update call
        String locationUuid = location.getUuid();
        location.setUuid(null);
        String jsonRequest = gson.toJson(location);
        restClient.postWithEmptyResponseBody(openmrsInstance.toInstancePathWithParams("/location/{uuid}", locationUuid),
                jsonRequest);
    }

    @Override
    public void removeLocation(String locationId) throws HttpException {
        restClient.delete(openmrsInstance.toInstancePathWithParams("/location/{uuid}?purge", locationId));
    }
}
