package org.motechproject.messagecampaign.service;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.config.core.constants.ConfigurationConstants;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.messagecampaign.EventKeys;
import org.motechproject.messagecampaign.contract.CampaignRequest;
import org.motechproject.messagecampaign.domain.CampaignNotFoundException;
import org.motechproject.messagecampaign.domain.campaign.CampaignEnrollment;
import org.motechproject.messagecampaign.loader.CampaignJsonLoader;
import org.motechproject.messagecampaign.scheduler.CampaignSchedulerFactory;
import org.motechproject.messagecampaign.scheduler.CampaignSchedulerService;
import org.motechproject.messagecampaign.dao.CampaignEnrollmentDataService;
import org.motechproject.messagecampaign.dao.CampaignRecordService;
import org.motechproject.messagecampaign.userspecified.CampaignRecord;
import org.motechproject.messagecampaign.web.ex.EnrollmentNotFoundException;
import org.motechproject.server.config.SettingsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link MessageCampaignService}
 */
@Service("messageCampaignService")
public class MessageCampaignServiceImpl implements MessageCampaignService {

    private EnrollmentService enrollmentService;
    private CampaignEnrollmentRecordMapper campaignEnrollmentRecordMapper;
    private CampaignEnrollmentDataService campaignEnrollmentDataService;
    private CampaignSchedulerFactory campaignSchedulerFactory;
    private CampaignRecordService campaignRecordService;
    private EventRelay relay;
    @Autowired
    @Qualifier("messageCampaignSettings")
    private SettingsFacade settingsFacade;

    @Autowired
    private CommonsMultipartResolver commonsMultipartResolver;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    public MessageCampaignServiceImpl(EnrollmentService enrollmentService, CampaignEnrollmentDataService campaignEnrollmentDataService, CampaignEnrollmentRecordMapper campaignEnrollmentRecordMapper,
                                      CampaignSchedulerFactory campaignSchedulerFactory, CampaignRecordService campaignRecordService, EventRelay relay) {
        this.enrollmentService = enrollmentService;
        this.campaignEnrollmentDataService = campaignEnrollmentDataService;
        this.campaignEnrollmentRecordMapper = campaignEnrollmentRecordMapper;
        this.campaignEnrollmentDataService = campaignEnrollmentDataService;
        this.campaignSchedulerFactory = campaignSchedulerFactory;
        this.campaignRecordService = campaignRecordService;
        this.relay = relay;
    }

    public void enroll(CampaignRequest request) {
        CampaignEnrollment enrollment = new CampaignEnrollment(request.externalId(), request.campaignName());
        enrollment.setReferenceDate(request.referenceDate());
        enrollment.setDeliverTime(request.deliverTime());
        enrollmentService.register(enrollment);
        CampaignSchedulerService campaignScheduler = campaignSchedulerFactory.getCampaignScheduler(request.campaignName());
        campaignScheduler.start(enrollment);

        Map<String, Object> param = new HashMap<>();
        param.put(EventKeys.EXTERNAL_ID_KEY, enrollment.getExternalId());
        param.put(EventKeys.CAMPAIGN_NAME_KEY, enrollment.getCampaignName());
        MotechEvent event = new MotechEvent(EventKeys.ENROLLED_USER_SUBJECT, param);

        relay.sendEventMessage(event);
    }

    @Override
    public void unenroll(String externalId, String campaignName) {
        enrollmentService.unregister(externalId, campaignName);
        CampaignEnrollment enrollment = campaignEnrollmentDataService.findByExternalIdAndCampaignName(externalId, campaignName);
        campaignSchedulerFactory.getCampaignScheduler(campaignName).stop(enrollment);

        Map<String, Object> param = new HashMap<>();
        param.put(EventKeys.EXTERNAL_ID_KEY, externalId);
        param.put(EventKeys.CAMPAIGN_NAME_KEY, campaignName);
        MotechEvent event = new MotechEvent(EventKeys.UNENROLLED_USER_SUBJECT, param);

        relay.sendEventMessage(event);
    }

    @Override
    public List<CampaignEnrollmentRecord> search(CampaignEnrollmentsQuery query) {
        List<CampaignEnrollmentRecord> campaignEnrollmentRecords = new ArrayList<>();
        for (CampaignEnrollment campaignEnrollment : enrollmentService.search(query)) {
            campaignEnrollmentRecords.add(campaignEnrollmentRecordMapper.map(campaignEnrollment));
        }
        return campaignEnrollmentRecords;
    }

