package org.motechproject.csd.web;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.csd.client.CSDHttpClient;
import org.motechproject.csd.client.SOAPClient;
import org.motechproject.csd.domain.CSD;
import org.motechproject.csd.domain.CommunicationProtocol;
import org.motechproject.csd.domain.Config;
import org.motechproject.csd.service.CSDService;
import org.motechproject.csd.service.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.xml.sax.SAXParseException;

import javax.xml.bind.UnmarshalException;
import java.io.IOException;
import java.util.Date;

@Controller
public class CSDController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CSDController.class);

    private ConfigService configService;

    private CSDHttpClient csdHttpClient;

    private SOAPClient soapClient;

    private CSDService csdService;

    @Autowired
    public CSDController(@Qualifier("configService") ConfigService configService, CSDHttpClient csdHttpClient,
                         SOAPClient soapClient, CSDService csdService) {
        this.configService = configService;
        this.csdHttpClient = csdHttpClient;
        this.soapClient = soapClient;
        this.csdService = csdService;
    }

    @RequestMapping(value = "/csd-consume", method = RequestMethod.GET)
    @ResponseBody
    public void consume() {
        Config config = configService.getConfig();
        String xmlUrl = config.getXmlUrl();
        CommunicationProtocol communicationProtocol = config.getCommunicationProtocol();

        if (xmlUrl == null) {
            throw new IllegalArgumentException("The CSD Registry URL is empty");
        }

        if (communicationProtocol.equals(CommunicationProtocol.REST)) {
            String xml = csdHttpClient.getXml(xmlUrl);
            if (xml == null) {
                throw new IllegalArgumentException("Couldn't load XML");
            }
            csdService.saveFromXml(xml);
        } else {
            DateTime lastModified;
            if (StringUtils.isNotEmpty(config.getLastModified())) {
                lastModified = DateTime.parse(config.getLastModified(),
                        DateTimeFormat.forPattern(Config.DATE_TIME_PICKER_FORMAT));
            } else {
                lastModified = new DateTime(new Date(0));
            }
            CSD csd = soapClient.getModifications(xmlUrl, lastModified).getCSD();
            csdService.update(csd);
        }

        config.setLastModified(DateTime.now().toString(Config.DATE_TIME_PICKER_FORMAT));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String handleException(Exception e) throws IOException {
        String message = e.getMessage();
        LOGGER.error(message, e);
        if (e.getCause() != null && e.getCause() instanceof UnmarshalException) {
            UnmarshalException cause = (UnmarshalException) e.getCause();
            if (cause.getLinkedException() != null && cause.getLinkedException() instanceof SAXParseException) {
                SAXParseException parseException = (SAXParseException) cause.getLinkedException();
                message += " -" + StringUtils.substringAfter(parseException.getMessage(), ":") +
                        " (line " + parseException.getLineNumber() + ")";
            }
        }
        return message;
    }
}
