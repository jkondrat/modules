package org.motechproject.vxml.service;

/**
 * Send an VXML
 */
public interface VxmlService {
    void send(final OutgoingVxml message);
}