    @Override
    public Map<String, List<DateTime>> getCampaignTimings(String externalId, String campaignName, DateTime startDate, DateTime endDate) {
        CampaignEnrollment enrollment = campaignEnrollmentDataService.findByExternalIdAndCampaignName(externalId, campaignName);
        if (!enrollment.isActive()) {
            return new HashMap<>();
        }
        return campaignSchedulerFactory.getCampaignScheduler(campaignName).getCampaignTimings(startDate, endDate, enrollment);
    }

    @Override
    public void updateEnrollment(CampaignRequest enrollRequest, Long enrollmentId) {
        CampaignEnrollment existingEnrollment = campaignEnrollmentDataService.findById(enrollmentId);

        if (existingEnrollment == null) {
            throw new EnrollmentNotFoundException("Enrollment with id " + enrollmentId + " not found");
        } else {
            CampaignEnrollment campaignEnrollment = campaignEnrollmentDataService.findByExternalIdAndCampaignName(
                    enrollRequest.externalId(), enrollRequest.campaignName());
            if (campaignEnrollment != null && !existingEnrollment.getExternalId().equals(campaignEnrollment.getExternalId())) {
                throw new IllegalArgumentException(String.format("%s is already enrolled in %s campaign",
                        enrollRequest.externalId(), enrollRequest.campaignName()));
            }
        }

        campaignSchedulerFactory.getCampaignScheduler(existingEnrollment.getCampaignName()).stop(existingEnrollment);

        existingEnrollment.setExternalId(enrollRequest.externalId());
        existingEnrollment.setDeliverTime(enrollRequest.deliverTime());
        existingEnrollment.setReferenceDate(enrollRequest.referenceDate());
        campaignEnrollmentDataService.update(existingEnrollment);

        campaignSchedulerFactory.getCampaignScheduler(existingEnrollment.getCampaignName()).start(existingEnrollment);
    }

    @Override
    public void stopAll(CampaignEnrollmentsQuery query) {
        List<CampaignEnrollment> enrollments = enrollmentService.search(query);
        for (CampaignEnrollment enrollment : enrollments) {
            enrollmentService.unregister(enrollment.getExternalId(), enrollment.getCampaignName());
            campaignSchedulerFactory.getCampaignScheduler(enrollment.getCampaignName()).stop(enrollment);
        }
    }

    @Override
    public void saveCampaign(CampaignRecord campaign) {
        CampaignRecord record = campaignRecordService.findByName(campaign.getName());
        if (record == null) {
            campaignRecordService.create(campaign);
        }
    }

    @Override
    public void deleteCampaign(String campaignName) {
        CampaignRecord campaignRecord = campaignRecordService.findByName(campaignName);

        if (campaignRecord == null) {
            throw new CampaignNotFoundException("Campaign not found: " + campaignName);
        } else {
            CampaignEnrollmentsQuery enrollmentsQuery = new CampaignEnrollmentsQuery().withCampaignName(campaignName);
            stopAll(enrollmentsQuery);

            campaignRecordService.delete(campaignRecord);
        }
    }

    @Override
    public CampaignRecord getCampaignRecord(String campaignName) {
        return campaignRecordService.findByName(campaignName);
    }

    @Override
    public List<CampaignRecord> getAllCampaignRecords() {
        return campaignRecordService.retrieveAll();
    }

    @Override
    public void campaignCompleted(String externalId, String campaignName) {
        unenroll(externalId, campaignName);
    }

    @PostConstruct
    @Override
    public void loadCampaigns() {
        InputStream inputStream = settingsFacade.getRawConfig(MESSAGE_CAMPAIGNS_JSON_FILENAME);
        if (inputStream != null) {
            List<CampaignRecord> records = new CampaignJsonLoader().loadCampaigns(inputStream);
            for (CampaignRecord record : records) {
                if(campaignRecordService.findByName(record.getName()) == null) {
                    campaignRecordService.create(record);
                }
            }
        }
    }

    @MotechListener(subjects = ConfigurationConstants.FILE_CHANGED_EVENT_SUBJECT)
    public void changeMaxUploadSize(MotechEvent event) {
        String uploadSize = configurationService.getPlatformSettings().getUploadSize();

        if (StringUtils.isNotBlank(uploadSize)) {
            commonsMultipartResolver.setMaxUploadSize(Long.valueOf(uploadSize));
        }
    }
}
