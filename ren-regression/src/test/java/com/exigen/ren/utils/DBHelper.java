package com.exigen.ren.utils;

import com.exigen.ipb.eisa.utils.RetryService;
import com.exigen.ipb.eisa.utils.db.DBService;
import com.exigen.istf.exceptions.IstfException;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class DBHelper {

    private final static String GET_DOCUMENT = "SELECT EV.*, DS.parentEvent_id, DS.year, DS.createdAt, cast(DS.BYTES_ as XML) doc " +
            "FROM EPB_OP_DOCGEN_EVENT EV LEFT JOIN EPB_OP_DOCGEN_DATASOURCE DS ON EV.docgenTicket=DS.docgenTicket " +
            "WHERE EV.entityRefNo='%s' AND EV.entityType='%s' ORDER BY id DESC";
    private static DBService dbService = DBService.get();

    public static XmlValidator getDocument(String entityNumber, EntityType entityType) {
        String request = String.format(GET_DOCUMENT, entityNumber, entityType.getEntityType());
        try {
            return new XmlValidator(RetryService.run(
                    optional -> Optional.ofNullable(optional).isPresent(),
                    () -> dbService.getRow(request).get("doc"),
                    StopStrategies.stopAfterAttempt(10), WaitStrategies.fixedWait(5, TimeUnit.SECONDS)));
        } catch (RuntimeException e) {
            throw new IstfException("Document not found", e);
        }
    }

    public static XmlValidator getDocumentByNumber(String entityNumber, EntityType entityType, int documentNumber) {
        String request = String.format(GET_DOCUMENT, entityNumber, entityType.getEntityType());
        try {
            return new XmlValidator(RetryService.run(
                    optional -> Optional.ofNullable(optional).isPresent(),
                    () -> dbService.getRows(request).get(documentNumber).get("doc"),
                    StopStrategies.stopAfterAttempt(10), WaitStrategies.fixedWait(5, TimeUnit.SECONDS)));
        } catch (RuntimeException e) {
            throw new IstfException("Document not found", e);
        }
    }

    public static XmlValidator getDocument(String entityNumber, EntityType entityType, EventName eventName) {
        String request = String.format("SELECT EV.*, DS.parentEvent_id, DS.year, DS.createdAt, cast(DS.BYTES_ as " +
                "XML) doc FROM EPB_OP_DOCGEN_EVENT EV LEFT JOIN EPB_OP_DOCGEN_DATASOURCE DS ON EV.docgenTicket=DS.docgenTicket where EV.entityRefNo='%s' AND EV.entityType='%s' AND EV.eventName='%s' ORDER BY id DESC",
                entityNumber, entityType.getEntityType(), eventName.getEventName());
        try {
            return new XmlValidator(RetryService.run(
                    optional -> Optional.ofNullable(optional).isPresent(),
                    () -> dbService.getRow(request).get("doc"),
                    StopStrategies.stopAfterAttempt(10), WaitStrategies.fixedWait(5, TimeUnit.SECONDS)));
        } catch (RuntimeException e) {
            throw new IstfException("Document not found", e);
        }
    }

    public enum EntityType {
        CASE("Case"),
        CUSTOMER("Customer"),
        CLAIM("claim"),
        AGENCY("Agency"),
        BILLING("Billing");
        private String entityType;

        EntityType(String entityType) {
            this.entityType = entityType;
        }

        public String getEntityType() {
            return entityType;
        }
    }

    public enum EventName {
        REN_POLICY_ISSUE_CLASS("REN_POLICY_ISSUE_CLASS"),
        REN_POLICY_ISSUE("REN_POLICY_ISSUE"),
        SEND_OVERPAYMENT_LETTER_POST_NOTIFICATION("SEND_OVERPAYMENT_LETTER_POST_NOTIFICATION"),
        SEND_OVERPAYMENT_LETTER_PRE_NOTIFICATION("SEND_OVERPAYMENT_LETTER_PRE_NOTIFICATION"),
        CLAIM_PAYMENT_ISSUE("CLAIM_PAYMENT_ISSUE");

        private String eventName;

        EventName(String eventName) {
            this.eventName = eventName;
        }

        public String getEventName() {
            return eventName;
        }
    }
}
