package org.motechproject.openmrs19.service.impl;

import org.apache.commons.lang.Validate;
import org.joda.time.DateTime;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.openmrs19.EventKeys;
import org.motechproject.openmrs19.domain.OpenMRSAttribute;
import org.motechproject.openmrs19.domain.OpenMRSPerson;
import org.motechproject.openmrs19.exception.OpenMRSException;
import org.motechproject.openmrs19.helper.EventHelper;
import org.motechproject.openmrs19.service.OpenMRSPersonService;
import org.motechproject.openmrs19.rest.HttpException;
import org.motechproject.openmrs19.resource.PersonResource;
import org.motechproject.openmrs19.resource.model.Attribute;
import org.motechproject.openmrs19.resource.model.Attribute.AttributeType;
import org.motechproject.openmrs19.resource.model.AttributeTypeListResult;
import org.motechproject.openmrs19.resource.model.Concept;
import org.motechproject.openmrs19.resource.model.Person;
import org.motechproject.openmrs19.resource.model.Person.PreferredAddress;
import org.motechproject.openmrs19.resource.model.Person.PreferredName;
import org.motechproject.openmrs19.util.ConverterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OpenMRSPersonServiceImpl implements OpenMRSPersonService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenMRSPersonServiceImpl.class);

    private final Map<String, String> attributeTypeUuidCache = new HashMap<String, String>();

    private final PersonResource personResource;
    private final EventRelay eventRelay;

    @Autowired
    public OpenMRSPersonServiceImpl(PersonResource personResource, EventRelay eventRelay) {
        this.personResource = personResource;
        this.eventRelay = eventRelay;
    }

    @Override
    public List<OpenMRSPerson> findByPersonId(String uuid) {
        Person person = null;
        List<OpenMRSPerson> personList = new ArrayList<>();
        try {
            person = personResource.getPersonById(uuid);
        } catch (HttpException e) {
            LOGGER.error("Failed to retrieve person with uuid: " + uuid);
            return personList;
        }
        personList.add(ConverterUtils.convertToMrsPerson(person));
        return personList;
    }

    @Override
    public OpenMRSPerson addPerson(OpenMRSPerson personMrs) {
        Validate.notNull(personMrs, "Person cannot be null");
        OpenMRSPerson person = ConverterUtils.createPerson(personMrs);
        Person converted = ConverterUtils.convertToPerson(person, true);
        Person saved = null;
        try {
            saved = personResource.createPerson(converted);
        } catch (HttpException e) {
            LOGGER.error("Failed to create person for: " + person.getFullName());
            throw new OpenMRSException(e);
        }

        person.setId(saved.getUuid());

        saveAttributesForPerson(person);
        eventRelay.sendEventMessage(new MotechEvent(EventKeys.CREATED_NEW_PERSON_SUBJECT, EventHelper.personParameters(person)));
        return person;
    }

    @Override
    public OpenMRSPerson addPerson(String personId, String firstName, String lastName, DateTime dateOfBirth,
                                   String gender, String address, List<OpenMRSAttribute> attributes)  {
        OpenMRSPerson person = new OpenMRSPerson(personId);
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setDateOfBirth(dateOfBirth);
        person.setGender(gender);
        person.setAddress(address);
        person.setAttributes(ConverterUtils.createAttributeList(attributes));

        Person converted = ConverterUtils.convertToPerson(person, true);
        Person saved = null;
        try {
            saved = personResource.createPerson(converted);
        } catch (HttpException e) {
            LOGGER.error("Failed to create person for: " + person.getFirstName() + " " + person.getMiddleName() + " " + person.getLastName());
            throw new OpenMRSException(e);
        }

        person.setPersonId(saved.getUuid());

        saveAttributesForPerson(person);
        eventRelay.sendEventMessage(new MotechEvent(EventKeys.CREATED_NEW_PERSON_SUBJECT, EventHelper.personParameters(person)));
        return person;
    }

    void saveAttributesForPerson(OpenMRSPerson person) {
        for (OpenMRSAttribute attribute : person.getAttributes()) {
            Attribute attr = new Attribute();
            attr.setValue(attribute.getValue());
            attr.setAttributeType(getAttributeTypeUuid(attribute.getName()));

            try {
                personResource.createPersonAttribute(person.getPersonId(), attr);
            } catch (HttpException e) {
                LOGGER.warn("Unable to add attribute to person with id: " + person.getPersonId());
            }
        }
    }

    private AttributeType getAttributeTypeUuid(String name) {
        if (!attributeTypeUuidCache.containsKey(name)) {
            AttributeTypeListResult result = null;
            try {
                result = personResource.queryPersonAttributeTypeByName(name);
            } catch (HttpException e) {
                LOGGER.error("HTTP request failed to get attribute type uuid for attribute with name: " + name);
                throw new OpenMRSException(e);
            }

            if (result.getResults().size() == 0) {
                LOGGER.error("No attribute found with name: " + name);
                throw new OpenMRSException("No attribute with name: " + name + " found in OpenMRS");
            } else if (result.getResults().size() > 1) {
                LOGGER.warn("Found more than 1 attribute with name: " + name);
            }
            attributeTypeUuidCache.put(name, result.getResults().get(0).getUuid());
        }

        AttributeType type = new AttributeType();
        type.setUuid(attributeTypeUuidCache.get(name));

        return type;
    }

    void deleteAllAttributes(OpenMRSPerson person) {
        Person saved = null;
        try {
            saved = personResource.getPersonById(person.getPersonId());
        } catch (HttpException e) {
            LOGGER.error("Failed to retrieve person when deleting attributes with uuid: " + person.getPersonId());
            throw new OpenMRSException(e);
        }

        List<Attribute> attributes = saved.getAttributes();
        for (Attribute attr : attributes) {
            try {
                personResource.deleteAttribute(person.getPersonId(), attr);
            } catch (HttpException e) {
                LOGGER.warn("Failed to delete attribute with name: " + attr.getName());
            }
        }
    }

    @Override
    public OpenMRSPerson updatePerson(OpenMRSPerson personMrs) {
        OpenMRSPerson person = ConverterUtils.createPerson(personMrs);
        Person converted = ConverterUtils.convertToPerson(person, false);
        try {
            // Must update the name and address separately when updating a
            // person.
            Person saved = personResource.getPersonById(person.getPersonId());

            PreferredName name = saved.getPreferredName();
            name.setGivenName(person.getFirstName());
            name.setMiddleName(person.getMiddleName());
            name.setFamilyName(person.getLastName());
            personResource.updatePersonName(saved.getUuid(), name);

            PreferredAddress addr = saved.getPreferredAddress();
            if (addr == null) {
                addr = new PreferredAddress();
            }

            addr.setAddress1(person.getAddress());
            personResource.updatePersonAddress(saved.getUuid(), addr);

            converted.setUuid(saved.getUuid());
            personResource.updatePerson(converted);
            eventRelay.sendEventMessage(new MotechEvent(EventKeys.UPDATED_PERSON_SUBJECT, EventHelper.personParameters(person)));
        } catch (HttpException e) {
            LOGGER.error("Failed to update a person in OpenMRS with id: " + person.getPersonId());
            throw new OpenMRSException(e);
        }
        return findByPersonId(person.getId()).get(0);
    }

    void savePersonCauseOfDeath(String patientId, Date dateOfDeath, String conceptName) {
        Person person = new Person();
        person.setUuid(patientId);
        person.setDead(true);
        person.setDeathDate(dateOfDeath);

        Concept concept = new Concept();
        concept.setDisplay(conceptName);
        person.setCauseOfDeath(concept);

        try {
            personResource.updatePerson(person);
        } catch (HttpException e) {
            LOGGER.error("Failed to save cause of death observation for patient id: " + patientId);
            throw new OpenMRSException(e);
        }
    }

    @Override
    public void removePerson(OpenMRSPerson personMrs) {
        try {
            personResource.removePerson(personMrs.getPersonId());
            eventRelay.sendEventMessage(new MotechEvent(EventKeys.DELETED_PERSON_SUBJECT, EventHelper.personParameters(personMrs)));
        } catch (HttpException e) {
            LOGGER.error("Failed to remove person for: " + personMrs.getLastName());
            throw new OpenMRSException(e);
        }
    }

}
